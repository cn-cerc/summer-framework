package cn.cerc.ui.columns;

import cn.cerc.ui.core.HtmlWriter;

public interface IColumn {

    int getSpaceWidth();

    String getCode();

    String getName();

    Object setName(String string);

    // 格模式输出
    void outputCell(HtmlWriter html);

    // 行模式输出
    void outputLine(HtmlWriter html);

}
