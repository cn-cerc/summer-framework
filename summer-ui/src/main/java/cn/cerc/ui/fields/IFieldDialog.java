package cn.cerc.ui.fields;

public interface IFieldDialog {
    UIDialogField getDialog();

    Object setDialog(String dialogfun);

    @Deprecated
    default Object setDialog(String dialogfun, String[] params) {
        setDialog(dialogfun);

        UIDialogField dialog = getDialog();
        for (String string : params) {
            dialog.add(string);
        }

        return this;
    }

}
