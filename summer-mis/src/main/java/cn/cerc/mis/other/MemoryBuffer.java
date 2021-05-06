package cn.cerc.mis.other;

import cn.cerc.core.ClassResource;
import cn.cerc.core.Utils;
import cn.cerc.db.cache.Buffer;
import cn.cerc.mis.SummerMIS;
import cn.cerc.mis.core.IBufferKey;
import cn.cerc.mis.core.SystemBuffer;

public class MemoryBuffer extends Buffer implements AutoCloseable {

    private static final ClassResource res = new ClassResource(MemoryBuffer.class, SummerMIS.ID);

    public MemoryBuffer(Enum<?> bufferType, String... keys) {
        super();
        this.setKey(buildKey(bufferType, keys));
    }

    public static void delete(Enum<?> bufferType, String... keys) {
        Buffer buffer = new Buffer(buildKey(bufferType, keys));
        buffer.clear();
    }

    public static String buildKey(Enum<?> bufferType, String... keys) {
        if (!(bufferType instanceof IBufferKey)) {
            throw new RuntimeException(res.getString(1, "错误的初始化参数！"));
        }

        IBufferKey bufferKey = (IBufferKey) bufferType;

        if (keys.length < bufferKey.getMinimumNumber()) {
            throw new RuntimeException(res.getString(3, "参数数量不足！"));
        }

        if (keys.length > bufferKey.getMaximumNumber()) {
            throw new RuntimeException(res.getString(4, "参数数量过多！"));
        }

        StringBuffer result = new StringBuffer();

        result.append(bufferKey.getStartingPoint() + bufferType.ordinal());

        for (String key : keys) {
            if (key == null)
                throw new RuntimeException(res.getString(2, "传值有误！"));
            result.append(".").append(key);
        }

        return result.toString();
    }

    @Override
    public final void close() {
        this.post();
    }

    public static String buildObjectKey(Class<?> class1) {
        return String.format("%d.%s", SystemBuffer.UserObject.ClassName.ordinal(), class1.getName());
    }

    public static String buildObjectKey(Class<?> class1, int version) {
        return String.format("%d.%s.%d", SystemBuffer.UserObject.ClassName.ordinal(), class1.getName(), version);
    }

    public static String buildObjectKey(Class<?> class1, String userCode) {
        if (Utils.isEmpty(userCode))
            throw new RuntimeException("userCode is empty");
        return String.format("%d.%s.%s", SystemBuffer.UserObject.ClassName.ordinal(), class1.getName(), userCode);
    }

    public static String buildObjectKey(Class<?> class1, String userCode, int version) {
        if (Utils.isEmpty(userCode))
            throw new RuntimeException("userCode is empty");
        return String.format("%d.%s.%s.%d", SystemBuffer.UserObject.ClassName.ordinal(), class1.getName(), userCode,
                version);
    }

    public static void main(String[] args) {
        System.out.println(buildObjectKey(MemoryBuffer.class));
        System.out.println(buildObjectKey(MemoryBuffer.class, 1));
        System.out.println(buildObjectKey(MemoryBuffer.class, "admin"));
        System.out.println(buildObjectKey(MemoryBuffer.class, "admin", 1));
    }
}
