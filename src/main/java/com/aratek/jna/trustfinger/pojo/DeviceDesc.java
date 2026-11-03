package com.aratek.jna.trustfinger.pojo;

/**
 * @author shijinlong
 * @date 2018-11-29
 */
public class DeviceDesc {

    /**
     * Device serial number
     * char[32]
     */
    private String serialNumber;

    /**
     * manufacturer
     * char[32]
     */
    private String manufacturer;

    /**
     * Device product name
     * char[64]
     */
    private String productName;

    /**
     * Device product model
     * char[32]
     */
    private String productModel;

    /**
     * Device firmware version
     * char[32]
     */
    private String fwVersion ;

    /**
     * Device revision
     * char[32]
     */
    private String hwVersion;

    /**
     * Device capture max image width
     */
    private int imageWidth;

    /**
     * Device capture max image height
     */
    private int imageHeight;

    /**
     * Device capture max image DPI
     */
    private int dpi;

    /**
     * Device id
     */
    private int deviceId;

    /**
     * device support USE connected type or not
     */
    private boolean isUSBSupported;

    /**
     * device suppport UART or not
     */
    private boolean isUARTSupported;

    /**
     * device support SPI or not
     */
    private boolean isSPISupported;

    /**
     * device support led or not
     */
    private boolean isLedSupported;

    /**
     * device support TrustMe or not (V3.3 新增)
     */
    private boolean isTrustMeSupported;

    /**
     * device support TrustAFIS or not (V3.3 新增)
     */
    private boolean isTrustAFISSupported;

    /**
     * device support TrustLink or not (V3.3 新增)
     */
    private boolean isTrustLinkSupported;

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductModel() {
        return productModel;
    }

    public void setProductModel(String productModel) {
        this.productModel = productModel;
    }

    public String getFwVersion() {
        return fwVersion;
    }

    public void setFwVersion(String fwVersion) {
        this.fwVersion = fwVersion;
    }

    public String getHwVersion() {
        return hwVersion;
    }

    public void setHwVersion(String hwVersion) {
        this.hwVersion = hwVersion;
    }

    public int getImageWidth() {
        return imageWidth;
    }

    public void setImageWidth(int imageWidth) {
        this.imageWidth = imageWidth;
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public void setImageHeight(int imageHeight) {
        this.imageHeight = imageHeight;
    }

    public int getDpi() {
        return dpi;
    }

    public void setDpi(int dpi) {
        this.dpi = dpi;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }

    public boolean getIsUSBSupported() {
        return isUSBSupported;
    }

    public void setIsUSBSupported(boolean isUSBSupported) {
        this.isUSBSupported = isUSBSupported;
    }

    public boolean getIsUARTSupported() {
        return isUARTSupported;
    }

    public void setIsUARTSupported(boolean isUARTSupported) {
        this.isUARTSupported = isUARTSupported;
    }

    public boolean getIsSPISupported() {
        return isSPISupported;
    }

    public void setIsSPISupported(boolean isSPISupported) {
        this.isSPISupported = isSPISupported;
    }

    public boolean getIsLedSupported() {
        return isLedSupported;
    }

    public void setIsLedSupported(boolean isLedSupported) {
        this.isLedSupported = isLedSupported;
    }

    public boolean getIsTrustMeSupported() {
        return isTrustMeSupported;
    }

    public void setIsTrustMeSupported(boolean isTrustMeSupported) {
        this.isTrustMeSupported = isTrustMeSupported;
    }

    public boolean getIsTrustAFISSupported() {
        return isTrustAFISSupported;
    }

    public void setIsTrustAFISSupported(boolean isTrustAFISSupported) {
        this.isTrustAFISSupported = isTrustAFISSupported;
    }

    public boolean getIsTrustLinkSupported() {
        return isTrustLinkSupported;
    }

    public void setIsTrustLinkSupported(boolean isTrustLinkSupported) {
        this.isTrustLinkSupported = isTrustLinkSupported;
    }
}
