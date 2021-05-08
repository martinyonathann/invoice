package connection;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseConnection {
    private Connection conn;
    public Connection getConnection(){
        String rootPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        String appConfPath = rootPath + "application.properties";
        Properties config = new Properties();
        try {
            config.load(new FileInputStream(appConfPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (conn == null) {
            try {
                DriverManager.registerDriver(new org.postgresql.Driver());
                String ip = config.getProperty("IP");
                String port = config.getProperty("PORT");
                String db = config.getProperty("DATABASE");
                String user = config.getProperty("USERNAME");
                String password = config.getProperty("PASSWORD");
                conn = DriverManager.getConnection("jdbc:postgresql://" + ip + ":" + port + "/" + db, user, password);
//                Logger.getLogger(DatabaseConnection.class.getName()).log(Level.INFO, "Database Connected!");
            } catch (SQLException ex) {
                Logger.getLogger(DatabaseConnection.class.getName()).log(Level.SEVERE, "Cannot connect the database!", ex);
            }
        }
        return conn;
    }
}
