package cn.cerc.ui.fields;

public interface IFieldDialog {
    DialogField getDialog();

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

}
