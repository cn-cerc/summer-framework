package cn.cerc.ui.panels;

import javax.servlet.http.HttpServletRequest;

import cn.cerc.core.Record;
import cn.cerc.mis.core.IForm;
import cn.cerc.ui.columns.IColumn;
import cn.cerc.ui.columns.IDataColumn;
import cn.cerc.ui.core.Component;
import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.core.UIOriginComponent;
import cn.cerc.ui.parts.UIComponent;
import cn.cerc.ui.vcl.UIButton;
import cn.cerc.ui.vcl.UIButtonSubmit;
import cn.cerc.ui.vcl.UIDiv;
import cn.cerc.ui.vcl.UIForm;

public class UIViewPanel extends UIOriginComponent {
    private UIForm uiform;
    private UIButton submit;
    private HttpServletRequest request;
    private UIComponent content;
    private Record record;
    private String title;
    private IForm form;

    public UIViewPanel(UIComponent owner) {
        super(owner);
        if (this.getOrigin() instanceof IForm) {
            form = (IForm) this.getOrigin();
            this.request = form.getRequest();
        }
        uiform = new UIForm(this);
        this.content = new UIOriginComponent(uiform);
        submit = new UIButtonSubmit(uiform.getBottom());
        submit.setText("确定");
        this.title = "查看";
    }

    @Override
    public void output(HtmlWriter html) {
        if (!form.getClient().isPhone()) {
            UIDiv div = new UIDiv();
            div.setCssClass("panelTitle");
            div.setText(this.getTitle());
            div.output(html);
        }
        
        uiform.setCssClass("viewPanel");
        uiform.outHead(html);

        for (UIComponent component : content) {
            if (component instanceof IDataColumn) {
                IDataColumn column = (IDataColumn) component;
                if (column.isHidden()) {
                    column.setRecord(record);
                    column.outputLine(html);
                }
            }
        }

        html.print("<ul>");
        for (UIComponent component : content) {
            if (component instanceof IColumn) {
                if (component instanceof IDataColumn) {
                    IDataColumn column = (IDataColumn) component;
                    if (!column.isHidden()) {
                        column.setRecord(record);
                        column.setReadonly(true);
                        html.print("<li>");
                        column.outputLine(html);
                        html.print("</li>");
                    }
                } else {
                    IColumn column = (IColumn) component;
                    html.print("<li>");
                    column.outputLine(html);
                    html.print("</li>");
                }
            }
        }
        html.print("</ul>");

        uiform.outFoot(html);
    }

    public void setAction(String action) {
        uiform.setAction(action);
    }

    public String readAll() {
        return request.getParameter(submit.getName());
    }

    public UIComponent getContent() {
        return content;
    }

    @Override
    public void addComponent(Component component) {
        if (component instanceof IColumn) {
            this.content.addComponent(component);
        } else {
            super.addComponent(component);
        }
    }

    public Record getRecord() {
        return record;
    }

    public UIViewPanel setRecord(Record record) {
        this.record = record;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    
    public UIForm getUiform() {
        return uiform;
    }
    
    public UIButton getSubmit() {
        return submit;
    }
}
