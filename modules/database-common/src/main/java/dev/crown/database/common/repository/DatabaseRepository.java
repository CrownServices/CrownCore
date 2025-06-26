package dev.crown.database.common.repository;

import java.util.List;

public interface DatabaseRepository<ID, O> {

    O onCreate(ID id);

    O onLoad(O object);

    O onSave(O object);

    O create(ID id);

    O load(ID id);

    void save(O object);

    O findFirstById(ID id);

    List<O> findManyById(ID id);

}
