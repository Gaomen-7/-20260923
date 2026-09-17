package com.gec.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gec.components.FileTemplate;
import com.gec.utils.FileUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public abstract class BaseController {
    static final int DEF_PAGE = 1;    //默认页码
    static final int DEF_LIMIT = 10;  //默认页大小。

    public Page newPage(Integer page, Integer limit){
        Integer _page = getInteger(page,DEF_PAGE);
        Integer _limit = getInteger(limit,DEF_LIMIT);
        return new Page(_page,_limit);
    }

    /* {2}做一个数据校验, 不能为空, 不能 <= 0 */
    private Integer getInteger(Integer value, int defVal) {
        if( value==null || value.intValue()<=0 ){
            return defVal;
        }
        return value;
    }
    protected abstract FileTemplate getFileTemplate();

    Map<String,String> MIMES = new HashMap();
    protected String getMIME(String fileName){
        String extName = FileUtils.extName(fileName);
        String mime = MIMES.get(extName);
        return mime;
    }
    //mime 是设置在响应头部, 再来告之浏览器, 当前这个文件的格式。
    public BaseController(){
        MIMES.put(".jpg","image/jpeg");
        MIMES.put(".jpeg","image/jpeg");
        MIMES.put(".png","image/png");
        MIMES.put(".html","text/html");
		MIMES.put(".css","text/css");
		MIMES.put(".js","text/javascript");
    }

    /*
    *  MIME: 媒体内容类型 (意指告诉浏览器当前的文件内型是什么)
    *  扩展名  MIME
    *  jpg    image/jpeg
    *  png    image/png
    *  html   text/html
    *  mp4    mpeg/mp4
    */

    //2.这个方法用来输出文件。
    protected void outFile(HttpServletResponse resp,
        String subDIR, String fileName)
        throws IOException{
        /* 1.获取文件操作操作模板类。*/
        FileTemplate temp = getFileTemplate();
        /* 2.读取文件数据。
        * d:\haida\space\
        * */
        byte[] data = temp.getFile(subDIR,fileName);
        /* 3.获取 MIME 类型。*/
        String mime =getMIME(fileName);
        /* 4.设置 MIME 到响应头。*/
        resp.setContentType(mime);
        /* 5.把数据输出到响应的缓冲区。*/
        resp.getOutputStream().write(data);

    }

    /*
    * 1.在目录下放置一个文件进来。
    *   位置: D:\\haida\\space\\upload
    *   文件: 404_pic.png
    */
    protected void send404File(HttpServletResponse resp) {
        try{
            outFile(resp, "", "404_pic.png");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
