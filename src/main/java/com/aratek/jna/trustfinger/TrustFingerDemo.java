package com.aratek.jna.trustfinger;

import com.aratek.jna.trustfinger.pojo.DeviceDesc;
import com.aratek.jna.trustfinger.pojo.Results;
import static com.aratek.jna.trustfinger.pojo.Results.*;
import com.aratek.jna.trustfinger.service.AraTrustFingerService;
import com.aratek.jna.trustfinger.util.FileUtil;

import java.io.UnsupportedEncodingException;

/**
 * TrustFinger 指纹仪完整开发流程示例
 *
 * <p>按实际业务开发顺序组织（特征格式默认 Aratek Bione®）：
 * <ol>
 *   <li>初始化 SDK 运行时环境</li>
 *   <li>枚举并识别设备</li>
 *   <li>打开设备、获取设备参数</li>
 *   <li>采集指纹图像（原始/BMP）</li>
 *   <li>提取指纹特征（采集 3 次，用于注册合成模板）</li>
 *   <li>合成指纹模板（GeneralizeTemplate，3 特征合成 1 模板）</li>
 *   <li>图像格式转换与质量评估</li>
 *   <li>1:1 指纹验证（Verify：现场特征 vs 合成模板）</li>
 *   <li>关闭设备、释放 SDK</li>
 * </ol>
 *
 * <p>免环境变量运行：DLL 已内置于 jar，直接 java -jar 即可。需接好指纹仪。
 *
 * @author demo
 * @date 2026-11-02
 */
public class TrustFingerDemo {

    /** SDK 服务实例 */
    private static final AraTrustFingerService sdk = new AraTrustFingerService();

    /** 返回结果成功值 */
    private static final int OK = 0;

    // ===================== 业务常量 =====================
    /** 设备索引号，多台设备时从 0 开始 */
    private static final int DEVICE_INDEX = 0;
    /** 采集超时时间，单位秒，建议 5 */
    private static final int TIMEOUT = 5;
    /** 指位：1=右手食指，详见头文件定义 */
    private static final int FINGER_POS = 1;
    /** 图像压缩算法：0=不压缩 1=WSQ 2=ISO 3=ANSI */
    private static final int IMG_COMPRESS_ALG = 0;
    /** 安全等级 1-5，级别越高越安全，建议 4 */
    private static final int SECURITY_LEVEL = 4;
    /** WSQ 压缩比特率，0.5-0.9 */
    private static final double BIT_RATE = 0.8;
    /** 设备标准分辨率（指纹仪通常 500 dpi） */
    private static final int DPI = 500;

    /** 输出目录：采集的图像保存在此 */
    private static final String OUTPUT_DIR = "C:\\TrustFinger\\demo";

    /** 设备句柄（openDevice 返回，closeDevice 时需传回） */
    private static long deviceHandle;

    /** 采集到的图像数据，供后续步骤使用 */
    private static int imageWidth;
    private static int imageHeight;
    private static byte[] imageRaw;
    private static byte[] imageBmp;

    /** 提取的 3 个特征数据（注册阶段，用于合成模板） */
    private static byte[] feature1;
    private static byte[] feature2;
    private static byte[] feature3;

    /** 合成后的指纹模板（1024 字节，可存库用于后续验证） */
    private static byte[] template;

    // ===================== 主流程 =====================
    public static void main(String[] args) {
        // 强制控制台输出 UTF-8，避免中文 Windows 下乱码
        try {
            java.io.PrintStream out = new java.io.PrintStream(System.out, true, "UTF-8");
            java.io.PrintStream err = new java.io.PrintStream(System.err, true, "UTF-8");
            java.lang.System.setOut(out);
            java.lang.System.setErr(err);
        } catch (java.io.UnsupportedEncodingException ignore) {
            // 某些平台不支持 UTF-8，忽略
        }

        System.out.println("================= TrustFinger 开发流程示例 =================");
        System.out.println("开始时间：" + System.currentTimeMillis());

        try {
            // 1. 初始化 SDK 运行时环境（进程内只需一次）
            step1_initSDK();

            // 2. 枚举设备、查看设备信息
            step2_enumerateDevice();

            // 3. 打开设备、获取图像参数
            step3_openDevice();

            // 4. 采集指纹图像
            step4_captureImage();

            // 5. 提取指纹特征（采集 3 次，用于注册合成模板）
            step5_extractFeature();

            // 6. 合成指纹模板（3 特征 → 1 模板）
            step6_generalizeTemplate();

            // 7. 图像格式转换与质量评估
            step7_convertAndQuality();

            // 8. 1:1 指纹验证（现场特征 vs 合成模板）
            step8_verify();

        } catch (Exception e) {
            System.out.println("[异常] 流程中断：" + e.getMessage());
            e.printStackTrace();
        } finally {
            // 9. 关闭设备、释放 SDK（务必执行，避免句柄泄漏）
            step9_closeAndFree();
            System.out.println("结束时间：" + System.currentTimeMillis());
            System.out.println("================= 示例结束 =================");
        }
    }

    // ===================== 步骤 1：初始化 SDK =====================
    /**
     * 初始化 SDK 运行时环境。
     * 整个进程内只需调用一次，加载 DLL、分配内部资源。
     */
    private static void step1_initSDK() {
        section("步骤1：初始化 SDK 运行时环境");
        BaseResult r = sdk.globalInit();
        check(r, "globalInit 初始化 SDK");
        System.out.println("  -> SDK 初始化成功");
    }

    // ===================== 步骤 2：枚举设备 =====================
    /**
     * 查询已连接的设备数量和设备描述信息。
     * 实际业务中可据此做设备选择、状态展示。
     */
    private static void step2_enumerateDevice() {
        section("步骤2：枚举并识别设备");

        // 查询设备总数
        DeviceCountResult countR = sdk.getDeviceCount();
        check(countR, "getDeviceCount 查询设备总数");
        int count = countR.getDeviceCount();
        System.out.println("  -> 已连接设备数：" + count);
        if (count <= 0) {
            System.out.println("  -> [警告] 未检测到设备，请确认指纹仪已连接并安装驱动");
            return;
        }
        if (DEVICE_INDEX >= count) {
            System.out.println("  -> [警告] 设备索引 " + DEVICE_INDEX + " 超出范围(0~" + (count - 1) + ")");
            return;
        }

        // 获取指定设备的详细描述
        DeviceDescResult descR = sdk.getDeviceDescription(DEVICE_INDEX);
        check(descR, "getDeviceDescription 获取设备描述");
        DeviceDesc desc = descR.getDeviceDesc();
        System.out.println("  -> 序列号        ：" + desc.getSerialNumber());
        System.out.println("  -> 厂商          ：" + desc.getManufacturer());
        System.out.println("  -> 产品名称      ：" + desc.getProductName());
        System.out.println("  -> 产品型号      ：" + desc.getProductModel());
        System.out.println("  -> 固件版本      ：" + desc.getFwVersion());
        System.out.println("  -> 硬件版本      ：" + desc.getHwVersion());
        System.out.println("  -> 最大图像宽    ：" + desc.getImageWidth());
        System.out.println("  -> 最大图像高    ：" + desc.getImageHeight());
        System.out.println("  -> 分辨率(DPI)   ：" + desc.getDpi());
        System.out.println("  -> 设备ID        ：" + desc.getDeviceId());
        System.out.println("  -> USB 支持      ：" + desc.getIsUSBSupported());
        System.out.println("  -> LED 支持      ：" + desc.getIsLedSupported());
        System.out.println("  -> TrustMe 支持  ：" + desc.getIsTrustMeSupported());
        System.out.println("  -> TrustAFIS 支持：" + desc.getIsTrustAFISSupported());
        System.out.println("  -> TrustLink 支持：" + desc.getIsTrustLinkSupported());
    }

    // ===================== 步骤 3：打开设备 =====================
    /**
     * 打开设备获取句柄，并查询图像参数（宽、高、DPI）。
     * 后续所有操作都基于此句柄。
     */
    private static void step3_openDevice() {
        section("步骤3：打开设备、获取图像参数");

        // 打开设备
        OpenDeviceResult openR = sdk.openDevice(DEVICE_INDEX);
        check(openR, "openDevice 打开设备");
        deviceHandle = openR.getDeviceHandle()[DEVICE_INDEX];
        System.out.println("  -> 设备句柄：" + deviceHandle);

        // 查询图像参数
        ImageInfoResult infoR = sdk.getImageInfo(deviceHandle);
        check(infoR, "getImageInfo 获取图像信息");
        imageWidth = infoR.getPnWidth();
        imageHeight = infoR.getPnHeight();
        System.out.println("  -> 图像宽：" + imageWidth + "  图像高：" + imageHeight + "  DPI：" + infoR.getPnDpi());
    }

    // ===================== 步骤 4：采集指纹图像 =====================
    /**
     * 采集指纹图像。
     * captureRawData 采集原始像素数据，再转成 BMP 便于查看。
     * 演示用 Led 指示灯反馈采集状态。
     */
    private static void step4_captureImage() {
        section("步骤4：采集指纹图像");
        System.out.println("  -> 请在 " + TIMEOUT + " 秒内将手指放在指纹仪上...");

        // 点亮 LED 提示开始采集
        setLed(true);

        // 采集原始图像
        CaptureDataResult rawR = sdk.captureRawData(deviceHandle, TIMEOUT, imageWidth, imageHeight);
        check(rawR, "captureRawData 采集原始图像");
        imageRaw = Results.copyBytes(rawR.getImageData());
        System.out.println("  -> 原始图像数据长度：" + rawR.getImageDataLength() + " 字节");

        // 原始数据转 BMP
        ImageConvertResult bmpR = sdk.rawToBitmap(imageRaw, imageWidth, imageHeight);
        check(bmpR, "rawToBitmap 原始转 BMP");
        imageBmp = Results.copyBytes(bmpR.getImageData());
        FileUtil.writeFile(imageBmp, OUTPUT_DIR, "fingerprint.bmp");
        System.out.println("  -> BMP 图像已保存：" + OUTPUT_DIR + "\\fingerprint.bmp");

        // 熄灭 LED
        setLed(false);
    }

    // ===================== 步骤 5：提取指纹特征 =====================
    /**
     * 提取指纹特征（Aratek Bione® 格式）。
     * 采集同一手指 3 次，提取 3 个特征，供后续合成模板使用。
     * 实际业务中这就是"指纹注册"的第一步。
     */
    private static void step5_extractFeature() {
        section("步骤5：提取指纹特征（3 次采集，Aratek Bione®）");

        // 第一次采集 + 提特征
        System.out.println("  -> 第 1/3 次采集，请放手指...");
        captureAndExtract("feature1", true);

        // 第二次采集 + 提特征
        System.out.println("  -> 第 2/3 次采集，请再次放手指...");
        captureAndExtract("feature2", false);

        // 第三次采集 + 提特征
        System.out.println("  -> 第 3/3 次采集，请再次放手指...");
        captureAndExtract("feature3", false);
    }

    /**
     * 采集一次 + 提特征，保存到对应字段和文件。
     *
     * @param fieldName 特征字段名（feature1/feature2/feature3），用于存字段和文件
     * @param first    是否点亮 LED（仅第一次采集时亮，后续静默）
     */
    private static void captureAndExtract(String fieldName, boolean first) {
        if (first) {
            setLed(true);
        }
        CaptureDataResult c = sdk.captureRawData(deviceHandle, TIMEOUT, imageWidth, imageHeight);
        check(c, "采集");
        FeatureResult f = sdk.extractFeature(deviceHandle, FINGER_POS);
        check(f, "提特征");
        byte[] data = Results.copyBytes(f.getFeatureData());
        FileUtil.writeFile(data, OUTPUT_DIR, fieldName + ".template");
        // 存入对应静态字段
        if ("feature1".equals(fieldName)) {
            feature1 = data;
        } else if ("feature2".equals(fieldName)) {
            feature2 = data;
        } else if ("feature3".equals(fieldName)) {
            feature3 = data;
        }
        System.out.println("  -> " + fieldName + " 长度：" + data.length + " 字节，已保存 " + fieldName + ".template");
    }

    // ===================== 步骤 6：合成指纹模板 =====================
    /**
     * 合成指纹模板（GeneralizeTemplate）。
     * 把同一手指的 3 个特征合成 1 个模板（1024 字节），实际业务中这就是"指纹注册"完成。
     * 模板可存库，后续验证时用现场采集的特征和库中模板做 1:1 比对。
     */
    private static void step6_generalizeTemplate() {
        section("步骤6：合成指纹模板（3 特征 → 1 模板）");
        FeatureTemplateResult gR = sdk.generalizeTemplate(deviceHandle, feature1, feature2, feature3);
        check(gR, "generalizeTemplate 合成模板");
        template = Results.copyBytes(gR.getFeatureTemplateData());
        FileUtil.writeFile(template, OUTPUT_DIR, "general_template.bin");
        System.out.println("  -> 合成模板长度：" + template.length + " 字节，已保存 general_template.bin");
        System.out.println("  -> [注册完成] 该模板可存库，用于后续 1:N 识别或 1:1 验证");
    }

    // ===================== 步骤 7：图像转换与质量评估 =====================
    /**
     * 演示 V3.3 新增能力：图像格式转换（WSQ 压缩、ISO/ANSI 标准格式）
     * 以及图像质量评估（imgQuality、getNFIQScore）。
     */
    private static void step7_convertAndQuality() {
        section("步骤7：图像格式转换与质量评估");

        // 6.1 原始图像转 WSQ 压缩格式
        WSQResult wsqR = sdk.rawToWSQ(imageRaw, imageWidth, imageHeight, DPI, BIT_RATE);
        check(wsqR, "rawToWSQ 转 WSQ 压缩");
        System.out.println("  -> WSQ 压缩后长度：" + wsqR.getLength() + " 字节（原始 " + imageRaw.length + " 字节）");

        // 6.2 图像质量评估
        QualityResult qR = sdk.imgQuality(imageBmp, imageWidth, imageHeight);
        check(qR, "imgQuality 图像质量评估");
        System.out.println("  -> 图像质量分：" + qR.getQuality() + "（越高越好）");

        // 6.3 NFIQ 质量评分
        QualityResult nfiqR = sdk.getNFIQScore(imageBmp, imageWidth, imageHeight);
        check(nfiqR, "getNFIQScore NFIQ 评分");
        System.out.println("  -> NFIQ 评分：" + nfiqR.getQuality() + "（0-50，越低越好）");
    }

    // ===================== 步骤 8：1:1 指纹验证 =====================
    /**
     * 1:1 指纹验证。
     * 现场采集一次手指 → 提取特征 → 与步骤6合成的模板做 verify 比对。
     * 这就是"验证身份"的标准流程：现场特征 vs 库中模板。
     */
    private static void step8_verify() {
        section("步骤8：1:1 指纹验证（现场特征 vs 合成模板）");

        // 现场采集一次手指，提特征
        System.out.println("  -> 请放手指进行验证采集...");
        setLed(true);
        CaptureDataResult c = sdk.captureRawData(deviceHandle, TIMEOUT, imageWidth, imageHeight);
        check(c, "验证采集");
        FeatureResult f = sdk.extractFeature(deviceHandle, FINGER_POS);
        check(f, "验证提特征");
        byte[] liveFeature = Results.copyBytes(f.getFeatureData());
        setLed(false);

        System.out.println("  -> 安全等级：" + SECURITY_LEVEL);
        System.out.println("  -> 现场特征长度：" + liveFeature.length + " 字节");
        System.out.println("  -> 合成模板长度：" + template.length + " 字节");

        // Verify：现场特征 vs 合成模板
        VerifyResult vR = sdk.verify(deviceHandle, SECURITY_LEVEL, liveFeature, template);
        check(vR, "verify 1:1 验证");
        int match = vR.getpMmatch();
        int similarity = vR.getPnSimilarity();
        System.out.println("  -> 匹配结果：" + (match == 1 ? "匹配" : "不匹配") + " (1=匹配, 0=不匹配)");
        System.out.println("  -> 相似度：" + similarity + "（0-1000，越高越相似）");
        System.out.println("  -> 结论：" + (match == 1 ? "✅ 身份验证通过" : "❌ 验证失败，请重试"));
    }

    // ===================== 步骤 9：关闭设备、释放 SDK =====================
    /**
     * 资源清理：关闭设备句柄、释放 SDK 运行时资源。
     * 必须在 finally 中调用，确保异常时也能释放。
     */
    private static void step9_closeAndFree() {
        section("步骤9：关闭设备、释放 SDK");
        try {
            BaseResult closeR = sdk.closeDevice(new long[]{deviceHandle});
            if (closeR.getCode() == OK) {
                System.out.println("  -> 设备已关闭");
            } else {
                System.out.println("  -> 关闭设备返回：" + closeR.getMsg());
            }
        } catch (Exception e) {
            System.out.println("  -> 关闭设备异常：" + e.getMessage());
        }
        try {
            BaseResult freeR = sdk.globalFree();
            if (freeR.getCode() == OK) {
                System.out.println("  -> SDK 已释放");
            }
        } catch (Exception e) {
            System.out.println("  -> 释放 SDK 异常：" + e.getMessage());
        }
    }

    // ===================== 工具方法 =====================

    /** LED 指示灯控制（设备不支持 LED 时静默跳过） */
    private static void setLed(boolean on) {
        try {
            sdk.setLedStatus(deviceHandle, 0, on ? 1 : 0);
        } catch (Exception ignore) {
            // 部分设备无 LED，忽略
        }
    }

    /** 校验返回码，非 0 时打印错误并抛出异常终止流程 */
    private static void check(BaseResult r, String action) {
        if (r == null) {
            System.out.println("  [错误] " + action + " 返回 null");
            throw new RuntimeException(action + " 返回 null");
        }
        if (r.getCode() != OK) {
            int code = r.getCode();
            String msg = r.getMsg();
            // 查一下错误码含义，便于排查
            ErrorInfoResult infoR = sdk.getErrorInfo(code);
            String detail = infoR != null ? infoR.getErrorInfo() : msg;
            System.out.println("  [错误] " + action + " 失败：code=" + code + ", msg=" + msg);
            if (detail != null && !detail.isEmpty()) {
                System.out.println("         错误详情：" + detail);
            }
            throw new RuntimeException(action + " 失败，code=" + code);
        }
    }

    /** 打印分节标题 */
    private static void section(String title) {
        System.out.println();
        System.out.println("---------------------- " + title + " ----------------------");
    }

    /** 十六进制打印前 n 字节（调试用） */
    private static String hex(byte[] data, int n) {
        if (data == null) return "null";
        int len = Math.min(data.length, n);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) {
            sb.append(String.format("%02X ", data[i] & 0xFF));
        }
        if (data.length > len) sb.append("...");
        return sb.toString();
    }
}
