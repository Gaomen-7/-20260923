package com.gec.service.publish;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gec.dao.GoodsInfoMapper;
import com.gec.dao.SpuGoodsAttrMapper;
import com.gec.dao.SkuInfoMapper;
import com.gec.dao.SkuAlbumMapper;
import com.gec.dao.SkuSaleAttrValueMapper;
import com.gec.domain.entity.GoodsDetail;
import com.gec.domain.entity.GoodsAttrValue;
import com.gec.domain.entity.GoodsInfo;
import com.gec.domain.entity.SpuGoodsAttr;
import com.gec.domain.entity.SkuAlbum;
import com.gec.domain.entity.SkuInfo;
import com.gec.domain.entity.SkuSaleAttrValue;
import com.gec.domain.vo.GoodsBaseInfoVO;
import com.gec.domain.vo.SkuGenerateVO;
import com.gec.service.publish.AttrLookup;
import com.gec.service.publish.PublishSessionStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 商品发布流程的 5 表持久化编排模块（tbl_goods_info / tbl_spu_goods_attr /
 * tbl_sku_info / tbl_sku_album / tbl_sku_sale_attr_value），
 * 从 GoodsDetailServiceImpl.persistAll 提取。
 */
@Service
public class GoodsPersister {

    @Autowired
    private PublishSessionStore sessionStore;

    @Autowired
    private GoodsInfoMapper goodsInfoMapper;

    @Autowired
    private SpuGoodsAttrMapper spuGoodsAttrMapper;

    @Autowired
    private SkuInfoMapper skuInfoMapper;

    @Autowired
    private SkuAlbumMapper skuAlbumMapper;

    @Autowired
    private SkuSaleAttrValueMapper skuSaleAttrValueMapper;

    @Autowired
    private AttrLookup attrLookup;

    /**
     * 将发布会话的四步缓存数据持久化到 5 张表。
     *
     * @param pubKey 发布会话 key
     * @return 自增 goodsId
     */
    @Transactional
    public Integer persist(String pubKey) {
        /* 1.从Redis读取四步数据。 */
        GoodsBaseInfoVO baseVO =
                (GoodsBaseInfoVO) sessionStore.get(pubKey, PublishSessionStore.PublishStep.BASE);
        Object goodsAttrData = sessionStore.get(pubKey, PublishSessionStore.PublishStep.GOODS_ATTR);
        Object saleAttrData = sessionStore.get(pubKey, PublishSessionStore.PublishStep.SALE_ATTR);
        Object skuData = sessionStore.get(pubKey, PublishSessionStore.PublishStep.SKU);

        /* Redis读出可能是List<LinkedHashMap>，统一用ObjectMapper转换。 */
        ObjectMapper mapper = new ObjectMapper();
        List<GoodsAttrValue> goodsAttrList = (goodsAttrData != null)
                ? mapper.convertValue(goodsAttrData, new TypeReference<List<GoodsAttrValue>>() {})
                : null;
        List<GoodsAttrValue> saleAttrList = (saleAttrData != null)
                ? mapper.convertValue(saleAttrData, new TypeReference<List<GoodsAttrValue>>() {})
                : null;
        List<SkuGenerateVO.SkuRow> skuRows = (skuData != null)
                ? mapper.convertValue(skuData, new TypeReference<List<SkuGenerateVO.SkuRow>>() {})
                : null;

        if (baseVO == null || baseVO.getGoodsDetail() == null) {
            throw new RuntimeException("基本信息缓存不存在，无法持久化。");
        }

        /* 2.写入 tbl_goods_info，获取自增goodsId。 */
        GoodsDetail gd = baseVO.getGoodsDetail();
        GoodsInfo goodsInfo = new GoodsInfo();
        goodsInfo.setGoodsName(gd.getGoodsName());
        /* 注意：GoodsDetail.goodsDetails → GoodsInfo.goodsDescription */
        goodsInfo.setGoodsDescription(gd.getGoodsDetails());
        goodsInfo.setCategoryId(gd.getCategoryId());
        goodsInfo.setBrandId(gd.getBrandId());
        goodsInfo.setMainImage(gd.getMainImage());
        goodsInfo.setWeight(gd.getWeight());
        goodsInfo.setPublishStatus(0);
        String now = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        goodsInfo.setCreateTime(now);
        goodsInfo.setUpdateTime(now);
        goodsInfoMapper.insert(goodsInfo);
        Integer goodsId = goodsInfo.getId();

        /* 3.写入 tbl_spu_goods_attr（规格参数）。
         * 先收集所有attrId，一次性批量查询属性名，循环内O(1)取，消除N+1。 */
        if (goodsAttrList != null) {
            List<Integer> attrIds = new ArrayList<>();
            for (GoodsAttrValue gav : goodsAttrList) {
                attrIds.add(gav.getAttrId());
            }
            Map<Integer, String> attrNameMap = attrLookup.loadAttrNamesById(attrIds);
            int sort = 0;
            for (GoodsAttrValue gav : goodsAttrList) {
                SpuGoodsAttr sga = new SpuGoodsAttr();
                sga.setGoodsId(goodsId);
                sga.setAttrId(gav.getAttrId());
                sga.setAttrName(attrNameMap.get(gav.getAttrId()));
                sga.setAttrValue(gav.getAttrValue());
                sga.setAttrSort(sort++);
                spuGoodsAttrMapper.insert(sga);
            }
        }

        /* 4.预加载销售属性名称→id索引，供SKU销售属性写入时O(1)查找。 */
        Map<String, Integer> saleAttrIdMap =
                attrLookup.loadSaleAttrIdsByName(gd.getCategoryId());

        /* 5.遍历skuRows写 tbl_sku_info（仅 enabled=true 的SKU）。 */
        if (skuRows != null) {
            for (SkuGenerateVO.SkuRow row : skuRows) {
                if (row.getEnabled() == null || !row.getEnabled()) {
                    continue;
                }
                SkuInfo sku = new SkuInfo();
                sku.setGoodsId(goodsId);
                sku.setSkuName(row.getSkuName());
                sku.setSkuDesc("");
                sku.setCategoryId(gd.getCategoryId());
                sku.setBrandId(gd.getBrandId());
                sku.setSkuTitle(row.getSkuTitle());
                sku.setSkuSubtitle(row.getSkuSubtitle());
                sku.setPrice(row.getPrice());
                sku.setSaleCount(0);
                skuInfoMapper.insert(sku);
                Integer skuId = sku.getSkuId();

                /* 6.写入 tbl_sku_album。 */
                if (row.getAlbumImages() != null && row.getAlbumImages().length > 0) {
                    SkuAlbum album = new SkuAlbum();
                    album.setGoodsId(goodsId);
                    album.setSkuId(skuId);
                    album.setImages(String.join(";", row.getAlbumImages()));
                    album.setDefaultImage(row.getDefaultImage());
                    album.setCreateTime(now);
                    album.setUpdateTime(now);
                    skuAlbumMapper.insert(album);
                }

                /* 7.写入 tbl_sku_sale_attr_value。 */
                if (row.getAttrValues() != null) {
                    int sort = 0;
                    for (Map.Entry<String, String> entry : row.getAttrValues().entrySet()) {
                        SkuSaleAttrValue ssav = new SkuSaleAttrValue();
                        ssav.setSkuId(skuId);
                        ssav.setAttrId(saleAttrIdMap.get(entry.getKey()));
                        ssav.setAttrName(entry.getKey());
                        ssav.setAttrValue(entry.getValue());
                        ssav.setAttrSort(sort++);
                        skuSaleAttrValueMapper.insert(ssav);
                    }
                }
            }
        }

        return goodsId;
    }
}
