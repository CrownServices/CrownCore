package dev.crown.database.mysql;

import dev.crown.database.common.DatabaseProvider;
import dev.crown.database.common.repository.AsyncDatabaseRepository;
import dev.crown.database.common.repository.DatabaseRepository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class MySQLDatabaseProvider implements DatabaseProvider {

    private final Map<Class<?>, DatabaseRepository<?, ?>> syncRepositories = new HashMap<>();
    private final Map<Class<?>, AsyncDatabaseRepository<?, ?>> asyncRepositories = new HashMap<>();
    Connection connection;

    MySQLDatabaseProvider(String host, int port, String database, String username, String password) {
        //create a connection to the MySQL database using the provided parameters
        String jdbcUrl = String.format("jdbc:mysql://%s:%d/%s", host, port, database);
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(jdbcUrl, username, password);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("MySQL JDBC Driver not found.", e);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to connect to MySQL database: " + jdbcUrl, e);
        }
    }

    @Override
    public String getName() {
        return "mysql";
    }

    @Override
    @SuppressWarnings("unchecked")
    public <ID, O> DatabaseRepository<ID, O> getRepository(Class<? extends DatabaseRepository<ID, O>> clazz) {
        return (DatabaseRepository<ID, O>) syncRepositories.get(clazz);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <ID, O> AsyncDatabaseRepository<ID, O> getAsyncRepository(Class<? extends AsyncDatabaseRepository<ID, O>> clazz) {
        return (AsyncDatabaseRepository<ID, O>) asyncRepositories.get(clazz);
    }

    @Override
    public void registerRepository(Class<? extends DatabaseRepository<?, ?>> clazz) {
        try {
            syncRepositories.put(clazz, clazz.getDeclaredConstructor(MySQLDatabaseProvider.class).newInstance(this));
        } catch (Exception e) {
            throw new RuntimeException("Failed to register repository: " + clazz, e);
        }
    }

    @Override
    public void registerAsyncRepository(Class<? extends AsyncDatabaseRepository<?, ?>> clazz) {
        try {
            asyncRepositories.put(clazz, clazz.getDeclaredConstructor(MySQLDatabaseProvider.class).newInstance(this));
        } catch (Exception e) {
            throw new RuntimeException("Failed to register async repository: " + clazz, e);
        }
    }

    public ResultSet executeQuery(String query) {
        try {
            return connection.createStatement().executeQuery(query);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int executeUpdate(String query) {
        try {
            return connection.createStatement().executeUpdate(query);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}

