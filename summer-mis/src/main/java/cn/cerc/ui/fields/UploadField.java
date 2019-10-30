package cn.cerc.ui.fields;

import cn.cerc.core.Record;
import cn.cerc.ui.parts.UIComponent;

public class UploadField extends AbstractField {

    public UploadField(UIComponent owner, String name, String field) {
        super(owner, name, 5);
        this.setField(field);
        this.setHtmType("file");
    }

    @Override
    public String getText(Record ds) {
        return ds.getString(field);
    }
}
