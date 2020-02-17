package cn.cerc.ui.core;

public interface IColumn extends IField {

    String format(Object value);

    default int getWidth() {
        return 1;
    }
}
