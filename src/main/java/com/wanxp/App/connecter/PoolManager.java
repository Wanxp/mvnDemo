package com.wanxp.App.connecter;

import java.sql.Connection;

public class PoolManager implements Pool{
    @Override
    public int getInitialConnections() {
        return 0;
    }

    @Override
    public void setInitialConnections(int initialConnections) {

    }

    @Override
    public int getIncrementalConnections() {
        return 0;
    }

    @Override
    public void setIncrementalConnections(int incrementalConnections) {

    }

    @Override
    public int getMaxConnections() {
        return 0;
    }

    @Override
    public void setMaxConnections(int maxConnections) {

    }

    @Override
    public void initPool() {

    }

    @Override
    public Connection getConnection() {
        return null;
    }

    @Override
    public void returnConnection(Connection conn) {

    }

    @Override
    public void refreshConnections() {

    }

    @Override
    public void closeConnectionPool() {

    }
}
