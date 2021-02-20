package cn.cerc.ui.grid;

import cn.cerc.ui.grid.lines.AbstractGridLine;

public interface OutputEvent {
    void process(AbstractGridLine line);
}
