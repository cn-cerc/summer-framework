package cn.cerc.mis.core;

public interface IOption {

    String getTitle();

    default String getKey() {
        String[] items = this.getClass().getName().split("\\.");
        return items[items.length - 1];
    }

    default String getDefault() {
        return null;
    }
}
