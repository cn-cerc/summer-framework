package cn.cerc.db.mssql;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JdbcMsSql {

    public static final String JDBC_URL = "jdbc:sqlserver://112.124.37.146:1433;databaseName=MIMRC_Std;";
    private static final String user = "sa";
    private static final String password = "Ping0909";

    public static Connection connection;

    public static void main(String[] args) {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            connection = DriverManager.getConnection(JDBC_URL, user, password);
            if (connection != null) {
                DatabaseMetaData meta = connection.getMetaData();
                log.info("Driver Name {}", meta.getDriverName());
                log.info("Driver Version {}", meta.getDriverVersion());
                log.info("Product Name {}", meta.getDatabaseProductName());
                log.info("Product Version {}", meta.getDatabaseProductVersion());
            }
        } catch (Exception sqlException) {
            sqlException.printStackTrace();
        }
    }
}