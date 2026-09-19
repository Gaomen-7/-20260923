# 图片基址收敛为公共 util — 执行席方案

> 来源：2026-09-19 双轴 Code Review 最严重 Spec 项（coverBase/BASE 硬编码 `localhost:8090`，端口/域名变更即全挂）。
> 目标：把散落各组件的 `http://localhost:8090/mall-sys` 图片基址收敛到**单一常量 + 一组拼接函数**，以后改端口/域名只动一个文件。
> 范围：纯前端 `D:\vue-space\mypro5`，**不改后端、不改接口、不改任何图片显示效果**。
> 硬约束：webpack3 + babel6，**禁止 `?.`、禁止 async/await、禁止新 npm 依赖**；保持 ES5 兼容写法。

---

## 一、现状盘点（指挥席已 Grep 核实，共 8 处硬编码）

| # | 文件 | 行号 | 现状 | 用途 |
|---|---|---|---|---|
| 1 | `src/network/request.js` | L6 | `baseURL: 'http://localhost:8090/mall-sys'` | axios 实例基址（**唯一"合法"硬编码，作为收敛源头**） |
| 2 | `src/components/GoodsManage.vue` | L183 + L80 | `coverBase: '.../PublishGoods/showImg'`，模板 `coverBase + '/goods/' + mainImage` | SPU 封面图 |
| 3 | `src/components/SimulateUser.vue` | L112 + L25 | 同上 coverBase | 模拟用户页商品主图 |
| 4 | `src/components/Brand.vue` | L136、L148、L320-321 | 两处 `let BASE = "http://localhost:8090/mall-sys"`，拼 `/Brand/showImg/` | 品牌列表 LOGO + 上传后预览 |
| 5 | `src/components/Category.vue` | L260-261 | `PREFIX = ".../mall-sys" + "/Brand/showImg"` | 类别页关联品牌小图 |
| 6 | `src/components/PublishBaseInfo.vue` | L244、L251、L264、L270 | 两处 `let BASE`，`BASE + resp.logoUri`（logoUri 自带 `/` 开头路径） | 主图/图集上传后预览 |
| 7 | `src/components/SetSku.vue` | L176 | `'.../PublishGoods/showImg/album/' + fileName` | SKU 相册图 |
| 8 | `src/components/AdvertManage.vue` | L269 | `'/mall-sys/Advert/showImg/' + fileName`（相对路径，**没带 host**） | 广告图 |

---

## 二、设计：新建 `src/utils/imageUrl.js`

新建一个文件，**全部基址只有这一处字面量**：

```js
/* src/utils/imageUrl.js — 全站图片展示地址唯一出口。
   以后换端口/换域名/打包部署，只改这里的 API_BASE。 */

export const API_BASE = 'http://localhost:8090/mall-sys';

/* 通用：拼接后端任意展示路径（path 以 / 开头，如 /Brand/showImg/d01.png） */
export function apiUrl( path ){
  return API_BASE + ( path || '' );
}

/* 商品 SPU 封面主图：fileName 为 mainImage 字段（如 p01.jpg），空值返回 '' */
export function goodsCoverUrl( fileName ){
  if( !fileName ) return '';
  return API_BASE + '/PublishGoods/showImg/goods/' + fileName;
}

/* SKU 相册图：fileName 为相册文件名 */
export function albumUrl( fileName ){
  if( !fileName ) return '';
  return API_BASE + '/PublishGoods/showImg/album/' + fileName;
}

/* 品牌 LOGO：fileName 为 logoName/logoUrl 字段，空值返回 '' */
export function brandLogoUrl( fileName ){
  if( !fileName ) return '';
  return API_BASE + '/Brand/showImg/' + fileName;
}

/* 广告图：fileName 为 image_url 字段，空值返回 '' */
export function advertUrl( fileName ){
  if( !fileName ) return '';
  return API_BASE + '/Advert/showImg/' + fileName;
}
```

> 说明：保留 `API_BASE` 具名导出，request.js 也引用它，做到"改一处，接口和图片一起变"。

---

## 三、逐文件改造（按此精确执行）

### 0. `src/network/request.js`

- 顶部加：`import { API_BASE } from '@/utils/imageUrl';`
- L6 改为：`baseURL: API_BASE,`
- 其余不动。

### 1. `src/components/GoodsManage.vue`

- script 顶部 `import { goodsCoverUrl } from '@/utils/imageUrl';`
- **删除** data 里 L183 的 `coverBase: '...'` 整行。
- methods 里新增一个方法（模板要用到）：

```js
coverUrl( fileName ){
  return goodsCoverUrl( fileName );
},
```

- 模板 L80 改为：
  `<img v-if="scope.row.mainImage" :src="coverUrl(scope.row.mainImage)" />`

### 2. `src/components/SimulateUser.vue`

- 顶部 `import { goodsCoverUrl } from '@/utils/imageUrl';`
- **删除** data 里 L112 的 `coverBase: '...'` 整行（连同上一行 `autoTimer: null,` 后的逗号注意别留语法错误）。
- methods 里新增：

```js
coverUrl( fileName ){
  return goodsCoverUrl( fileName );
},
```

- 模板 L25 改为：
  `<img v-if="g.mainImage" class="goods-img-real" :src="coverUrl(g.mainImage)" />`

### 3. `src/components/Brand.vue`

- 顶部 `import { brandLogoUrl } from '@/utils/imageUrl';`
- L136 删除 `let BASE = ...`；L148 改为：
  `br.imgUrl = brandLogoUrl( br.logoName || br.logoUrl );`
  （保留 L147 的 `br.logoName = br.logoName || br.logoUrl;` 兼容逻辑不动）
- L320 删除 `let BASE = ...`；L321 改为：
  `this.brandForm.imgUrl = brandLogoUrl( fileName );`

### 4. `src/components/Category.vue`

- 顶部 `import { brandLogoUrl } from '@/utils/imageUrl';`
- L260-261 的 `let PREFIX = "http://localhost:8090/mall-sys" + "/Brand/showImg";` 改为：
  `let PREFIX = brandLogoUrl('').slice(0, -1);  // 不带文件名的品牌图目录前缀`
  - 即 `brandLogoUrl('')` 返回 `''`，**不行**——改成直接导出目录常量。请在 `imageUrl.js` 额外加：
    `export const BRAND_IMG_PREFIX = API_BASE + '/Brand/showImg';`
  - Category.vue 改为 `import { BRAND_IMG_PREFIX } from '@/utils/imageUrl';`，`let PREFIX = BRAND_IMG_PREFIX;`
- `iterateList(PREFIX, ...)` 调用与函数体**不动**（它内部自己拼 PREFIX + 文件名）。

### 5. `src/components/PublishBaseInfo.vue`

- 顶部 `import { apiUrl } from '@/utils/imageUrl';`
- L244 删除 `let BASE = ...`；L251 改为 `this.imageUrl = apiUrl( resp.logoUri );`
- L264 删除 `let BASE = ...`；L270 改为 `url: apiUrl( resp.logoUri )`

### 6. `src/components/SetSku.vue`

- 顶部 `import { albumUrl } from '@/utils/imageUrl';`
- L175-177 的方法体改为：

```js
imgUrl( fileName ){
  return albumUrl( fileName );
},
```

模板 L39 调用不动。

### 7. `src/components/AdvertManage.vue`

- 顶部 `import { advertUrl } from '@/utils/imageUrl';`
- L266-270 的 `getImageUrl` 改为：

```js
getImageUrl( fileName ){
  if( !fileName ) return '';
  if( fileName.indexOf('http') === 0 ) return fileName;
  return advertUrl( fileName );
},
```

（原来走相对路径 `/mall-sys/...`，现统一带 host；行为对开发环境一致，对将来部署反而是修复。）

---

## 四、验收标准（指挥席会逐条核对）

1. Grep 全 `src` 目录 `localhost:8090`：**只应命中 `src/utils/imageUrl.js` 1 处**（API_BASE 定义）+ request.js 0 处（已改 import）。其余 8 处全部清零。
2. Grep `showImg`：拼接逻辑全部来自 util 函数，组件里不再出现裸路径串。
3. `npm run build` **Build complete / exit 0**，无 webpack 报错。
4. 浏览器人工过 6 个页面图片仍正常显示（用户验收）：
   - 商品管理封面图、模拟用户页商品图、品牌管理 LOGO、类别页品牌小图、发布商品主图/图集上传预览、SKU 设置相册缩略图、广告管理图。
5. **回归点**：Brand.vue 的 `logoName || logoUrl` 兼容、"无图"占位必须保留；PublishBaseInfo 上传成功后图片预览不能变空；SetSku 相册选择面板图不能变空。

## 五、不做什么（scope 边界）

- 不改 request.js 拦截器、不改后端、不动任何接口字段。
- 不引入环境变量 / webpack DefinePlugin（课程作业级别，单一常量足够，避免增加配置面）。
- 不动 SkuManage.vue（它的 defaultImage 目前只显示文件名/首字，未拼 URL，不在本次范围）。
