package cn.cerc.core;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Transient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Utils {
    private static final Logger log = LoggerFactory.getLogger(Utils.class);
    public static final String vbCrLf = "\r\n";

    /**
     * 保障查询安全，防范注入攻击
     * 
     * @param value 用户输入值
     * @return 经过处理后的值
     */
    public static String safeString(String value) {
        return value == null ? "" : value.replaceAll("'", "''");
    }

    public static String serializeToString(Object obj) throws IOException {
        if (obj == null)
            return null;
        ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
        ObjectOutputStream objOut = new ObjectOutputStream(byteOut);
        objOut.writeObject(obj);
        return byteOut.toString("ISO-8859-1");// 此处只能是ISO-8859-1,但是不会影响中文使用;
    }

    public static Object deserializeToObject(String str) throws IOException, ClassNotFoundException {
        if (str == null)
            return null;
        ByteArrayInputStream byteIn = new ByteArrayInputStream(str.getBytes("ISO-8859-1"));
        ObjectInputStream objIn = new ObjectInputStream(byteIn);
        return objIn.readObject();
    }

    public static String encode(Object obj) {
        if (obj == null)
            return null;
        try {
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            ObjectOutputStream objOut = new ObjectOutputStream(byteOut);
            objOut.writeObject(obj);
            return byteOut.toString("ISO-8859-1");// 此处只能是ISO-8859-1,但是不会影响中文使用;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object decode(String str) {
        if (str == null)
            return null;
        try {
            ByteArrayInputStream byteIn = new ByteArrayInputStream(str.getBytes("ISO-8859-1"));
            ObjectInputStream objIn = new ObjectInputStream(byteIn);
            return objIn.readObject();
        } catch (ClassNotFoundException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static double roundTo(double val, int scale) {
        if (scale <= 0) {
            String str = "0.000000000000";
            str = str.substring(0, str.indexOf(".") - scale + 1);
            DecimalFormat df = new DecimalFormat(str);
            return Double.parseDouble(df.format(val));
        } else {
            String str = val + "";
            int pointPosition = str.indexOf(".");
            String tempStr = str.substring(0, pointPosition - scale + 1);
            Double tempD = Double.parseDouble(tempStr) / 10;
            int tempInt = Math.round(tempD.floatValue());
            double ret = tempInt * Math.pow(10, scale);
            return ret;
        }
    }

    // 兼容 delphi 代码
    public static int pos(String sub, String text) {
        return text.indexOf(sub) + 1;
    }

    // 兼容 delphi 代码
    public static String intToStr(int value) {
        return "" + value;
    }

    // 兼容 delphi 代码
    public static String intToStr(double value) {
        return "" + value;
    }

    // 兼容 delphi 代码
    public static int strToIntDef(String str, int def) {
        int result;
        try {
            result = Integer.parseInt(str);
        } catch (Exception e) {
            result = def;
        }
        return result;
    }

    // 兼容 delphi 代码
    public static double strToDoubleDef(String str, double def) {
        double result;
        try {
            result = Double.parseDouble(str);
        } catch (Exception e) {
            result = def;
        }
        return result;
    }

    // 兼容 delphi 代码
    public static String floatToStr(Double value) {
        return value + "";
    }

    // 兼容 delphi 代码
    public static String newGuid() {
        UUID uuid = UUID.randomUUID();
        return '{' + uuid.toString() + '}';
    }

    // 兼容 delphi 代码
    public static String copy(String text, int iStart, int iLength) {
        if (text == null)
            return "";
        if (text != null && iLength >= text.length()) {
            if (iStart > text.length()) {
                return "";
            }
            if (iStart - 1 < 0) {
                return "";
            }
            return text.substring(iStart - 1, text.length());
        } else if (text.equals("")) {
            return "";
        }
        return text.substring(iStart - 1, iStart - 1 + iLength);
    }

    // 兼容 delphi 代码
    public static String replace(String text, String sub, String rpl) {
        return text.replace(sub, rpl);
    }

    // 兼容 delphi 代码
    public static String trim(String str) {
        return str.trim();
    }

    // 兼容 delphi 代码
    // 取得大于等于X的最小的整数，即：进一法
    public static int ceil(double val) {
        int result = (int) val;
        return (val > result) ? result + 1 : result;
    }

    // 兼容 delphi 代码
    // 取得X的整数部分，即：去尾法
    public static double trunc(double val) {
        return (int) val;
    }

    // 兼容 delphi 代码
    /**
     * @param text=要检测的文本
     * @return 判断字符串是否全部为数字
     */
    public static boolean isNumeric(String text) {
        if (text == null)
            return false;
        if (".".equals(text))
            return false;
        return text.matches("[0-9,.]*");
    }

    // 兼容 delphi 代码
    public static boolean assigned(Object object) {
        return object != null;
    }

    // 兼容 delphi 代码
    public static String isNull(String text, String def) {
        // 判断是否为空如果为空就返回。
        return text.equals("") ? def : text;
    }

    // 兼容 delphi 代码
    public static String formatFloat(String fmt, double value) {
        DecimalFormat df = new DecimalFormat(fmt);
        fmt = df.format(value);
        return fmt;
    }

    // 创建指定长度的随机数
    public static String getNumRandom(int len) {
        Random random = new Random();
        String verify = "";
        for (int i = 0; i < len; i++) {
            verify = verify + random.nextInt(10);
        }
        return verify;
    }

    // 转成指定类型的对象
    public static <T> T recordAsObject(Record record, Class<T> clazz) {
        T obj;
        try {
            obj = clazz.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException e) {
            throw new RuntimeException(e.getMessage());
        }
        for (Field method : clazz.getDeclaredFields()) {
            if (method.getAnnotation(Transient.class) != null)
                continue;
            Column column = method.getAnnotation(Column.class);
            String dbField = method.getName();
            String field = method.getName().substring(0, 1).toUpperCase() + method.getName().substring(1);
            if (column != null && !"".equals(column.name()))
                dbField = column.name();
            if (record.exists(dbField)) {
                try {
                    if (method.getType().equals(Integer.class)) {
                        Integer value = record.getInt(dbField);
                        Method set = clazz.getMethod("set" + field, value.getClass());
                        set.invoke(obj, value);
                    } else if (method.getType().equals(int.class)) {
                        int value = record.getInt(dbField);
                        Method set = clazz.getMethod("set" + field, int.class);
                        set.invoke(obj, value);

                    } else if ((method.getType().equals(Double.class))) {
                        Double value = record.getDouble(dbField);
                        Method set = clazz.getMethod("set" + field, value.getClass());
                        set.invoke(obj, value);
                    } else if ((method.getType().equals(double.class))) {
                        double value = record.getDouble(dbField);
                        Method set = clazz.getMethod("set" + field, double.class);
                        set.invoke(obj, value);

                    } else if ((method.getType().equals(Long.class))) {
                        Double value = record.getDouble(dbField);
                        Method set = clazz.getMethod("set" + field, value.getClass());
                        set.invoke(obj, value);
                    } else if ((method.getType().equals(long.class))) {
                        long value = (long) record.getDouble(dbField);
                        Method set = clazz.getMethod("set" + field, long.class);
                        set.invoke(obj, value);

                    } else if (method.getType().equals(Boolean.class)) {
                        Boolean value = record.getBoolean(dbField);
                        Method set = clazz.getMethod("set" + field, value.getClass());
                        set.invoke(obj, value);
                    } else if (method.getType().equals(boolean.class)) {
                        boolean value = record.getBoolean(dbField);
                        Method set = clazz.getMethod("set" + field, boolean.class);
                        set.invoke(obj, value);

                    } else if (method.getType().equals(TDateTime.class)) {
                        TDateTime value = record.getDateTime(dbField);
                        Method set = clazz.getMethod("set" + field, value.getClass());
                        set.invoke(obj, value);
                    } else if (method.getType().equals(TDate.class)) {
                        TDate value = record.getDate(dbField);
                        Method set = clazz.getMethod("set" + field, value.getClass());
                        set.invoke(obj, value);
                    } else if (method.getType().equals(String.class)) {
                        String value = record.getString(dbField);
                        Method set = clazz.getMethod("set" + field, value.getClass());
                        set.invoke(obj, value);
                    } else {
                        log.warn(String.format("field:%s, other type:%s", field, method.getType().getName()));
                        String value = record.getString(dbField);
                        Method set = clazz.getMethod("set" + field, value.getClass());
                        set.invoke(obj, value);
                    }
                } catch (NoSuchMethodException | SecurityException | IllegalArgumentException
                        | InvocationTargetException | IllegalAccessException e) {
                    log.warn(e.getMessage());
                }
            }
        }
        return obj;
    }

    public static <T> void objectAsRecord(Record record, T object) {
        Class<?> clazz = object.getClass();
        for (Field method : clazz.getDeclaredFields()) {
            if (method.getAnnotation(Transient.class) != null)
                continue;
            GeneratedValue generatedValue = method.getAnnotation(GeneratedValue.class);
            if (generatedValue != null && generatedValue.strategy().equals(GenerationType.IDENTITY))
                continue;

            String field = method.getName();
            Column column = method.getAnnotation(Column.class);
            String dbField = field;
            if (column != null && !"".equals(column.name()))
                dbField = column.name();

            Method get;
            try {
                field = field.substring(0, 1).toUpperCase() + field.substring(1);
                get = clazz.getMethod("get" + field);
                Object value = get.invoke(object);
                record.setField(dbField, value);
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
                    | InvocationTargetException e) {
                // e.printStackTrace();
            }
        }
    }

    // 将内容转成 Map
    public static <T> Map<String, T> dataSetAsMap(DataSet dataSet, Class<T> clazz, String... keys) {
        Map<String, T> items = new HashMap<String, T>();
        for (Record rs : dataSet) {
            String key = "";
            for (String field : keys) {
                if ("".equals(key))
                    key = rs.getString(field);
                else
                    key += ";" + rs.getString(field);
            }
            items.put(key, recordAsObject(rs, clazz));
        }
        return items;
    }

    // 将内容转成 List
    public static <T> List<T> dataSetAsList(DataSet dataSet, Class<T> clazz) {
        List<T> items = new ArrayList<T>();
        for (Record rs : dataSet)
            items.add(recordAsObject(rs, clazz));
        return items;
    }

    // 混淆字符串指定位置
    public static String confused(String mobile, int fromLength, int endLength) {
        int length = mobile.length();
        if (length < (fromLength + endLength)) {
            throw new RuntimeException("字符串长度不符合要求");
        }
        int len = mobile.length() - fromLength - endLength;
        String star = "";
        for (int i = 0; i < len; i++) {
            star += "*";
        }
        return mobile.substring(0, fromLength) + star + mobile.substring(mobile.length() - endLength);
    }

    public static String guidFixStr() {
        String guid = newGuid();
        String str = guid.substring(1, guid.length() - 1);
        return str.replaceAll("-", "");
    }

    // 获取数字和字母的混合字符串
    public static String getStrRandom(int length) {
        String result = "";
        Random random = new Random();

        for (int i = 0; i < length; i++) {
            String symbol = random.nextInt(2) % 2 == 0 ? "char" : "num";

            if ("char".equalsIgnoreCase(symbol)) {
                // 随机获取大小写字母
                int letterIndex = random.nextInt(2) % 2 == 0 ? 65 : 97;
                result += (char) (random.nextInt(26) + letterIndex);
            } else if ("num".equalsIgnoreCase(symbol)) {
                result += String.valueOf(random.nextInt(10));
            }
        }
        return result;
    }

    // 兼容 delphi 代码
    public static int random(int value) {
        return (int) (Math.random() * value);
    }

    // 兼容 delphi 代码
    public static String iif(boolean flag, String val1, String val2) {
        return flag ? val1 : val2;
    }

    // 兼容 delphi 代码
    public static double iif(boolean flag, double val1, double val2) {
        return flag ? val1 : val2;
    }

    // 兼容 delphi 代码
    public static int iif(boolean flag, int val1, int val2) {
        return flag ? val1 : val2;
    }

    // 兼容 delphi 代码
    @Deprecated
    public static int round(double d) {
        return (int) Math.round(d);
    }
}
