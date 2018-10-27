package com.wanxp;

import com.wanxp.App.LoadDataToSQL;
import junit.framework.TestCase;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.LinkedList;

public class LoadDataToMySQLTest extends TestCase {
    private static int num = 0;
    private String pathStr = "D:\\BaiduYunDownload\\20160319\\wifi";
//    private String pathStr =
    private static int THREAD_MAX = 10;
    public void testLoadFileToSQLByLines() {

        File pathFile = new File(pathStr);

        File[] files = pathFile.listFiles();
        LinkedList fileList = new LinkedList();
        Arrays.stream(files).filter(x -> x.isFile()).forEach(file -> {
//            new Thread (() -> {
                try {
                    num++;
                    System.out.println("Thread " + num + " is run");
                    LoadDataToSQL.loadFileToSQLByLines(file);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
//            }).start();

        });



    }

    /**
     * 450多秒
     */
    public void testMutilThread() {
        File pathFile = new File("D:\\BaiduYunDownload\\20160319\\wifi");

        File[] files = pathFile.listFiles();
        LinkedList fileList = new LinkedList();
        Arrays.stream(files).filter(x -> x.isFile()).forEach(file -> {
            new Thread (() -> {
                try {
                    num++;
                    System.out.println("Thread " + num + " is run");
                    try {
                        LoadDataToSQL.loadFileToSQLByLines(file);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        });
    }

    /**
     * 449410
     */
    public void testSingleThread() {
        long start = System.currentTimeMillis();
        File pathFile = new File("D:\\BaiduYunDownload\\20160319\\wifi");

        File[] files = pathFile.listFiles();
        LinkedList fileList = new LinkedList();
        Arrays.stream(files).filter(x -> x.isFile()).forEach(file -> {
                    try {
                        num++;
                        System.out.println("File " + num + " is handle");
                        try {
                            LoadDataToSQL.loadFileToSQLByLines(file);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        );
        System.out.println("总耗时: " + (System.currentTimeMillis() - start));
    }

}
