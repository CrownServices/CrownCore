package dev.crown.database.mysql.repository;

import dev.crown.database.common.annotation.Id;
import dev.crown.database.common.repository.AsyncDatabaseRepository;
import dev.crown.database.common.repository.DatabaseRepository;
import dev.crown.database.mysql.MySQLDatabaseProvider;
import dev.crown.database.mysql.utility.MySQLQueryBuilder;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public abstract class MySQLRepository<ID, O> implements DatabaseRepository<ID, O>, AsyncDatabaseRepository<ID, O> {

    public Class<O> entityClass;
    private Class<ID> idField;

    //Cache entities
    Map<ID, O> cache;

    protected String tableName;
    protected MySQLDatabaseProvider manager;

    @SuppressWarnings("unchecked")
    public MySQLRepository() {
        this.entityClass = (Class<O>) ((java.lang.reflect.ParameterizedType)
                getClass().getGenericSuperclass()).getActualTypeArguments()[1];
        for (Field field : entityClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(Id.class)) {
                this.idField = (Class<ID>) field.getType();
                break;
            }
        }
    }

    public MySQLRepository(MySQLDatabaseProvider manager) {
        this();
        this.manager = manager;
        Class<?> repoClass = this.getClass();

        if (repoClass.isAnnotationPresent(dev.crown.database.common.annotation.Collection.class)) {
            this.tableName = repoClass.getAnnotation(dev.crown.database.common.annotation.Collection.class).value();
        } else {
            throw new IllegalStateException("Repository " + repoClass.getSimpleName()
                    + " ist nicht mit @Collection annotiert!");
        }
    }

    private Field getIdField() {
        for (Field field : entityClass.getDeclaredFields()) {
            if (field.isAnnotationPresent(Id.class)) {
                field.setAccessible(true);
                return field;
            }
        }
        throw new IllegalStateException("Keine @Id in " + entityClass.getSimpleName());
    }



    @Override
    public O create(ID id) {
        O obj = onCreate(id);
        try {
            Field idField = getIdField();
            idField.setAccessible(true);
            idField.set(obj, id);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Fehler beim Setzen der ID", e);
        }
        return obj;
    }

    @Override
    public O load(ID id) {
        Field idField = getIdField();
        String idColumn = idField.getName();

        if (cache != null && cache.containsKey(id)) {
            return cache.get(id);
        }

        String query = MySQLQueryBuilder.builder()
                .table(tableName)
                .select("*")
                .where(idColumn, id)
                .build();

        try (ResultSet rs = manager.executeQuery(query)) {
            if (rs.next()) {
                O obj = entityClass.getDeclaredConstructor().newInstance();
                for (Field field : entityClass.getDeclaredFields()) {
                    field.setAccessible(true);
                    Object value = rs.getObject(field.getName());
                    field.set(obj, value);
                }
                obj = onLoad(obj);
                cache.put(id, obj);
                return obj;
            }
        } catch (Exception e) {
            throw new RuntimeException("Fehler beim Laden aus MySQL", e);
        }
        return null;
    }



    @Override
    public void save(O object) {
        try {
            Field idField = getIdField();
            idField.setAccessible(true);
            Object idValue = idField.get(object);

            object = onSave(object);

            MySQLQueryBuilder insertBuilder = MySQLQueryBuilder.builder().table(tableName);
            MySQLQueryBuilder updateBuilder = MySQLQueryBuilder.builder().table(tableName);

            for (Field field : entityClass.getDeclaredFields()) {
                field.setAccessible(true);
                String name = field.getName();
                Object value = field.get(object);
                insertBuilder.insert(name, value);

                if (!field.isAnnotationPresent(Id.class)) {
                    updateBuilder.update(name, value);
                }
            }

            String insert = insertBuilder.build();
            String update = updateBuilder.build().replaceFirst("UPDATE " + tableName + " SET ", "");

            String finalQuery = insert + " ON DUPLICATE KEY UPDATE " + update + ";";
            manager.executeUpdate(finalQuery);

        } catch (Exception e) {
            throw new RuntimeException("Fehler beim Speichern in MySQL", e);
        }
    }


    @Override
    public O findFirstById(ID id) {
        O obj = load(id);
        return obj;
    }

    @Override
    public List<O> findManyById(ID id) {
        O obj = load(id);
        return obj != null ? List.of(obj) : List.of();
    }

    @Override
    public CompletableFuture<O> asyncCreate(ID id) {
        return CompletableFuture.supplyAsync(() -> create(id));
    }

    @Override
    public CompletableFuture<O> asyncLoad(ID id) {
        return CompletableFuture.supplyAsync(() -> load(id));
    }

    @Override
    public CompletableFuture<Void> asyncSave(O object) {
        return CompletableFuture.runAsync(() -> save(object));
    }


    @Override
    public CompletableFuture<O> asyncFindFirstById(ID id) {
        return CompletableFuture.supplyAsync(() -> findFirstById(id));
    }

    @Override
    public CompletableFuture<List<O>> asyncFindManyById(ID id) {
        return CompletableFuture.supplyAsync(() -> findManyById(id));
    }
}
