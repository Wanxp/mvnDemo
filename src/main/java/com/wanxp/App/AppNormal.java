package com.wanxp.App;

import com.wanxp.App.connecter.PoolManager;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.LinkedList;

/**
 * Hello world!
 *
 */
public class AppNormal
{
    public static void main( String[] args ) throws SQLException {
        long start = System.currentTimeMillis();
        File pathFile = new File("C:\\Users\\hugh\\Downloads\\数据");
        File[] files = pathFile.listFiles();

        //初始化连接池从数据库中获取数据库连接
        PoolManager poolManager= PoolManager.getInstance();
        poolManager.initPool();
        Arrays.stream(files).filter(x -> x.isFile()).forEach(file -> {
                try {
                    System.out.println("File " + file.getName() + " is handle");
                    try {
                        LoadDataToSQL.loadFileToSQLByLines(file);
//                        LoadDataToSQL.loadFileToSQLByLinesOpr1(file);
//                        LoadDataToSQL.loadFileToSQLByBytes(file);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        );
        System.out.println("总耗时: " + (System.currentTimeMillis() - start));
        poolManager.closeConnectionPool();
    }
}
