package cn.cerc.mis.magic;

public class FieldDefine {
    private String code;
    private String name;
    private String dataType;
    private boolean autoIncrement;
    private String defaultValue;
    private boolean nullable;

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public boolean isAutoIncrement() {
        return autoIncrement;
    }

    public void setAutoIncrement(boolean autoIncrement) {
        this.autoIncrement = autoIncrement;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public boolean isNullable() {
        return nullable;
    }

    public void setNullable(boolean nullable) {
        this.nullable = nullable;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return this.name;
    }

    public Object getVarType() {
        if (dataType.startsWith("int("))
            return "int";
        else if (dataType.startsWith("bigint("))
            return "long";
        else if (dataType.startsWith("decimal("))
            return "double";
        else if (dataType.startsWith("varchar("))
            return "String";
        else if (dataType.startsWith("datetime"))
            return "TDateTime";
        else if (dataType.startsWith("date"))
            return "TDateTime";
        else
            return dataType;
    }

    public String getVarCode() {
        String ss = code;
        if (code.endsWith("_"))
            ss = code.substring(0, code.length() - 1);
        return ss;
    }

    public void setName(String name) {
        this.name = name;
    }

}
