package cn.cerc.db.cache;

public class Buffer extends CacheQuery {

    public Buffer() {
        super();
    }

    public Buffer(Class<?> clazz) {
        super();
        this.setKey(clazz.getName());
    }

    public Buffer(Object... keys) {
        super();
        StringBuffer str = new StringBuffer();
        for (int i = 0; i < keys.length; i++) {
            if (i > 0) {
                str.append(".");
            }
            str.append(keys[i]);
        }
        setKey(str.toString());
    }

}
