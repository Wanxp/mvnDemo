package com.wanxp.App;

import com.wanxp.App.connecter.PoolManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Queue;

/**
 * 批量导入数据库
 */
public class Application
{
    private static Logger LOGGER = LogManager.getLogger(Application.class);
    public static String FILES_PATH = "C:\\Users\\hugh\\Downloads\\数据";//文件路径
    private static PoolManager poolManager= PoolManager.getInstance();//创建连接池
    private static String JDBC_DRIVER = "com.mysql.jdbc.Driver"; //数据库驱动
    private static String JDBC_URL = "jdbc:mysql://localhost:32773/wifidb?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";//数据库url
    private static String DB_USER_NAME = "root";//数据库账户
    private static String DB_PASSWROD = "0001";//数据库密码

    /**
     * 初始化连接池
     */
    static {
        poolManager.initPool(JDBC_DRIVER, JDBC_URL, DB_USER_NAME, DB_PASSWROD);
    }

    public static void main( String[] args ) {
        LOGGER.info(String.format("[file.path]:%s", FILES_PATH));
        long start = System.currentTimeMillis();
        //读取文件列表
        DataLoader dataLoader = new DataLoader(FILES_PATH);
        String firstTableName = "";
        //上传文件并分析数据
        Queue<String> tableNames = dataLoader.uploadData();
        int i = 0;
        for (String tableName : tableNames) {
            DataAnalysiser dataAnalysiser = new DataAnalysiser(tableName);
            dataAnalysiser.analysiseToFile(FILES_PATH, false);
            if (i == 0) {
                firstTableName = tableName;
                dataAnalysiser.analysiseToFile(FILES_PATH, true);
            }
            i++;
        }
        String daliyTableName = firstTableName.replace("hour", "day").substring(0,
                firstTableName.length() - 3);
        DataAnalysiser dataAnalysiser = new DataAnalysiser(daliyTableName);
        dataAnalysiser.analysiseToFile(FILES_PATH, true);
        System.out.println("总耗时: " + (System.currentTimeMillis() - start));
        poolManager.closeConnectionPool();
    }


}
