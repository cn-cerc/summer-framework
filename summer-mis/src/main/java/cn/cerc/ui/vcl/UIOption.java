package cn.cerc.ui.vcl;

import java.util.LinkedHashMap;
import java.util.Map;

import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.parts.UIComponent;

public class UIOption extends UIComponent {
    private String name;
    private Map<String, String> options = new LinkedHashMap<>();

    private String role;
    private String form;// form_id
    private String selectedKey;

    private boolean autofocus;
    private boolean disabled;
    private boolean multiple;
    private boolean required;
    private int size;

    public UIOption() {
        super();
    }

    public UIOption(UIComponent owner) {
        super(owner);
    }

    @Override
    public void output(HtmlWriter html) {
        if (options.size() == 0) {
            throw new RuntimeException("下拉列表不允许为空");
        }
        html.print("<select ");
        if (getId() != null) {
            html.print(" id='%s'", getId());
        }

        if (getName() != null) {
            html.print(" name='%s'", getName());
        }

        if (getRole() != null) {
            html.print(" role='%s'", getRole());
        }
        if (getForm() != null) {
            html.print(" form='%s'", getForm());
        }
        if (getAutofocus()) {
            html.print(" autofocus");
        }
        if (getDisabled()) {
            html.print(" disabled");
        }
        if (getMultiple()) {
            html.print(" multiple");
        }
        if (getRequired()) {
            html.print(" required");
        }
        if (getSize() > 0) {
            html.print(" size='%d'", getSize());
        }
        html.println(">");
        for (String key : getOptions().keySet()) {
            if (key.equals(getSelectedKey())) {
                html.println("<option value ='%s' selected>%s</option>", key, getOptions().get(key));
            } else {
                html.println("<option value ='%s'>%s</option>", key, getOptions().get(key));
            }
        }
        html.println("</select>");
    }

    public UIOption copyValues(Map<String, String> items) {
        for (String key : items.keySet()) {
            this.add(key, items.get(key));
        }
        return this;
    }

    public UIOption add(String key, String value) {
        options.put(key, value);
        return this;
    }

    public String getName() {
        return name;
    }

    public UIOption setName(String name) {
        this.name = name;
        return this;
    }

    public Map<String, String> getOptions() {
        return options;
    }

    public String getRole() {
        return role;
    }

    public UIOption setRole(String role) {
        this.role = role;
        return this;
    }

    public String getForm() {
        return form;
    }

    public UIOption setForm(String form) {
        this.form = form;
        return this;
    }

    public String getSelectedKey() {
        return selectedKey;
    }

    public UIOption setSelectedKey(String selectedKey) {
        this.selectedKey = selectedKey;
        return this;
    }

    public boolean getAutofocus() {
        return autofocus;
    }

    public UIOption setAutofocus(boolean autofocus) {
        this.autofocus = autofocus;
        return this;
    }

    public boolean getDisabled() {
        return disabled;
    }

    public UIOption setDisabled(boolean disabled) {
        this.disabled = disabled;
        return this;
    }

    public boolean getMultiple() {
        return multiple;
    }

    public UIOption setMultiple(boolean multiple) {
        this.multiple = multiple;
        return this;
    }

    public boolean getRequired() {
        return required;
    }

    public UIOption setRequired(boolean required) {
        this.required = required;
        return this;
    }

    public int getSize() {
        return size;
    }

    public UIOption setSize(int size) {
        this.size = size;
        return this;
    }

}
