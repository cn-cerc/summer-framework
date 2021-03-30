package cn.cerc.ui.core;

public interface IFormatColumn extends IField {

    String format(Object value);

    @Override
    default int getWidth() {
        return 1;
    }
}
