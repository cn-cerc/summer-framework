package cn.cerc.ui.other;

import cn.cerc.core.Record;
import cn.cerc.ui.core.HtmlWriter;

public interface BuildText {
    void outputText(Record record, HtmlWriter html);
}
