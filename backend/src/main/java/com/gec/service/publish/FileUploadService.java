package com.gec.service.publish;
import com.gec.components.FileTemplate;
import com.gec.domain.vo.UploadResultVO;
import com.gec.utils.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@Service
public class FileUploadService {
    @Autowired
    private FileTemplate fileTemplate;

    public UploadResultVO upload(MultipartFile file, String subDir, String uriPrefix) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("file is empty");
        }
        String newName = makeNewName(file);
        String logoUri = uriPrefix + subDir + "/" + newName;
        try {
            fileTemplate.setMultipartFile(file);
            fileTemplate.saveFile(subDir, newName);
        } catch (IOException e) {
            throw new RuntimeException("upload failed", e);
        }
        UploadResultVO result = new UploadResultVO();
        result.setFileName(newName);
        result.setLogoUri(logoUri);
        return result;
    }

    public byte[] getFileBytes(String subDir, String fileName) throws IOException {
        return fileTemplate.getFile(subDir, fileName);
    }

    private String makeNewName(MultipartFile file) {
        String uuid = FileUtils.makeUUID();
        String fileName = file.getOriginalFilename();
        String extName = FileUtils.extName(fileName);
        return uuid + extName;
    }
}
