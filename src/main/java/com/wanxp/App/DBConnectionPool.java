package com.wanxp.App;

import java.util.Vector;

/**
 *  自定义数据库连接池
 */
public class DBConnectionPool {
    private String jdbcDriver = ""; //数据库驱动
    private String dbUrl = "";//数据库url
    private String dbUserName = "";//数据库账户
    private String dbPassword = "";//数据库密码

    private int initalConnections = 10;//初始连接池大小
    private int maxConnections = 50; //最大连接池大小
    private Vector connections = null;//连接池存放向量

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



}
