package com.zoo.whale.business.fdkj;

import cn.hutool.core.codec.Base64;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipUtils {

    public static final String compress2Base64(String paramString) {
        byte[] zipByte = compress(paramString);
        if(zipByte!=null && zipByte.length>0){
            return Base64.encode(zipByte);
        }
        return null;
    }

    public static final byte[] compress(String paramString) {
        if (paramString == null)
            return null;
        byte[] arrayOfByte;
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             ZipOutputStream zipOutputStream = new ZipOutputStream(byteArrayOutputStream)){
            zipOutputStream.putNextEntry(new ZipEntry("0"));
            zipOutputStream.write(paramString.getBytes());
            zipOutputStream.flush();
            zipOutputStream.closeEntry();
            if(zipOutputStream!=null){
                zipOutputStream.close();
            }
            arrayOfByte = byteArrayOutputStream.toByteArray();
            if(byteArrayOutputStream!=null){
                byteArrayOutputStream.close();
            }
        } catch (IOException localIOException5) {
            arrayOfByte = null;
            localIOException5.printStackTrace();
        }
        return arrayOfByte;
    }

    public static final String decompress(byte[] paramArrayOfByte) {
        if (paramArrayOfByte == null)
            return null;
        String str;
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(paramArrayOfByte);
             ZipInputStream zipInputStream = new ZipInputStream(byteArrayInputStream)){
            ZipEntry localZipEntry = zipInputStream.getNextEntry();
            byte[] arrayOfByte = new byte[1024];
            int i ;
            while ((i = zipInputStream.read(arrayOfByte)) != -1)
                byteArrayOutputStream.write(arrayOfByte, 0, i);
            str = byteArrayOutputStream.toString();
            if(zipInputStream!=null){
                zipInputStream.close();
            }
            if(byteArrayInputStream!=null){
                byteArrayInputStream.close();
            }
            if(byteArrayOutputStream!=null){
                byteArrayOutputStream.close();
            }
        } catch (IOException localIOException7) {
            str = null;
            localIOException7.printStackTrace();
        }
        return str;
    }

    public static void main(String[] args) {
        String z = "xx";
        String s= compress2Base64(z);
        String zipout= decompress(Base64.decode(s));
        System.out.println(zipout);
        System.out.println(compress2Base64(zipout));
    }
}
