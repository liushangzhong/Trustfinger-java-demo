package com.aratek.jna.trustfinger;

import com.aratek.jna.trustfinger.library.AraTrustFingerLibrary;
import com.aratek.jna.trustfinger.util.FileUtil;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Platform;
import com.sun.jna.ptr.IntByReference;

import java.io.IOException;

/**
 * @author shijinlong
 * @date 2018-11-21
 */
public class JnaTest {
    static final long[] HANDLE = new long[1];
//    static final Long HANDLE = new Long(1);
//    static final NativeLong HANDLE = new NativeLong(1);
//            static PointerByReference HANDLE = null; //new PointerByReference();
//    static IntByReference HANDLE = null;//new IntByReference();
//    static long HANDLE;
//    static Pointer HANDLE = Pointer.createConstant(0);
//    static  Pointer pointer = Pointer.createConstant(10);
//    static  AraTrustFingerLibrary.HANDLE HANDLE = new AraTrustFingerLibrary.HANDLE(pointer);


    // 定义接口CLibrary，继承自com.sun.jna.Library
    public interface CLibrary extends Library {
        // msvcrt为dll名称,msvcrt目录的位置为:C:\Windows\System32下面
        CLibrary INSTANCE = (CLibrary)
                Native.loadLibrary((Platform.isWindows() ? "msvcrt" : "c"),
                        CLibrary.class);

        // printf为msvcrt.dll中的一个方法
        void printf(String format, Object... args);
    }

    public static String unicode2String(String unicode) {
        StringBuffer string = new StringBuffer();
        String[] hex = unicode.split("\\\\u");
        for (int i = 1; i < hex.length; i++) {
            // 转换出每一个代码点
            int data = Integer.parseInt(hex[i], 16);
            // 追加成string
            string.append((char) data);
        }
        return string.toString();
    }

    public static String string2Unicode(String string) {
        StringBuffer unicode = new StringBuffer();
        for (int i = 0; i < string.length(); i++) {
            // 取出每一个字符
            char c = string.charAt(i);
            // 转换为unicode
            unicode.append("\\u" + Integer.toHexString(c));
        }
        return unicode.toString();
    }

    public static String asciiToString(String value) {
        StringBuffer sbu = new StringBuffer();
        String[] chars = value.split(",");
        for (int i = 0; i < chars.length; i++) {
            sbu.append((char) Integer.parseInt(chars[i]));
        }
        return sbu.toString();
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        System.setProperty("jna.encoding", "UTF-8");
        System.setProperty("jna.debug_load", "true");
        //
//        LibraryUtil.copyLibToTemp("AraTrustFinger");
//        long[] HANDLE = new long[1];
        System.out.println("===================================================================");
        //1.
        int result = AraTrustFingerLibrary.INSTANCE.ARAFPSCAN_GlobalInit();
        System.out.println("1.GlobalInit.result=" + result);
        System.gc();
        Thread.sleep(100);
        System.out.println("===================================================================");
        //2.
        IntByReference intByReference = new IntByReference(0);
        result = AraTrustFingerLibrary.INSTANCE.ARAFPSCAN_GetDeviceCount(intByReference);
        System.out.println("2.GetDeviceCount.result=" + result);
        System.out.println("2.device count=" + intByReference.getValue());
        System.gc();
        Thread.sleep(100);
        System.out.println("===================================================================");
        //3.
        final AraTrustFingerLibrary.DeviceDescStruct.ByReference deviceDescStruct = new AraTrustFingerLibrary.DeviceDescStruct.ByReference();
        result = AraTrustFingerLibrary.INSTANCE.ARAFPSCAN_GetDeviceDescription(0, deviceDescStruct);
        System.out.println("3.GetDeviceDescription.result=" + result);
        System.out.println("3.serialNumber=" + new String(deviceDescStruct.serialNumber));
        System.out.println("3.manufacturer=" + new String(deviceDescStruct.manufacturer));
        System.out.println("3.productName=" + new String(deviceDescStruct.productName));
        System.out.println("3.productModel=" + new String(deviceDescStruct.productModel));
        System.out.println("3.fwVersion=" + new String(deviceDescStruct.fwVersion));
        System.out.println("3.hwVersion=" + new String(deviceDescStruct.hwVersion));
        System.out.println("3.imageWidth=" + deviceDescStruct.imageWidth);
        System.out.println("3.imageHeight=" + deviceDescStruct.imageHeight);
        System.out.println("3.Dpi=" + deviceDescStruct.Dpi);
        System.out.println("3.deviceId=" + deviceDescStruct.deviceId);
        System.out.println("3.isUSBSupported=" + deviceDescStruct.isUSBSupported);
        System.out.println("3.isUARTSupported=" + deviceDescStruct.isUARTSupported);
        System.out.println("3.isSPISupported=" + deviceDescStruct.isSPISupported);
        System.out.println("3.isLedSupported=" + deviceDescStruct.isLedSupported);
        System.gc();
        Thread.sleep(100);
        System.out.println("===================================================================");
        //4.
//        Long HANDLE = 0L;

//        WinNT.HANDLE handle = new WinNT.HANDLE();
//        System.out.println("4.HANDLE=" + HANDLE.toString());
        result = AraTrustFingerLibrary.INSTANCE.ARAFPSCAN_OpenDevice(HANDLE, 0);
        System.out.println("OpenDevice.result=" + result);
        System.out.println("4.HANDLE=" + HANDLE);
        System.gc();
        Thread.sleep(100);
        System.out.println("===================================================================");
        //
//        IntByReference ledStatus = new IntByReference(3);
//        result = AraTrustFingerLibrary.INSTANCE.ARAFPSCAN_GetLedStatus(HANDLE,0,ledStatus);
//        System.out.println("GetLedStatus.result=" + result);
//        System.out.println("GetLedStatus.ledStatus=" + ledStatus.getValue());
        //5.
//        String pRawData = new String();
//        char[] pRawData = new char[256*360+1078];
        byte[] pBmpData = new byte[256 * 360 + 1078];
//        Pointer pRawData = Pointer.createConstant(256*360);
        result = AraTrustFingerLibrary.INSTANCE.ARAFPSCAN_CaptureBitmapData(HANDLE[0], 5, pBmpData);
//        do {
//            result = AraTrustFingerLibrary.INSTANCE.ARAFPSCAN_CaptureRawData(HANDLE,5,pRawData);
//            System.out.println("5.result=" + result);
//            Thread.sleep(5000);
//        } while (result != 0);
        System.out.println("CaptureRawData.result=" + result);
        System.out.println("5.pBmpData=" + pBmpData);
        System.gc();
        Thread.sleep(100);
        System.out.println("===================================================================");
        //
        System.out.println("pBmpData.length=" + pBmpData.length);
        boolean bool = FileUtil.writeFile(pBmpData, "C:\\TrustFinger\\image", "1.bmp");
        System.out.println("bool=" + bool);
        System.out.println("===================================================================");
        //ARAFPSCAN_ExtractFeature
        byte cFingerPos = 1;
//        char cFingerPos = 11111;
        byte[] pFeatureData = new byte[512];
        result = AraTrustFingerLibrary.INSTANCE.ARAFPSCAN_ExtractFeature(HANDLE[0], cFingerPos, pFeatureData);
        System.out.println("ExtractFeature.result=" + result);
        System.out.println("pFeatureData=" + new String(pFeatureData));
        System.gc();
        Thread.sleep(100);
        System.out.println("===================================================================");
        //
        byte[] pRawData = FileUtil.readFileToByte("C:\\TrustFinger\\image\\raw.bmp");
        byte[] imageData = new byte[256 * 360];
        IntByReference length = new IntByReference();
        result = AraTrustFingerLibrary.INSTANCE.ARAFPSCAN_RawToWSQ(pRawData, 256, 360, 500, 0.8, imageData, length);
        System.out.println("ARAFPSCAN_RawToWSQ.result=" + result);
        System.out.println("===================================================================");
        //
        IntByReference pnWidth = new IntByReference(0), pnHeight = new IntByReference(0), pnDpi = new IntByReference(0);
//        byte[] pnWidth = new byte[10], pnHeight = new byte[10], pnDpi = new byte[10];
//        Pointer pointer = new Pointer(10);
        result = AraTrustFingerLibrary.INSTANCE.ARAFPSCAN_GetImageInfo(HANDLE[0], pnWidth, pnHeight, pnDpi);
        System.out.println("GetImageInfo.result=" + result);
//        System.out.println("6.pnWidth=" + pnWidth.getValue());
//        System.out.println("6.pnHeight=" + pnHeight.getValue());
//        System.out.println("6.pnDpi=" + pnDpi.getValue());
        System.out.println("6.pnWidth=" + pnWidth.getValue());
        System.out.println("6.pnHeight=" + pnHeight.getValue());
        System.out.println("6.pnDpi=" + pnDpi.getValue());
        System.gc();
        Thread.sleep(100);
        System.out.println("===================================================================");
        //
        result = AraTrustFingerLibrary.INSTANCE.ARAFPSCAN_CloseDevice(HANDLE);
        System.out.println("CloseDevice.result=" + result);
        System.gc();
        Thread.sleep(100);
        System.out.println("===================================================================");
        //
        result = AraTrustFingerLibrary.INSTANCE.ARAFPSCAN_GlobalFree();
        System.out.println("GlobalFree.result=" + result);
        System.gc();
        Thread.sleep(100);
        System.out.println("===================================================================");
    }
}
