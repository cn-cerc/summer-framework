package cn.cerc.ui.panels;

import javax.servlet.http.HttpServletRequest;

import cn.cerc.core.Record;
import cn.cerc.core.Utils;
import cn.cerc.mis.core.IForm;
import cn.cerc.ui.columns.IArrayColumn;
import cn.cerc.ui.columns.IColumn;
import cn.cerc.ui.columns.IDataColumn;
import cn.cerc.ui.core.Component;
import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.core.UIOriginComponent;
import cn.cerc.ui.parts.UIComponent;
import cn.cerc.ui.vcl.UIButtonSubmit;
import cn.cerc.ui.vcl.UIForm;

public class UISearchPanel extends UIOriginComponent {
    private UIComponent filterPanel;
    private UIComponent controlPanel;
    private UIButtonSubmit submit;
    private HttpServletRequest request;
    private Record record;
    private UIForm uiform;

    public UISearchPanel(UIComponent owner) {
        super(owner);
        if (this.getOrigin() instanceof IForm) {
            this.request = ((IForm) this.getOrigin()).getRequest();
        }
        this.uiform = new UIForm();
        this.record = new Record();
        this.filterPanel = new UIOriginComponent(uiform);
        this.controlPanel = new UIOriginComponent(uiform);
        submit = new UIButtonSubmit(uiform.getBottom());
        submit.setText("查询");
    }

    @Override
    public void output(HtmlWriter html) {
        uiform.setCssClass("searchPanel");
        uiform.outHead(html);

        html.print("<ul>");
        for (UIComponent component : filterPanel) {
            html.print("<li>");
            if (component instanceof IColumn) {
                IColumn column = ((IColumn) component);
                if (component instanceof IDataColumn) {
                    ((IDataColumn) column).setRecord(record);
                }
                column.outputLine(html);
            } else {
                component.output(html);
            }
            html.print("</li>");
        }
        html.print("</ul>");

        for (UIComponent component : controlPanel) {
            html.print("<div>");
            component.output(html);
            html.print("</div>");
        }

        uiform.outFoot(html);
    }

    public String readAll() {
        String result = request.getParameter(submit.getName());
        if (Utils.isNotEmpty(result)) {
            // 将用户值或缓存值存入到dataSet中
            for (UIComponent component : this.filterPanel) {
                if (component instanceof IColumn) {
                    IColumn column = (IColumn) component;
                    if (component instanceof IArrayColumn) {
                        String[] values = request.getParameterValues(column.getCode());
                        record.setField(column.getCode(), String.join(",", values));
                    } else {
                        String val = request.getParameter(column.getCode());
                        record.setField(column.getCode(), val == null ? "" : val);
                    }
                }
            }
        }
        return result;
    }

    public UIComponent getControlPanel() {
        return controlPanel;
    }

    public UIComponent getFilterPanel() {
        return filterPanel;
    }

    public UIButtonSubmit getSubmit() {
        return submit;
    }

    @Override
    public void addComponent(Component component) {
        if (component instanceof IColumn) {
            this.filterPanel.addComponent(component);
        } else {
            super.addComponent(component);
        }
    }

    public Record getRecord() {
        return record;
    }

    public void setRecord(Record record) {
        this.record = record;
    }

    public UIForm getUiform() {
        return uiform;
    }
}
