package cn.cerc.ui.fields;

import cn.cerc.core.Record;
import cn.cerc.ui.parts.UIComponent;

public class TextAreaField extends AbstractField {
    public TextAreaField(UIComponent owner, String name, String field) {
        super(owner, name, 0);
        this.setField(field);
        this.setHtmlTag("textarea");
    }

    public TextAreaField(UIComponent owner, String name, String field, int width) {
        super(owner, name, 0);
        this.setField(field);
        this.setWidth(width);
        this.setHtmlTag("textarea");
    }

    @Override
    public String getText(Record rs) {
        return getDefaultText(rs);
    }

}
