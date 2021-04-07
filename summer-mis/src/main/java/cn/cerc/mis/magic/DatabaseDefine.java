package cn.cerc.mis.magic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.cerc.core.ClassConfig;
import cn.cerc.core.ISession;
import cn.cerc.db.mysql.SqlQuery;
import cn.cerc.mis.SummerMIS;
import cn.cerc.mis.custom.SessionDefault;

public class DatabaseDefine implements Iterable<TableDefine> {
    private static final Logger log = LoggerFactory.getLogger(DatabaseDefine.class);
    // 获取所有表
    public static final String DataTables = "information_schema.tables";
    // 获取表字段
    public static final String TableColumns = "information_schema.columns";
    // 所有的表对象
    private static Map<String, TableDefine> tables = new HashMap<>();

    static {
        if (tables.size() == 0) {
            ISession session = new SessionDefault();
            ClassConfig config = new ClassConfig(DatabaseDefine.class, SummerMIS.ID);

            String dbName = config.getString("rds.database", null);
            if (dbName != null) {
                SqlQuery ds = new SqlQuery(session);
                ds.add("select table_name,table_comment from %s where table_schema='%s'", DataTables, dbName);
                ds.open();
                while (ds.fetch()) {
                    TableDefine table = new TableDefine();
                    table.setCode(ds.getString("table_name"));
                    table.setComment(ds.getString("table_comment"));
                    table.init(session, dbName);
                    tables.put(ds.getString("table_name"), table);
                }
            } else {
                log.error("rds.database not config.");
            }
        }
    }

    public static Map<String, TableDefine> getTables() {
        return tables;
    }

    public static TableDefine getTable(String table) {
        return tables.get(table);
    }

    @Override
    public Iterator<TableDefine> iterator() {
        List<TableDefine> list = new ArrayList<>();
        for (String code : tables.keySet()) {
            list.add(tables.get(code));
        }
        return list.iterator();
    }

    public static FieldDefine getField(String fieldPath) {
        String[] args = fieldPath.split("\\.");
        TableDefine table = getTable(args[0]);
        if (table != null)
            return table.getField(args[1]);
        else
            return null;
    }

}
