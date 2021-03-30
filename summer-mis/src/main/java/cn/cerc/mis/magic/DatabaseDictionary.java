package cn.cerc.mis.magic;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.LinkedHashMap;
import java.util.Map;

import cn.cerc.core.ClassConfig;
import cn.cerc.core.DataSet;
import cn.cerc.core.Record;
import cn.cerc.core.Utils;
import cn.cerc.db.core.IHandle;
import cn.cerc.db.mysql.SqlQuery;
import cn.cerc.mis.SummerMIS;
import cn.cerc.mis.core.Application;
import cn.cerc.mis.core.Handle;

/**
 * 建立数据库字典
 * <p>
 * 注意事项：
 * <p>
 * 1、数据库字典直接输出到项目的src/test/resources下
 * <p>
 * 2、请使用eclipse直接格式化xml
 */
public class DatabaseDictionary {

    private static String DataBase;
    // 获取所有表
    private static final String DataTables = "information_schema.tables";
    // 获取表字段
    private static final String TableColumns = "information_schema.columns";

    private final IHandle handle;
    
    static {
        ClassConfig config = new ClassConfig();
        DataBase = config.getString("rds.database", "trainingdb");
    }

    public DatabaseDictionary() {
        Application.init(null);
        handle  = new Handle(Application.createSession());
    }

    public void run() {
        SqlQuery ds = new SqlQuery(handle);
        ds.add("select table_name,table_comment from %s where table_schema='%s'", DataTables, DataBase);
        ds.open();
        try {
            File file = new File(".\\src\\test\\resources\\database.xml");
            file.createNewFile();

            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            writer.write("<?xml-stylesheet type=\"text/xsl\" href=\"Database.xsl\"?>");
            writer.write("<database xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"Database.xsd\">");
            writer.write("<caption>系统数据库结构</caption>");
            writer.write(String.format("<name>%s</name>", DataBase));
            writer.write("<tables>");
            while (ds.fetch()) {
                String tableName = ds.getString("table_name");
                String comment = ds.getString("table_comment");
                // 视图不处理
                if ("VIEW".equals(comment)) {
                    continue;
                }
                StringBuilder builder1 = new StringBuilder();
                builder1.append("<table");
                builder1.append(String.format(" code=\"%s\"", tableName));
                builder1.append(">");
                builder1.append("<comment>");
                builder1.append(comment);
                builder1.append("</comment>");
                // 获取字段
                builder1.append(getTableColumns(tableName));
                // 获取索引
                builder1.append(getTableIndex(tableName));
                builder1.append("</table>");

                writer.write(builder1.toString());

                // 把缓存区内容压入文件
                writer.flush();
            }

            writer.write("</tables>");
            writer.write("</database>");
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取单个表字段、索引信息
     * @param tableName 表名
     */
    public void getOneTableInfo(String tableName) {
        SqlQuery ds = new SqlQuery(handle);
        ds.add("select table_comment from %s where table_schema='%s'", DataTables, DataBase);
        ds.add("and table_name='%s'", tableName);
        ds.open();

        StringBuilder builder1 = new StringBuilder();
        builder1.append("<table");
        builder1.append(String.format(" code=\"%s\"", tableName));
        builder1.append(">");
        builder1.append("<comment>");
        String name = ds.getString("table_comment");
        builder1.append(name);
        builder1.append("</comment>");

        builder1.append(getTableColumns(tableName));
        builder1.append(getTableIndex(tableName));
        builder1.append("</table>");
        System.out.println(builder1.toString());
    }

    private Object getTableColumns(String tableName) {
        StringBuilder builder2 = new StringBuilder();
        builder2.append("<columns>");
        SqlQuery ds = new SqlQuery(handle);
        ds.add("select COLUMN_NAME,COLUMN_TYPE,EXTRA,IS_NULLABLE,COLUMN_COMMENT,COLUMN_DEFAULT");
        ds.add("from %s", TableColumns);
        ds.add("where TABLE_SCHEMA='%s' and table_name='%s'", DataBase, tableName);
        ds.open();
        while (ds.fetch()) {
            StringBuilder builder3 = new StringBuilder();
            builder3.append("<column ");
    
            String code = ds.getString("COLUMN_NAME");
            builder3.append(String.format("code=\"%s\"", code));
            
            String dataType = ds.getString("COLUMN_TYPE");
            if (dataType.contains("unsigned")) {
                dataType = dataType.substring(0, dataType.indexOf(")") + 1);
            }
            builder3.append(String.format(" type=\"%s\"", dataType));
            String extra = ds.getString("EXTRA");
            if ("auto_increment".equals(extra)) {
                builder3.append(" auto_increment=\"true\"");
            }
            String nullable = ds.getString("IS_NULLABLE");
            builder3.append(String.format(" null=\"%s\"", "YES".equals(nullable) ? "true" : "false"));
            String def = ds.getString("COLUMN_DEFAULT");
            builder3.append(String.format(" default=\"%s\"", def));
            builder3.append(">");
    
            builder3.append("<comment>");
            String name = ds.getString("COLUMN_COMMENT");
            builder3.append(name);
            builder3.append("</comment>");
    
            builder3.append("</column>");
            builder2.append(builder3);
        }
        builder2.append("</columns>");
        return builder2.toString();
    }

    private Object getTableIndex(String tableName) {
        StringBuilder builder2 = new StringBuilder();
        builder2.append("<indexs>");
        SqlQuery ds = new SqlQuery(handle);
        ds.add("show index from %s", tableName);
        ds.open();
        // 读取全部数据再保存
        Map<String, DataSet> items = new LinkedHashMap<>();
        String oldKeyName = "";
        while (ds.fetch()) {
            String keyName = ds.getString("Key_name");
            DataSet dataIn = items.get(keyName);
            if (dataIn == null && Utils.isNotEmpty(keyName)) {
                dataIn = new DataSet();
                dataIn.getHead().copyValues(ds.getCurrent(), "Non_unique", "Key_name");
                items.put(keyName, dataIn);
                oldKeyName = keyName;
            } else {
                dataIn = items.get(oldKeyName);
            }
            dataIn.append();
            dataIn.setField("Column_name", ds.getString("Column_name"));
            dataIn.setField("Collation", ds.getString("Collation"));
        }
        for (String key : items.keySet()) {
            DataSet dataIn = items.get(key);
            Record record = dataIn.getHead();
            int non_unique = record.getInt("Non_unique");
            String keyName = record.getString("Key_name");
            StringBuilder builder3 = new StringBuilder();
            builder3.append("<index ");
            if (non_unique == 0 && keyName.equals("PRIMARY")) {
                builder3.append("type=\"primary\"");
            } else if (non_unique == 0){
                builder3.append("type=\"unique\"");
            } else {
                builder3.append("type=\"normal\"");
            }
            builder3.append(String.format(" code=\"%s\">", record.getString("Key_name")));
            while (dataIn.fetch()) {
                builder3.append(String.format("<field sort=\"%s\" code=\"%s\" />", 
                        "A".equals(dataIn.getString("Collation")) ? "ASC" : "null", dataIn.getString("Column_name")));
            }
            builder3.append("</index>");
            builder2.append(builder3);
        }
        builder2.append("</indexs>");
        return builder2.toString();
    }

    public static void main(String[] args) {
        Application.init(SummerMIS.ID);
        DatabaseDictionary obj = new DatabaseDictionary();
        obj.run();
    }
}
