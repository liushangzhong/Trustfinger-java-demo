package com.aratek.jna.trustfinger.service;

import com.aratek.jna.trustfinger.library.AraTrustFingerLibrary;
import com.aratek.jna.trustfinger.pojo.DeviceDesc;
import com.aratek.jna.trustfinger.util.ConvertUtil;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.IntByReference;

import static com.aratek.jna.trustfinger.pojo.Results.*;

/**
 * Aratek TrustFinger SDK V3.3 服务层封装
 *
 * @author shijinlong
 * @date 2018-11-28
 */
public class AraTrustFingerService {

//    private static final Logger LOGGER = LoggerFactory.getLogger(AraTrustFingerService.class);


    /**
     * 初始化SDK运行时环境。通常在应用程序的进程中，这个函数只需要调用一次
     *
     * @return BaseResult
     * code:
     * 0 初始化成功
     * -115 SDK 已经初始化
     */
    public BaseResult globalInit() {
        return new BaseResult(AraTrustFingerLibrary.INSTANCE.ARAFPSCAN_GlobalInit());
    }

    /**
     * 释放SDK运行时环境
     *
     * @return BaseResult
     * code:
     * 0 释放成功
     * -102 环境未进行初始化
     */
    public BaseResult globalFree() {
        return new BaseResult(AraTrustFingerLibrary.INSTANCE.ARAFPSCAN_GlobalFree());
    }

    /**
     * 获取当前连接到PC的可使用的指纹采集设备总数
     *
     * @return DeviceCountResult
     * code:
     * 0 获取连接设备总数成功
     * -102 环境未进行初始化
     */
    public DeviceCountResult getDeviceCount() {
        IntByReference deviceCount = new IntByReference();
        int result = AraTrustFingerLibrary.INSTANCE.ARAFPSCAN_GetDeviceCount(deviceCount);
        return new DeviceCountResult(result, deviceCount.getValue());
    }

    /**
     * 打开指定指纹采集设备
     *
     * @param index 指纹仪设备索引号
     * @return OpenDeviceResult
     * code:
     * 0 打开指纹采集设备成功
     * -100 设备不存在。可能设备未接入或者驱动程序未安装导致
     * -102 环境未进行初始化
     * -117 设备已经打开
     * -118 获取设备信息失败
     * -200 初始化指纹算法失败
     * -900 参数错误
     */
    public OpenDeviceResult openDevice(int index) {
        long[] deviceHandle = new long[1];
        int result = AraTrustFingerLibrary.INSTANCE.ARAFPSCAN_OpenDevice(deviceHandle, index);
        return new OpenDeviceResult(result, deviceHandle);
    }

    /**
     * 关闭指纹采集设备
     *
     * @param deviceHandle 指向指纹采集设备的句柄
     * @return BaseResult
     * code:
     * 0 关闭指纹采集设备成功
     * -102 环境未进行初始化
     * -103 设备未打开
     */
    public BaseResult closeDevice(long[] deviceHandle) {
        int result = AraTrustFingerLibrary.INSTANCE.ARAFPSCAN_CloseDevice(deviceHandle);
        return new BaseResult(result);
    }

    /**
     * 获取指纹采集设备的图像像素的宽，高以及分辨率
     *
     * @param deviceHandle 指向指纹采集设备的句柄
     * @return ImageInfoResult
     * code:
     * 0 获取指纹图像信息成功
     * -102 环境未初始化
     * -103 设备未打开
     * -900 参数错误
     */
    public ImageInfoResult getImageInfo(long deviceHandle) {
        IntByReference pnWidth = new IntByReference();
        IntByReference pnHeight = new IntByReference();
        IntByReference pnDpi = new IntByReference();
        int result = AraTrustFingerLibrary.INSTANCE.ARAFPSCAN_GetImageInfo(deviceHandle, pnWidth, pnHeight, pnDpi);
        return new ImageInfoResult(result, pnWidth.getValue(), pnHeight.getValue(), pnDpi.getValue());
    }

    /**
     * 获取指纹采集设备信息
     *
     * @param deviceIndex 指向指纹采集设备的索引号
     * @return DeviceDescResult
     * code:
     * 0 获取指纹采集设备信息成功
     * 10 参数内存分配为空
     * -100 设备不存在
     * -118 获取设备信息失败
     */
    public DeviceDescResult getDeviceDescription(int deviceIndex) {
        AraTrustFingerLibrary.DeviceDescStruct.ByReference deviceDescStruct = new AraTrustFingerLibrary.DeviceDescStruct.ByReference();
        int result = AraTrustFingerLibrary.INSTANCE.ARAFPSCAN_GetDeviceDescription(deviceIndex, deviceDescStruct);
        DeviceDesc deviceDesc = ConvertUtil.convertDeviceDesc(deviceDescStruct);
        return new DeviceDescResult(result, deviceDesc);
    }

    /**
     * 采集一帧指纹RAW 图像
     *
     * @param deviceHandle 指向指纹采集设备的句柄
     * @param nTimeout     采集超时时间，单位为秒。建议设置 5
     * @param pnWidth      指纹图像像素宽
     * @param pnHeight     指纹图像像素高
     * @return CaptureDataResult
     * code:
     * 0 获取一帧 Raw 格式的指纹图像成功
     * -113 获取图像超时
     * -114 设备被其它进程或线程占用
     * 等，详见 SDK 文档
     */
    public CaptureDataResult captureRawData(long deviceHandle, int nTimeout, int pnWidth, int pnHeight) {
        byte[] pRawData = new byte[pnWidth * pnHeight];
        int result = AraTrustFingerLibrary.INSTANCE.ARAFPSCAN_CaptureRawData(deviceHandle, nTimeout, pRawData);
        return new CaptureDataResult(result, pRawData);
    }

    /**
     * 采集一帧指纹BMP 图像
     *
     * @param deviceHandle 指向指纹采集设备的句柄
     * @param nTimeout     采集超时时间，单位为秒。建议设置 5
     * @param pnWidth      指纹图像像素宽
     * @param pnHeight     指纹图像像素高
     * @return CaptureDataResult
     */
    public CaptureDataResult captureBitmapData(long deviceHandle, int nTimeout, int pnWidth, int pnHeight) {
        byte[] pBmpData = new byte[pnWidth * pnHeight + 1078];
        int result = AraTrustFingerLibrary.INSTANCE.ARAFPSCAN_CaptureBitmapData(deviceHandle, nTimeout, pBmpData);
        return new CaptureDataResult(result, pBmpData);
    }

    /**
     * 采集一帧符合 ISO 标准的指纹图像
     *
     * @param deviceHandle   指向指纹采集设备的句柄
     * @param nTimeout       采集超时时间，单位为秒。建议设置 5
     * @param fingerPos      指位信息
     * @param imgCompressAlg 图像压缩算法
     * @param pnWidth        指纹图像像素宽
     * @param pnHeight       指纹图像像素高
     * @return CaptureDataResult
     */
    public CaptureDataResult captureISOData(long deviceHandle, int nTimeout,
                                            int fingerPos, int imgCompressAlg,
                                            int pnWidth, int pnHeight) {
        byte[] pISOData = new byte[pnWidth * pnHeight + 46];
        IntByReference imageDataLength = new IntByReference();
        int result = AraTrustFingerLibrary.INSTANCE.ARAFPSCAN_CaptureISOData(deviceHandle, nTimeout, fingerPos, imgCompressAlg, pISOData, imageDataLength);
        return new CaptureDataResult(result, pISOData, imageDataLength.getValue());
    }

    /**
     * 采集一帧符合ANSI 标准的指纹图像
     *
     * @param deviceHandle   指向指纹采集设备的句柄
     * @param nTimeout       采集超时时间，单位为秒。建议设置 5
     * @param fingerPos      指位信息
     * @param imgCompressAlg 图像压缩算法
     * @param pnWidth        指纹图像像素宽
     * @param pnHeight       指纹图像像素高
     * @return CaptureDataResult
     */
    public CaptureDataResult captureANSIData(long deviceHandle, int nTimeout,
                                             int fingerPos, int imgCompressAlg,
                                             int pnWidth, int pnHeight) {
        byte[] pANSIData = new byte[pnWidth * pnHeight + 50];
        IntByReference imageDataLength = new IntByReference();
        int result = AraTrustFingerLibrary.INSTANCE.ARAFPSCAN_CaptureANSIData(deviceHandle, nTimeout, fingerPos, imgCompressAlg, pANSIData, imageDataLength);
        return new CaptureDataResult(result, pANSIData, imageDataLength.getValue());
    }

    /**
     * 实时采集操作的前缀函数
     *
     * @param deviceHandle 指向指纹采集设备的句柄
     * @return BaseResult
     */
    public BaseResult beginLiveCapture(long deviceHandle) {
        int result = AraTrustFingerLibrary.INSTANCE.ARAFPSCAN_BeginLiveCapture(deviceHandle);
        return new BaseResult(result);
    }

    /**
     * 实时采集一帧RAW 格式指纹图像
     *
     * @param deviceHandle 指向指纹采集设备的句柄
     * @param pnWidth      指纹图像像素宽
     * @param pnHeight     指纹图像像素高
     * @return CaptureDataResult
     */
    public CaptureDataResult liveCaptureRawData(long deviceHandle, int pnWidth, int pnHeight) {
        byte[] pRawData = new byte[pnWidth * pnHeight];
        int result = AraTrustFingerLibrary.INSTANCE.ARAFPSCAN_LiveCaptureRawData(deviceHandle, pRawData);
        return new CaptureDataResult(result, pRawData);
    }

    /**
     * 实时采集一帧BMP 格式指纹图像
     *
     * @param deviceHandle 指向指纹采集设备的句柄
     * @param pnWidth      指纹图像像素宽
     * @param pnHeight     指纹图像像素高
     * @return CaptureDataResult
     */
    public CaptureDataResult liveCaptureBitmap(long deviceHandle, int pnWidth, int pnHeight) {
        byte[] pBmpData = new byte[pnWidth * pnHeight + 1078];
        int result = AraTrustFingerLibrary.INSTANCE.ARAFPSCAN_LiveCaptureBitmap(deviceHandle, pBmpData);
        return new CaptureDataResult(result, pBmpData);
    }

    /**
     * 实时采集操作的结束函数
     *
     * @param deviceHandle 指向指纹采集设备的句柄
     * @return BaseResult
     */
    public BaseResult endLiveCapture(long deviceHandle) {
        int result = AraTrustFingerLibrary.INSTANCE.ARAFPSCAN_EndLiveCapture(deviceHandle);
        return new BaseResult(result);
    }

    /**
     * 获取指纹采集设备的 LED 灯的状态
     *
     * @param deviceHandle 指向指纹采集设备的句柄
     * @param nLedIndex    LED 的索引号。设备的索引号从 0 开始，在出厂时候已经分配号。不同设备索引号不同
     * @return LedResult
     */
    public LedResult getLedStatus(long deviceHandle, int nLedIndex) {
        IntByReference nLedStatus = new IntByReference();
        int result = AraTrustFingerLibrary.INSTANCE.ARAFPSCAN_GetLedStatus(deviceHandle, nLedIndex, nLedStatus);
        return new LedResult(result, nLedStatus.getValue());
    }

    /**
     * 设置指纹采集设备 LED 状态
     *
     * @param deviceHandle 指向指纹采集设备的句柄
     * @param index        LED 的索引号。设备的索引号从 0 开始，在出厂时候已经分配号。不同设备索引号不同
     * @param status       LED 灯状态 0 表示灯灭 1 表示灯亮
     * @return BaseResult
     */
    public BaseResult setLedStatus(long deviceHandle, int index, int status) {
        int result = AraTrustFingerLibrary.INSTANCE.ARAFPSCAN_SetLedStatus(deviceHandle, index, status);
        return new BaseResult(result);
    }

    /**
     * 提取指纹特征
     *
     * @param deviceHandle 指向指纹采集设备的句柄
     * @param fingerPos    手指指位信息
     * @return FeatureResult
     */
    public FeatureResult extractFeature(long deviceHandle, int fingerPos) {
        byte[] featureData = new byte[512];
        int result = AraTrustFingerLibrary.INSTANCE.ARAFPSCAN_ExtractFeature(deviceHandle, ConvertUtil.intToByte(fingerPos), featureData);
        return new FeatureResult(result, featureData);
    }

    /**
     * 依照ANSI 标准提取指纹特征
     *
     * @param deviceHandle 指向指纹采集设备的句柄
     * @param fingerPos    指位编码信息
     * @return FeatureResult
     */
    public FeatureResult extractANSIFeature(long deviceHandle, int fingerPos) {
        byte[] featureData = new byte[512];
        int result = AraTrustFingerLibrary.INSTANCE.ARAFPSCAN_ExtractANSIFeature(deviceHandle, ConvertUtil.intToByte(fingerPos), featureData);
        return new FeatureResult(result, featureData);
    }

    /**
     * 依照 ISO 指纹特征标准提取指纹特征
     *
     * @param deviceHandle 指向指纹采集设备的句柄
     * @param fingerPos    手指指位信息
     * @return FeatureResult
     */
    public FeatureResult extractISOFeature(long deviceHandle, int fingerPos) {
        byte[] featureData = new byte[512];
        int result = AraTrustFingerLibrary.INSTANCE.ARAFPSCAN_ExtractISOFeature(deviceHandle, ConvertUtil.intToByte(fingerPos), featureData);
        return new FeatureResult(result, featureData);
    }

    /**
     * 提取 FMCCS 特征（V3.3 新增）
     *
     * @param deviceHandle 指向指纹采集设备的句柄
     * @param fingerPos    指位信息
     * @return FeatureResult（featureData 前 featureLength 字节为有效特征）
     */
    public FeatureResult extractFMCCSFeature(long deviceHandle, int fingerPos) {
        byte[] featureData = new byte[512];
        IntByReference featureLength = new IntByReference();
        int result = AraTrustFingerLibrary.INSTANCE.ARAFPSCAN_ExtractFMCCSFeature(deviceHandle, ConvertUtil.intToByte(fingerPos), featureData, featureLength);
        return new FeatureResult(result, featureData);
    }

    /**
     * 提取 BioMove192 特征（V3.3 新增）
     *
     * @param deviceHandle 指向指纹采集设备的句柄
     * @param fingerPos    指位信息
     * @return FeatureResult（featureData 前 featureLength 字节为有效特征）
     */
    public FeatureResult extractBioMove192Feature(long deviceHandle, int fingerPos) {
        byte[] featureData = new byte[512];
        IntByReference featureLength = new IntByReference();
        int result = AraTrustFingerLibrary.INSTANCE.ARAFPSCAN_ExtractBioMove192Feature(deviceHandle, ConvertUtil.intToByte(fingerPos), featureData, featureLength);
        return new FeatureResult(result, featureData);
    }

    /**
     * 获取 ISO 特征的实际长度（V3.3 新增）
     *
     * @param featureData ISO 特征数据
     * @return 特征实际长度
     */
    public int getISOFeatureLength(byte[] featureData) {
        IntByReference length = new IntByReference();
        AraTrustFingerLibrary.INSTANCE.ARAFPSCAN_GetISOFeatureLength(featureData, length);
        return length.getValue();
    }

    /**
     * 获取 ANSI 特征的实际长度（V3.3 新增）
     */
    public int getANSIFeatureLength(byte[] featureData) {
        IntByReference length = new IntByReference();
        AraTrustFingerLibrary.INSTANCE.ARAFPSCAN_GetANSIFeatureLength(featureData, length);
        return length.getValue();
    }

    /**
     * 特征格式转换（V3.3 新增）
     *
     * @param pSrcFeatureData 源特征数据
     * @param pDstFeatureData 目标特征数据缓冲区（大小见 SDK 文档）
     * @param imgHeight       图像高度
     * @return ImageConvertResult
     */
    public ImageConvertResult transformFeature(byte[] pSrcFeatureData, byte[] pDstFeatureData, int imgHeight) {
        int result = AraTrustFingerLibrary.INSTANCE.ARAFPSCAN_TransformFeature(pSrcFeatureData, pDstFeatureData, imgHeight);
        return new ImageConvertResult(result, pDstFeatureData);
    }

    /**
     * 扩展特征提取（V3.3 新增）
     *
     * @param deviceHandle  指向指纹采集设备的句柄
     * @param fingerPos     指位信息
     * @param featureType   特征类型：BIONE=0, ANSI=1, ISO=2, MCCS=3, BIOMOVE192=4, ISO2011=5
     * @return FeatureResult（featureData 前 featureExLength 字节为有效特征）
     */
    public FeatureResult extractFeatureEx(long deviceHandle, int fingerPos, int featureType) {
        byte[] featureData = new byte[512];
        IntByReference featureExLength = new IntByReference();
        int result = AraTrustFingerLibrary.INSTANCE.ARAFPSCAN_ExtractFeatureEx(deviceHandle, ConvertUtil.intToByte(fingerPos), featureType, featureData, featureExLength);
        return new FeatureResult(result, featureData);
    }

    /**
     * 把来自同一个手指的 3 个指纹特征值合成一个指纹模板
     *
     * @param deviceHandle  指向指纹采集设备的句柄
     * @param pFeatureData1 指纹特征数据 1
     * @param pFeatureData2 指纹特征数据 2
     * @param pFeatureData3 指纹特征数据 3
     * @return FeatureTemplateResult
     */
    public FeatureTemplateResult generalizeTemplate(long deviceHandle, byte[] pFeatureData1, byte[] pFeatureData2, byte[] pFeatureData3) {
        byte[] pTemplateData = new byte[512];
        int result = AraTrustFingerLibrary.INSTANCE.ARAFPSCAN_GeneralizeTemplate(deviceHandle, pFeatureData1, pFeatureData2, pFeatureData3, pTemplateData);
        return new FeatureTemplateResult(result, pTemplateData);
    }

    /**
     * 将原始图像数据转换为 BMP 格式的图像数据。这里的 BMP 图像为 8 位灰度图
     *
     * @param pRawData 数据源，RAW 原始图像数据
     * @param pnWidth  图像宽度
     * @param pnHeight 图像高度
     * @return ImageConvertResult
     */
    public ImageConvertResult rawToBitmap(byte[] pRawData, int pnWidth, int pnHeight) {
        byte[] imageData = new byte[pnWidth * pnHeight + 1078];
        int result = AraTrustFingerLibrary.INSTANCE.ARAFPSCAN_RawToBitmap(pRawData, imageData, pnWidth, pnHeight);
        return new ImageConvertResult(result, imageData);
    }

    /**
     * 将BMP 格式图像数据转为 RAW 原始图像数据
     *
     * @param pBmpData 数据源，BMP 格式图像数据
     * @param pnWidth  图像宽度
     * @param pnHeight 图像高度
     * @return ImageConvertResult
     */
    public ImageConvertResult bitmapToRaw(byte[] pBmpData, int pnWidth, int pnHeight) {
        byte[] imageData = new byte[pnWidth * pnHeight];
        int result = AraTrustFingerLibrary.INSTANCE.ARAFPSCAN_BitmapToRaw(pBmpData, imageData, pnWidth, pnHeight);
        return new ImageConvertResult(result, imageData);
    }

    /**
     * 将原始图像数据压缩为 WSQ 格式的图像数据
     *
     * @param pRawData 数据源，RAW 原始图像数据
     * @param pnWidth  图像宽度
     * @param pnHeight 图像高度
     * @param dpi      图像分辨率（V3.3 新增参数）
     * @param bitrate  比特率
     * @return WSQResult
     */
    public WSQResult rawToWSQ(byte[] pRawData, int pnWidth, int pnHeight, int dpi, double bitrate) {
        byte[] imageData = new byte[pnWidth * pnHeight];
        IntByReference length = new IntByReference();
        int result = AraTrustFingerLibrary.INSTANCE.ARAFPSCAN_RawToWSQ(pRawData, pnWidth, pnHeight, dpi, bitrate, imageData, length);
        return new WSQResult(result, imageData, length.getValue());
    }

    /**
     * 将原始图像数据转为 ISO 标准图像（V3.3 新增）
     */
    public CaptureDataResult rawToISO(byte[] pRawData, int width, int height, int fingerPos, int imgCompressAlg,
                                     short deviceId, float factor) {
        byte[] pISOData = new byte[width * height + 46];
        IntByReference pLenOutput = new IntByReference();
        int result = AraTrustFingerLibrary.INSTANCE.ARAFPSCAN_RawToISO(pRawData, width, height, fingerPos, imgCompressAlg, pISOData, pLenOutput, deviceId, factor);
        return new CaptureDataResult(result, pISOData, pLenOutput.getValue());
    }

    /**
     * 将原始图像数据转为 ISO 2011 标准图像（V3.3 新增）
     */
    public CaptureDataResult rawToISO2011(byte[] pRawData, int width, int height, int fingerPos, int imgCompressAlg,
                                          short deviceId, float factor) {
        byte[] pISOData = new byte[width * height + 46];
        IntByReference pLenOutput = new IntByReference();
        int result = AraTrustFingerLibrary.INSTANCE.ARAFPSCAN_RawToISO2011(pRawData, width, height, fingerPos, imgCompressAlg, pISOData, pLenOutput, deviceId, factor);
        return new CaptureDataResult(result, pISOData, pLenOutput.getValue());
    }

    /**
     * 将原始图像数据转为 ANSI 标准图像（V3.3 新增）
     */
    public CaptureDataResult rawToANSI(byte[] pRawData, int width, int height, int fingerPos, int imgCompressAlg,
                                       short deviceId, float factor) {
        byte[] pANSIData = new byte[width * height + 50];
        IntByReference pLenOutput = new IntByReference();
        int result = AraTrustFingerLibrary.INSTANCE.ARAFPSCAN_RawToANSI(pRawData, width, height, fingerPos, imgCompressAlg, pANSIData, pLenOutput, deviceId, factor);
        return new CaptureDataResult(result, pANSIData, pLenOutput.getValue());
    }

    /**
     * 将原始图像数据转为 JPEG 或 JPEG2000 格式（V3.3 新增）。destType 见 SDK 文档
     */
    public CaptureDataResult rawToJPEGorJPEG2000(int destType, byte[] pRawData, int width, int height, float factor) {
        byte[] pOutputData = new byte[width * height + 1078];
        IntByReference pLenOutput = new IntByReference();
        int result = AraTrustFingerLibrary.INSTANCE.ARAFPSCAN_RawToJPEGorJPEG2000(destType, pRawData, width, height, factor, pOutputData, pLenOutput);
        return new CaptureDataResult(result, pOutputData, pLenOutput.getValue());
    }

    /**
     * 图像格式转换统一接口（V3.3 新增）。enConvertType 见 EnRawConvertImgType
     */
    public CaptureDataResult convertImage(long deviceHandle, byte[] pInputData, int nWidth, int nHeight,
                                          int enConvertType, double bitrate, float factor, int fingerPos, int imgCompressAlg) {
        AraTrustFingerLibrary.RawdataConvertParam.ByValue stConvertParam = new AraTrustFingerLibrary.RawdataConvertParam.ByValue();
        stConvertParam.bitrate = bitrate;
        stConvertParam.factor = factor;
        stConvertParam.fingerPos = fingerPos;
        stConvertParam.imgCompressAlg = imgCompressAlg;
        byte[] pOutputData = new byte[nWidth * nHeight + 1078];
        IntByReference pLenOutput = new IntByReference();
        int result = AraTrustFingerLibrary.INSTANCE.ARAFPSCAN_ConvertImage(deviceHandle, pInputData, nWidth, nHeight, enConvertType, stConvertParam, pOutputData, pLenOutput);
        return new CaptureDataResult(result, pOutputData, pLenOutput.getValue());
    }

    /**
     * 获取图像质量分数，分值为 0~100 得分越高表示质量越好，
     * 推荐质量分数判断设为 50，该图像质量能满足一般应用场景的需求，根据不同场景的需求可以自行决定
     *
     * @param pImageData 图像数据
     * @param pnWidth    图像像素宽
     * @param pnHeight   图像像素高
     * @return QualityResult
     */
    public QualityResult imgQuality(byte[] pImageData, int pnWidth, int pnHeight) {
        ByteByReference quality = new ByteByReference();
        int result = AraTrustFingerLibrary.INSTANCE.ARAFPSCAN_ImgQuality(pnWidth, pnHeight, pImageData, quality);
        return new QualityResult(result, quality.getValue() & 0xFF);
    }

    /**
     * 获取NFIQ 标准图像质量等级，范围为 1-5，分值越低质量等级越高
     *
     * @param pImageData 图像数据
     * @param pnWidth    图像像素宽
     * @param pnHeight   图像像素高
     * @return QualityResult
     */
    public QualityResult getNFIQScore(byte[] pImageData, int pnWidth, int pnHeight) {
        ByteByReference quality = new ByteByReference();
        int result = AraTrustFingerLibrary.INSTANCE.ARAFPSCAN_GetNFIQScore(pnWidth, pnHeight, pImageData, quality);
        return new QualityResult(result, quality.getValue() & 0xFF);
    }

    /**
     * 获取 NFIQ 质量分数（V3.3 新增统一接口）。
     * enImgQualityType: ARATEK_IQ = 0, NFIQ1 = 1, NFIQ2 = 2
     *
     * @param deviceHandle 指向指纹采集设备的句柄
     * @param pImageData   图像数据
     * @param pnWidth      图像像素宽
     * @param pnHeight     图像像素高
     * @param qualityType  质量算法类型
     * @return QualityResult
     */
    public QualityResult getNFIQ(long deviceHandle, byte[] pImageData, int pnWidth, int pnHeight, int qualityType) {
        ByteByReference quality = new ByteByReference();
        int result = AraTrustFingerLibrary.INSTANCE.ARAFPSCAN_GetNFIQ(deviceHandle, pImageData, pnWidth, pnHeight, qualityType, quality);
        return new QualityResult(result, quality.getValue() & 0xFF);
    }

    /**
     * 错误码的返回信息,长度不超过 256
     *
     * @param code 错误码
     * @return ErrorInfoResult
     */
    public ErrorInfoResult getErrorInfo(int code) {
        byte[] errorInfo = new byte[256];
        int result = AraTrustFingerLibrary.INSTANCE.ARAFPSCAN_GetErrorInfo(code, errorInfo);
        return new ErrorInfoResult(result, errorInfo);
    }

    /**
     * 两个指纹特征格式的比对
     *
     * @param deviceHandle   指向指纹采集设备的句柄
     * @param nSecurityLevel 安全等级，1-5 级，级别越高，越安全，建议 4 级
     * @param pFeatureData   需比对的指纹特征数据或模板数据
     * @param pTemplateData  需比对的指纹特征数据或模板数据
     * @return VerifyResult
     */
    public VerifyResult verify(long deviceHandle, int nSecurityLevel, byte[] pFeatureData, byte[] pTemplateData) {
        IntByReference pnSimilarity = new IntByReference();
        IntByReference pMmatch = new IntByReference();
        int result = AraTrustFingerLibrary.INSTANCE.ARAFPSCAN_Verify(deviceHandle, nSecurityLevel, pFeatureData, pTemplateData, pnSimilarity, pMmatch);
        return new VerifyResult(result, pnSimilarity.getValue(), pMmatch.getValue());
    }

    /**
     * 使能/关闭活体检测（V3.3 新增）。pEnableLFD 1 使能 0 关闭，lfdLevel 检测等级
     */
    public BaseResult enableLFD(long deviceHandle, boolean enableLFD, int lfdLevel) {
        int result = AraTrustFingerLibrary.INSTANCE.ARAFPSCAN_EnableLFD(deviceHandle, (byte) (enableLFD ? 1 : 0), lfdLevel);
        return new BaseResult(result);
    }

    /**
     * 设置 ISO/ANSI 图像压缩因子（V3.3 新增）
     */
    public BaseResult setISOANSICompressionFactor(long deviceHandle, float factor) {
        int result = AraTrustFingerLibrary.INSTANCE.ARAFPSCAN_SetISOANSICompressionFactor(deviceHandle, factor);
        return new BaseResult(result);
    }

    /**
     * 获取指纹算法版本信息（V3.3 新增），长度不超过 256
     *
     * @return ErrorInfoResult（errorInfo 字段存放版本字符串）
     */
    public ErrorInfoResult getFingerPrintAlgorithmVersion() {
        byte[] versionInfo = new byte[256];
        int result = AraTrustFingerLibrary.INSTANCE.ARAFPSCAN_GetFingerPrintAlgorithmVersion(versionInfo);
        return new ErrorInfoResult(result, versionInfo);
    }
}
