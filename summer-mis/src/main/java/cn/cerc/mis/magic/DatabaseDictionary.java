package cn.cerc.mis.magic;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JTextField;

import cn.cerc.core.ClassConfig;
import cn.cerc.core.DataSet;
import cn.cerc.core.ISession;
import cn.cerc.core.Record;
import cn.cerc.core.Utils;
import cn.cerc.db.core.IHandle;
import cn.cerc.db.mssql.MssqlServer;
import cn.cerc.db.mysql.MysqlServerMaster;
import cn.cerc.db.mysql.SqlQuery;
import cn.cerc.mis.core.Application;
import cn.cerc.mis.vcl.TApplication;
import cn.cerc.mis.vcl.TButton;
import cn.cerc.mis.vcl.TEdit;
import cn.cerc.mis.vcl.TLabel;
import cn.cerc.mis.vcl.TMainForm;
import cn.cerc.mis.vcl.TPanel;
import cn.cerc.mis.vcl.TStatusBar;

/**
 * 建立数据库字典
 * <p>
 * 注意事项：
 * <p>
 * 1、数据库字典直接输出到项目的src/test/resources下
 * <p>
 * 2、请使用eclipse直接格式化xml
 */
@SuppressWarnings("serial")
public class DatabaseDictionary extends TMainForm implements IHandle {
    // 获取所有表
    private static final String DataTables = "information_schema.tables";
    // 获取表字段
    private static final String TableColumns = "information_schema.columns";
    private static final ClassConfig config = new ClassConfig();

    private final TButton btnSubmit;
    private final JTextField edtServer;
    private final TEdit edtDatabase;
    private ISession session;

    public DatabaseDictionary() {
        super();
        this.setTitle("重置数据库字典");

        this.getContent().setLayout(new GridLayout(2, 1));
        TStatusBar statusBar = this.getStatusBar();

        TPanel gridInput = new TPanel(this);
        gridInput.setLayout(new GridLayout(5, 2));

        gridInput.add(new JPanel());
        gridInput.add(new JPanel());

        new TLabel(new TPanel(gridInput).setAlign(FlowLayout.RIGHT)).setText("请输入服务器地址：");
        edtServer = new TEdit(new TPanel(gridInput).setAlign(FlowLayout.LEFT));

        new TLabel(new TPanel(gridInput).setAlign(FlowLayout.RIGHT)).setText("请输入用户名：");
        new TEdit(new TPanel(gridInput).setAlign(FlowLayout.LEFT));

        new TLabel(new TPanel(gridInput).setAlign(FlowLayout.RIGHT)).setText("请输入密码：");
        new TEdit(new TPanel(gridInput).setAlign(FlowLayout.LEFT));

        new TLabel(new TPanel(gridInput).setAlign(FlowLayout.RIGHT)).setText("请输入数据库名称：");
        edtDatabase = new TEdit(new TPanel(gridInput).setAlign(FlowLayout.LEFT));
        edtDatabase.setText(config.getString("rds.database", "trainingdb"));

        btnSubmit = new TButton(new TPanel(this));
        btnSubmit.setText("执行同步");
        btnSubmit.addActionListener((ActionEvent e) -> {
            if (edtServer.getText().trim().length() == 0) {
                statusBar.setText("服务器地址不允许为空！");
                return;
            }

            try (ISession session = new ISession() {
                private MysqlServerMaster mysql;

                @Override
                public Object getProperty(String key) {
                    if (MssqlServer.SessionId.equals(key)) {
                        if (mysql == null)
                            mysql = new MysqlServerMaster();
                        return mysql;
                    } else
                        return null;
                }

                @Override
                public void setProperty(String key, Object value) {

                }

                @Override
                public boolean logon() {
                    return false;
                }

                @Override
                public void close() {
                    if (mysql != null) {
                        try {
                            mysql.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        mysql = null;
                    }
                }
            }) {
                this.setSession(session);
                this.run();
                statusBar.setText("数据字典创建完成");
            }
        });

        statusBar.setText("请点击执行按钮开始重新生成数据字典！");
    }

    public void run() {
        SqlQuery ds = new SqlQuery(this);
        ds.add("select table_name,table_comment from %s where table_schema='%s'", DataTables, edtDatabase.getText());
        ds.open();
        try {
            File file = new File(".\\src\\test\\resources\\database.xml");
            file.createNewFile();

            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            writer.write("<?xml-stylesheet type=\"text/xsl\" href=\"Database.xsl\"?>");
            writer.write(
                    "<database xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"Database.xsd\">");
            writer.write("<caption>系统数据库结构</caption>");
            writer.write(String.format("<name>%s</name>", edtDatabase.getText()));
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
     * 
     * @param tableName 表名
     */
    public void getOneTableInfo(String tableName) {
        SqlQuery ds = new SqlQuery(this);
        ds.add("select table_comment from %s where table_schema='%s'", DataTables, edtDatabase.getText());
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
        SqlQuery ds = new SqlQuery(this);
        ds.add("select COLUMN_NAME,COLUMN_TYPE,EXTRA,IS_NULLABLE,COLUMN_COMMENT,COLUMN_DEFAULT");
        ds.add("from %s", TableColumns);
        ds.add("where TABLE_SCHEMA='%s' and table_name='%s'", edtDatabase.getText(), tableName);
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
        SqlQuery ds = new SqlQuery(this);
        ds.add("show index from %s", tableName);
        ds.open();
        // 读取全部数据再保存
        Map<String, DataSet> items = new LinkedHashMap<>();
        String oldKeyName = "";
        while (ds.fetch()) {
            String keyName = ds.getString("Key_name");
            DataSet dataIn = items.get(keyName);
            if (dataIn == null && !Utils.isEmpty(keyName)) {
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
            } else if (non_unique == 0) {
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

    @Override
    public ISession getSession() {
        return session;
    }

    @Override
    public void setSession(ISession session) {
        this.session = session;
    }

    public static void main(String[] args) {
        Application.initOnlyFramework();
        TApplication app = new TApplication();
        app.createForm(DatabaseDictionary.class);
        app.run();
    }

}
