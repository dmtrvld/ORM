package com.netcracker.dao;

/**
 * Created by dimka on 18.12.2016.
 */
public abstract class DAOFactory {

    public static DAOFactory getDAOFactory(String propertyFile) {
        return new OracleDAOFactory(propertyFile);
    }

    public abstract Session getOperation();

}
