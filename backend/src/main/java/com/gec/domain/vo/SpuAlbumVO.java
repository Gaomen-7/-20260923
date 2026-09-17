package com.gec.domain.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class SpuAlbumVO
    implements java.io.Serializable{
    private String goodsId;
    private String skuId;
    private String[] images;
}
