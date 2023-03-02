package com.tym.srb.base.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StreamUtils {
    /**
     * 将输入流转换成byte[]，即可以把文件的内容读入到byte[]
     * @param is
     * @return
     * @throws Exception
     */
    public static byte[] streamToByteArray(InputStream is) throws Exception{
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] bytes = new byte[1024];
        int len;
        while ((len=is.read(bytes))!=-1){
            bos.write(bytes,0,len);//把读取到的数据写入到bos
        }
        byte[] array = bos.toByteArray();//将bos读取的内容转换成字节数组
        bos.close();
        return array;
    }

    public static String streamToString(InputStream is) throws Exception{
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder builder= new StringBuilder();
        String line;
        while((line=reader.readLine())!=null){ //当读取到 null时，就表示结束
            builder.append(line+"\r\n");
        }
        return builder.toString();

    }
}
