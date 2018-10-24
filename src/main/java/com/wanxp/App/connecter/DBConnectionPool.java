package com.wanxp.App.connecter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.util.Enumeration;
import java.util.Vector;


/**
 *  自定义数据库连接池
 */
public class DBConnectionPool {
    private static final Logger LOGGER = LogManager.getLogger(DBConnectionPool.class.getName());

    private String jdbcDriver = ""; //数据库驱动
    private String dbUrl = "";//数据库url
    private String dbUserName = "";//数据库账户
    private String dbPassword = "";//数据库密码

    private int initalConnections = 10;//初始连接池大小
    private int maxConnections = 50; //最大连接池大小
    private Vector connections = null;//连接池存放向量
    private int incrementalConnections = 5;

    /**
     * 连接池构造方法
     * @param jdbcDriver String JDBC 驱动类
     * @param dbUrl String 数据库 URL
     * @param dbUserName String 数据库用户名
     * @param dbPassword String 数据库密码
     */
    public DBConnectionPool(String jdbcDriver, String dbUrl, String dbUserName, String dbPassword) {
        this.jdbcDriver = jdbcDriver;
        this.dbUrl = dbUrl;
        this.dbUserName = dbUserName;
        this.dbPassword = dbPassword;
    }

    /**
     * 获取初始连接池大小
     * @return
     */
    public int getInitalConnections() {
        return initalConnections;
    }

    /**
     *  设定初始连接池大小
     * @param initalConnections int  初始连接池大小
     */
    public void setInitalConnections(int initalConnections) {
        this.initalConnections = initalConnections;
    }

    /**
     *  获取可创建的最大连接数量
     * @return
     */
    public int getMaxConnections() {
        return maxConnections;
    }

    /**
     *  设定最大可连接数量
     * @param maxConnections 最大连接池大小
     */
    public void setMaxConnections(int maxConnections) {
        this.maxConnections = maxConnections;
    }

    /**
     * 创建连接池容器
     * @throws Exception
     */
    public synchronized void createPool() throws  Exception {
        if (connections != null)
            return;
        Driver driver = (Driver) (Class.forName(this.jdbcDriver).newInstance());
        DriverManager.registerDriver(driver);
        connections = new Vector();
        createConnections(this.initalConnections);
        LOGGER.info("DB Connection Pool create successed!");
    }

    /**
     * 创建连接并加入至连接池
     * @param numConnections
     * @throws Exception
     */
    private void createConnections(int numConnections) throws Exception{
        for (int i = 0;i < numConnections;i++) {
            if (this.maxConnections <=0 || this.connections.size() > this.maxConnections)
                break;
            try {
                connections.addElement(new PooledConnection(newConnection()));
            }catch (SQLException e) {
                LOGGER.error(e);
            }
        }
    }

    /**
     * 建立连接
     * @return
     * @throws SQLException
     */
    private Connection newConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(dbUrl, dbUserName, dbPassword);
        if (connections.size() == 0) {
            DatabaseMetaData metaData = connection.getMetaData();
            int driverMaxConnections = metaData.getMaxConnections();
            if (driverMaxConnections > 0 && this.maxConnections < driverMaxConnections) {
                this.maxConnections = driverMaxConnections;
            }
        }
        return connection;
    }

    /**
     * 获取连接池中连接
     * @return
     * @throws SQLException
     * @throws InterruptedException
     */
    public synchronized Connection getConnection() throws Exception {
        if (connections == null)
            return null;
        Connection connection = getFreeConnection();
        while (connection == null) {
            wait(250);
            connection = getFreeConnection();
        }
        return connection;
    }

    /**
     * 获取连接池中可用连接
     * @return
     * @throws Exception
     */
    private Connection getFreeConnection() throws Exception {
        Connection connection = findFreeConnection();
        if (connection == null) {
            createConnections(incrementalConnections);
            connection = findFreeConnection();
            if (connection == null)
                return null;
        }
        return connection;
    }

    /**
     * 找到或建立新的可用连接
     * @return
     */
    private Connection findFreeConnection() {
        Connection connection = null;
        PooledConnection pooledConnection = null;
        Enumeration enumeration = connections.elements();
        while (enumeration.hasMoreElements()) {
            pooledConnection = (PooledConnection) enumeration.nextElement();
            if (!pooledConnection.isBusy()) {
                connection = pooledConnection.getConnection();
                pooledConnection.setBusy(true);
                if (!testConnection(connection)) {
                    try {
                        connection = newConnection();
                    }catch (SQLException e) {
                        LOGGER.error("DB Connection create fail! ERROR_MESSAGE" + e.getMessage());
                        return null;
                    }
                }
                pooledConnection.setConnection(connection);
            }
            break;
        }
        return connection;
    }

    /**
     * 测试连接
     * @param connection
     * @return
     */
    private boolean testConnection(Connection connection) {
        try {
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            LOGGER.error("DB Connection connection test fail! ERROR_MESSAGE:" + e.getMessage());
            closeConnection(connection);
            return false;
        }
        return true;
    }

    /**
     * 关闭连接
     * @param connection
     */
    private void closeConnection(Connection connection) {
        try {
            connection.close();
        }catch (SQLException e) {
            LOGGER.error("DB Connection can not closed! ERROR_MESSAGE:" + e.getMessage());
        }
    }

    public void returnConnection(Connection conn) {
        if (connections == null) {
            LOGGER.warn("DB Connection is not exist and it can not return!");
            return;
        }
        PooledConnection pConn = null;
        Enumeration enumerate = connections.elements();
        while (enumerate.hasMoreElements()) {
            pConn = (PooledConnection) enumerate.nextElement();
            if (conn == pConn.getConnection()) {
                pConn.setBusy(false);
                break;
            }
        }
    }

    /**
     * 关闭连接池
     * @throws SQLException
     */
    private void closeConnectionPool() {
        if (connections == null) {
            LOGGER.warn("DB Connections is null and it can not closed");
            return;
        }
        PooledConnection pooledConnection = null;
        Enumeration enumeration = connections.elements();
        while (enumeration.hasMoreElements()) {
            pooledConnection = (PooledConnection) enumeration.nextElement();
            if (pooledConnection.isBusy()) {
                wait(500);
            }
            closeConnection(pooledConnection.getConnection());
            connections.removeElement(pooledConnection);
        }
        connections = null;
    }

    /**
     * 刷新连接池
     * @throws SQLException
     */
    public synchronized void refreshConnections() throws SQLException {
        if (connections == null) {
            LOGGER.warn("DB Connection Pool is not Exist and it can not refresh!");
            return;
        }
        PooledConnection pooledConnection = null;
        Enumeration enumerate = connections.elements();
        while (enumerate.hasMoreElements()) {
            pooledConnection = (PooledConnection) enumerate.nextElement();
            if (pooledConnection.isBusy()) {
                wait(5000); // 等 5 秒
            }
            closeConnection(pooledConnection.getConnection());
            pooledConnection.setConnection(newConnection());
            pooledConnection.setBusy(false);
        }
    }

    /**
     * 等待方法
     * @param mSeconds
     */
    private void wait(int mSeconds) {
        try {
            Thread.sleep(mSeconds);
        }catch (InterruptedException e) {
            LOGGER.error("wait fail!");
        }
    }
}
