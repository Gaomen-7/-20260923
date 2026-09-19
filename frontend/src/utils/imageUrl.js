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

/* 品牌 LOGO 目录前缀（不带文件名），供需要自行拼接的场景 */
export const BRAND_IMG_PREFIX = API_BASE + '/Brand/showImg';

/* 广告图：fileName 为 image_url 字段，空值返回 '' */
export function advertUrl( fileName ){
  if( !fileName ) return '';
  return API_BASE + '/Advert/showImg/' + fileName;
}
