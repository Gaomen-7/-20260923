package com.gec.domain.vo;
import lombok.Data;
import java.io.Serializable;
@Data
public class UploadResultVO implements Serializable {
    private String fileName;
    private String logoUri;
}
