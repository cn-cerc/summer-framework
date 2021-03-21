package cn.cerc.ui.fields;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class FieldTitle {
    private String name;
    private String type;
    private String dateFormat;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    @Override
    public String toString() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode json = mapper.createObjectNode();
        json.put("name", this.name);
        if (this.type != null) {
            json.put("type", this.type);
        }
        if (this.dateFormat != null) {
            json.put("dateFormat", this.dateFormat);
        }
        return json.toString().replace("\"", "'");
    }
}
