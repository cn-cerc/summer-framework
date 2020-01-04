package cn.cerc.menu;

import java.util.HashMap;
import java.util.Map;

import cn.cerc.core.TDateTime;
import cn.cerc.db.core.StubHandle;
import cn.cerc.db.mysql.SqlQuery;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SplitMenus {

    private static final String App_Menus = "s_menus";

    private static final String Verlist_Menu = "s_verlist_menu";

    public static void main(String[] args) {
        StubHandle handle = new StubHandle();

        SqlQuery verlist = new SqlQuery(handle);
        verlist.add("select * from s_verlist");
        verlist.open();
        Map<Integer, String> items = new HashMap<>();
        while (verlist.fetch()) {
            items.put(verlist.getInt("UID_"), verlist.getString("Code_"));
        }

        SqlQuery menus = new SqlQuery(handle);
        menus.add("select * from %s", SplitMenus.App_Menus);
//        menus.setMaximum(10);
        menus.open();
        while (menus.fetch()) {
            splitMenu(handle, items, menus.getString("VerList_"), menus.getString("Code_"));
        }
    }

    private static void splitMenu(StubHandle handle, Map<Integer, String> items, String verList, String menuCode) {
        SqlQuery verMenu = new SqlQuery(handle);
        verMenu.add("select * from %s where MenuCode_='%s'", menuCode, SplitMenus.Verlist_Menu);
        verMenu.open();
        String[] list = verList.split(",");
        for (String ver : list) {
            int key = Integer.parseInt(ver);
            String verCode = items.get(key);
            if (!verMenu.locate("VerCode_", verCode)) {
                log.info("版本 {}，菜单 {}", verCode, menuCode);
                verMenu.append();
                verMenu.setField("VerCode_", verCode);
                verMenu.setField("MenuCode_", menuCode);
                verMenu.setField("AppUser_", "admin");
                verMenu.setField("AppDate_", TDateTime.Now());
                verMenu.post();
            }
        }
    }

}
