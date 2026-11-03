package com.aratek.jna.trustfinger.pojo;

import com.aratek.jna.trustfinger.util.StringUtil;

import java.io.Serializable;

/**
 * @author shijinlong
 * @date 2018-11-28
 */
public class Results {
    
    public static byte[] copyBytes(byte[] bytes){
        byte[] result = new byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            result[i] = bytes[i];
        }
        return result;
    }

    /**
     * 基础返回对象
     */
    public static class BaseResult implements Serializable {

        private static final long serialVersionUID = 5868745040628079002L;

        private static final int UNKNOWN_ERROR = 99999;

        private int code;

        private String msg;

        public BaseResult(int code) {
            this.code = code;
            this.msg = ResultCodeMap.getInstance().get(code);
            if (StringUtil.isBlank(this.msg)) {
                this.code = UNKNOWN_ERROR;
                this.msg = "未知错误";
            }
        }

        public BaseResult(int code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        public int getCode() {
            return code;
        }

        public void setCode(int code) {
            this.code = code;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }
    }


    /**
     * 获取连接设备总数
     */
    public static class DeviceCountResult extends BaseResult {


        private static final long serialVersionUID = -3108234074569777113L;
        private int deviceCount;

        public DeviceCountResult(int code) {
            super(code);
        }

        public DeviceCountResult(int code, int deviceCount) {
            super(code);
            this.deviceCount = deviceCount;
        }

        public int getDeviceCount() {
            return deviceCount;
        }

        public void setDeviceCount(int deviceCount) {
            this.deviceCount = deviceCount;
        }
    }

    /**
     * 指纹采集设备 句柄
     */
    public static class OpenDeviceResult extends BaseResult {


        private long[] deviceHandle;

        public OpenDeviceResult(int code) {
            super(code);
        }

        public OpenDeviceResult(int code, long[] deviceHandle) {
            super(code);
            this.deviceHandle = deviceHandle;
        }

        public long[] getDeviceHandle() {
            return deviceHandle;
        }

        public void setDeviceHandle(long[] deviceHandle) {
            this.deviceHandle = deviceHandle;
        }
    }

    /**
     * 指纹采集设备的图像像素的宽,高,分辨率
     */
    public static class ImageInfoResult extends BaseResult {


        private int pnWidth; //指纹图像像素宽
        private int pnHeight; //指纹图像像素高
        private int pnDpi; //指纹图像的分辨率

        public ImageInfoResult(int code) {
            super(code);
        }

        public ImageInfoResult(int code, int pnWidth, int pnHeight, int pnDpi) {
            super(code);
            this.pnWidth = pnWidth;
            this.pnHeight = pnHeight;
            this.pnDpi = pnDpi;
        }

        public int getPnWidth() {
            return pnWidth;
        }

        public void setPnWidth(int pnWidth) {
            this.pnWidth = pnWidth;
        }

        public int getPnHeight() {
            return pnHeight;
        }

        public void setPnHeight(int pnHeight) {
            this.pnHeight = pnHeight;
        }

        public int getPnDpi() {
            return pnDpi;
        }

        public void setPnDpi(int pnDpi) {
            this.pnDpi = pnDpi;
        }
    }

    /**
     * 设备信息
     */
    public static class DeviceDescResult extends BaseResult {

        /**
         * 设备信息
         */
        private DeviceDesc deviceDesc;

        public DeviceDescResult(int code) {
            super(code);
        }

        public DeviceDescResult(int code, DeviceDesc deviceDesc) {
            super(code);
            this.deviceDesc = deviceDesc;
        }

        public DeviceDesc getDeviceDesc() {
            return deviceDesc;
        }

        public void setDeviceDesc(DeviceDesc deviceDesc) {
            this.deviceDesc = deviceDesc;
        }
    }

    /**
     * 采集指纹图片返回对象
     */
    public static class CaptureDataResult extends BaseResult {

        /**
         * 指纹图片数据
         */
        private byte[] imageData;

        /**
         * 图片实际大小
         */
        private int imageDataLength;

        public CaptureDataResult(int code) {
            super(code);
        }

        public CaptureDataResult(int code, byte[] imageData) {
            super(code);
            this.imageData = copyBytes(imageData);
            this.imageDataLength = imageData.length;
        }

        public CaptureDataResult(int code, byte[] imageData, int imageDataLength) {
            super(code);
            this.imageData = copyBytes(imageData);
            this.imageDataLength = imageDataLength;
        }

        public byte[] getImageData() {
            return imageData;
        }

        public void setImageData(byte[] imageData) {
            this.imageData = imageData;
        }

        public int getImageDataLength() {
            return imageDataLength;
        }

        public void setImageDataLength(int imageDataLength) {
            this.imageDataLength = imageDataLength;
        }
    }

    /**
     * LED信息
     */
    public static class LedResult extends BaseResult {

        /**
         * LED灯状态：0,表示灯灭; 1,表示灯亮;
         */
        private int status;


        public LedResult(int code) {
            super(code);
        }

        public LedResult(int code, int status) {
            super(code);
            this.status = status;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }
    }

    /**
     * 指纹特征信息
     */
    public static class FeatureResult extends BaseResult {

        /**
         * 指纹特征
         */
        private byte[] featureData;


        public FeatureResult(int code) {
            super(code);
        }

        public FeatureResult(int code, byte[] featureData) {
            super(code);
            this.featureData = copyBytes(featureData);
        }

        public byte[] getFeatureData() {
            return featureData;
        }

        public void setFeatureData(byte[] featureData) {
            this.featureData = featureData;
        }
    }

    /**
     * 指纹模板信息
     */
    public static class FeatureTemplateResult extends BaseResult {

        /**
         * 指纹模板
         */
        private byte[] featureTemplateData;


        public FeatureTemplateResult(int code) {
            super(code);
        }

        public FeatureTemplateResult(int code, byte[] featureTemplateData) {
            super(code);
            this.featureTemplateData = copyBytes(featureTemplateData);
        }

        public byte[] getFeatureTemplateData() {
            return featureTemplateData;
        }

        public void setFeatureTemplateData(byte[] featureTemplateData) {
            this.featureTemplateData = featureTemplateData;
        }
    }

    /**
     * 比较结果信息
     */
    public static class VerifyResult extends BaseResult {

        /**
         * 相似度，0-1000
         */
        private int pnSimilarity;

        /**
         *指纹匹配结果. 1表示匹配 0表示不匹配
         */
        private int pMmatch;


        public VerifyResult(int code) {
            super(code);
        }

        public VerifyResult(int code, int pnSimilarity,int pMmatch) {
            super(code);
            this.pnSimilarity = pnSimilarity;
            this.pMmatch = pMmatch;
        }

        public int getPnSimilarity() {
            return pnSimilarity;
        }

        public void setPnSimilarity(int pnSimilarity) {
            this.pnSimilarity = pnSimilarity;
        }

        public int getpMmatch() {
            return pMmatch;
        }

        public void setpMmatch(int pMmatch) {
            this.pMmatch = pMmatch;
        }
    }

    /**
     * 指纹图片格式转换
     */
    public static class ImageConvertResult extends BaseResult {

        /**
         * 指纹图片数据
         */
        private byte[] imageData;


        public ImageConvertResult(int code) {
            super(code);
        }

        public ImageConvertResult(int code, byte[] imageData) {
            super(code);
            this.imageData = copyBytes(imageData);
        }

        public byte[] getImageData() {
            return imageData;
        }

        public void setImageData(byte[] imageData) {
            this.imageData = imageData;
        }
    }

    /**
     * 指纹图片WSQ
     */
    public static class WSQResult extends BaseResult {

        /**
         * 指纹图片数据
         */
        private byte[] imageData;

        /**
         * 图像数据长度
         */
        private int length;


        public WSQResult(int code) {
            super(code);
        }

        public WSQResult(int code, byte[] imageData,int length) {
            super(code);
            this.imageData = copyBytes(imageData);
            this.length = length;
        }

        public byte[] getImageData() {
            return imageData;
        }

        public void setImageData(byte[] imageData) {
            this.imageData = imageData;
        }

        public int getLength() {
            return length;
        }

        public void setLength(int length) {
            this.length = length;
        }
    }

    /**
     * 图片质量
     */
    public static class QualityResult extends BaseResult {

        /**
         * 质量
         */
        private int quality;


        public QualityResult(int code) {
            super(code);
        }

        public QualityResult(int code, int quality) {
            super(code);
            this.quality = quality;
        }

        public int getQuality() {
            return quality;
        }

        public void setQuality(int quality) {
            this.quality = quality;
        }
    }

    /**
     * 错误信息
     */
    public static class ErrorInfoResult extends BaseResult {

        /**
         * 错误信息
         */
        private String errorInfo;

        public ErrorInfoResult(int code) {
            super(code);
        }

        public ErrorInfoResult(int code, byte[] errorInfo) {
            super(code);
            if (errorInfo != null) {
                // C DLL 返回的是 GBK 编码的字节，必须用 GBK 解码，否则中文乱码
                try {
                    this.errorInfo = new String(errorInfo, "GBK").trim();
                } catch (java.io.UnsupportedEncodingException e) {
                    // GBK 是 Windows 标准编码，几乎不会走到这里
                    this.errorInfo = new String(errorInfo).trim();
                }
            }
        }

        public String getErrorInfo() {
            return errorInfo;
        }

        public void setErrorInfo(String errorInfo) {
            this.errorInfo = errorInfo;
        }
    }
}
