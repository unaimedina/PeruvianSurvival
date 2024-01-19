package gg.voltic.hope.utils;

import gg.voltic.hope.Hope;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.sql.Connection;
import java.sql.DriverManager;

public class MySQLConnector {

    private final @NotNull FileConfiguration config = Hope.getInstance().getConfig();

    private final String host = config.getString("MYSQL.HOST");
    private final String port = config.getString("MYSQL.PORT");
    private final String database = config.getString("MYSQL.DATABASE");
    private final String username = config.getString("MYSQL.USERNAME");
    private final String password = config.getString("MYSQL.PASSWORD");

    private Connection connection;

    public MySQLConnector() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            this.getConnection();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error connecting to the database", e);
        }

        return connection;
    }

}
