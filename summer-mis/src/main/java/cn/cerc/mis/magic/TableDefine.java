package cn.cerc.mis.magic;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cn.cerc.core.ISession;
import cn.cerc.db.mysql.SqlQuery;

public class TableDefine implements Iterable<FieldDefine> {
    private Map<String, FieldDefine> fields = new LinkedHashMap<>();
    private String code;
    private String comment;

    public void init(ISession session, String dbName) {
        SqlQuery ds = new SqlQuery(session);
        ds.add("select COLUMN_NAME,COLUMN_TYPE,EXTRA,IS_NULLABLE,COLUMN_COMMENT,COLUMN_DEFAULT");
        ds.add("from %s", DatabaseDefine.TableColumns);
        ds.add("where TABLE_SCHEMA='%s' and table_name='%s'", dbName, this.getCode());
        ds.open();
        while (ds.fetch()) {
            FieldDefine field = new FieldDefine();
            field.setCode(ds.getString("COLUMN_NAME"));

            String dataType = ds.getString("COLUMN_TYPE");
            if (dataType.contains("unsigned")) {
                dataType = dataType.substring(0, dataType.indexOf(")") + 1);
            }
            field.setDataType(dataType);

            field.setAutoIncrement("auto_increment".equals(ds.getString("EXTRA")));
            field.setNullable("YES".equals(ds.getString("IS_NULLABLE")));
            field.setDefaultValue(ds.getString("COLUMN_DEFAULT"));
            field.setName(ds.getString("COLUMN_COMMENT"));

            fields.put(field.getCode(), field);
        }
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public Iterator<FieldDefine> iterator() {
        List<FieldDefine> list = new ArrayList<>();
        for (String code : fields.keySet()) {
            list.add(fields.get(code));
        }
        return list.iterator();
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Map<String, FieldDefine> getFields() {
        return fields;
    }

    public FieldDefine getField(String fieldCode) {
        return fields.get(fieldCode);
    }

    private void print(String text, Object... args) {
        System.out.println(String.format(text, args));
    }

    public void createCodeAppendService() {
        print("    // 增加服务");
        print("    public boolean append() throws DataValidateException {");
        print("        Record headIn = getDataIn().getHead();");
        for (FieldDefine field : this) {
            print(String.format("        DataValidateException.stopRun(\"%s不允许为空！\", !headIn.hasValue(\"%s\"));",
                    field.getName(), field.getCode()));
        }
        print("");
        for (FieldDefine field : this) {
            print(String.format("        %s %s = headIn.getString(\"%s\"); // %s", field.getVarType(),
                    field.getVarCode(), field.getCode(), field.getName()));
        }
        print("");
        print(String.format("        String tableId = \"%s\";", this.getCode()));
        print("        SqlQuery query = new SqlQuery(this);");
        print("        query.add(\"select * from %%s\", tableId);");
        int count = 0;
        for (FieldDefine field : this) {
            if (count == 0) {
                print(String.format("        query.add(\"where %s='%%s'\", %s);", field.getCode(), field.getVarCode()));
            } else {
                print(String.format("        query.add(\"and %s='%%s'\", %s);", field.getCode(), field.getVarCode()));
            }
            count++;
        }
        print("        query.open();");
        print("        DataValidateException.stopRun(\"该记录已经存在\", !query.eof());");
        print("");
        print("        query.append();");
        print("        query.setField(\"id_\", Utils.newGuid());");
        for (FieldDefine field : this) {
            System.out
                    .println(String.format("        query.setField(\"%s\", %s);", field.getCode(), field.getVarCode()));
        }
        print("        query.setField(\"create_user_\", this.getUserCode());");
        print("        query.setField(\"create_time_\", TDateTime.now());");
        print("        query.setField(\"update_user_\", this.getUserCode());");
        print("        query.setField(\"update_time_\", TDateTime.now());");
        print("        query.post();");
        print("");
        print("        getDataOut().getHead().copyValues(query.getCurrent());");
        print("        return true;");
        print("    }");
    }

    public void createCodeModifyService() {
        print("    // 修改");
        print("    public boolean modify() throws DataValidateException {");
        print("        Record headIn = getDataIn().getHead();");
        for (FieldDefine field : this) {
            print("        DataValidateException.stopRun(\"%s不允许为空！\", !headIn.hasValue(\"%s\"));", field.getName(),
                    field.getCode());
        }
        print("");
        for (FieldDefine field2 : this) {
            print("        %s %s = headIn.getString(\"%s\");", field2.getVarType(), field2.getVarCode(),
                    field2.getCode());
        }
        print("");
        print("        String tableId = \"%s\";", this.getCode());
        print("        SqlQuery query = new SqlQuery(this);");
        print("        query.add(\"select * from %%s\", tableId);");
        print("        query.add(\"where materKey_='%%s'\", materKey);");
        print("        query.open();");
        print("        DataValidateException.stopRun(\"记录不存在\", query.eof());");
        print("");
        print("        query.edit();");
        for (FieldDefine field : this) {
            print("        query.setField(\"%s\", %s);", field.getCode(), field.getVarCode());
        }
        print("        query.setField(\"update_user_\", this.getUserCode());");
        print("        query.setField(\"update_time_\", TDateTime.now());");
        print("        query.post();");
        print("        return true;");
        print("    }");
    }

    public void createCodeDownloadService() {
        print("    // 下载");
        print("    public boolean download() throws DataValidateException {");
        print("        Record headIn = getDataIn().getHead();");
        for (FieldDefine field : this) {
            print("        DataValidateException.stopRun(\"%s不允许为空！\", !headIn.hasValue(\"%s\"));", field.getName(),
                    field.getCode());
        }
        print("        String userCode = headIn.getString(\"user_code_\");");
        print("");
        print("        String tableId = \"%s\";", this.getCode());
        print("        SqlQuery query = new SqlQuery(this);");
        print("        query.add(\"select * from %%s\", tableId);");
        print("        query.add(\"where materKey_='%%s'\", materKey);");
        print("        query.open();");
        print("        DataValidateException.stopRun(\"记录不存在\", query.eof());");
        print("");
        print("        getDataOut().getHead().copyValues(query.getCurrent());");
        print("        return true;");
        print("    }");
    }

    public void createCodeSearchService() {
        print("    // 查询服务");
        print("    public boolean search() {");
        print("        String tableId = \"%s\";", this.getCode());
        print("        Record headIn = getDataIn().getHead();");
        print("        BuildQuery f = new BuildQuery(this);");
        print("        f.add(\"select * from %%s\", tableId);");
        print("");

        for (FieldDefine field : this) {
            print("        if (headIn.hasValue(\"%s\")) {", field.getCode());
            print("            f.byField(\"%s\", headIn.getString(\"%s\"));", field.getCode(), field.getCode());
            print("        }");
        }

        print("        if (headIn.hasValue(\"searchText_\")) {");
        print("            f.byLink(new String[] { \"%s\" }, headIn.getString(\"searchText_\"));",
                this.getFields().get(0).getCode());
        print("        }");
        print("        f.open();");
        print("        getDataOut().appendDataSet(f.getDataSet());");
        print("        return true;");
        print("    }");
    }

    public void createCodeColumns() {
        for (FieldDefine field : this) {
            print(String.format("new StringColumn(line1, \"%s\", \"%s\", 6));", field.getName(), field.getCode()));
        }
    }

}
