package com.aratek.jna.trustfinger;

import com.aratek.jna.trustfinger.pojo.Results;
import com.aratek.jna.trustfinger.service.AraTrustFingerService;
import com.aratek.jna.trustfinger.util.FileUtil;

import java.io.UnsupportedEncodingException;

import static com.aratek.jna.trustfinger.pojo.Results.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author shijinlong
 * @date 2018-11-30
 */
public class AraTrustFingerMainTest {
    private static AraTrustFingerService araTrustFingerService = new AraTrustFingerService();

    private static final String UTF8 = "UTF-8";
    //返回结果成功值
    private static final int OK = 0;
    //线程休眠时间
    private static final long SLEEP_MILLIS = 1000;

    //指向指纹采集设备的句柄
    private static long[] HANDLE;
    //指纹仪设备索引号
    private static final int DEFAULT_DEVICE_INDEX = 0;

    //采集超时时间，单位为秒。建议设置 5
    private static final int DEFAULT_TIMEOUT = 3;
    //指位信息
    private static final int FINGER_POS = 1;
    //图像压缩算法
    private static final int IMG_COMPRESS_ALG = 2;

    //LED的索引号。设备的索引号从 0 开始，在出厂时候已经分配号。不同设备索引号不同
    private static final int LED_INDEX = 0;
    //LED 灯状态 0 表示灯灭 1 表示灯亮
    private static final int LED_STATUS_CLOSE = 0;
    private static final int LED_STATUS_OPEN = 1;

    //安全等级，1-5 级，级别越高，越安全，建议 4 级
    private static final int SECURITY_LEVEL = 4;

    //比特率
    private static final double BIT_RATE = 0.8;
    //模板中特征的个数
    private static final int dbNumFeature = 5;

    //指纹图像像素宽
    private static int IMAGE_WIDTH;
    //指纹图像像素高
    private static int IMAGE_HEIGHT;

    //指纹图片二进制数据
    private static byte[] image_data_RAW;
    private static byte[] image_data_BMP;
    private static byte[] image_data_ISO;
    private static byte[] image_data_ANSI;


    //指纹特征(模板)
    private static byte[] image_template_ARA_1;
    private static byte[] image_template_ARA_2;
    private static byte[] image_template_ARA_3;
    private static byte[] image_template_ANSI;
    private static byte[] image_template_ISO;

    //合成特征
    private static byte[] image_template_Generalize;

    public static void main(String[] args) throws Exception {
        String libPath = System.getenv("AraTrustFingerLibraryPath");
        System.out.println("\n=====libPath=" + libPath);
        AraTrustFingerMainTest test = new AraTrustFingerMainTest();
        test.doBeforeClass();
        test.test01_globalInit();
        test.test02_openDevice();
        test.test03_getDeviceCount();
        test.test04_getImageInfo();
        test.test05_getDeviceDescription();
        try {
            test.test06_getLedStatus();
            test.test07_setLedStatus();
        } catch (Exception e) {
            e.printStackTrace();
        }
        test.test08_captureRawData();
        test.test09_extractFeature();
        test.test10_captureBitmapData();
        test.test11_captureANSIData();
        test.test12_extractANSIFeature();
        test.test13_captureISOData();
        test.test14_extractISOFeature();
        test.test15_liveCaptureTest();
        test.test16_generalizeTemplate();
        test.test17_verify();
        test.test18_rawToBitmap();
        test.test19_bitmapToRaw();
        test.test20_rawToWSQ();
        test.test21_imgQuality();
        test.test22_getNFIQScore();
        test.test23_getErrorInfo();
        test.test24_closeDevice();
        test.test25_globalFree();
        test.doAfterClass();
    }

    public void doBeforeClass() throws Exception {
        System.out.println("\n=========================doBeforeClass begin==========================================");
        System.setProperty("jna.encoding", "UTF-8");
        System.setProperty("jna.debug_load", "true");
        System.out.println("DEFAULT_DEVICE_INDEX=" + DEFAULT_DEVICE_INDEX);
        //globalInit
        BaseResult globalInitResult = araTrustFingerService.globalInit();
        assertThat(globalInitResult.getCode(), is(OK));
        //openDevice
        OpenDeviceResult openDeviceResult = araTrustFingerService.openDevice(DEFAULT_DEVICE_INDEX);
        assertThat(openDeviceResult.getCode(), is(OK));
        HANDLE = openDeviceResult.getDeviceHandle();
        System.out.println(String.format("HANDLE[%d]=%d", DEFAULT_DEVICE_INDEX, HANDLE[DEFAULT_DEVICE_INDEX]));
        //getDeviceCount
        DeviceCountResult deviceCountResult = araTrustFingerService.getDeviceCount();
        assertThat(deviceCountResult.getCode(), is(OK));
        System.out.println("deviceCount=" + deviceCountResult.getDeviceCount());
        //getDeviceDescription
        DeviceDescResult deviceDescResult = araTrustFingerService.getDeviceDescription(0);
        assertThat(deviceDescResult.getCode(), is(OK));
        IMAGE_WIDTH = deviceDescResult.getDeviceDesc().getImageWidth();
        IMAGE_HEIGHT = deviceDescResult.getDeviceDesc().getImageHeight();
        System.out.println("IMAGE_WIDTH=" + IMAGE_WIDTH);
        System.out.println("IMAGE_HEIGHT=" + IMAGE_HEIGHT);
        //gc
        System.gc();
        //sleep
        Thread.sleep(SLEEP_MILLIS);
        System.out.println("=========================doBeforeClass   end==========================================");
    }

    public void doAfterClass() throws Exception {
        System.out.println("\n=========================doAfterClass begin==========================================");
        //closeDevice
        BaseResult closeDeviceResult = araTrustFingerService.closeDevice(HANDLE);
        assertThat(closeDeviceResult.getCode(), is(OK));
        //globalFree
        BaseResult globalFreeResult = araTrustFingerService.globalFree();
        assertThat(globalFreeResult.getCode(), is(OK));
        //gc
        System.gc();
        //sleep
        Thread.sleep(SLEEP_MILLIS);
        System.out.println("=========================doAfterClass   end==========================================");
    }

    public void     test01_globalInit() {
        System.out.println("\n=========================globalInit begin==========================================");
        System.out.println("=========================globalInit   end==========================================");
    }

    public void test25_globalFree() {
        System.out.println("\n=========================globalFree begin==========================================");
        System.out.println("=========================globalFree   end==========================================");
    }

    public void test03_getDeviceCount() {
        System.out.println("\n=========================getDeviceCount begin==========================================");
        DeviceCountResult deviceCountResult = araTrustFingerService.getDeviceCount();
        assertThat(deviceCountResult.getCode(), is(OK));
        System.out.println("deviceCount=" + deviceCountResult.getDeviceCount());
        System.out.println("=========================getDeviceCount   end==========================================");
    }

    public void test02_openDevice() {
        System.out.println("\n=========================openDevice begin==========================================");
        System.out.println("=========================openDevice   end==========================================");
    }

    public void test24_closeDevice() {
        System.out.println("\n=========================closeDevice begin==========================================");
        System.out.println("=========================closeDevice   end==========================================");
    }

    public void test04_getImageInfo() {
        System.out.println("\n=========================getImageInfo begin==========================================");
        ImageInfoResult imageInfoResult = araTrustFingerService.getImageInfo(HANDLE[DEFAULT_DEVICE_INDEX]);
        assertThat(imageInfoResult.getCode(), is(OK));
        System.out.println("pnWidth=" + imageInfoResult.getPnWidth());
        System.out.println("pnHeight=" + imageInfoResult.getPnHeight());
        System.out.println("pnDpi=" + imageInfoResult.getPnDpi());
        System.out.println("=========================getImageInfo   end==========================================");
    }

    public void test05_getDeviceDescription() {
        System.out.println("\n=========================getDeviceDescription begin==========================================");
        DeviceDescResult deviceDescResult = araTrustFingerService.getDeviceDescription(DEFAULT_DEVICE_INDEX);
        assertThat(deviceDescResult.getCode(), is(OK));
        System.out.println("serialNumber=" + deviceDescResult.getDeviceDesc().getSerialNumber());
        System.out.println("manufacturer=" + deviceDescResult.getDeviceDesc().getManufacturer());
        System.out.println("productName=" + deviceDescResult.getDeviceDesc().getProductName());
        System.out.println("productModel=" + deviceDescResult.getDeviceDesc().getProductModel());
        System.out.println("fwVersion=" + deviceDescResult.getDeviceDesc().getFwVersion());
        System.out.println("hwVersion=" + deviceDescResult.getDeviceDesc().getHwVersion());
        System.out.println("imageWidth=" + deviceDescResult.getDeviceDesc().getImageWidth());
        System.out.println("imageHeight=" + deviceDescResult.getDeviceDesc().getImageHeight());
        System.out.println("Dpi=" + deviceDescResult.getDeviceDesc().getDpi());
        System.out.println("deviceId=" + deviceDescResult.getDeviceDesc().getDeviceId());
        System.out.println("isUSBSupported=" + deviceDescResult.getDeviceDesc().getIsUSBSupported());
        System.out.println("isUARTSupported=" + deviceDescResult.getDeviceDesc().getIsUARTSupported());
        System.out.println("isSPISupported=" + deviceDescResult.getDeviceDesc().getIsSPISupported());
        System.out.println("isLedSupported=" + deviceDescResult.getDeviceDesc().getIsLedSupported());
        System.out.println("=========================getDeviceDescription   end==========================================");
    }

    public void test08_captureRawData() {
        System.out.println("\n=========================captureRawData begin==========================================");
        CaptureDataResult captureDataResult = araTrustFingerService.captureRawData(HANDLE[0], DEFAULT_TIMEOUT, IMAGE_WIDTH, IMAGE_HEIGHT);
        assertThat(captureDataResult.getCode(), is(OK));
        image_data_RAW = Results.copyBytes(captureDataResult.getImageData());
        FileUtil.writeFile(image_data_RAW, "C:\\TrustFinger\\image", "raw.bmp");
        System.out.println("ImageData Length=" + captureDataResult.getImageData().length);
        System.out.println("ImageDataLength=" + captureDataResult.getImageDataLength());
        try {
            System.out.println("ImageData=" + new String(captureDataResult.getImageData(), UTF8));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        System.out.println("=========================captureRawData   end==========================================");
    }

    public void test10_captureBitmapData() {
        System.out.println("\n=========================captureBitmapData begin==========================================");
        CaptureDataResult captureDataResult = araTrustFingerService.captureBitmapData(HANDLE[0], DEFAULT_TIMEOUT, IMAGE_WIDTH, IMAGE_HEIGHT);
        assertThat(captureDataResult.getCode(), is(OK));
        image_data_BMP = Results.copyBytes(captureDataResult.getImageData());
        FileUtil.writeFile(image_data_BMP, "C:\\TrustFinger\\image", "bmp.bmp");
        System.out.println("ImageData Length=" + captureDataResult.getImageData().length);
        System.out.println("ImageDataLength=" + captureDataResult.getImageDataLength());
        try {
            System.out.println("ImageData=" + new String(captureDataResult.getImageData(), UTF8));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        System.out.println("=========================captureBitmapData   end==========================================");
    }

    public void test13_captureISOData() {
        System.out.println("\n=========================captureISOData begin==========================================");
        CaptureDataResult captureDataResult = araTrustFingerService.captureISOData(HANDLE[0], DEFAULT_TIMEOUT, FINGER_POS, IMG_COMPRESS_ALG, IMAGE_WIDTH, IMAGE_HEIGHT);
        assertThat(captureDataResult.getCode(), is(OK));
        image_data_ISO = Results.copyBytes(captureDataResult.getImageData());
        FileUtil.writeFile(image_data_ISO, "C:\\TrustFinger\\image", "iso.bmp");
        System.out.println("ImageData Length=" + captureDataResult.getImageData().length);
        System.out.println("ImageDataLength=" + captureDataResult.getImageDataLength());
        try {
            System.out.println("ImageData=" + new String(captureDataResult.getImageData(), UTF8));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        System.out.println("=========================captureISOData   end==========================================");
    }

    public void test11_captureANSIData() {
        System.out.println("\n=========================captureANSIData begin==========================================");
        CaptureDataResult captureDataResult = araTrustFingerService.captureANSIData(HANDLE[0], DEFAULT_TIMEOUT, FINGER_POS, IMG_COMPRESS_ALG, IMAGE_WIDTH, IMAGE_HEIGHT);
        assertThat(captureDataResult.getCode(), is(OK));
        image_data_ANSI = Results.copyBytes(captureDataResult.getImageData());
        FileUtil.writeFile(image_data_ANSI, "C:\\TrustFinger\\image", "ansi.bmp");
        System.out.println("ImageData Length=" + captureDataResult.getImageData().length);
        System.out.println("ImageDataLength=" + captureDataResult.getImageDataLength());
        try {
            System.out.println("ImageData=" + new String(captureDataResult.getImageData(), UTF8));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        System.out.println("=========================captureANSIData   end==========================================");
    }

    public void test15_liveCaptureTest() {
        System.out.println("\n=========================beginLiveCapture begin==========================================");
        BaseResult beginLiveCaptureResult = araTrustFingerService.beginLiveCapture(HANDLE[0]);
        assertThat(beginLiveCaptureResult.getCode(), is(OK));
        System.out.println("=========================beginLiveCapture   end==========================================");
        System.out.println("\n=========================liveCaptureRawData begin==========================================");
        try {
            Thread.sleep(SLEEP_MILLIS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        CaptureDataResult liveCaptureRawDataResult = araTrustFingerService.liveCaptureRawData(HANDLE[0], IMAGE_WIDTH, IMAGE_HEIGHT);
        assertThat(liveCaptureRawDataResult.getCode(), is(OK));
        System.out.println("liveCaptureRawData ImageData Length=" + liveCaptureRawDataResult.getImageData().length);
        System.out.println("liveCaptureRawData ImageDataLength=" + liveCaptureRawDataResult.getImageDataLength());
        try {
            System.out.println("liveCaptureRawData ImageData=" + new String(liveCaptureRawDataResult.getImageData(), UTF8));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        System.out.println("=========================liveCaptureRawData   end==========================================");
        System.out.println("\n=========================liveCaptureBitmap begin==========================================");
        try {
            Thread.sleep(SLEEP_MILLIS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        CaptureDataResult liveCaptureBitmapResult = araTrustFingerService.liveCaptureBitmap(HANDLE[0], IMAGE_WIDTH, IMAGE_HEIGHT);
        assertThat(liveCaptureBitmapResult.getCode(), is(OK));
        System.out.println("liveCaptureBitmapResult ImageData Length=" + liveCaptureBitmapResult.getImageData().length);
        System.out.println("liveCaptureBitmapResult ImageDataLength=" + liveCaptureBitmapResult.getImageDataLength());
        try {
            System.out.println("liveCaptureBitmapResult ImageData=" + new String(liveCaptureBitmapResult.getImageData(), UTF8));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        System.out.println("=========================liveCaptureBitmap   end==========================================");
        System.out.println("\n=========================endLiveCapture begin==========================================");
        try {
            Thread.sleep(SLEEP_MILLIS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        BaseResult endLiveCaptureResult = araTrustFingerService.endLiveCapture(HANDLE[0]);
        assertThat(endLiveCaptureResult.getCode(), is(OK));
        System.out.println("=========================endLiveCapture   end==========================================");
    }

    public void test06_getLedStatus() {
        System.out.println("\n=========================getLedStatus begin==========================================");
        LedResult ledResult = araTrustFingerService.getLedStatus(HANDLE[0], LED_INDEX);
        assertThat(ledResult.getCode(), is(OK));
        System.out.println("LedStatus=" + ledResult.getStatus());
        System.out.println("=========================getLedStatus   end==========================================");
    }

    public void test07_setLedStatus() {
        System.out.println("\n=========================setLedStatus begin==========================================");
        BaseResult ledResult = araTrustFingerService.setLedStatus(HANDLE[0], LED_INDEX, LED_STATUS_CLOSE);
        assertThat(ledResult.getCode(), is(OK));
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ledResult = araTrustFingerService.setLedStatus(HANDLE[0], LED_INDEX, LED_STATUS_OPEN);
        assertThat(ledResult.getCode(), is(OK));
        System.out.println("=========================setLedStatus   end==========================================");
    }

    public void test09_extractFeature() {
        System.out.println("\n=========================extractFeature begin==========================================");
        //1.
        FeatureResult featureResult = araTrustFingerService.extractFeature(HANDLE[0], FINGER_POS);
        assertThat(featureResult.getCode(), is(OK));
        image_template_ARA_1 = Results.copyBytes(featureResult.getFeatureData());
        FileUtil.writeFile(image_template_ARA_1, "C:\\TrustFinger\\template", "ara1.template");
        System.out.println("featureData length=" + featureResult.getFeatureData().length);
        try {
            System.out.println("featureData=" + new String(featureResult.getFeatureData(), UTF8));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //2.
        featureResult = araTrustFingerService.extractFeature(HANDLE[0], FINGER_POS);
        assertThat(featureResult.getCode(), is(OK));
        image_template_ARA_2 = Results.copyBytes(featureResult.getFeatureData());
        FileUtil.writeFile(image_template_ARA_2, "C:\\TrustFinger\\template", "ara2.template");
        System.out.println("featureData length=" + featureResult.getFeatureData().length);
        try {
            System.out.println("featureData=" + new String(featureResult.getFeatureData(), UTF8));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //3.
        featureResult = araTrustFingerService.extractFeature(HANDLE[0], FINGER_POS);
        assertThat(featureResult.getCode(), is(OK));
        image_template_ARA_3 = Results.copyBytes(featureResult.getFeatureData());
        FileUtil.writeFile(image_template_ARA_3, "C:\\TrustFinger\\template", "ara3.template");
        System.out.println("featureData length=" + featureResult.getFeatureData().length);
        try {
            System.out.println("featureData=" + new String(featureResult.getFeatureData(), UTF8));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        System.out.println("=========================extractFeature   end==========================================");
    }

    public void test12_extractANSIFeature() {
        System.out.println("\n=========================extractANSIFeature begin==========================================");
        FeatureResult featureResult = araTrustFingerService.extractANSIFeature(HANDLE[0], FINGER_POS);
        assertThat(featureResult.getCode(), is(OK));
        image_template_ANSI = Results.copyBytes(featureResult.getFeatureData());
        FileUtil.writeFile(image_template_ANSI, "C:\\TrustFinger\\template", "ansi.template");
        System.out.println("featureData length=" + featureResult.getFeatureData().length);
        try {
            System.out.println("featureData=" + new String(featureResult.getFeatureData(), UTF8));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        System.out.println("=========================extractANSIFeature   end==========================================");
    }

    public void test14_extractISOFeature() {
        System.out.println("\n=========================extractISOFeature begin==========================================");
        FeatureResult featureResult = araTrustFingerService.extractISOFeature(HANDLE[0], FINGER_POS);
        assertThat(featureResult.getCode(), is(OK));
        image_template_ISO = Results.copyBytes(featureResult.getFeatureData());
        FileUtil.writeFile(image_template_ISO, "C:\\TrustFinger\\template", "iso.template");
        System.out.println("featureData length=" + featureResult.getFeatureData().length);
        try {
            System.out.println("featureData=" + new String(featureResult.getFeatureData(), UTF8));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        System.out.println("=========================extractISOFeature   end==========================================");
    }

    public void test16_generalizeTemplate() {
        System.out.println("\n=========================generalizeTemplate begin==========================================");
        System.out.println("HANDLE[0]=" + HANDLE[0]);
        System.out.println("image_template_ARA_1.length=" + image_template_ARA_1.length);
        System.out.println("image_template_ARA_2.length=" + image_template_ARA_2.length);
        System.out.println("image_template_ARA_3.length=" + image_template_ARA_3.length);
        FeatureTemplateResult featureTemplateResult = araTrustFingerService.generalizeTemplate(HANDLE[0], image_template_ARA_1, image_template_ARA_2, image_template_ARA_3);
        assertThat(featureTemplateResult.getCode(), is(OK));
        image_template_Generalize = Results.copyBytes(featureTemplateResult.getFeatureTemplateData());
        System.out.println("generalizeTemplate ImageData Length=" + featureTemplateResult.getFeatureTemplateData().length);
        try {
            System.out.println("generalizeTemplate ImageData=" + new String(featureTemplateResult.getFeatureTemplateData(), UTF8));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        System.out.println("=========================generalizeTemplate   end==========================================");
    }

    public void test17_verify() {
        System.out.println("\n=========================verify begin==========================================");
        System.out.println("HANDLE[0]=" + HANDLE[0]);
        System.out.println("SECURITY_LEVEL=" + SECURITY_LEVEL);
        System.out.println("image_template_ARA_1.length=" + image_template_ARA_1.length);
        System.out.println("image_template_ARA_2.length=" + image_template_ARA_2.length);
        VerifyResult verifyResult = araTrustFingerService.verify(HANDLE[0], SECURITY_LEVEL, image_template_ARA_1, image_template_ARA_2);
        assertThat(verifyResult.getCode(), is(OK));
        System.out.println("指纹匹配结果. 1表示匹配 0表示不匹配=" + verifyResult.getpMmatch());
        System.out.println("相似度，0-1000=" + verifyResult.getPnSimilarity());
        System.out.println("=========================verify   end==========================================");
    }

    public void test18_rawToBitmap() {
        System.out.println("\n=========================rawToBitmap begin==========================================");
        try {
            Thread.sleep(SLEEP_MILLIS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("image_data_RAW.length=" + image_data_RAW.length);
        System.out.println("IMAGE_WIDTH=" + IMAGE_WIDTH);
        System.out.println("IMAGE_HEIGHT=" + IMAGE_HEIGHT);
        ImageConvertResult imageConvertResult = araTrustFingerService.rawToBitmap(image_data_RAW, IMAGE_WIDTH, IMAGE_HEIGHT);
        assertThat(imageConvertResult.getCode(), is(OK));
        System.out.println("rawToBitmap ImageData Length=" + imageConvertResult.getImageData().length);
        try {
            System.out.println("rawToBitmap ImageData=" + new String(imageConvertResult.getImageData(), UTF8));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        System.out.println("=========================rawToBitmap   end==========================================");
    }

    public void test19_bitmapToRaw() {
        System.out.println("\n=========================bitmapToRaw begin==========================================");
        try {
            Thread.sleep(SLEEP_MILLIS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("image_data_BMP.length=" + image_data_BMP.length);
        System.out.println("IMAGE_WIDTH=" + IMAGE_WIDTH);
        System.out.println("IMAGE_HEIGHT=" + IMAGE_HEIGHT);
        ImageConvertResult imageConvertResult = araTrustFingerService.bitmapToRaw(image_data_BMP, IMAGE_WIDTH, IMAGE_HEIGHT);
        assertThat(imageConvertResult.getCode(), is(OK));
        System.out.println("bitmapToRaw ImageData Length=" + imageConvertResult.getImageData().length);
        try {
            System.out.println("bitmapToRaw ImageData=" + new String(imageConvertResult.getImageData(), UTF8));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        System.out.println("=========================bitmapToRaw   end==========================================");
    }

    public void test20_rawToWSQ() {
        System.out.println("\n=========================rawToWSQ begin==========================================");
        try {
            Thread.sleep(SLEEP_MILLIS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("image_data_RAW.length=" + image_data_RAW.length);
        System.out.println("IMAGE_WIDTH=" + IMAGE_WIDTH);
        System.out.println("IMAGE_HEIGHT=" + IMAGE_HEIGHT);
        System.out.println("BIT_RATE=" + BIT_RATE);
        WSQResult wsqResult = araTrustFingerService.rawToWSQ(image_data_RAW, IMAGE_WIDTH, IMAGE_HEIGHT, 500, BIT_RATE);
        assertThat(wsqResult.getCode(), is(OK));
        System.out.println("rawToWSQ ImageData Length=" + wsqResult.getImageData().length);
        System.out.println("rawToWSQ ImageDataLength=" + wsqResult.getLength());
        try {
            System.out.println("rawToWSQ ImageData=" + new String(wsqResult.getImageData(), UTF8));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        System.out.println("=========================rawToWSQ   end==========================================");
    }

    public void test21_imgQuality() {
        System.out.println("\n=========================imgQuality begin==========================================");
        try {
            Thread.sleep(SLEEP_MILLIS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        QualityResult qualityResult = araTrustFingerService.imgQuality(image_data_BMP, IMAGE_WIDTH, IMAGE_HEIGHT);
        assertThat(qualityResult.getCode(), is(OK));
        System.out.println("Quality=" + qualityResult.getQuality());
        System.out.println("=========================imgQuality   end==========================================");
    }

    public void test22_getNFIQScore() {
        System.out.println("\n=========================getNFIQScore begin==========================================");
        try {
            Thread.sleep(SLEEP_MILLIS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        QualityResult qualityResult = araTrustFingerService.getNFIQScore(image_data_BMP, IMAGE_WIDTH, IMAGE_HEIGHT);
        assertThat(qualityResult.getCode(), is(OK));
        System.out.println("Quality=" + qualityResult.getQuality());
        System.out.println("=========================getNFIQScore   end==========================================");
    }

    public void test23_getErrorInfo() {
        System.out.println("\n=========================getErrorInfo begin==========================================");
        ErrorInfoResult errorInfoResult = araTrustFingerService.getErrorInfo(-122);
        assertThat(errorInfoResult.getCode(), is(OK));
        System.out.println("errorInfo=" + errorInfoResult.getErrorInfo());
        System.out.println("=========================getErrorInfo   end==========================================");
    }
}
