package com.aratek.jna.trustfinger.library;

import com.aratek.jna.trustfinger.util.LibraryUtil;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.PointerType;
import com.sun.jna.Structure;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;

import java.util.Arrays;
import java.util.List;

/**
 * Aratek TrustFinger SDK V3.3 接口映射（对应 SDK 头文件 TrustFinger.h）
 * <p>
 * 注意：新版 SDK 的 DLL 名称为 TrustFinger.dll（旧版为 AraTrustFinger.dll），
 * 且所有导出函数均为 __stdcall 调用约定，因此必须继承 StdCallLibrary。
 *
 * @author shijinlong
 * @date 2018-11-21
 */
public interface AraTrustFingerLibrary extends StdCallLibrary {

    String LIB_NAME = "TrustFinger";

    /**
     * 加载 SDK 动态库。优先使用环境变量 AraTrustFingerLibraryPath 指定的路径，
     * 未设置时回退到 LibraryUtil.loadLib() 从 jar 内置资源解压加载。
     */
    AraTrustFingerLibrary INSTANCE = (AraTrustFingerLibrary) Native.loadLibrary(resolveLibPath(), AraTrustFingerLibrary.class);

    static String resolveLibPath() {
        String envPath = System.getenv("AraTrustFingerLibraryPath");
        if (envPath != null && !envPath.trim().isEmpty()) {
            return envPath.trim();
        }
        return LibraryUtil.loadLib();
    }

    /**
     * 初始化SDK 运行时环境。通常在应用程序的进程中，这个函数只需要调用一次
     *
     * @return 0 初始化成功
     * -115 SDK 已经初始化
     */
    int ARAFPSCAN_GlobalInit();

    /**
     * 释放SDK 运行时环境
     *
     * @return 0 释放成功
     * -102 环境未进行初始化
     */
    int ARAFPSCAN_GlobalFree();

    /**
     * 获取当前连接到PC 的可使用的指纹采集设备总数
     *
     * @param nDeviceCount 输出参数。符合型号列表范围的设备数
     * @return 0 获取连接设备总数成功
     * -102 环境未进行初始化
     */
    int ARAFPSCAN_GetDeviceCount(IntByReference nDeviceCount);

    /**
     * 打开指定指纹采集设备. 先通过调用方法ARAFPSCAN_GetDeviceCount 接口函数查看当前 PC 有多少指纹采集设备接入。
     * 如果有多个指纹采集设备，那么从 0开始表示接入的指纹采集设备索引号。
     * 如果设备未接入或者驱动未安装，则返回-100 错误码
     *
     * @param nHandle 输出参数。指向设备的句柄
     * @param nIndex  指纹仪设备索引号
     * @return 0 打开指纹采集设备成功
     * -100 设备不存在。可能设备未接入或者驱动程序未安装导致
     * -102 环境未进行初始化
     * -117 设备已经打开
     * -118 获取设备信息失败
     * -200 初始化指纹算法失败
     * -900 参数错误
     */
    int ARAFPSCAN_OpenDevice(long[] nHandle, int nIndex);

    /**
     * 关闭指纹采集设备
     *
     * @param nHandle 输入参数。指向指纹采集设备的句柄
     * @return 0 关闭指纹采集设备成功
     * -102 环境未进行初始化
     * -103 设备未打开
     */
    int ARAFPSCAN_CloseDevice(long[] nHandle);

    /**
     * 获取指纹采集设备的图像像素的宽，高以及分辨率
     *
     * @param nHandle  输入参数。指向指纹采集设备的句柄
     * @param pnWidth  输出参数。指纹图像像素宽
     * @param pnHeight 输出参数。指纹图像像素高
     * @param pnDpi    输出参数。指纹图像的分辨率
     * @return 0 获取指纹图像信息成功
     * -102 环境未初始化
     * -103 设备未打开
     * -900 参数错误
     */
    int ARAFPSCAN_GetImageInfo(long nHandle, IntByReference pnWidth, IntByReference pnHeight, IntByReference pnDpi);

    /**
     * 获取指纹采集设备信息
     *
     * @param deviceIndex 输入参数。指向指纹采集设备的索引号
     * @param pDeviceDesc 输出参数。描述指纹采集设备信息的结构体
     * @return 0 获取指纹采集设备信息成功
     * 10 参数内存分配为空
     * -100 设备不存在
     * -118 获取设备信息失败
     */
    int ARAFPSCAN_GetDeviceDescription(int deviceIndex, DeviceDescStruct.ByReference pDeviceDesc);

    /**
     * 采集一帧指纹RAW 图像。图像所需内存空间为：
     * 当前图像像素宽度(imageWidth)×当前图像像素高度(imageHeight)
     * 图像的内存空间由调用者管理内存分配和释放，采集图像像素高和宽可以通过调
     * 用ARAFPSCAN_GetImageInfo 接口函数获得。当指纹采集设备触发采集图像时
     * 候，采集算法会进行指纹探测，判断是否采集到有效的指纹。设置超时时间
     * nTimeout >0 表示当一定时间段内找不到有效的指纹图像则采集超时。若超时时
     * 间设置为0，则无论当前采集器表面是否有指纹按下，该函数均立即返回，即不
     * 会进行指纹探测。建议超时时间设为 5 秒
     *
     * @param nHandle  输入参数。指向指纹采集设备的句柄
     * @param nTimeout 输入参数。采集超时时间，单位为秒。建议设置 5
     * @param pRawData 返回采集到的 RAW 格式指纹图像
     * @return 0 获取一帧 Raw 格式的指纹图像成功
     */
    int ARAFPSCAN_CaptureRawData(long nHandle, int nTimeout, byte[] pRawData);

    /**
     * 采集一帧指纹BMP 图像
     *
     * @param nHandle  输入参数。指向指纹采集设备的句柄
     * @param nTimeout 输入参数。采集超时时间，单位为秒。建议设置 5
     * @param pBmpData 指向存放 8 位灰度 BMP 格式采集数据的内存块，调用者分配。返回 8 位灰度 BMP 格式图像数据。大小应为：当前图像采集宽度（imageWidth）×当前图像采集高度（imageHeight）+1078
     * @return 0 获取一帧 bmp 格式的指纹图像成功
     */
    int ARAFPSCAN_CaptureBitmapData(long nHandle, int nTimeout, byte[] pBmpData);

    /**
     * 采集一帧符合 ISO 标准的指纹图像
     *
     * @param nHandle        输入参数。指向指纹采集设备的句柄
     * @param nTimeout       输入参数。采集超时时间，单位为秒。建议设置 5
     * @param FingerPos      输入参数。指位信息
     * @param Imgcompressalg 图像压缩算法
     * @param ISOImgData     输出图像
     * @param pLenOutput     图像大小
     * @return 0 获取一帧符合 ISO 标准的指纹图像成功
     */
    int ARAFPSCAN_CaptureISOData(long nHandle, int nTimeout, int FingerPos, int Imgcompressalg,
                                 byte[] ISOImgData, IntByReference pLenOutput);

    /**
     * 采集一帧符合ANSI 标准的指纹图像
     *
     * @param nHandle        输入参数。指向指纹采集设备的句柄
     * @param nTimeout       输入参数。采集超时时间，单位为秒。建议设置 5
     * @param FingerPos      输入参数。指位信息
     * @param Imgcompressalg 图像压缩算法
     * @param ISOImgData     输出图像
     * @param pLenOutput     图像大小
     * @return 0 获取一帧符合 ISO 标准的指纹图像成功
     */
    int ARAFPSCAN_CaptureANSIData(long nHandle, int nTimeout, int FingerPos, int Imgcompressalg,
                                  byte[] ISOImgData, IntByReference pLenOutput);

    /**
     * 实时采集操作的前缀函数
     *
     * @param nHandle 输入参数。指向指纹采集设备的句柄
     * @return 0 前缀函数调用成功
     */
    int ARAFPSCAN_BeginLiveCapture(long nHandle);

    /**
     * 实时采集一帧RAW 格式指纹图像
     *
     * @param nHandle  输入参数。指向指纹采集设备的句柄
     * @param pRawData 返回采集到的 RAW 格式指纹图像
     * @return 0 获取一帧 RAW 格式的指纹图像成功
     */
    int ARAFPSCAN_LiveCaptureRawData(long nHandle, byte[] pRawData);

    /**
     * 实时采集一帧BMP 格式指纹图像
     *
     * @param nHandle  输入参数。指向指纹采集设备的句柄
     * @param pBmpData 指向存放 8 位灰度 BMP 格式采集数据的内存块，调用者分配。返回 8 位灰度 BMP 格式图像数据。大小应为：当前图像采集宽度（imageWidth）×当前图像采集高度（imageHeight）+1078
     * @return 0 获取一帧 bmp 格式的指纹图像成功
     */
    int ARAFPSCAN_LiveCaptureBitmap(long nHandle, byte[] pBmpData);

    /**
     * 实时采集操作的结束函数
     *
     * @param nHandle 输入参数。指向指纹采集设备的句柄
     * @return 0 调用成功
     */
    int ARAFPSCAN_EndLiveCapture(long nHandle);

    /**
     * 获取指纹采集设备的 LED 灯的状态
     *
     * @param nHandle   输入参数。指向指纹采集设备的句柄
     * @param nLedIndex 输入参数。LED 的索引号。设备的索引号从 0 开始，在出厂时候已经分配号。不同设备索引号不同
     * @param pStatus   输出参数。LED 灯状态 0 表示灯灭 1 表示灯亮
     * @return 0 获取 LED 状态成功
     */
    int ARAFPSCAN_GetLedStatus(long nHandle, int nLedIndex, IntByReference pStatus);

    /**
     * 设置指纹采集设备 LED 状态
     *
     * @param nHandle    输入参数。指向指纹采集设备的句柄
     * @param nLedIndex  输入参数。LED 的索引号。设备的索引号从 0 开始，在出厂时候已经分配号。不同设备索引号不同
     * @param nLedStatus 输入参数。LED 灯状态 0 表示灯灭 1 表示灯亮
     * @return 0 设置 LED 状态成功
     */
    int ARAFPSCAN_SetLedStatus(long nHandle, int nLedIndex, int nLedStatus);

    /**
     * 提取指纹特征。调用该函数之前，先调用 ARAFPSCAN_CaptureRawData 接口函数
     * 或者ARAFPSCAN_CaptureBitmapData 接口函数取到一帧指纹图像。指纹特征
     * 提取受限于实际采集的指纹图像，如果采集的图像提取不到特征，返回错误码 -221
     *
     * @param nHandle      输入参数。指向指纹采集设备的句柄
     * @param cFingerPos   输入参数。手指指位信息
     * @param pFeatureData 输出参数。返回 ARATEK AUF®格式的指纹特征
     * @return 0 提取指纹特征成功
     */
    int ARAFPSCAN_ExtractFeature(long nHandle, byte cFingerPos, byte[] pFeatureData);

    /**
     * 依照ANSI 标准提取指纹特征
     */
    int ARAFPSCAN_ExtractANSIFeature(long nHandle, byte cFingerPos, byte[] pFeatureData);

    /**
     * 依照 ISO 指纹特征标准提取指纹特征
     */
    int ARAFPSCAN_ExtractISOFeature(long nHandle, byte cFingerPos, byte[] pFeatureData);

    /**
     * 提取 FMCCS 特征（V3.3 新增）。featureLength 输出实际特征长度
     */
    int ARAFPSCAN_ExtractFMCCSFeature(long nHandle, byte cFingerPos, byte[] pFeatureData, IntByReference featureLength);

    /**
     * 提取 BioMove192 特征（V3.3 新增）。featureLength 输出实际特征长度
     */
    int ARAFPSCAN_ExtractBioMove192Feature(long nHandle, byte cFingerPos, byte[] pFeatureData, IntByReference featureLength);

    /**
     * 获取 ISO 特征的实际长度（V3.3 新增）
     */
    int ARAFPSCAN_GetISOFeatureLength(byte[] pFeatureData, IntByReference length);

    /**
     * 获取 ANSI 特征的实际长度（V3.3 新增）
     */
    int ARAFPSCAN_GetANSIFeatureLength(byte[] pFeatureData, IntByReference length);

    /**
     * 特征格式转换（V3.3 新增）
     *
     * @param pSrcFeatureData 源特征数据
     * @param pDstFeatureData 目标特征数据（缓冲区大小见 SDK 文档）
     * @param imgHeight       图像高度
     */
    int ARAFPSCAN_TransformFeature(byte[] pSrcFeatureData, byte[] pDstFeatureData, int imgHeight);

    /**
     * 扩展特征提取（V3.3 新增）。EnFeatureType:
     * BIONE_FEATURE_TYPE = 0, ANSI_FEATURE_TYPE = 1, ISO_FEATURE_TYPE = 2,
     * MCCS_FEATURE_TYPE = 3, BIOMOVE192_FEATURE_TYPE = 4, ISO2011_FEATURE_TYPE = 5
     */
    int ARAFPSCAN_ExtractFeatureEx(long nHandle, byte cFingerPos, int enFeatureType,
                                   byte[] pFeatureExData, IntByReference featureExLength);

    /**
     * 把来自同一个手指的 3 个指纹特征值合成一个指纹模板，
     * 如果指纹合成失败,返回错误码-222
     *
     * @param nHandle       输入参数。指向指纹采集设备的句柄
     * @param pFeatureData1 指纹特征数据 1
     * @param pFeatureData2 指纹特征数据 2
     * @param pFeatureData3 指纹特征数据 3
     * @param pTemplateData 输出参数。返回指纹特征模板
     * @return 0 生成指纹特征模板成功
     */
    int ARAFPSCAN_GeneralizeTemplate(long nHandle, byte[] pFeatureData1, byte[] pFeatureData2,
                                     byte[] pFeatureData3, byte[] pTemplateData);

    /**
     * 将原始图像数据转换为 BMP 格式的图像数据。这里的 BMP 图像为 8 位灰度图
     *
     * @param pRawData 数据源，RAW 原始图像数据
     * @param pBmpData 目标数据，BMP 格式图像数据，其大小为:图像宽度*高度+1078，空间由调用者分配
     * @param pnWidth  图像宽度
     * @param pnHeight 图像高度
     * @return 0 转换成功
     */
    int ARAFPSCAN_RawToBitmap(byte[] pRawData,
                              byte[] pBmpData,
                              int pnWidth,
                              int pnHeight);

    /**
     * 将BMP 格式图像数据转为 RAW 原始图像数据
     *
     * @param pBmpData 数据源，BMP 格式图像数据
     * @param pRawData 目标数据，RAW 格式原始图像数据
     * @param pnWidth  图像宽度
     * @param pnHeight 图像高度
     * @return 0 转换成功
     */
    int ARAFPSCAN_BitmapToRaw(byte[] pBmpData,
                              byte[] pRawData,
                              int pnWidth,
                              int pnHeight);

    /**
     * 将原始图像数据压缩为 WSQ 格式的图像数据
     *
     * @param pRawData  数据源，RAW 原始图像数据
     * @param pnWidth   图像宽度
     * @param pnHeight  图像高度
     * @param dpi       图像分辨率（V3.3 新增参数）
     * @param bitrate   比特率
     * @param pWSQData  目标数据，WSQ 格式图像数据
     * @param lengthWSQ 压缩后图像数据长度
     * @return 0 压缩成功
     */
    int ARAFPSCAN_RawToWSQ(byte[] pRawData,
                           int pnWidth,
                           int pnHeight,
                           int dpi,
                           double bitrate,
                           byte[] pWSQData,
                           IntByReference lengthWSQ);

    /**
     * 将原始图像数据转为 ISO 标准图像（V3.3 新增）
     */
    int ARAFPSCAN_RawToISO(byte[] pRawData, int width, int height, int fingerPos, int imgCompressAlg,
                           byte[] pISOData, IntByReference pLenOutput, short deviceId, float factor);

    /**
     * 将原始图像数据转为 ISO 2011 标准图像（V3.3 新增）
     */
    int ARAFPSCAN_RawToISO2011(byte[] pRawData, int width, int height, int fingerPos, int imgCompressAlg,
                               byte[] pISOData, IntByReference pLenOutput, short deviceId, float factor);

    /**
     * 将原始图像数据转为 ANSI 标准图像（V3.3 新增）
     */
    int ARAFPSCAN_RawToANSI(byte[] pRawData, int width, int height, int fingerPos, int imgCompressAlg,
                            byte[] pANSIData, IntByReference pLenOutput, short deviceId, float factor);

    /**
     * 将原始图像数据转为 JPEG 或 JPEG2000 格式（V3.3 新增）。destType 见 SDK 文档
     */
    int ARAFPSCAN_RawToJPEGorJPEG2000(int destType, byte[] pRawData, int width, int height,
                                      float factor, byte[] pOutputData, IntByReference pLenOutput);

    /**
     * 图像格式转换统一接口（V3.3 新增）。enConvertType 见 EnRawConvertImgType:
     * RAW_TO_BMP_TYPE = 0, RAW_TO_WSQ_TYPE = 1, RAW_TO_ISO_TYPE = 2,
     * RAW_TO_ANSI_TYPE = 3, RAW_TO_JPEG_TYPE = 4, RAW_TO_JPEG2000_TYPE = 5,
     * BMP_TO_RAW_TYPE = 6, RAW_TO_ISO2011_TYPE = 7
     *
     * @param stConvertParam Rawdata_convert_param 结构体（传值）
     */
    int ARAFPSCAN_ConvertImage(long nHandle, byte[] pInputData, int nWidth, int nHeight,
                               int enConvertType, RawdataConvertParam.ByValue stConvertParam,
                               byte[] pOutputData, IntByReference pLenOutput);

    /**
     * 获取图像质量分数，分值为 0~100 得分越高表示质量越好，
     * 推荐质量分数判断设为 50，该图像质量能满足一般应用场景的需求，根据不同场景的需求可以自行决定
     *
     * @param pnWidth    图像像素宽
     * @param pnHeight   图像像素高
     * @param pImageData 输出。图像数据源
     * @param Quality    输出。图像质量分数,0-100（V3.3 为 BYTE 指针输出）
     * @return 0 获得指纹图像质量分数成功
     * -211 图像质量太差
     */
    int ARAFPSCAN_ImgQuality(int pnWidth, int pnHeight,
                             byte[] pImageData, ByteByReference Quality);

    /**
     * 获取NFIQ 标准图像质量等级，范围为 1-5，分值越低质量等级越高
     *
     * @param width      图像像素宽
     * @param height     图像像素高
     * @param pImageData 输出。图像数据源
     * @param Quality    输出。图像质量,1-5（V3.3 为 BYTE 指针输出）
     * @return 0 获得指纹图像质量分数成功
     * -203 缺少 NFIQ 算法库
     * -211 图像质量太差
     */
    int ARAFPSCAN_GetNFIQScore(int width, int height,
                               byte[] pImageData, ByteByReference Quality);

    /**
     * 获取 NFIQ 质量分数（V3.3 新增统一接口）。
     * enImgQualityType: ARATEK_IQ = 0, NFIQ1 = 1, NFIQ2 = 2
     */
    int ARAFPSCAN_GetNFIQ(long nHandle, byte[] pImageData, int nWidth, int nHeight,
                          int enImgQualityType, ByteByReference quality);

    /**
     * 错误码的返回信息,长度不超过 256
     *
     * @param nErrorNo     输入，错误码
     * @param pszErrorInfo 输出，错误信息
     * @return 0 获得错误码信息成功
     */
    int ARAFPSCAN_GetErrorInfo(int nErrorNo, byte[] pszErrorInfo);

    /**
     * 两个指纹特征格式的比对。参数 nHandle 为已打开的设备句柄，在
     * ARAFPSCAN_OpenDevice 接口函数获得。参数 nSecurityLevel 为比对安全等级,
     * 等级数从1 到 5，等级数越高越安全。推荐使用安全等级 4 级
     *
     * @param nHandle        输入参数。指向指纹采集设备的句柄
     * @param nSecurityLevel 安全等级，1-5 级，级别越高，越安全，建议 4 级
     * @param pFeatureData   需比对的指纹特征数据
     * @param pTemplateData  需比对的指纹模板数据
     * @param pnSimilarity   输出参数。返回指纹相似度
     * @param pMmatch        输出参数。返回指纹匹配结果. 1 表示匹配 0 表示不匹配
     * @return 0 函数调用成功
     */
    int ARAFPSCAN_Verify(long nHandle,
                         int nSecurityLevel,
                         byte[] pFeatureData,
                         byte[] pTemplateData,
                         IntByReference pnSimilarity,
                         IntByReference pMmatch);

    /**
     * 使能/关闭活体检测（V3.3 新增）。pEnableLFD 1 使能 0 关闭，LFD_Level 检测等级
     */
    int ARAFPSCAN_EnableLFD(long nHandle, byte pEnableLFD, int lfdLevel);

    /**
     * 设置 ISO/ANSI 图像压缩因子（V3.3 新增）
     */
    int ARAFPSCAN_SetISOANSICompressionFactor(long nHandle, float factor);

    /**
     * 获取指纹算法版本信息（V3.3 新增），长度不超过 256
     */
    int ARAFPSCAN_GetFingerPrintAlgorithmVersion(byte[] versionInfo);

    /**
     * 自定义
     */
    public static class HANDLE extends PointerType {
        public HANDLE(Pointer address) {
            super(address);
        }

        public HANDLE() {
            super();
        }
    }

    /**
     * 结构体
     */
    public static class DeviceDescStruct extends Structure {

        //结构体传指针
        public static class ByReference extends com.aratek.jna.trustfinger.library.AraTrustFingerLibrary.DeviceDescStruct implements Structure.ByReference {
        }

        //结构体传值
        public static class ByValue extends com.aratek.jna.trustfinger.library.AraTrustFingerLibrary.DeviceDescStruct implements Structure.ByValue {
        }

        public DeviceDescStruct() {
            super();
        }

        public DeviceDescStruct(Pointer peer) {
            super(peer);
        }


        /**
         * Device serial number
         * char[32]
         */
        public byte[] serialNumber = new byte[32];

        /**
         * manufacturer
         * char[32]
         */
        public byte[] manufacturer = new byte[32];

        /**
         * Device product name
         * char[64]
         */
        public byte[] productName = new byte[64];

        /**
         * Device product model
         * char[32]
         */
        public byte[] productModel = new byte[32];

        /**
         * Device firmware version
         * char[32]
         */
        public byte[] fwVersion = new byte[32];

        /**
         * Device revision
         * char[32]
         */
        public byte[] hwVersion = new byte[32];

        /**
         * Device capture max image width
         */
        public int imageWidth;

        /**
         * Device capture max image height
         */
        public int imageHeight;

        /**
         * Device capture max image DPI
         */
        public int Dpi;

        /**
         * Device id
         */
        public short deviceId;

        /**
         * device support USE connected type or not
         */
        public byte isUSBSupported;

        /**
         * device suppport UART or not
         */
        public byte isUARTSupported;

        /**
         * device support SPI or not
         */
        public byte isSPISupported;

        /**
         * device support led or not
         */
        public byte isLedSupported;

        /**
         * device support TrustMe or not (V3.3 新增)
         */
        public byte isTrustMeSupported;

        /**
         * device support TrustAFIS or not (V3.3 新增)
         */
        public byte isTrustAFISSupported;

        /**
         * device support TrustLink or not (V3.3 新增)
         */
        public byte isTrustLinkSupported;

        /**
         * Reserved (V3.3 新增)
         */
        public byte reserved0;

        /**
         * Reserved (V3.3 新增)
         */
        public byte reserved1;

        /**
         * Reserved (V3.3 新增)
         */
        public byte reserved2;

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("serialNumber", "manufacturer", "productName", "productModel", "fwVersion", "hwVersion", "imageWidth", "imageHeight", "Dpi", "deviceId", "isUSBSupported", "isUARTSupported", "isSPISupported", "isLedSupported", "isTrustMeSupported", "isTrustAFISSupported", "isTrustLinkSupported", "reserved0", "reserved1", "reserved2");
        }

    }

    /**
     * 图像转换参数结构体 Rawdata_convert_param（对应 SDK 头文件，传值使用）
     */
    public static class RawdataConvertParam extends Structure {

        public static class ByReference extends RawdataConvertParam implements Structure.ByReference {
        }

        public static class ByValue extends RawdataConvertParam implements Structure.ByValue {
        }

        public RawdataConvertParam() {
            super();
        }

        public RawdataConvertParam(Pointer peer) {
            super(peer);
        }

        /**
         * 比特率（WSQ 压缩使用）
         */
        public double bitrate;

        /**
         * 压缩因子（ISO/ANSI/JPEG 使用）
         */
        public float factor;

        /**
         * 指位信息
         */
        public int fingerPos;

        /**
         * 图像压缩算法
         */
        public int imgCompressAlg;

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("bitrate", "factor", "fingerPos", "imgCompressAlg");
        }
    }
}
