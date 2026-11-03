//package com.aratek.jna.trustfinger.struct;
//
//import com.sun.jna.Pointer;
//import com.sun.jna.Structure;
//
//import java.util.Arrays;
//import java.util.List;
//
///**
// * 设备描述 结构体
// * @author shijinlong
// * @date 2018-11-22
// */
//public class DeviceDescStruct extends Structure {
//
//    //结构体传指针
//    public static class ByReference extends DeviceDescStruct implements Structure.ByReference{
//    }
//
//    //结构体传值
//    public static class ByValue extends DeviceDescStruct implements Structure.ByValue{
//    }
//
//    public DeviceDescStruct() {
//        super();
//    }
//
//    public DeviceDescStruct(Pointer peer) {
//        super(peer);
//    }
//
//
//    /**
//     * Device serial number
//     * char[32]
//     */
//    public byte[] serialNumber = new byte[32];
//
//    /**
//     * manufacturer
//     * char[32]
//     */
//    public byte[] manufacturer = new byte[32];
//
//    /**
//     * Device product name
//     * char[64]
//     */
//    public byte[] productName = new byte[64];
//
//    /**
//     * Device product model
//     * char[32]
//     */
//    public byte[] productModel = new byte[32];
//
//    /**
//     * Device firmware version
//     * char[32]
//     */
//    public byte[] fwVersion = new byte[32];
//
//    /**
//     * Device revision
//     * char[32]
//     */
//    public byte[] hwVersion = new byte[32];
//
//    /**
//     * Device capture max image width
//     */
//    public int imageWidth;
//
//    /**
//     * Device capture max image height
//     */
//    public int imageHeight;
//
//    /**
//     * Device capture max image DPI
//     */
//    public int Dpi;
//
//    /**
//     * Device id
//     */
//    public short deviceId;
//
//    /**
//     * device support USE connected type or not
//     */
//    public byte isUSBSupported;
//
//    /**
//     * device suppport UART or not
//     */
//    public byte isUARTSupported;
//
//    /**
//     * device support SPI or not
//     */
//    public byte isSPISupported;
//
//    /**
//     * device support led or not
//     */
//    public byte isLedSupported;
//
//    @Override
//    protected List<String> getFieldOrder() {
//        return Arrays.asList("serialNumber", "manufacturer", "productName", "productModel", "fwVersion", "hwVersion", "imageWidth", "imageHeight", "Dpi", "deviceId", "isUSBSupported", "isUARTSupported", "isSPISupported", "isLedSupported");
//    }
//
//}
