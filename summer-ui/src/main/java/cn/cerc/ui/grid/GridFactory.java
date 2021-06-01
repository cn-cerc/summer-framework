package cn.cerc.ui.grid;

import cn.cerc.mis.core.IForm;
import cn.cerc.ui.parts.UIComponent;

@Deprecated
public class GridFactory {

    public static DataGrid build(IForm form, UIComponent owner) {
        DataGrid grid;
        if (form.getClient().isPhone()) {
            grid = new PhoneGrid(form, owner);
        } else {
            grid = new DataGrid(form, owner);
        }
        return grid;
    }
    
}