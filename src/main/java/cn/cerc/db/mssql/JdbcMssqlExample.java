package cn.cerc.db.mssql;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JdbcMssqlExample {

    /**
     * 数据库引擎
     */
    private static final String JDBC_DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    /**
     * 注意若出现加载或者连接数据库失败一般是这里出现问题
     */
    public static final String JDBC_URL = "jdbc:sqlserver://127.0.0.1:1433;databaseName=MIMRC_Std;";
    /**
     * 用户名称
     */
    private static final String JDBC_USER = "sa";
    /**
     * 用户密码
     */
    private static final String JDBC_PASSWORD = "sa";
    private static final Logger log = LoggerFactory.getLogger(JdbcMssqlExample.class);

    public static void main(String[] args) {
        try {
            // 1、注册数据库驱动
            Class.forName(JDBC_DRIVER);
        } catch (ClassNotFoundException e) {
            log.error("jdbc driver load error {}", e.getMessage());
            System.exit(0);
        }

        try {
            // 2、创建数据库连接
            Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
            if (connection == null) {
                log.error("jdbc connection is error");
                System.exit(0);
            }

            // 查询连接信息
            DatabaseMetaData meta = connection.getMetaData();
            log.info("Driver Name: {}", meta.getDriverName());
            log.info("Driver Version: {}", meta.getDriverVersion());
            log.info("Product Name: {}", meta.getDatabaseProductName());
            log.info("Product Version: {}", meta.getDatabaseProductVersion());

            // 连接数据库对象
            Statement statement = connection.createStatement();

            // 创建表格
            log.info("------开始创建表------");
            String query = "create table test_userInfo(ID_ NCHAR(11), Name_ NCHAR(10))";
            statement.executeUpdate(query);// 执行SQL命令对象
            log.info("------表创建成功------");

            // 插入数据
            log.info("------开始插入数据------");
            String inert_1 = "insert into test_userInfo values('1','刘备')";
            statement.executeUpdate(inert_1);

            String inert_2 = "insert into test_userInfo values('2','张飞')";
            statement.executeUpdate(inert_2);

            String inert_3 = "insert into test_userInfo values('3','关羽')";
            statement.executeUpdate(inert_3);
            log.info("------插入数据成功------");

            // 返回SQL语句查询结果集(集合)
            ResultSet result = statement.executeQuery("select * from test_userInfo");
            // 循环输出每一条记录
            while (result.next()) {
                // 输出每个字段
                log.info("{} {}", result.getString("ID_"), result.getString("Name_"));
            }

            log.info("------开始删除表格------");
            statement.executeUpdate("drop table test_userInfo");
            log.info("------删除表格成功------");

            // 3、关闭数据连接
            statement.close();
            connection.close();
        } catch (Exception e) {
            log.error("数据库连接失败 {}", e.getMessage());
            System.exit(0);
        }
    }
}