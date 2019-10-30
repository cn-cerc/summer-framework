package cn.cerc.ui.core;

public interface IColumn extends IField {

    public String format(Object value);

    default public int getWidth() {
        return 1;
    }
}
