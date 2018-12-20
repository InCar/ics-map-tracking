package com.incarcloud.base.util;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileUtil {
    /**
     * 从InputStream读取流数据写入OutputStream中
     * @param is
     * @param os
     */
    public static void write(InputStream is, OutputStream os) throws IOException{
        int len;
        byte[] content=new byte[1024];
        while((len=is.read(content))!=-1){
            os.write(content,0,len);
            os.flush();
        }
    }

    /**
     * 如果文件夹已存在则不创建
     * @param path
     */
    public static void createDirectories(Path path) throws IOException{
        if(path==null){
            return;
        }
        Files.createDirectories(path);
    }


}
