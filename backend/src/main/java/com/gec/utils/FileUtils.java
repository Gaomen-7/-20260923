package com.gec.utils;

import java.util.UUID;

public class FileUtils {
    //1.获取文件的扩展名。".mp3"
    public static String extName(String fileName){
        //1.从后往前找 "." 的位置。
        int index = fileName.lastIndexOf(".");
        int len = fileName.length();
        if( index!=-1 ){
            //2.截取点到末尾的那一截。
            return fileName.substring(
                index, len );
        }
        return "";
    }
    //2.生成 uuid (随机的数字 + 字母)
    public static String makeUUID(){
        String uuid = UUID.randomUUID().toString();
        //2.替换 uuid 中的 "-"
        uuid = uuid.replace("-","");
        //3.截取 16 位长度。
        uuid = uuid.substring(0,16);
        return uuid;
    }
}
