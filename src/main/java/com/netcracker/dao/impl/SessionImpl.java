package com.netcracker.dao.impl;

import com.netcracker.annotations.FieldName;
import com.netcracker.annotations.TableName;
import com.netcracker.dao.Session;
import exception.DaoException;
import oracle.jdbc.pool.OracleDataSource;

import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dimka on 18.12.2016.
 */
public class SessionImpl implements Session {

    private OracleDataSource dataSource;

    public SessionImpl(DataSource dataSource) {
        this.dataSource = (OracleDataSource) dataSource;
    }

    @Override
    public List findAll(Class objectClass) {

        List resultList = null;
        Connection dbConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            dbConnection = dataSource.getConnection();
            preparedStatement = dbConnection.prepareStatement(getSqlQuetyForSelect(objectClass, null));
            resultSet = preparedStatement.executeQuery();
            resultList = rowMapper(objectClass, resultSet);
        } catch (SQLException e) {
            new DaoException(e.getMessage(), e);
        } finally {
            try {
                if(resultSet != null) resultSet.close();
                if(preparedStatement != null) preparedStatement.close();
                if(dbConnection != null) dbConnection.close();
            } catch (SQLException e) {
            }
        }

        return resultList;
    }

    @Override
    public Object getById(Class objectClass, Long id) {

        Object object = null;

        Connection dbConnection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            dbConnection = dataSource.getConnection();
            preparedStatement = dbConnection.prepareStatement(getSqlQuetyForSelect(objectClass, id));
            resultSet = preparedStatement.executeQuery();

            object = rowMapperObject(objectClass, resultSet, 1);

        } catch (SQLException e) {
            new DaoException(e.getMessage(), e);
        } finally {
            try {
                if(resultSet != null) resultSet.close();
                if(preparedStatement != null) preparedStatement.close();
                if(dbConnection != null) dbConnection.close();
            } catch (SQLException e) {
            }
        }

        return object;
    }

    @Override
    public void save(Object object) {
        Connection dbConnection = null;
        PreparedStatement preparedStatement = null;

        try {
            dbConnection = dataSource.getConnection();
            preparedStatement = dbConnection.prepareStatement(getSqlQueryForSave(object));

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            new DaoException(e.getMessage(), e);
        }
        finally {
            try {
                if(preparedStatement != null) preparedStatement.close();
                if(dbConnection != null) dbConnection.close();
            } catch (SQLException e) {
            }
        }
    }

    @Override
    public void delete(Object object) {

    }

    @Override
    public void update(Object object) {

    }

    private String getSqlQueryForSave(Object object) {
        StringBuffer resultQuery = new StringBuffer("INSERT INTO ");
        Class objectClass = object.getClass();

        TableName tableName = (TableName) objectClass.getAnnotation(TableName.class);
        resultQuery.append(tableName.tableName() + "(");

        Field[] fields = objectClass.getDeclaredFields();
        for(Field field : fields) {
            FieldName fieldName = field.getAnnotation(FieldName.class);
            resultQuery.append(fieldName.fieldName() + ",");
        }
        resultQuery.deleteCharAt(resultQuery.length() - 1);
        resultQuery.append(") VALUES (");

        Method[] methods = new Method[fields.length];
        for(int i = 0; i < fields.length; i++) {
            methods[i] = getGetterMethods(fields[i], object);
            try {
                if(Long.class == methods[i].getReturnType()) {
                    resultQuery.append(methods[i].invoke(object, null) + ",");
                } else if (String.class == methods[i].getReturnType()) {
                    resultQuery.append("'" + methods[i].invoke(object, null) + "',");
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                    new DaoException(e.getMessage(), e);
            }
        }

        resultQuery.deleteCharAt(resultQuery.length() - 1);
        resultQuery.append(")");
        return resultQuery.toString();
    }

    private String getSqlQuetyForSelect(Class objectClass, Long id) {
        StringBuffer resultQuery = new StringBuffer("SELECT ");

        Field[] fields = objectClass.getDeclaredFields();
        for(Field field : fields) {
            FieldName fieldName = field.getAnnotation(FieldName.class);
            resultQuery.append(fieldName.fieldName() + ",");
        }
        resultQuery.deleteCharAt(resultQuery.length() - 1);

        TableName tableName = (TableName) objectClass.getAnnotation(TableName.class);
        resultQuery.append(" FROM " + tableName.tableName());

        if(id != null) {
            resultQuery.append(" WHERE ");
            for(Field field : fields) {
                FieldName fieldName = field.getAnnotation(FieldName.class);
                if(fieldName.fieldName().contains("id")) {
                    resultQuery.append(fieldName.fieldName() + " = " + id);
                }
            }
        }

        return resultQuery.toString();
    }

    private Object rowMapperObject(Class objectClass, ResultSet resultSet, Integer rowsExpected) throws SQLException {
        List list = this.rowMapper(objectClass, resultSet);
        if(list.size() > rowsExpected) {
            new DaoException("Return more than " + rowsExpected + " objects");
        }
        return list.get(0);
    }

    private List rowMapper(Class objectClass, ResultSet resultSet) throws SQLException {

        List list = new ArrayList();

        Field[] fields = objectClass.getDeclaredFields();
        Method[] methods = new Method[fields.length];
        for(int i = 0; i < fields.length; i++) {
            methods[i] = getSetterMethods(fields[i], objectClass);
        }

        Object object = null;
        try {
            while (resultSet.next()) {
                object = objectClass.newInstance();
                for(int i = 0; i < fields.length; i++) {
                    if(Long.class == methods[i].getParameterTypes()[0]) {
                        Long resLong = resultSet.getLong(fields[i].getAnnotation(FieldName.class).fieldName());
                        methods[i].invoke(object, resLong);
                    } else if(String.class == methods[i].getParameterTypes()[0]) {
                        String resString = resultSet.getString(fields[i].getAnnotation(FieldName.class).fieldName());
                        methods[i].invoke(object, resString);
                    }
                }
                list.add(object);
            }
        } catch (SQLException e ) {
            new DaoException(e.getMessage(), e);
        } catch (IllegalAccessException | InvocationTargetException |InstantiationException e ) {
            new DaoException(e.getMessage(), e);
        }

        return list;
    }

    private Method getGetterMethods (Field field, Object object) {
        for (Method method : object.getClass().getMethods()) {
            if ((method.getName().startsWith("get")) && (method.getName().length() == (field.getName().length() + 3))) {
                if (method.getName().toLowerCase().endsWith(field.getName().toLowerCase())) {
                    return method;
                }
            }
        }
        return null;
    }

    private Method getSetterMethods (Field field, Class objectClass) {
        for (Method method : objectClass.getMethods()) {
            if ((method.getName().startsWith("set")) && (method.getName().length() == (field.getName().length() + 3))) {
                if (method.getName().toLowerCase().endsWith(field.getName().toLowerCase())) {
                    return method;
                }
            }
        }
        return null;
    }
}