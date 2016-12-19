package com.netcracker.model;

import com.netcracker.annotations.FieldName;
import com.netcracker.annotations.TableName;

/**
 * Created by dimka on 18.12.2016.
 */
@TableName(tableName = "book_shelf")
public class BookShelf {

    @FieldName(fieldName = "id")
    private Long id;

    @FieldName(fieldName = "name_book_shelf")
    private String nameBookShelf;

    @FieldName(fieldName = "count")
    private Long count;

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNameBookShelf() {
        return nameBookShelf;
    }

    public void setNameBookShelf(String nameBookShelf) {
        this.nameBookShelf = nameBookShelf;
    }

    @Override
    public String toString() {
        return "id: " + id + ", name: " + nameBookShelf + ", count: " + count;
    }
}
