package cn.cerc.ui.panels;

import javax.servlet.http.HttpServletRequest;

import cn.cerc.core.Record;
import cn.cerc.core.Utils;
import cn.cerc.mis.core.IForm;
import cn.cerc.ui.columns.IColumn;
import cn.cerc.ui.columns.IDataColumn;
import cn.cerc.ui.core.Component;
import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.core.UIOriginComponent;
import cn.cerc.ui.parts.UIComponent;
import cn.cerc.ui.vcl.UIButton;
import cn.cerc.ui.vcl.UIButtonSubmit;
import cn.cerc.ui.vcl.UIForm;

public class UIAppendPanel extends UIOriginComponent {
    private UIForm uiform;
    private UIButton submit;
    private HttpServletRequest request;
    private String submitValue;
    private Record record = new Record();
    private UIComponent inputPanel;

    public UIAppendPanel(UIComponent owner) {
        super(owner);
        if (this.getOrigin() instanceof IForm) {
            this.request = ((IForm) this.getOrigin()).getRequest();
        }
        uiform = new UIForm(this);
        this.inputPanel = new UIOriginComponent(uiform);
        submit = new UIButtonSubmit(uiform.getBottom());
        submit.setText("保存");
    }

    @Override
    public void output(HtmlWriter html) {
        uiform.setCssClass("appendPanel");
        uiform.outHead(html);

        for (UIComponent component : inputPanel) {
            if (component instanceof IDataColumn) {
                IDataColumn column = (IDataColumn) component;
                if (column.isHidden()) {
                    column.setRecord(record);
                    column.outputLine(html);
                }
            }
        }

        html.print("<ul>");
        for (UIComponent component : inputPanel) {
            if (component instanceof IColumn) {
                if (component instanceof IDataColumn) {
                    IDataColumn column = (IDataColumn) component;
                    if (!column.isHidden()) {
                        html.print("<li>");
                        column.setRecord(record);
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
        submitValue = request.getParameter(submit.getName());
        if (Utils.isNotEmpty(submitValue)) {
            for (UIComponent component : this.inputPanel) {
                if (component instanceof IColumn) {
                    IColumn column = (IColumn) component;
                    String val = request.getParameter(column.getCode());
                    record.setField(column.getCode(), val == null ? "" : val);
                }
            }
        }
        return submitValue;
    }

    @Override
    public void addComponent(Component component) {
        if (component instanceof IColumn) {
            this.inputPanel.addComponent(component);
        } else {
            super.addComponent(component);
        }
    }

    public UIComponent getInputPanel() {
        return inputPanel;
    }

    public void setInputPanel(UIComponent inputPanel) {
        this.inputPanel = inputPanel;
    }

    public Record getRecord() {
        return record;
    }

    public void setRecord(Record record) {
        this.record = record;
    }

}
