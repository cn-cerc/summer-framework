package cn.cerc.ui.columns;

import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.core.IReadonlyOwner;
import cn.cerc.ui.parts.UIComponent;

public class RadioColumn extends AbstractColumn implements IDataColumn {

    public RadioColumn(UIComponent owner) {
        super(owner);
        // TODO Auto-generated constructor stub
    }

    public RadioColumn(UIComponent owner, String name, String code, int width) {
        super(owner);
        this.setCode(code).setName(name).setSpaceWidth(width);
        if (owner instanceof IReadonlyOwner) {
            this.setReadonly(((IReadonlyOwner) owner).isReadonly());
        }
    }

    @Override
    public void outputCell(HtmlWriter html) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void outputLine(HtmlWriter html) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public boolean isReadonly() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Object setReadonly(boolean readonly) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isHidden() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Object setHidden(boolean hidden) {
        // TODO Auto-generated method stub
        return null;
    }

    //FIXME: 此处功能未完成，需要修正
    public void add(String... params) {
        // TODO Auto-generated method stub
        
    }

}
