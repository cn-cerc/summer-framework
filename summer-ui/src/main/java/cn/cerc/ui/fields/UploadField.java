package cn.cerc.ui.fields;

import cn.cerc.core.Record;
import cn.cerc.ui.parts.UIComponent;

public class UploadField extends AbstractField implements IFieldMultiple {
    // 用于文件上传是否可以选则多个文件
    private boolean multiple = false;

    public UploadField(UIComponent owner, String name, String field) {
        super(owner, name, 5);
        this.setField(field);
        this.setHtmType("file");
    }

    @Override
    public String getText(Record record) {
        return record.getString(field);
    }

    @Override
    public boolean isMultiple() {
        return multiple;
    }

    @Override
    public UploadField setMultiple(boolean multiple) {
        this.multiple = multiple;
        return this;
    }
}
