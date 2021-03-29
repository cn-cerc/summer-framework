package cn.cerc.ui.menu;

import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import cn.cerc.core.ClassResource;
import cn.cerc.core.DataSet;
import cn.cerc.core.IUserLanguage;
import cn.cerc.core.Utils;
import cn.cerc.db.cache.Redis;
import cn.cerc.db.core.IHandle;
import cn.cerc.mis.core.CenterService;
import cn.cerc.mis.core.SystemBufferType;
import cn.cerc.mis.language.R;
import cn.cerc.mis.other.IDataCache;
import cn.cerc.ui.SummerUI;

/**
 * 系统菜单缓存 此对象不应该放在框架中
 */
public class MenuList implements IDataCache, IUserLanguage {
    private final ClassResource res = new ClassResource(this, SummerUI.ID);

    private final IHandle handle;
    private final Map<String, MenuModel> buff = new LinkedHashMap<>();
    private final String buffKey;
    private final int Version = 5;

    public static MenuList create(IHandle handle) {
        return new MenuList(handle);
    }

    private MenuList(IHandle handle) {
        super();
        this.handle = handle;
        buffKey = String.format("%d.%s.%d", SystemBufferType.getObject.ordinal(), this.getClass().getName(), Version);
    }

    public String getName(String menu) {
        // 不允许用户帐号为空
        if (Utils.isEmpty(menu)) {
            return "";
        }

        // 从缓存中取回值
        MenuModel result = get(menu);
        return result == null ? menu : result.getName();
    }

    public MenuModel get(String menu) {
        if (Utils.isEmpty(menu)) {
            throw new RuntimeException(res.getString(1, "模组代码不允许为空！"));
        }
        // 初始化缓存
        this.init();
        return buff.get(menu);
    }

    private void init() {
        if (buff.size() > 0) {
            return;
        }

        String data = Redis.get(buffKey);
        Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
        if (data != null && !"".equals(data)) {
            Type type = new TypeToken<Map<String, MenuModel>>() {
            }.getType();
            Map<String, MenuModel> items = gson.fromJson(data, type);
            for (String key : items.keySet()) {
                buff.put(key, items.get(key));
            }
            return;
        }

        // 从数据库中读取
        CenterService svr = new CenterService(handle);
        svr.setService("ApiMenus.getMenus");
        if (!svr.exec()) {
            throw new RuntimeException(svr.getMessage());
        }
        DataSet cdsMenu = svr.getDataOut();
        while (cdsMenu.fetch()) {
            String key = cdsMenu.getString("Code_");
            MenuModel item = new MenuModel();
            item.setModule(cdsMenu.getString("Module_"));
            item.setCode(cdsMenu.getString("Code_"));
            item.setName(cdsMenu.getString("Name_"));
            item.setVerlist(cdsMenu.getString("VerList_"));
            item.setProcCode(cdsMenu.getString("ProcCode_"));
            item.setStatus(cdsMenu.getInt("Status_"));
            item.setDeadline(cdsMenu.getString("DeadLine_"));
            item.setSecurity(cdsMenu.getBoolean("Security_"));
            item.setHide(cdsMenu.getBoolean("Hide_"));
            item.setWin(cdsMenu.getBoolean("Win_"));
            item.setWeb(cdsMenu.getBoolean("Web_"));
            item.setPhone(cdsMenu.getBoolean("Phone_"));
            item.setPrice(cdsMenu.getDouble("Price_"));
            item.setCustom(cdsMenu.getBoolean("Custom_"));
            item.setOrderType(cdsMenu.getInt("OrderType_"));
            item.setRemark(cdsMenu.getString("Remark_"));
            item.setMenuIconType(cdsMenu.getInt("MenuIconType_"));
            buff.put(key, item);
        }

        // 存入到缓存中
        Redis.set(buffKey, gson.toJson(buff));
    }

    public Map<String, String> getList() {
        Map<String, String> items = new LinkedHashMap<>();
        init();
        buff.forEach((k, v) -> items.put(k, v.getName()));
        return items;
    }

    public static String getFirst(Map<String, String> map) {
        Map.Entry<String, String> item = map.entrySet().iterator().next();
        return item.getKey();
    }

    @Override
    public boolean exist(String key) {
        this.init();
        return buff.get(key) != null;
    }

    @Override
    public void clear() {
        Redis.delete(buffKey);
    }

    @Override
    public String getLanguageId() {
        return R.getLanguageId(this.handle);
    }
}