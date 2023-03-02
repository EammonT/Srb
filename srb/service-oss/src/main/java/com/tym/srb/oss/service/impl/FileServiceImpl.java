package com.tym.srb.oss.service.impl;

import com.tym.srb.base.util.StreamUtils;
import com.tym.srb.oss.service.FileService;
import org.springframework.stereotype.Service;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {
    @Override
    public String upload(InputStream inputStream, String module, String originalFilename) {
        // 上传文件流
            // 创建文件目录结构
            SimpleDateFormat sdf = new SimpleDateFormat("/yyyy/MM/dd/");
            String timeFolder = "E:\\idea\\SpringCloud\\srb\\"+sdf.format(new Date());
        try {
            //得到上传文件
            byte[] b = StreamUtils.streamToByteArray(inputStream); //b是上传的文件内容
            // 生成文件名
            originalFilename = UUID.randomUUID() + originalFilename.substring(originalFilename.lastIndexOf("."));
            File dir = new File(timeFolder);
            //判断当前目录是否存在
            if (!dir.exists()) {
                //目录不存在，需要创建
                dir.mkdirs();
            }
            // 将临时文件转存到指定位置
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(timeFolder + originalFilename));
            bos.write(b);
            bos.close();
            inputStream.close();


        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        // 返回文件的url地址
        return timeFolder + originalFilename;
    }

    @Override
    public boolean removeFile(String url) {
        File file = new File(url);
        if (file.exists()){
            file.delete();
            System.out.println("文件删除成功！");
            return true;
        }else {
            System.out.println("文件不存在！");
            return false;
        }
    }
}
