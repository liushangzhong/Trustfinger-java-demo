package com.aratek.jna.trustfinger.pojo;

import java.util.HashMap;
import java.util.Map;

/**
 * @author shijinlong
 * @date 2018-11-28
 */
public class ResultCodeMap extends HashMap<Integer, String> {

    private static final ResultCodeMap INSTANCE = new ResultCodeMap();

    static {
        INSTANCE.put(0, "成功");
        INSTANCE.put(10, "参数内存分配为空");
        INSTANCE.put(11, "实时采集结束");
        INSTANCE.put(-100, "设备不存在。可能设备未接入或者驱动程序未安装导致");
        INSTANCE.put(-101, "设备未授权");
        INSTANCE.put(-102, "环境未初始化");
        INSTANCE.put(-103, "设备未打开");
        INSTANCE.put(-104, "设备不支持");
        INSTANCE.put(-106, "获取/设置LED灯状态失败");
        INSTANCE.put(-110, "准备采集一帧图像失败，当前正在获取图像中");
        INSTANCE.put(-111, "未调用准备一帧图像");
        INSTANCE.put(-112, "获取图像失败");
        INSTANCE.put(-113, "获取图像超时");
        INSTANCE.put(-114, "设备被其它进程或线程占用");
        INSTANCE.put(-115, "SDK已经初始化");
        INSTANCE.put(-117, "设备已经打开");
        INSTANCE.put(-118, "获取设备信息失败");
        INSTANCE.put(-119, "设备通讯失败");
        INSTANCE.put(-120, "设备通讯协议不支持");
        INSTANCE.put(-121, "指令错误");
        INSTANCE.put(-122, "编码失败");
        INSTANCE.put(-123, "解码失败");
        INSTANCE.put(-124, "设备太干");
        INSTANCE.put(-125, "设备太湿");
        INSTANCE.put(-126, "特征太少");
        INSTANCE.put(-127, "实时采集运行中");
        INSTANCE.put(-128, "实时采集未开始");
        INSTANCE.put(-129, "活体检测失败");
        INSTANCE.put(-130, "假指纹");
        INSTANCE.put(-131, "未知指纹");
        INSTANCE.put(-132, "无指纹");
        INSTANCE.put(-200, "初始化指纹算法失败");
        INSTANCE.put(-201, "无WSQ算法库");
        INSTANCE.put(-202, "原始图像压缩成WSQ格式失败");
        INSTANCE.put(-203, "无NFIQ算法库");
        INSTANCE.put(-204, "原始图像转ISO格式失败");
        INSTANCE.put(-205, "原始图像转ANSI格式失败");
        INSTANCE.put(-206, "原始图像转JPG格式失败");
        INSTANCE.put(-207, "无JPG算法库");
        INSTANCE.put(-210, "图像缓冲区没有有效指纹数据");
        INSTANCE.put(-211, "图像质量不满足");
        INSTANCE.put(-212, "不是有效的BMP数据");
        INSTANCE.put(-220, "算法库未初始化");
        INSTANCE.put(-221, "提取指纹特征失败 ");
        INSTANCE.put(-222, "生成指纹模板失败");
        INSTANCE.put(-230, "指纹比对失败");
        INSTANCE.put(-900, "参数错误");
        INSTANCE.put(-901, "内存分配失败，没有分配到足够的内存");
        INSTANCE.put(-902, "功能未实现");
        INSTANCE.put(-903, "非法的错误");
        INSTANCE.put(-904, "固件错误");
        INSTANCE.put(-905, "初始化失败");
        INSTANCE.put(-910, "其它错误");
    }

    private ResultCodeMap() {

    }

    public static ResultCodeMap getInstance() {
        return INSTANCE;
    }


    public static void main(String[] args) {
        Map testMap = new HashMap<Integer, String>();
        testMap.put(-1, "-1");
        testMap.put(0, "0");
        testMap.put(1, "1");
        System.out.println(testMap.get(-1));
        System.out.println(testMap.get(0));
        System.out.println(testMap.get(1));
        System.out.println(ResultCodeMap.getInstance().get(-114));
    }

}
