package cn.cerc.ui.parts;

import java.util.HashMap;
import java.util.Map;

import cn.cerc.ui.core.Component;
import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.vcl.UIGroupBox;

public class UIActionForm extends UIComponent {
    private String action;
    private String method = "post";
    private Map<String, String> items = new HashMap<>();
    private String enctype;

    public UIActionForm() {
        super(null);
    }

    public UIActionForm(UIGroupBox owner) {
        super.setOwner(owner);
    }

    public UIActionForm(UIContent owner) {
        super.setOwner(owner);
    }

    @Deprecated
    public UIActionForm(UIContent owner, String id) {
        super(owner);
        this.setId(id);
    }

    @Override
    @Deprecated
    public void setOwner(Component owner) {
        super.setOwner(owner);
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    @Override
    public void output(HtmlWriter html) {
        outHead(html);
        super.output(html);
        outFoot(html);
    }

    /**
     * 输入头部
     * 
     * @param html 输出器
     */
    public void outHead(HtmlWriter html) {
        html.print("<form");
        if (this.action != null)
            html.print(" action=\"%s\"", this.action);
        if (this.method != null)
            html.print(" method=\"%s\"", this.method);
        if (this.getId() != null)
            html.print(" id=\"%s\"", this.getId());
        if (this.enctype != null)
            html.print(" enctype=\"%s\"", this.enctype);
        html.println(">");
        for (String key : items.keySet()) {
            String value = items.get(key);
            html.print("<input");
            html.print(" type=\"hidden\"");
            html.print(" name=\"%s\"", key);
            html.print(" id=\"%s\"", key);
            html.print(" value=\"%s\"", value);
            html.println("/>");
        }
    }

    /**
     * 输出尾部
     * 
     * @param html 输出器
     */
    public void outFoot(HtmlWriter html) {
        html.println("</form>");
    }

    public void addHidden(String key, String value) {
        items.put(key, value);
    }

    public String getEnctype() {
        return enctype;
    }

    public void setEnctype(String enctype) {
        this.enctype = enctype;
    }

    public Map<String, String> getItems() {
        return items;
    }

}
