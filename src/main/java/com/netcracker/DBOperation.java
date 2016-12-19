package com.netcracker;

import com.netcracker.dao.Session;
import com.netcracker.dao.DAOFactory;
import com.netcracker.model.Book;
import com.netcracker.model.BookShelf;
import exception.DaoException;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class DBOperation {

    public static void main(String[] args) {
        Locale.setDefault(new Locale("en", "US"));
        try {
            LogManager.getLogManager().readConfiguration(
                    DBOperation.class.getClassLoader().getResourceAsStream("logging.properties"));
        } catch (IOException e) {
            System.err.println("Could not setup logger configuration: " + e.toString());
        }

        Logger LOGGER = Logger.getLogger(DBOperation.class.getName());

        DAOFactory daoFactory = DAOFactory.getDAOFactory("config.properties");
        Session operation = daoFactory.getOperation();

        for(long i = 0; i < 10; i++) {
            BookShelf bookShelf = new BookShelf();
            bookShelf.setId(i);
            bookShelf.setNameBookShelf(i + "a");
            bookShelf.setCount(i+10);
            try {
                operation.save(bookShelf);
                LOGGER.log(Level.INFO, bookShelf.toString() + " : save");
            } catch (DaoException e) {
                LOGGER.log(Level.WARNING, "Can't save object: " + bookShelf, e);
            }
        }

        List resBookShelf = operation.findAll(BookShelf.class);

        for(Object o : resBookShelf) {
            LOGGER.log(Level.INFO, o.toString() + " : select");
        }

        for(long i = 0; i < 10; i++) {
            Book book = new Book();
            book.setId(i);
            book.setName(i + "b");
            try {
                operation.save(book);
                LOGGER.log(Level.INFO, book.toString() + " : save");
            } catch (DaoException e) {
                LOGGER.log(Level.WARNING, "Can't save object: " + book, e);
            }
        }

        List resBook = operation.findAll(Book.class);

        for(Object o : resBook) {
            LOGGER.log(Level.INFO, o.toString() + " : select");
        }

    }
}
