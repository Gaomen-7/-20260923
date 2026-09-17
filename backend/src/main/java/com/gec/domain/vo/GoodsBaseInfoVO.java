package com.gec.domain.vo;

import com.gec.domain.entity.Category;
import com.gec.domain.entity.GoodsDetail;
import com.gec.domain.entity.PointRule;
import lombok.Data;

import java.util.List;

@Data
public class GoodsBaseInfoVO
    implements java.io.Serializable{
    private GoodsDetail goodsDetail;
    private PointRule pointRule;
    private SpuAlbumVO spuAlbumVO;
}
