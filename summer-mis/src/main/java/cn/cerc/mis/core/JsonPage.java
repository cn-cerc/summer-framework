package cn.cerc.mis.core;

import com.google.gson.Gson;

import javax.servlet.ServletException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class JsonPage implements IPage {
    protected IForm form;
    private Object data;
    private Map<String, Object> items = null;

    public JsonPage() {
        super();
    }

    public JsonPage(IForm form) {
        super();
        this.setForm(form);
    }

    @Deprecated
    public JsonPage(IForm form, Object data) {
        super();
        this.setForm(form);
        this.data = data;
    }

    @Override
    public IForm getForm() {
        return form;
    }

    @Override
    public void setForm(IForm form) {
        this.form = form;
    }

    @Override
    public String execute() throws ServletException, IOException {
        PrintWriter writer = getResponse().getWriter();
        if (this.data == null) {
            if (items == null) {
                items = new HashMap<>();
            }
            writer.print(new Gson().toJson(items));
        } else {
            writer.print(new Gson().toJson(this.data));
        }
        return null;
    }

    @Deprecated
    public JsonPage add(String key, Object value) {
        return put(key, value);
    }

    public JsonPage put(String key, Object value) {
        if (this.data != null) {
            throw new RuntimeException("data is not null");
        }
        if (items == null) {
            items = new HashMap<>();
        }
        items.put(key, value);
        return this;
    }

    public Object getData() {
        return data;
    }

    public JsonPage setData(Object data) {
        if (this.items != null) {
            throw new RuntimeException("data is not null, json page items must be null.");
        }
        this.data = data;
        return this;
    }

    public JsonPage setResultMessage(boolean result, String message) {
        this.put("result", result);
        this.put("message", message);
        return this;
    }

    public Map<String, Object> getItems() {
        if (items == null) {
            items = new HashMap<>();
        }
        return items;
    }

    public void setItems(Map<String, Object> items) {
        this.items = items;
    }

}
