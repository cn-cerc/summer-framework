package cn.cerc.ui.fields;

public interface IDialogFieldOwner {
    DialogField getDialog();

    AbstractField setDialog(String dialogfun);

    AbstractField setDialog(String dialogfun, String[] params);
}
