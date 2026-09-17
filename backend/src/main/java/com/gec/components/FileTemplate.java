package com.gec.components;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Component
public class FileTemplate {

    //1.打开这个注释。
    @Value("${web.uploadDir}")
    private String DIR;

    private MultipartFile mFile;

    public FileTemplate(){}
    public FileTemplate(MultipartFile mfile){
        this.mFile = mfile;
    }
    public FileTemplate(String DIR){
        this.DIR = DIR;
    }

    /*
    *         DIR        (DIR)最终输出
    * 情况1   d:\space    d:\space\
    * 情况2   d:\space\   d:\space\

    *         SUB        (SUB)最终输出
    * 情况1   upload      upload\
    * 情况1   upload\     upload\

    */
    private String getPrefixPath(String DIR, String SUB){
        //1.先判断 DIR 有没有 \, 没有就加上。
        if( !DIR.endsWith("\\") ){
            DIR = DIR +"\\";
        }
        if( SUB==null ) SUB = "";
        if( SUB.length()>0 ){
            if( !SUB.endsWith("\\") ){
                SUB = SUB +"\\";
            }
        }
        return DIR + SUB;
    }

    //1.此方法仅用于小文件的处理。
    public byte[] getFile(String subDir, String fileName)
        throws IOException {
        String prefix = getPrefixPath(DIR, subDir);
        String path = prefix + fileName;
        //1.创建 file 对象。
        File _file = new File(path);
        //2.创建文件输入流
        InputStream fis = new FileInputStream(_file);
        int len = fis.available();   //文件长度
        //3.创建与文件等大的数组。
        byte[] buff = new byte[ len ];
        //4.读满这个数组。
        fis.read( buff );
        fis.close();
        //5.返回文件字节数据。
        return buff;
    }

    public void setMultipartFile(MultipartFile mFile){
        this.mFile = mFile;
    }

    public boolean saveFile(String subDir, String fileName)
        throws IOException {
        String prefix = getPrefixPath(DIR, subDir);
        String path = prefix + fileName;
        File _target = new File(path);
        /*
        * 确保目标目录存在, 不存在则自动创建。
        */
        File _parentDir = _target.getParentFile();
        if( _parentDir != null && !_parentDir.exists() ){
            _parentDir.mkdirs();
        }
        /*
        * mFile: 它是文件上传的封装类
        * (包含有文件上传的数据)
        * transferTo() 可以将里面数据写入本地磁盘。
        */
        this.mFile.transferTo( _target );
        return true;
    }

    public void printDir(){
        System.out.println("DIR:"+ DIR);
    }

}
