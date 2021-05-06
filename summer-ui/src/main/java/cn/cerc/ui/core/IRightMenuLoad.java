package cn.cerc.ui.core;

import java.util.List;

import cn.cerc.mis.core.IForm;

public interface IRightMenuLoad {

    void loadMenu(IForm form, List<UrlRecord> rightMenus);

}
