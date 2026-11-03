package com.aratek.jna.trustfinger.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * @author shijinlong
 * @date 2018-08-01
 * @description 文件帮助类
 */
public class FileUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileUtil.class);

    /**
     * 读取文件，返回byte数组
     *
     * @param fileFullPath
     * @return
     */
    public static byte[] readFileToByte(String fileFullPath) {
        FileInputStream fis = null;
        ByteArrayOutputStream bos = null;
        try {
            File file = new File(fileFullPath);
            fis = new FileInputStream(file);
            bos = new ByteArrayOutputStream(1000);
            byte[] b = new byte[1000];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            byte[] data = bos.toByteArray();
            bos.close();
            return data;
        } catch (IOException e) {
            LOGGER.error("readFileToByte.error:", e);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    LOGGER.error("readFileToByte.error:", e);
                }
            }
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    LOGGER.error("readFileToByte.error:", e);
                }
            }
        }
        return null;
    }

    /**
     * 将byte数组写为文件
     *
     * @param bytes
     * @param filePath
     * @param fileName
     * @return
     */
    public static boolean writeFile(byte[] bytes, String filePath, String fileName) {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        try {
            File dir = new File(filePath);
            //递归创建目录
            mkDir(dir);
            File file = new File(filePath + File.separator + fileName);
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(bytes);
            return true;
        } catch (Exception e) {
            LOGGER.error("writeFile.error:", e);
            return false;
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    LOGGER.error("writeFile.error:", e);
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    LOGGER.error("writeFile.error:", e);
                }
            }
        }
    }

    /**
     * 将字符串写入文件
     *
     * @param content  文件内容
     * @param filePath 文件路径,如:/usr/local
     * @param fileName 文件名,如:xxx.txt
     * @return
     */
    public static boolean writeFile(String content, String filePath, String fileName) {
        byte[] bytes;
        try {
            bytes = content.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("writeFile.error:", e);
            return false;
        }
        return writeFile(bytes, filePath, fileName);
    }

    /**
     * 递归创建目录
     *
     * @param file
     */
    public static void mkDir(File file) {
        file.mkdirs();
//        if (!file.isDirectory()) {
//            return;
//        }
//        if (file.exists()) {
//            return;
//        }
//        if (file.getParentFile().exists()) {
//            file.mkdir();
//        } else {
//            mkDir(file.getParentFile());
//            file.mkdir();
//        }
    }

    /*public static void main(String[] args) {
        File file = new File("D:\\temp\\dataTransferPlatform\\equipmentLogs\\20180906\\123.txt");
        if (file.isFile()) {
            System.out.println("is file");
        } else {
            file.mkdirs();
        }
    }*/
}
