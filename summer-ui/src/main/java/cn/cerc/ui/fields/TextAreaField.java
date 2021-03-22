package cn.cerc.ui.fields;

import cn.cerc.core.Record;
import cn.cerc.ui.parts.UIComponent;

public class TextAreaField extends AbstractField implements IFieldPlaceholder, IFieldRequired, IFieldTextArea {
    private String placeholder;
    private boolean required;
    // 最大字符串数
    private int maxlength;
    // 可见行数
    private int rows;
    // 可见宽度
    private int cols;
    // 是否禁用
    private boolean resize = true;

    public TextAreaField(UIComponent owner, String name, String field) {
        super(owner, name, 0);
        this.setField(field);
    }

    public TextAreaField(UIComponent owner, String name, String field, int width) {
        super(owner, name, 0);
        this.setField(field);
        this.setWidth(width);
    }

    @Override
    public String getText(Record record) {
        return getDefaultText(record);
    }

    @Override
    public String getPlaceholder() {
        return placeholder;
    }

    @Override
    public TextAreaField setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        return this;
    }

    @Override
    public boolean isRequired() {
        return required;
    }

    @Override
    public TextAreaField setRequired(boolean required) {
        this.required = required;
        return this;
    }

    @Override
    public int getMaxlength() {
        return maxlength;
    }

    @Override
    public TextAreaField setMaxlength(int maxlength) {
        this.maxlength = maxlength;
        return this;
    }

    @Override
    public int getRows() {
        return rows;
    }

    @Override
    public TextAreaField setRows(int rows) {
        this.rows = rows;
        return this;
    }

    @Override
    public int getCols() {
        return cols;
    }

    @Override
    public TextAreaField setCols(int cols) {
        this.cols = cols;
        return this;
    }

    @Override
    public boolean isResize() {
        return resize;
    }

    @Override
    public TextAreaField setResize(boolean resize) {
        this.resize = resize;
        return this;
    }

}
