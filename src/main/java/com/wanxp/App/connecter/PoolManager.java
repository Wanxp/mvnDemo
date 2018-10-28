package com.wanxp.App.connecter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class PoolManager implements Pool{

    private static Logger LOGGER = LogManager.getLogger(PoolManager.class);

    private static PoolManager poolManager = new PoolManager();
    private static DBConnectionPool pool = null;
    private static String jdbcDriver = "com.mysql.jdbc.Driver"; //数据库驱动
    private static String dbUrl = "jdbc:mysql://localhost:32773/wifidb?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";//数据库url
//    private static String dbUrl = "jdbc:mysql://localhost:3306/wifidb?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";//数据库url
//    private static String dbUrl = "jdbc:mysql://localhost:3306";//数据库url
    private static String dbUserName = "root";//数据库账户
    private static String dbPassword = "0001";//数据库密码

    /**
     * 构造器
     */
    private PoolManager() {
    }

    /**
     *
     * @return
     */
    public static PoolManager getInstance() {
        return poolManager;
    }

    /**
     * 初始化
     */
    static {

        pool = new DBConnectionPool(jdbcDriver, dbUrl, dbUserName, dbPassword);

    }

    @Override
    public int getInitialConnections() {
        return pool.getInitalConnections();
    }

    @Override
    public void setInitialConnections(int initialConnections) {
        pool.setInitalConnections(initialConnections);
    }

    @Override
    public int getIncrementalConnections() {
        return pool.getIncrementalConnections();
    }

    @Override
    public void setIncrementalConnections(int incrementalConnections) {
        pool.setIncrementalConnections(incrementalConnections);
    }

    @Override
    public int getMaxConnections() {
        return pool.getMaxConnections();
    }

    @Override
    public void setMaxConnections(int maxConnections) {
        pool.setMaxConnections(maxConnections);
    }

    @Override
    public void initPool() {
        try {
            pool.createPool();
        } catch (Exception e) {
            LOGGER.error("DB Connection pool create failed.", e);
        }
    }

    /**
     * 带参初始化
     * @param jdbcDriver
     * @param dbUrl
     * @param dbUserName
     * @param dbPassword
     */
    public void initPool(String jdbcDriver, String dbUrl, String dbUserName, String dbPassword) {
        try {
            pool = new DBConnectionPool(jdbcDriver, dbUrl, dbUserName, dbPassword);
            initPool();
        } catch (Exception e) {
            LOGGER.error("DB Connection pool create failed.", e);
        }
    }

    @Override
    public Connection getConnection() {
        Connection connection = null;
        try {
            connection = pool.getConnection();
        } catch (Exception e) {
            LOGGER.error("DB Connection can not be created.", e);
        }
        return connection;
    }

    @Override
    public void returnConnection(Connection conn) {
        pool.returnConnection(conn);
    }

    @Override
    public void refreshConnections() {
        try {
            pool.refreshConnections();
        } catch (SQLException e) {
            LOGGER.error("DB Connection pool can not be refreshed.", e);
        }
    }

    @Override
    public void closeConnectionPool() {
        pool.closeConnectionPool();
    }
}
