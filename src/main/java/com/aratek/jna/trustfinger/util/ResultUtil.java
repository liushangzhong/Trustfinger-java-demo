//package com.aratek.jna.trustfinger.util;
//
//import com.aratek.jna.trustfinger.pojo.ResultCodeMap;
//
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * @author shijinlong
// * @date 2018-11-28
// */
//public class ResultUtil extends HashMap<String, Object> {
//
//    private static final int CODE_OTHER_ERROR = 99999;
//
//    public ResultUtil() {
//        this.put("code", 0);
//        this.put("message", "成功");
//    }
//
//    public static ResultUtil result(int code) {
//        String msg = ResultCodeMap.getInstance().get(code);
//        if (StringUtil.isBlank(msg)) {
//            return error(CODE_OTHER_ERROR, "未知错误");
//        } else {
//            return error(code, msg);
//        }
//    }
//
//    public static ResultUtil error() {
//        return error(500, "未知错误");
//    }
//
//    public static ResultUtil error(int code) {
//        String msg = ResultCodeMap.getInstance().get(code);
//        if (StringUtil.isBlank(msg)) {
//            return error(CODE_OTHER_ERROR, "未知错误");
//        } else {
//            return error(code, msg);
//        }
//    }
//
//    public static ResultUtil error(int code, String msg) {
//        ResultUtil r = new ResultUtil();
//        r.put("code", code);
//        r.put("message", msg);
//        return r;
//    }
//
//    public static ResultUtil ok(String msg) {
//        ResultUtil r = new ResultUtil();
//        r.put("message", msg);
//        return r;
//    }
//
//    public static ResultUtil ok(Map<String, Object> map) {
//        ResultUtil r = new ResultUtil();
//        r.putAll(map);
//        return r;
//    }
//
//    public static ResultUtil ok() {
//        return new ResultUtil();
//    }
//
//    public ResultUtil put(String key, Object value) {
//        super.put(key, value);
//        return this;
//    }
//
//    public ResultUtil putData(Object value) {
//        super.put("data", value);
//        return this;
//    }
//
//}
