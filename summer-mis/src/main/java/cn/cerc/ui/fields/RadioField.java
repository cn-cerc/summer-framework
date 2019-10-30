package cn.cerc.ui.fields;

import java.util.ArrayList;
import java.util.List;

import cn.cerc.core.Record;
import cn.cerc.ui.parts.UIComponent;

public class RadioField extends AbstractField {
    private List<String> items = new ArrayList<>();

    public RadioField(UIComponent owner, String name, String field, int width) {
        super(owner, name, width);
        this.setField(field);
    }

    @Override
    public String getText(Record dataSet) {
        if (dataSet == null)
            return null;
        int val = dataSet.getInt(field);
        if (val < 0 || val > items.size() - 1)
            return "" + val;
        String result = items.get(val);
        if (result == null)
            return "" + val;
        else
            return result;
    }

    public RadioField add(String... items) {
        for (String item : items)
            this.items.add(item);
        return this;
    }
}
