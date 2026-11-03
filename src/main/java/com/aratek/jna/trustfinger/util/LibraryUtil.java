package com.aratek.jna.trustfinger.util;

import com.sun.jna.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * Library 帮助类
 *
 * @author shijinlong
 * @date 2018-11-27
 */
public class LibraryUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(LibraryUtil.class);

    private static final String SUFFIX_WINDOWS = ".dll";
    private static final String SUFFIX_LINUX = ".so";

    private static final String WINDOWS = "windows";
    private static final String LINUX = "linux";

    private static final String X86 = "x86";
    private static final String X64 = "x64";

    private static String BIN_LIB = "";
    private static String LIB_EXTENSION = "";
    private static String LIB_FULL_PATH_NAME = "";

    /**
     * 需要从 jar 内置资源解压到临时目录的全部 DLL（含 TrustFinger.dll 的依赖库）。
     * 必须按依赖顺序排列：底层算法库 → 格式转换库 → 主库，
     * 确保预加载时基础库先就位。
     */
    private static final String[] LIB_NAMES = {
        "openjp2",
        "opencv_core2411",
        "opencv_imgproc2411",
        "opencv_ml2411",
        "AraNFIQ2",
        "ImgFormatConvert",
        "AraWSQ",
        "AraNFIQ",
        "TrustFinger"
    };

    /** 主库名称（JNA 最终加载的就是这个） */
    private static final String LIB_NAME_3 = "TrustFinger";

    private static final String NATIVE_TEMP_DIR = System.getProperty("java.io.tmpdir");

    /**
     * 从 jar 内置资源解压全部 DLL 到临时目录，并按依赖顺序预加载，
     * 最后返回主库 TrustFinger.dll 的完整路径，供 JNA 加载。
     *
     * <p>这样无需设置任何环境变量（AraTrustFingerLibraryPath / PATH），
     * 开箱即用。若用户设置了 AraTrustFingerLibraryPath，则外部仍会优先走环境变量分支。
     */
    public synchronized static String loadLib() {
        try {
            for (String libName : LIB_NAMES) {
                copyLibToTemp(libName);
            }
        } catch (IOException e) {
            LOGGER.error("copyLibToTemp failed", e);
            return null;
        }

        if (!Platform.isWindows()) {
            // 非 Windows 平台暂不支持内置 DLL
            LOGGER.warn("Non-Windows platform, built-in DLL loading not supported");
            return null;
        }

        String libFullName = LIB_NAME_3 + SUFFIX_WINDOWS;
        String libFullPath = NATIVE_TEMP_DIR + File.separator + libFullName;

        // 按依赖顺序预加载：先加载底层依赖库，再加载主库。
        // System.load() 使用绝对路径，确保每个 DLL 从临时目录加载，
        // 且依赖库先就位，避免主库加载时找不到依赖。
        for (String libName : LIB_NAMES) {
            String depPath = NATIVE_TEMP_DIR + File.separator + libName + SUFFIX_WINDOWS;
            try {
                File depFile = new File(depPath);
                if (!depFile.exists()) {
                    LOGGER.warn("DLL not extracted, skip: {}", depPath);
                    continue;
                }
                System.load(depPath);
                LOGGER.debug("Pre-loaded DLL: {}", libName);
            } catch (Throwable t) {
                // 某个依赖库可能已被其他库加载（Windows DLL 共享），忽略
                LOGGER.debug("Skip pre-load (maybe already loaded): {} - {}", libName, t.getMessage());
            }
        }

        LOGGER.debug("LIB_FULL_PATH_NAME={}", libFullPath);
        return libFullPath;
    }

    //getResourceAsStream以JAR中根路径为开始点
    public synchronized static void copyLibToTemp(String libName) throws IOException {
        //处理后缀名
        if (Platform.isWindows()) {
            LIB_EXTENSION = SUFFIX_WINDOWS;
            BIN_LIB = "/" + WINDOWS;
        } else {
            LIB_EXTENSION = SUFFIX_LINUX;
            BIN_LIB = "/" + LINUX;
        }
        //文件全名
        String libFullName = libName + LIB_EXTENSION;
        //文件在jar包中的位置
        if (Platform.is64Bit()) {
            BIN_LIB = BIN_LIB + "/" + X64;
        } else {
            BIN_LIB = BIN_LIB + "/" + X86;
        }
        LOGGER.debug("BIN_LIB=" + BIN_LIB);
        LOGGER.debug("NATIVE_TEMP_DIR=" + NATIVE_TEMP_DIR);
        InputStream in = null;
        BufferedInputStream reader;
        FileOutputStream writer = null;
        File extractedLibFile = new File(NATIVE_TEMP_DIR + File.separator + libFullName);

        if (!extractedLibFile.exists()) {
            LOGGER.debug("============ extractedLibFile not exists ===============");
            try {
                LOGGER.debug("libFullName=" + libFullName);
                in = LibraryUtil.class.getResourceAsStream(BIN_LIB + "/" + libFullName);
                if (in == null) {
                    in = LibraryUtil.class.getResourceAsStream(libFullName);
                }
                if (in == null) {
                    LOGGER.warn("Library resource not found in jar: " + libFullName
                            + " (looked in " + BIN_LIB + "). Skip extraction.");
                    return;
                }
                reader = new BufferedInputStream(in);
                writer = new FileOutputStream(extractedLibFile);
                byte[] buffer = new byte[1024];
                while (reader.read(buffer) > 0) {
                    writer.write(buffer);
                    buffer = new byte[1024];
                }
            } catch (IOException e) {
                LOGGER.error("", e);
                throw e;
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        LOGGER.error("", e);
                    }
                }
                if (writer != null) {
                    try {
                        writer.close();
                    } catch (IOException e) {
                        LOGGER.error("", e);
                    }
                }
            }
        }
    }

    /**
     * -----------------------------------------------------------------------
     * getAppPath需要一个当前程序使用的Java类的class属性参数，它可以返回打包过的
     * Java可执行文件（jar，war）所处的系统目录名或非打包Java程序所处的目录
     *
     * @param cls 为Class类型
     * @return 返回值为该类所在的Java程序运行的目录
     * -------------------------------------------------------------------------
     */
    public static String getAppPath(Class cls) {
        //检查用户传入的参数是否为空
        if (cls == null)
            throw new java.lang.IllegalArgumentException("参数不能为空！");
        ClassLoader loader = cls.getClassLoader();
        //获得类的全名，包括包名
        String clsName = cls.getName() + ".class";
        //获得传入参数所在的包
        Package pack = cls.getPackage();
        String path = "";
        //如果不是匿名包，将包名转化为路径
        if (pack != null) {
            String packName = pack.getName();
            //此处简单判定是否是Java基础类库，防止用户传入JDK内置的类库
            if (packName.startsWith("java.") || packName.startsWith("javax."))
                throw new java.lang.IllegalArgumentException("不要传送系统类！");
            //在类的名称中，去掉包名的部分，获得类的文件名
            clsName = clsName.substring(packName.length() + 1);
            //判定包名是否是简单包名，如果是，则直接将包名转换为路径，
            if (packName.indexOf(".") < 0) path = packName + "/";
            else {//否则按照包名的组成部分，将包名转换为路径
                int start = 0, end = 0;
                end = packName.indexOf(".");
                while (end != -1) {
                    path = path + packName.substring(start, end) + "/";
                    start = end + 1;
                    end = packName.indexOf(".", start);
                }
                path = path + packName.substring(start) + "/";
            }
        }
        //调用ClassLoader的getResource方法，传入包含路径信息的类文件名
        java.net.URL url = loader.getResource(path + clsName);
        //从URL对象中获取路径信息
        String realPath = url.getPath();
        //去掉路径信息中的协议名"file:"
        int pos = realPath.indexOf("file:");
        if (pos > -1) realPath = realPath.substring(pos + 5);
        //去掉路径信息最后包含类文件信息的部分，得到类所在的路径
        pos = realPath.indexOf(path + clsName);
        realPath = realPath.substring(0, pos - 1);
        //如果类文件被打包到JAR等文件中时，去掉对应的JAR等打包文件名
        if (realPath.endsWith("!"))
            realPath = realPath.substring(0, realPath.lastIndexOf("/"));
	   /*------------------------------------------------------------
	    ClassLoader的getResource方法使用了utf-8对路径信息进行了编码，当路径
	     中存在中文和空格时，他会对这些字符进行转换，这样，得到的往往不是我们想要
	     的真实路径，在此，调用了URLDecoder的decode方法进行解码，以便得到原始的
	     中文及空格路径
	   -------------------------------------------------------------*/
        try {
            realPath = java.net.URLDecoder.decode(realPath, "utf-8");
        } catch (Exception e) {
            LOGGER.error("", e);
            throw new RuntimeException(e);
        }
        return realPath;
    }

    /*public static void main(String[] args) throws IOException {
        copyLibToTemp("TrustFinger");
    }*/

}
