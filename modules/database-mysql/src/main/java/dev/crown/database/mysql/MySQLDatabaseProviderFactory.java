package dev.crown.database.mysql;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class MySQLDatabaseProviderFactory {

    String host = "localhost"; // Default MySQL host
    int port = 3306; // Default MySQL port
    String database = "crown"; // Default database name
    String username = "root"; // Default username
    String password = ""; // Default password

    private MySQLDatabaseProviderFactory() {

    }

    public static MySQLDatabaseProviderFactory builder() {
        return new MySQLDatabaseProviderFactory();
    }

    public MySQLDatabaseProviderFactory host(String host) {
        this.host = host;
        return this;
    }

    public MySQLDatabaseProviderFactory port(int port) {
        this.port = port;
        return this;
    }

    public MySQLDatabaseProviderFactory database(String database) {
        this.database = database;
        return this;
    }

    public MySQLDatabaseProviderFactory username(String username) {
        this.username = username;
        return this;
    }

    public MySQLDatabaseProviderFactory password(String password) {
        this.password = password;
        return this;
    }

    public MySQLDatabaseProviderFactory fromResource(String resource) {
        Properties props = new Properties();
        try (InputStream in = getClass().getClassLoader().getResourceAsStream(resource)) {
            if (in == null) {
                throw new IllegalArgumentException("Resource not found: " + resource);
            }
            props.load(in);
            if (props.getProperty("host") != null) this.host(props.getProperty("host"));
            if (props.getProperty("port") != null) this.port(Integer.parseInt(props.getProperty("port")));
            if (props.getProperty("database") != null) this.database(props.getProperty("database"));
            if (props.getProperty("username") != null) this.username(props.getProperty("username"));
            if (props.getProperty("password") != null) this.password(props.getProperty("password"));
        } catch (IOException e) {
            throw new RuntimeException("Failed to load MySQL configuration from resource: " + resource, e);
        }
        return this;
    }

    public MySQLDatabaseProvider build() {
        if (host == null || database == null || username == null || password == null) {
            throw new IllegalStateException("MySQL configuration is incomplete. Please set all required fields.");
        }
        return new MySQLDatabaseProvider(host, port, database, username, password);
    }

}
