package cn.cerc.mis.other;

import cn.cerc.core.ClassResource;
import cn.cerc.db.cache.Buffer;
import cn.cerc.mis.SummerMIS;

public class MemoryBuffer extends Buffer implements AutoCloseable {

    private static final ClassResource res = new ClassResource(MemoryBuffer.class, SummerMIS.ID);

    public MemoryBuffer(Enum<?> bufferType, String... keys) {
        super();
        this.setKey(buildKey(bufferType, keys));
    }

    public static void delete(Enum<?> bufferType, String... keys) {
        Buffer buff = new Buffer(buildKey(bufferType, keys));
        buff.clear();
    }

    public static String buildKey(Enum<?> bufferType, String... keys) {
        if (keys == null || keys.length == 0) {
            throw new RuntimeException(res.getString(1, "[MemoryBuffer]错误的初始化参数！"));
        }

        if (keys.length == 1 && keys[0] == null) {
            throw new RuntimeException(res.getString(2, "传值有误！"));
        }

        StringBuffer str = new StringBuffer();
        str.append(bufferType.ordinal());
        for (String key : keys) {
            str.append(".").append(key);
        }
        return str.toString();
    }

    @Override
    public final void close() {
        this.post();
    }
}
