package dev.crown.database.common;

import dev.crown.database.common.repository.AsyncDatabaseRepository;
import dev.crown.database.common.repository.DatabaseRepository;

public interface DatabaseProvider {

    /**
     * Returns the name of the database provider.
     * @return the name of the database provider (e.g., "MySQL", "Redisson", etc.)
     */
    String getName();

    <ID, O> DatabaseRepository<ID, O> getRepository(Class<? extends DatabaseRepository<ID, O>> databaseRepositoryClass);

    <ID, O> AsyncDatabaseRepository<ID, O> getAsyncRepository(Class<? extends AsyncDatabaseRepository<ID, O>> databaseRepositoryClass);

    void registerRepository(Class<? extends DatabaseRepository<?, ?>> databaseRepositoryClass);

    void registerAsyncRepository(Class<? extends AsyncDatabaseRepository<?, ?>> asyncDatabaseRepositoryClass);

}
