package com.netcracker.model;

import com.netcracker.annotations.FieldName;
import com.netcracker.annotations.TableName;

/**
 * Created by dimka on 18.12.2016.
 */
@TableName(tableName = "Book")
public class Book {

    @FieldName(fieldName = "id")
    private Long id;

    @FieldName(fieldName = "name")
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "id: " + id + ", name: " + name;
    }
}
