package cn.cerc.db.other;

import cn.cerc.core.DataSet;
import cn.cerc.core.Record;
import cn.cerc.core.Utils;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Slf4j
@Deprecated
public class utils {

    @Deprecated // 已移至 Utils
    public static final String vbCrLf = "\r\n";

    @Deprecated // 请改使用 Utils.roundTo
    public static double roundTo(double val, int scale) {
        return Utils.roundTo(val, scale);
    }

    @Deprecated // 已移至 Utils
    public static int random(int value) {
        return Utils.random(value);
    }

    @Deprecated // 已移至 Utils
    public static int pos(String sub, String text) {
        return Utils.pos(sub, text);
    }

    @Deprecated // 已移至 Utils
    public static String intToStr(int value) {
        return Utils.intToStr(value);
    }

    @Deprecated // 已移至 Utils
    public static String intToStr(double value) {
        return Utils.intToStr(value);
    }

    @Deprecated // 已移至 Utils
    public static int strToIntDef(String str, int def) {
        return Utils.strToIntDef(str, def);
    }

    @Deprecated // 改为使用 Utils.strToDoubleDef
    public static double strToFloatDef(String str, double def) {
        return Utils.strToDoubleDef(str, def);
    }

    @Deprecated // 已移至 Utils
    public static double strToDoubleDef(String str, double def) {
        return Utils.strToDoubleDef(str, def);
    }

    @Deprecated // 已移至 Utils
    public static String floatToStr(Double value) {
        return Utils.floatToStr(value);
    }

    @Deprecated // 已移至 Utils
    public static String newGuid() {
        return Utils.newGuid();
    }

    /**
     * 保障查询安全，防范注入攻击 (已停用，请改使用cn.cerc.jdb.core.Utils.safeString)
     *
     * @param value 用户输入值
     * @return 经过处理后的值
     */
    @Deprecated
    public static String safeString(String value) {
        return Utils.safeString(value);
    }

    @Deprecated // 已移至 Utils
    public static String copy(String text, int iStart, int iLength) {
        return Utils.copy(text, iStart, iLength);
    }

    @Deprecated // 已移至 Utils
    public static String replace(String text, String sub, String rpl) {
        return Utils.replace(text, sub, rpl);
    }

    @Deprecated // 已移至 Utils
    public static String trim(String str) {
        return Utils.trim(str);
    }

    @Deprecated // 已移至 Utils
    // 取得大于等于X的最小的整数，即：进一法
    public static int ceil(double val) {
        return Utils.ceil(val);
    }

    @Deprecated // 已移至 Utils
    // 取得X的整数部分，即：去尾法
    public static double trunc(double val) {
        return Utils.trunc(val);
    }

    @Deprecated
    public static String iif(boolean flag, String val1, String val2) {
        return Utils.iif(flag, val1, val2);
    }

    @Deprecated
    public static double iif(boolean flag, double val1, double val2) {
        return Utils.iif(flag, val1, val2);
    }

    @Deprecated
    public static int iif(boolean flag, int val1, int val2) {
        return Utils.iif(flag, val1, val2);
    }

    @Deprecated // 已移至 Utils
    public static int round(double d) {
        return Utils.round(d);
    }

    /**
     * @param text=要检测的文本
     * @return 判断字符串是否全部为数字
     */
    @Deprecated // 已移至 Utils
    public static boolean isNumeric(String text) {
        return Utils.isNumeric(text);
    }

    @Deprecated // 已移至 Utils
    public static boolean assigned(Object object) {
        return Utils.assigned(object);
    }

    @Deprecated // 已移至 Utils
    public static String isNull(String text, String def) {
        return Utils.isNull(text, def);
    }

    @Deprecated // 已移至 Utils
    public static String formatFloat(String fmt, double value) {
        return Utils.formatFloat(fmt, value);
    }

    @Deprecated // 已移至 Utils
    // 转成指定类型的对象
    public static <T> T recordAsObject(Record record, Class<T> clazz) {
        return Utils.recordAsObject(record, clazz);
    }

    @Deprecated // 已移至 Utils
    public static <T> void objectAsRecord(Record record, T object) {
        Utils.objectAsRecord(record, object);
    }

    @Deprecated // 已移至 Utils
    // 将内容转成 Map
    public static <T> Map<String, T> dataSetAsMap(DataSet dataSet, Class<T> clazz, String... keys) {
        return Utils.dataSetAsMap(dataSet, clazz, keys);
    }

    @Deprecated // 已移至 Utils
    // 将内容转成 List
    public static <T> List<T> dataSetAsList(DataSet dataSet, Class<T> clazz) {
        return Utils.dataSetAsList(dataSet, clazz);
    }

    @Deprecated // 已移至 Utils
    // 混淆字符串指定位置
    public static String confused(String mobile, int fromLength, int endLength) {
        return Utils.confused(mobile, fromLength, endLength);
    }

    @Deprecated // 请改使用 RemoteAddress.get
    // 获取最终访问者的ip地址
    public static String getRemoteAddr(HttpServletRequest request) {
        return RemoteAddress.get(request);
    }

    @Deprecated // 已移至 Utils
    public static String guidFixStr() {
        return Utils.guidFixStr();
    }

    @Deprecated // 已移至 Utils
    // 获取数字和字母的混合字符串
    public static String getStrRandom(int length) {
        return Utils.getStrRandom(length);
    }

    // 创建指定长度的随机数
    @Deprecated // 已移至 Utils
    public static String getNumRandom(int len) {
        return Utils.getNumRandom(len);
    }
}
