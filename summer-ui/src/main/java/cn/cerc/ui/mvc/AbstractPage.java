package cn.cerc.ui.mvc;

import java.util.List;
import java.util.Map;

import cn.cerc.core.ClassConfig;
import cn.cerc.core.DataSet;
import cn.cerc.core.IUserLanguage;
import cn.cerc.core.Record;
import cn.cerc.core.TDate;
import cn.cerc.core.TDateTime;
import cn.cerc.core.Utils;
import cn.cerc.db.cache.Buffer;
import cn.cerc.mis.SummerMIS;
import cn.cerc.mis.cdn.CDN;
import cn.cerc.mis.core.Application;
import cn.cerc.mis.core.IForm;
import cn.cerc.mis.core.IPage;
import cn.cerc.mis.language.R;
import cn.cerc.ui.core.Component;
import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.parts.UIComponent;

public abstract class AbstractPage extends UIComponent implements IPage, IUserLanguage {
    protected static final ClassConfig config = new ClassConfig(AbstractPage.class, SummerMIS.ID);

    private IForm form;

    public AbstractPage() {
        super();
    }

    @Override
    public final IForm getForm() {
        return form;
    }

    @Override
    public final void setForm(IForm form) {
        this.form = form;
        this.add("cdn", Application.getStaticPath());
        this.add("version", config.getString(CDN.BROWSER_CACHE_VERSION, "1.0.0.0"));
        if (form != null) {
            this.put("jspPage", this);
            // 为兼容而设计
            this.add("summer_js", CDN.get(config.getString("summer.js", "js/summer.js")));
            this.add("myapp_js", CDN.get(config.getString("myapp.js", "js/myapp.js")));
        }
    }

    @Override
    public void addComponent(Component component) {
        if (component.getId() != null) {
            this.put(component.getId(), component);
        }
        super.addComponent(component);
    }

    protected void put(String id, Object value) {
        getRequest().setAttribute(id, value);
    }

    public final String getMessage() {
        return form.getParam("message", null);
    }

    public final void setMessage(String message) {
        form.setParam("message", message);
    }

    @Override
    public void output(HtmlWriter html) {
        for (Component component : this.getComponents()) {
            if (component instanceof UIComponent) {
                ((UIComponent) component).output(html);
            }
        }
    }

    // 从请求或缓存读取数据
    public final String getValue(Buffer buff, String reqKey) {
        String result = getRequest().getParameter(reqKey);
        if (result == null) {
            String val = buff.getString(reqKey).replace("{}", "");
            if (Utils.isNumeric(val) && val.endsWith(".0")) {
                result = val.substring(0, val.length() - 2);
            } else {
                result = val;
            }
        } else {
            result = result.trim();
            buff.setField(reqKey, result);
        }
        this.add(reqKey, result);
        return result;
    }

    public void add(String id, String value) {
        getRequest().setAttribute(id, value);
    }

    public void add(String id, boolean value) {
        put(id, value);
    }

    public void add(String id, double value) {
        put(id, value);
    }

    public void add(String id, int value) {
        put(id, value);
    }

    public void add(String id, List<?> value) {
        put(id, value);
    }

    public void add(String id, Map<?, ?> value) {
        put(id, value);
    }

    public void add(String id, DataSet value) {
        put(id, value);
    }

    public void add(String id, Record value) {
        put(id, value);
    }

    public void add(String id, TDate value) {
        put(id, value);
    }

    public void add(String id, TDateTime value) {
        put(id, value);
    }

    public void add(String id, UIComponent value) {
        put(id, value);
    }

    @Override
    public String getLanguageId() {
        return R.getLanguageId(form.getHandle());
    }

}
