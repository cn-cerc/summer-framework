package cn.cerc.mis.other;

import cn.cerc.core.ClassResource;
import cn.cerc.db.cache.Buffer;

public class MemoryBuffer extends Buffer implements AutoCloseable {

    private static final ClassResource res = new ClassResource("summer-mvc", MemoryBuffer.class);

    public MemoryBuffer(BufferType bt, String... keys) {
        super();
        this.setKey(buildKey(bt, keys));
    }

    public static void delete(BufferType bt, String... keys) {
        Buffer buff = new Buffer(buildKey(bt, keys));
        buff.clear();
    }

    public static String buildKey(BufferType bt, String... keys) {
        if (keys == null || keys.length == 0) {
            throw new RuntimeException(res.getString(1, "[MemoryBuffer]错误的初始化参数！"));
        }

        if (keys.length == 1 && keys[0] == null) {
            throw new RuntimeException(res.getString(2, "传值有误！"));
        }

        StringBuffer str = new StringBuffer();
        str.append(bt.ordinal());
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
