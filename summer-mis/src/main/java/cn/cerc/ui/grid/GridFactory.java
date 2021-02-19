package cn.cerc.ui.grid;

import cn.cerc.mis.core.IForm;
import cn.cerc.ui.parts.UIComponent;

public class GridFactory {

    public static AbstractGrid build(IForm form, UIComponent owner) {
        AbstractGrid grid;
        if (form.getClient().isPhone())
            grid = new PhoneGrid(form, owner);
        else
            grid = new DataGrid(form, owner);
        return grid;
    }
}
