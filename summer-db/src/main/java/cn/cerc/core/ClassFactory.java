package cn.cerc.core;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ClassFactory {
    private static Map<Class<?>, ClassData> classes = new ConcurrentHashMap<>();

    public static ClassData get(Class<?> clazz) {
        ClassData info = classes.get(clazz);
        if (info == null) {
            info = new ClassData(clazz);
            classes.put(clazz, info);
        }
        return info;
    }

    public static Map<Class<?>, ClassData> getClasses() {
        return classes;
    }

    public static void printDebugInfo(Class<?> clazz) {
        ClassData data = ClassFactory.get(clazz);
        System.out.println("tableId:" + data.getTableId());
        System.out.println("select:" + data.getSelect());
        System.out.println();
        System.out.println("updateKey:" + data.getUpdateKey());
        for (String key : data.getSearchKeys()) {
            System.out.println("serachKey:" + key);
        }
        Field field = data.getGenerationIdentityField();
        if (field != null) {
            System.out.println("generationIdentityField:" + field.getName());
        }
        System.out.println();
        for (String key : data.getFields().keySet()) {
            System.out.println("field:" + key);
        }
    }
}
