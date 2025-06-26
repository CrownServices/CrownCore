package dev.crown.database.common.repository;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface AsyncDatabaseRepository<ID, O> {

    CompletableFuture<O> onAsyncCreate(ID id);

    CompletableFuture<O> onAsyncLoad(O object);

    CompletableFuture<O> onAsyncSave(O object);

    CompletableFuture<O> asyncCreate(ID id);

    CompletableFuture<O> asyncLoad(ID id);

    CompletableFuture<Void> asyncSave(O object);

    CompletableFuture<O> asyncFindFirstById(ID id);

    CompletableFuture<List<O>> asyncFindManyById(ID id);

}
