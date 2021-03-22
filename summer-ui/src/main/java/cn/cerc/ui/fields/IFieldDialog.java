package cn.cerc.ui.fields;

public interface IFieldDialog {
    DialogField getDialog();

    String getIcon();

    Object setDialog(String dialogfun);

    Object setDialog(String dialogfun, String[] params);

    Object setIcon(String icon);
}
