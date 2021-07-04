package cn.cerc.db.sqlite;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.cerc.core.ClassConfig;
import cn.cerc.core.Utils;
import cn.cerc.db.SummerDB;
import cn.cerc.db.core.IHandle;
import cn.cerc.db.core.SqlOperator;
import cn.cerc.db.core.SqlServer;

public class SqliteServer implements SqlServer {
    public static final ClassConfig config = new ClassConfig(SqliteServer.class, SummerDB.ID);
    private static final Logger log = LoggerFactory.getLogger(SqliteServer.class);
    private List<String> tables;
    private String database;
    private String path;

    public SqliteServer() {
        super();
        this.database = config.getProperty("sqlite.database", null);
        this.path = System.getProperty("user.home") + System.getProperty("file.separator");
    }

    public SqliteServer(String database) {
        super();
        this.database = database;
    }

    public final Connection getConnection() {
        try {
            Class.forName("org.sqlite.JDBC");
            if (Utils.isEmpty(this.database))
                throw new RuntimeException("sqlite.database is empty");
            return DriverManager.getConnection("jdbc:sqlite:" + this.path + this.database);
        } catch (ClassNotFoundException | SQLException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        if (!this.database.equals(database)) {
            if (tables != null) {
                tables.clear();
                tables = null;
            }
        }
        this.database = database;
    }

    /**
     * 执行表创建指令
     * 
     * @param createTableSql 创建表的sql指令，形如 create table dept(code varchar(10), name
     *                       varchar(30))
     * @param overwrite      创建时，是否强制覆盖，一般仅用于初始化时
     * @return 执行成功返回true
     */
    public final boolean createTable(String createTableSql, boolean overwrite) {
        String table = findTableName(createTableSql);
        if (table == null)
            throw new RuntimeException("sql error: table is null");
        if (!overwrite && getTables().contains(table))
            return false;

        try (Connection conn = this.getConnection()) {
            try (Statement state = conn.createStatement()) {
                state.executeUpdate(String.format("drop table if exists %s", table));
                state.executeUpdate(createTableSql);
                return true;
            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }
    }

    public final void dropTable(String table) {
        try (Connection conn = this.getConnection()) {
            try (Statement state = conn.createStatement()) {
                state.executeUpdate(String.format("drop table if exists %s", table));
            }
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 取得数据库中所有的表名
     * 
     * @return 返回列表
     */
    public final List<String> getTables() {
        if (tables != null)
            return tables;
        tables = new ArrayList<>();
        SqliteQuery query = new SqliteQuery();
        query.add("SELECT name FROM sqlite_master WHERE type='table'");
        query.open();
        while (query.fetch())
            tables.add(query.getString("name"));
        return tables;
    }

    private final String findTableName(String sql) {
        String[] cmd = sql.split("\\(")[0].split(" ");
        String table = null;
        if ((cmd.length == 3) && "create".equals(cmd[0].toLowerCase()) && "table".equals(cmd[1].toLowerCase())) {
            table = cmd[2];
        }
        return table;
    }

    @Override
    public boolean execute(String sql) {
        log.debug(sql);
        try (Statement st = this.getConnection().createStatement()) {
            st.execute(sql);
            return true;
        } catch (SQLException e) {
            log.error("error mssql: {}", sql);
            return false;
        }
    }

    @Override
    public SqliteClient getClient() {
        return new SqliteClient(this.getConnection());
    }

    public static void showTable(String table, String title) {
        if (title != null)
            System.out.println(String.format("====== %s ======", title));
        SqliteQuery query = new SqliteQuery();
        query.add("select * from " + table);
        query.open();
        while (query.fetch())
            System.out.println(query.getCurrent());
    }

    public String getPath() {
        return path;
    }

    public static void main(String[] args) {
        SqliteServer server = new SqliteServer();
        // 建表
        server.createTable(
                "create table user(id integer PRIMARY KEY autoincrement, code varchar(30), name varchar(50), value integer)",
                true);

        // 增加
        SqliteQuery query = new SqliteQuery();
        query.add("select * from user");
        query.open();
        query.append();
        query.setField("code", "001");
        query.setField("name", "张三");
        query.post();

        query.append();
        query.setField("code", "002");
        query.setField("name", "李四");
        query.post();

        showTable("user", "增加后");

        // 修改
        query.first();
        while (query.fetch()) {
            query.edit();
            query.setField("value", query.getInt("id") + 1);
            query.post();
        }
        showTable("user", "修改后");

        // 删除
        SqliteQuery query2 = new SqliteQuery();
        query2.add("select * from user");
        query2.open();
        query2.delete();
        query2.post();
        showTable("user", "删除后");
    }

    @Override
    public SqlOperator getDefaultOperator(IHandle handle) {
        return new SqliteOperator();
    }

}
