package cn.cerc.ui.fields;

public interface IFieldTextArea {

    int getMaxlength();

    Object setMaxlength(int maxlength);

    int getRows();

    Object setRows(int rows);

    int getCols();

    Object setCols(int cols);

    boolean isResize();

    Object setResize(boolean resize);

}
