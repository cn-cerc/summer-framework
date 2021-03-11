package cn.cerc.ui.fields;

import cn.cerc.core.ClassResource;
import cn.cerc.core.Record;
import cn.cerc.ui.SummerUI;
import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.other.SearchItem;
import cn.cerc.ui.parts.UIComponent;

public class ExpendField extends AbstractField implements SearchItem {
    private static final ClassResource res = new ClassResource(ExpendField.class, SummerUI.ID);

    private boolean search;
    private String hiddenId = "hidden";

    public ExpendField(UIComponent owner) {
        this(owner, "", "_opera_", 5);
    }

    public ExpendField(UIComponent owner, String name, String field) {
        this(owner, name, field, 0);
    }

    public ExpendField(UIComponent owner, String name, String field, int width) {
        super(owner, name, width);
        this.setField(field);
        this.setAlign("center");
        this.setCSSClass_phone("right");
    }

    @Override
    public String getText(Record record) {
        if (record == null) {
            return null;
        }
        if (this.search) {
            return this.getName();
        }
        if (buildText != null) {
            HtmlWriter html = new HtmlWriter();
            buildText.outputText(record, html);
            return html.toString();
        }
        return String.format("<a href=\"javascript:displaySwitch('%d')\">%s</a>", dataSource.getDataSet().getRecNo(), res.getString(1, "展开"));
    }

    @Override
    public void output(HtmlWriter html) {
        if (this.search) {
            html.print("<a href=\"javascript:displaySwitch('%s')\">%s</a>", this.getHiddenId(), this.getName());
        } else {
            super.output(html);
        }
    }

    public boolean isSearch() {
        return search;
    }

    @Override
    public void setSearch(boolean search) {
        this.search = search;
    }

    public String getHiddenId() {
        if (this.search) {
            return hiddenId;
        }
        return "" + dataSource.getDataSet().getRecNo();
    }

    public void setHiddenId(String hiddenId) {
        this.hiddenId = hiddenId;
    }

}
