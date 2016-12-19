package com.netcracker.dao;

import java.util.List;

/**
 * Created by dimka on 18.12.2016.
 */
public interface Session {

    List<Object> findAll(Class objectClass);

    Object getById(Class objectClass, Long id);

    void save(Object object);

    void delete(Object object);

    void update(Object object);

}
