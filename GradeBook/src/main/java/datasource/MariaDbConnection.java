package datasource;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class MariaDbConnection {

    private static Connection conn = null;

    // 加载db.properties文件（从classpath读取，即src/main/resources）
    private static Properties loadDbProperties() {
        Properties props = new Properties();
        try (InputStream input = MariaDbConnection.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (input == null) {
                System.out.println("db.properties not found in classpath, will rely on environment variables or defaults.");
                return props;  // 返回空的props，后续走默认值或环境变量
            }
            props.load(input);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return props;
    }

    public static Connection getConnection() {
        if (conn == null) {
            try {
                Properties props = loadDbProperties();

                // 优先读取环境变量，找不到再从db.properties取，最后兜底默认值
                String dbHost = System.getenv("DB_HOST");
                if (dbHost == null || dbHost.isBlank()) {
                    dbHost = props.getProperty("DB_HOST", "localhost");
                }

                String dbPort = System.getenv("DB_PORT");
                if (dbPort == null || dbPort.isBlank()) {
                    dbPort = props.getProperty("DB_PORT", "3306");
                }

                String dbName = System.getenv("DB_NAME");
                if (dbName == null || dbName.isBlank()) {
                    dbName = props.getProperty("DB_NAME", "gradebook_localized");
                }

                String dbUser = System.getenv("DB_USER");
                if (dbUser == null || dbUser.isBlank()) {
                    dbUser = props.getProperty("DB_USER", "grade_admin");
                }

                String dbPass = System.getenv("DB_PASS");
                if (dbPass == null || dbPass.isBlank()) {
                    dbPass = props.getProperty("DB_PASS", "password");
                }

                // 组装JDBC URL
                String url = String.format("jdbc:mariadb://%s:%s/%s", dbHost, dbPort, dbName);

                System.out.println("Connecting to: " + url);  // 方便排查日志
                conn = DriverManager.getConnection(url, dbUser, dbPass);

            } catch (SQLException e) {
                System.out.println("Database connection failed.");
                e.printStackTrace();
            }
        }
        return conn;
    }

    public static void terminate() {
        try {
            if (conn != null) {
                conn.close();
                conn = null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
