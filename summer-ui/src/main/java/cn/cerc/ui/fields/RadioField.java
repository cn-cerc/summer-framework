package cn.cerc.ui.fields;

import cn.cerc.core.Record;
import cn.cerc.ui.parts.UIComponent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class RadioField extends AbstractField {

    private final List<String> items = new ArrayList<>();

    public RadioField(UIComponent owner, String name, String field, int width) {
        super(owner, name, width);
        this.setField(field);
    }

    @Override
    public String getText(Record record) {
        if (record == null) {
            return null;
        }
        int val = record.getInt(field);
        if (val < 0 || val > items.size() - 1) {
            return "" + val;
        }
        String result = items.get(val);
        if (result == null) {
            return "" + val;
        } else {
            return result;
        }
    }

    public RadioField add(String... items) {
        this.items.addAll(Arrays.asList(items));
        return this;
    }

    public RadioField add(Collection<String> items) {
        this.items.addAll(items);
        return this;
    }

}
