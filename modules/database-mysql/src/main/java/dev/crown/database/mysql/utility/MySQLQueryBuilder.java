package dev.crown.database.mysql.utility;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

public class MySQLQueryBuilder {

    private String table;
    private final Map<String, Object> columns = new LinkedHashMap<>();
    private final Map<String, Object> where = new LinkedHashMap<>();
    private String queryType = "SELECT";
    private String[] selectColumns = new String[]{"*"};

    private final List<String> statements = new ArrayList<>();

    private MySQLQueryBuilder() {

    }

    public static MySQLQueryBuilder builder() {
        return new MySQLQueryBuilder();
    }

    public MySQLQueryBuilder table(String table) {
        this.table = table;
        return this;
    }

    public MySQLQueryBuilder select(String... columns) {
        this.queryType = "SELECT";
        if (columns != null && columns.length > 0) {
            this.selectColumns = columns;
        }
        return this;
    }

    public MySQLQueryBuilder insert(String column, Object value) {
        this.queryType = "INSERT";
        this.columns.put(column, value);
        return this;
    }

    public MySQLQueryBuilder update(String column, Object value) {
        this.queryType = "UPDATE";
        this.columns.put(column, value);
        return this;
    }

    public MySQLQueryBuilder where(String column, Object value) {
        this.where.put(column, value);
        return this;
    }

    public String build() {
        // Support for multiple statements
        if (!statements.isEmpty()) {
            // Add the current statement if not empty
            String current = buildSingle();
            if (current != null && !current.isBlank()) {
                statements.add(current);
            }
            return String.join("; ", statements) + ";";
        } else {
            return buildSingle();
        }
    }

    private String buildSingle() {
        switch (queryType) {
            case "SELECT":
                return buildSelect();
            case "INSERT":
                return buildInsert();
            case "UPDATE":
                return buildUpdate();
            default:
                throw new IllegalStateException("Unknown query type: " + queryType);
        }
    }

    private String buildSelect() {
        StringJoiner sj = new StringJoiner(", ");
        for (String col : selectColumns) {
            sj.add(col);
        }
        StringBuilder sb = new StringBuilder("SELECT ")
                .append(sj)
                .append(" FROM ")
                .append(table);
        if (!where.isEmpty()) {
            sb.append(" WHERE ").append(buildWhere());
        }
        return sb.toString();
    }

    private String buildInsert() {
        StringJoiner cols = new StringJoiner(", ");
        StringJoiner vals = new StringJoiner(", ");
        for (Map.Entry<String, Object> entry : columns.entrySet()) {
            cols.add(entry.getKey());
            vals.add(formatValue(entry.getValue()));
        }
        return "INSERT INTO " + table + " (" + cols + ") VALUES (" + vals + ")";
    }

    private String buildUpdate() {
        StringJoiner sets = new StringJoiner(", ");
        for (Map.Entry<String, Object> entry : columns.entrySet()) {
            sets.add(entry.getKey() + "=" + formatValue(entry.getValue()));
        }
        StringBuilder sb = new StringBuilder("UPDATE ")
                .append(table)
                .append(" SET ")
                .append(sets);
        if (!where.isEmpty()) {
            sb.append(" WHERE ").append(buildWhere());
        }
        return sb.toString();
    }

    private String buildWhere() {
        StringJoiner sj = new StringJoiner(" AND ");
        for (Map.Entry<String, Object> entry : where.entrySet()) {
            sj.add(entry.getKey() + "=" + formatValue(entry.getValue()));
        }
        return sj.toString();
    }

    private String formatValue(Object value) {
        if (value == null) return "NULL";
        if (value instanceof Number) return value.toString();
        return "'" + value.toString().replace("'", "''") + "'";
    }

    /**
     * Finalizes the current statement and prepares for a new one.
     * Returns this builder for chaining.
     */
    public MySQLQueryBuilder and() {
        String stmt = buildSingle();
        if (stmt != null && !stmt.isBlank()) {
            statements.add(stmt);
        }
        // Reset state for next statement
        columns.clear();
        where.clear();
        queryType = "SELECT";
        selectColumns = new String[]{"*"};
        // table is not cleared to allow for multiple statements on the same table
        return this;
    }

    /**
     * Clears all statements and resets the builder.
     */
    public void clearAll() {
        clear();
        statements.clear();
    }

    public void clear() {
        columns.clear();
        where.clear();
        table = null;
        queryType = "SELECT";
        selectColumns = new String[]{"*"};
    }
}
