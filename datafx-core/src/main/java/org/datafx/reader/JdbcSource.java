package org.datafx.reader;

import java.sql.Array;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.datafx.reader.util.JdbcConverter;
import org.datafx.reader.util.JdbcDataSourceUtil;

/**
 *
 * @author johan
 */
public class JdbcSource<T> implements DataReader<T> {

    private final String jdbcUrl;
    private final String selectStatement;
    private final JdbcConverter<T> converter;
    private boolean single;
    private boolean connectionCreated;
    private boolean lastResult;
    private ResultSet resultSet;

    public JdbcSource(String jdbcUrl, JdbcConverter<T> converter, String table, String... cols) {
        this(jdbcUrl, JdbcDataSourceUtil.createSelectStatement(table, cols), converter);
    }

    public JdbcSource(String jdbcUrl, String selectStatement,
            JdbcConverter<T> converter) {
        this.jdbcUrl = jdbcUrl;
        this.selectStatement = selectStatement;
        this.converter = converter;
    }

    public void setSingle(boolean v) {
        this.single = v;
    }

    private synchronized void createConnection() {
        System.out.println("Creating connection");
        if (connectionCreated) {
            System.out.println("Already created");
            return;
        }
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(jdbcUrl);
            Statement query = connection.createStatement();
            if (single) {
                System.out.println("single, maxrows = 1");
                query.setMaxRows(1);
            }
            resultSet = query.executeQuery(selectStatement);
            lastResult = resultSet.next();
           
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        connectionCreated = true;
    }

    public T getData() {
        System.out.println("Get data called, resultset = "+resultSet);
       
        if (!connectionCreated) {
            createConnection();
        }
        if (hasMoreData()) {
            try {
                T answer = converter.convert(resultSet);
                lastResult = resultSet.next();
                return answer;
            } catch (SQLException ex) {
                Logger.getLogger(JdbcSource.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
      
        return null;
    }

    public boolean hasMoreData() {
        if (!connectionCreated) {
            createConnection();
        }
        try {
            return lastResult;
//            if  (resultSet.next()) {
//                resultSet.previous();
//                return true;
//            }
        } catch (Exception ex) {
            Logger.getLogger(JdbcSource.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
}
