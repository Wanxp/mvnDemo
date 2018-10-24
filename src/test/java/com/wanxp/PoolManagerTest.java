package com.wanxp;

import com.wanxp.App.connecter.PoolManager;
import junit.framework.TestCase;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;

public class PoolManagerTest extends TestCase {
    public void testPoolManagerCreate() {
        long start = System.currentTimeMillis();
        PoolManager pool = PoolManager.getInstance();
        pool.initPool();


        for (int i = 0;i < 10;i++) {

            Thread thread = new Thread(()-> {
                    Connection connection = pool.getConnection();
                    try {
                        Statement statement = connection.createStatement();
                        ResultSet result = statement.executeQuery("show databases");
                        while (result.next()) {
                            String dbName = result.getString("Database");
                            System.out.println(dbName);
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    System.out.println("connection:"+connection);
            });
            thread.start();
        }
        System.out.println(System.currentTimeMillis() - start);

    }
}
