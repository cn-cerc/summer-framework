package cn.cerc.ui.fields;

public interface IFieldDialog {
    DialogField getDialog();

    String getIcon();

    Object setDialog(String dialogfun);

    @Deprecated
    default Object setDialog(String dialogfun, String[] params) {
        setDialog(dialogfun);
        
        DialogField dialog = getDialog();
        for (String string : params) {
            dialog.add(string);
        }
        
        return this;
    }

    Object setIcon(String icon);
}
