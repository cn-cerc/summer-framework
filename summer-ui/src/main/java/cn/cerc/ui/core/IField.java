package cn.cerc.ui.core;

import cn.cerc.core.Record;

public interface IField extends IReadonlyOwner, INameOwner {
    String getTitle();

    String getField();

    default int getWidth() {
        return 1;
    }

    String getAlign();

    String getText();

    // 隐藏输出
    void outputHidden(HtmlWriter html);

    Record getRecord();

}
