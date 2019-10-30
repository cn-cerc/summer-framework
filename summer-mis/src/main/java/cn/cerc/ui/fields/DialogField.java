package cn.cerc.ui.fields;

import java.util.ArrayList;
import java.util.List;

public class DialogField {
    private List<String> params = new ArrayList<>();
    private String inputId;
    private String dialogfun;
    private boolean show = true;

    public DialogField(String dialogfun) {
        this.dialogfun = dialogfun;
    }

    public String getUrl() {
        if (dialogfun == null) {
            throw new RuntimeException("dialogfun is null");
        }

        StringBuilder build = new StringBuilder();
        build.append("javascript:");
        build.append(dialogfun);
        build.append("(");

        build.append("'");
        build.append(inputId);
        build.append("'");
        if (params.size() > 0) {
            build.append(",");
        }

        int i = 0;
        for (String param : params) {
            build.append("'");
            build.append(param);
            build.append("'");
            if (i != params.size() - 1) {
                build.append(",");
            }
            i++;
        }
        build.append(")");

        return build.toString();
    }

    public List<String> getParams() {
        return params;
    }

    public DialogField add(String param) {
        params.add(param);
        return this;
    }

    public String getDialogfun() {
        return dialogfun;
    }

    public DialogField setDialogfun(String dialogfun) {
        this.dialogfun = dialogfun;
        return this;
    }

    public String getInputId() {
        return inputId;
    }

    public DialogField setInputId(String inputId) {
        this.inputId = inputId;
        return this;
    }

    public DialogField close() {
        this.show = false;
        return this;
    }

    public boolean isOpen() {
        return show;
    }

    public static void main(String[] args) {
        DialogField obj = new DialogField("showVipInfo");
        obj.setInputId("inputid");
        obj.add("1");
        obj.add("2");
        obj.add("3");
        System.out.println(obj.getUrl());
    }

}
