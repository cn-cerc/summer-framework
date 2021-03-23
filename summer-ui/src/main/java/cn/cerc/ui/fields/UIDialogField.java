package cn.cerc.ui.fields;

import java.util.ArrayList;
import java.util.List;

import cn.cerc.core.ClassConfig;
import cn.cerc.mis.cdn.CDN;
import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.parts.UIComponent;

public class UIDialogField extends UIComponent {
    private List<String> params = new ArrayList<>();
    private String inputId;
    private String dialogFunc;
    private boolean show = true;
    private String icon;
    private ClassConfig config;

    public UIDialogField(String dialogFunc) {
        this.dialogFunc = dialogFunc;
    }

    @Override
    public void output(HtmlWriter html) {
        html.print("<a href=\"%s\">", this.getUrl());
        if (this.getIcon() != null) {
            html.print("<img src=\"%s\">", CDN.get(this.getIcon()));
        } else if (config != null) {
            html.print("<img src=\"%s\">", CDN.get(config.getClassProperty("icon", "")));
        }else {
            html.print("(*)");
        }
        html.print("</a>");
        return;
    }

    public String getUrl() {
        if (dialogFunc == null) {
            throw new RuntimeException("dialogFunc is null");
        }

        StringBuilder build = new StringBuilder();
        build.append("javascript:");
        build.append(dialogFunc);
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

    public UIDialogField add(String param) {
        params.add(param);
        return this;
    }

    public String getDialogfun() {
        return dialogFunc;
    }

    public UIDialogField setDialogfun(String dialogfun) {
        this.dialogFunc = dialogfun;
        return this;
    }

    public String getInputId() {
        return inputId;
    }

    public UIDialogField setInputId(String inputId) {
        this.inputId = inputId;
        return this;
    }

    public UIDialogField close() {
        this.show = false;
        return this;
    }

    public boolean isOpen() {
        return show;
    }

    public static void main(String[] args) {
        UIDialogField obj = new UIDialogField("showVipInfo");
        obj.setInputId("inputid");
        obj.add("1");
        obj.add("2");
        obj.add("3");
        System.out.println(obj.getUrl());
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public ClassConfig getConfig() {
        return config;
    }

    public UIDialogField setConfig(ClassConfig config) {
        this.config = config;
        return this;
    }

}
