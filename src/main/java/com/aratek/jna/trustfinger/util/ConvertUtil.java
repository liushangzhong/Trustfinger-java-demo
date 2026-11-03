package com.aratek.jna.trustfinger.util;

import com.aratek.jna.trustfinger.library.AraTrustFingerLibrary;
import com.aratek.jna.trustfinger.pojo.DeviceDesc;

/**
 * @author shijinlong
 * @date 2018-11-29
 */
public class ConvertUtil {

    /**
     * 将DeviceDescStruct结构体转为DeviceDesc对象
     *
     * @param deviceDescStruct
     * @return
     */
    public static DeviceDesc convertDeviceDesc(AraTrustFingerLibrary.DeviceDescStruct.ByReference deviceDescStruct) {
        DeviceDesc deviceDesc = new DeviceDesc();
        deviceDesc.setSerialNumber(convertBytesToString(deviceDescStruct.serialNumber));
        deviceDesc.setManufacturer(convertBytesToString(deviceDescStruct.manufacturer));
        deviceDesc.setProductName(convertBytesToString(deviceDescStruct.productName));
        deviceDesc.setProductModel(convertBytesToString(deviceDescStruct.productModel));
        deviceDesc.setFwVersion(convertBytesToString(deviceDescStruct.fwVersion));
        deviceDesc.setHwVersion(convertBytesToString(deviceDescStruct.hwVersion));
        deviceDesc.setImageWidth(deviceDescStruct.imageWidth);
        deviceDesc.setImageHeight(deviceDescStruct.imageHeight);
        deviceDesc.setDpi(deviceDescStruct.Dpi);
        deviceDesc.setDeviceId(deviceDescStruct.deviceId);
        deviceDesc.setIsUSBSupported(convertByteToBoolean(deviceDescStruct.isUSBSupported));
        deviceDesc.setIsUARTSupported(convertByteToBoolean(deviceDescStruct.isUARTSupported));
        deviceDesc.setIsSPISupported(convertByteToBoolean(deviceDescStruct.isSPISupported));
        deviceDesc.setIsLedSupported(convertByteToBoolean(deviceDescStruct.isLedSupported));
        deviceDesc.setIsTrustMeSupported(convertByteToBoolean(deviceDescStruct.isTrustMeSupported));
        deviceDesc.setIsTrustAFISSupported(convertByteToBoolean(deviceDescStruct.isTrustAFISSupported));
        deviceDesc.setIsTrustLinkSupported(convertByteToBoolean(deviceDescStruct.isTrustLinkSupported));
        return deviceDesc;
    }

    /**
     * byte 转 boolean
     *
     * @param b
     * @return
     */
    public static boolean convertByteToBoolean(byte b) {
        if (b == 0) {
            return false;
        } else if (b == 1) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * int 转 byte
     *
     * @param x
     * @return
     */
    public static byte intToByte(int x) {
        return (byte) x;
    }

    /**
     * byte 转 int
     *
     * @param b
     * @return
     */
    public static int byteToInt(byte b) {
        //Java 总是把 byte 当做有符处理；我们可以通过将其和 0xFF 进行二进制与得到它的无符值
        return b & 0xFF;
    }

    /**
     * 将 C 结构体中的定长字节数组（char[N]，GBK 编码）转为 String。
     * <p>C DLL 返回的定长数组尾部是零字节（未填充部分），且数据本身是 GBK 编码。
     * 直接 new String(byte[]) 会：1) 把零字节当字符输出导致尾部乱码；2) 编码不匹配导致中文乱码。
     * 因此这里先截断零字节，再用 GBK 解码，最后 trim 去掉首尾空白。
     *
     * @param bytes 定长字节数组
     * @return 解码后的字符串
     */
    public static String convertBytesToString(byte[] bytes) {
        if (bytes == null) {
            return "";
        }
        // 截断第一个零字节（C 字符串以 \0 结尾）
        int len = 0;
        for (byte b : bytes) {
            if (b == 0) {
                break;
            }
            len++;
        }
        byte[] trimmed = new byte[len];
        System.arraycopy(bytes, 0, trimmed, 0, len);
        try {
            return new String(trimmed, "GBK").trim();
        } catch (java.io.UnsupportedEncodingException e) {
            return new String(trimmed).trim();
        }
    }

    /**
     * byteArray 转 int
     *
     * @param b
     * @return
     */
    public static int byteArrayToInt(byte[] b) {
        return b[3] & 0xFF |
                (b[2] & 0xFF) << 8 |
                (b[1] & 0xFF) << 16 |
                (b[0] & 0xFF) << 24;
    }

    /**
     * int 转 byteArray
     *
     * @param a
     * @return
     */
    public static byte[] intToByteArray(int a) {
        return new byte[]{
                (byte) ((a >> 24) & 0xFF),
                (byte) ((a >> 16) & 0xFF),
                (byte) ((a >> 8) & 0xFF),
                (byte) (a & 0xFF)
        };
    }
}
