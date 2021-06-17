package security.menu;

import java.util.List;

import cn.cerc.db.core.IHandle;

public interface IAppMenus extends IHandle{

    List<IMenuItem> getItems();

    IMenuItem getItem(String menuCode);

}
