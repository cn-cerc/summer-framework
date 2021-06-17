package cn.cerc.ui.columns;

import cn.cerc.core.Record;
import cn.cerc.ui.core.Component;
import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.core.IReadonlyOwner;
import cn.cerc.ui.parts.UIComponent;

public class CustomColumn extends AbstractColumn implements IDataColumn {
    private boolean readonly;
    private boolean hidden;
    private IDefineContent defineLine;
    private IDefineContent defineCell;

    public CustomColumn(UIComponent owner) {
        super(owner);
        this.setName("操作");
    }

    public CustomColumn(UIComponent owner, String name, String code) {
        super(owner);
        this.setCode(code).setName(name);
        if (owner instanceof IReadonlyOwner) {
            this.setReadonly(((IReadonlyOwner) owner).isReadonly());
        }
    }

    public CustomColumn(UIComponent owner, String name, String code, int width) {
        super(owner);
        this.setCode(code).setName(name).setSpaceWidth(width);
        if (owner instanceof IReadonlyOwner) {
            this.setReadonly(((IReadonlyOwner) owner).isReadonly());
        }
    }
    
    @Override
    public void outputCell(HtmlWriter html) {
        if (defineCell != null) {
            this.getComponents().clear();
            defineCell.execute(this, getRecord());
        } else if (defineLine != null) {
            this.getComponents().clear();
            defineLine.execute(this, getRecord());
        }
        for (Component item : this.getComponents()) {
            html.print(item.toString());
        }
    }

    @Override
    public void outputLine(HtmlWriter html) {
        if (defineLine != null) {
            this.getComponents().clear();
            defineLine.execute(this, getRecord());
        } else if (defineCell != null) {
            this.getComponents().clear();
            defineCell.execute(this, getRecord());
        }
        for (Component item : this.getComponents()) {
            html.print(item.toString());
        }
    }

    @Override
    public boolean isReadonly() {
        return readonly;
    }

    @Override
    public CustomColumn setReadonly(boolean readonly) {
        this.readonly = readonly;
        return this;
    }

    @Override
    public boolean isHidden() {
        return hidden;
    }

    @Override
    public CustomColumn setHidden(boolean hidden) {
        this.hidden = hidden;
        return this;
    }

    public interface IDefineContent {
        void execute(CustomColumn sender, Record record);
    }

    public CustomColumn defineCell(IDefineContent defineCell) {
        this.defineCell = defineCell;
        return this;
    }

    public CustomColumn defineLine(IDefineContent defineLine) {
        this.defineLine = defineLine;
        return this;
    }

}
