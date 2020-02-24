package cn.cerc.ui.core;

public interface IColumn extends IField {

    String format(Object value);

    @Override
    default int getWidth() {
        return 1;
    }
}
