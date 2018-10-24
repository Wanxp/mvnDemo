package com.wanxp.App.connecter;

import java.sql.Connection;

/**
 * 连接封装类,暂存连接的状态
 * @author wanxp
 */
public class PooledConnection {
    private Connection connection = null; //连接
    private Boolean busy = false; //状态
    public PooledConnection(Connection connection) {
        this.connection = connection;
    }

    /**
     * 获取连接
     * @return
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * 设置连接
     * @param connection
     */
    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    /**
     * 获取连接状态
     * @return
     */
    public Boolean isBusy() {
        return busy;
    }

    /**
     * 设置连接状态
     * @param busy
     */
    public void setBusy(Boolean busy) {
        this.busy = busy;
    }
}
