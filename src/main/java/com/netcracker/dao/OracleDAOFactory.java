package com.netcracker.dao;

import com.netcracker.DBOperation;
import com.netcracker.dao.impl.SessionImpl;
import exception.DaoException;
import oracle.jdbc.pool.OracleDataSource;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Created by dimka on 18.12.2016.
 */
public class OracleDAOFactory<E> extends DAOFactory {

    private OracleDataSource dataSource = null;
    private String propertyFile = null;

    public OracleDAOFactory(String propertyFile) {
        this.propertyFile = propertyFile;
    }

    public Session getOperation() {
        return new SessionImpl(getDataSource());
    }

    private DataSource getDataSource() {
        Properties config = new Properties();
        InputStream inputStream = null;

        try {
            inputStream = DBOperation.class.getClassLoader().getResourceAsStream(propertyFile);
            config.load(inputStream);
            dataSource = new OracleDataSource();
            dataSource.setURL(config.getProperty("ORACLE_DB_URL"));
            dataSource.setUser(config.getProperty("ORACLE_DB_USERNAME"));
            dataSource.setPassword(config.getProperty("ORACLE_DB_PASSWORD"));
        } catch (IOException e) {
            new DaoException(e.getMessage(), e);
        } catch (SQLException e) {
            new DaoException(e.getMessage(), e);
        } finally {
            try {
                if(inputStream != null) inputStream.close();
            } catch (IOException e) {
            }
        }

        return dataSource;
    }
}
