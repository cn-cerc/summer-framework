package cn.cerc.ui.core;

import java.util.List;

import cn.cerc.db.core.ISessionOwner;
import cn.cerc.ui.mvc.IMenuBar;

public interface IRightMenuLoad extends ISessionOwner{

    void loadMenu(List<IMenuBar> items);

}
