package com.wanxp.App;

import com.wanxp.App.connecter.PoolManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.Properties;
import java.util.Queue;

/**
 * 批量导入数据库
 */
public class Application
{
    private static Logger LOGGER = LogManager.getLogger(Application.class);
    public static String filePath = "C:\\Users\\hugh\\Downloads\\数据";//文件路径
    private static PoolManager poolManager= PoolManager.getInstance();//创建连接池
    private static String jdbcDriver = "com.mysql.jdbc.Driver"; //数据库驱动
    private static String dbUrl = "jdbc:mysql://localhost:32773/wifidb?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";//数据库url
    private static String dbUserName = "root";//数据库账户
    private static String dbPassword = "0001";//数据库密码



    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        importPropertioes(args[0]);
        poolManager.initPool(jdbcDriver, dbUrl, dbUserName, dbPassword);
        LOGGER.info(String.format("[file.path]:%s", filePath));
        //读取文件列表
        DataLoader dataLoader = new DataLoader(filePath);
        String firstTableName = "";
        //上传文件并分析数据
        Queue<String> tableNames = dataLoader.uploadData();
        int i = 0;
        for (String tableName : tableNames) {
            DataAnalysiser dataAnalysiser = new DataAnalysiser(tableName);
            dataAnalysiser.analysiseToFile(filePath, false);
            if (i == 0) {
                firstTableName = tableName;
                dataAnalysiser.analysiseToFile(filePath, true);
            }
            i++;
        }
        String daliyTableName = firstTableName.replace("hour", "day").substring(0,
                firstTableName.length() - 3);
        DataAnalysiser dataAnalysiser = new DataAnalysiser(daliyTableName);
        dataAnalysiser.analysiseToFile(filePath, true);
        System.out.println("总耗时: " + (System.currentTimeMillis() - start));
        poolManager.closeConnectionPool();
    }

    /**
     * 获取用户的配置文件
     * @param propertiesFile
     */
    private static void importPropertioes(String propertiesFile) {
        String configPath = propertiesFile;
        if (propertiesFile == null || "".equals(propertiesFile)) {
            configPath = "../../../application.properties";
        }

        Properties properties = new Properties();
        try {
            InputStream in = new BufferedInputStream(new FileInputStream(configPath));
            properties.load(in);
        } catch (Exception e) {
            LOGGER.error("propertiesFile cannot find, load default properties.", e);
        }
        String file_Path = properties.getProperty("filePath");
        if (file_Path != null && !"".equals(file_Path))
            filePath = file_Path;
        String driver = properties.getProperty("jdbcDriver");
        if (driver != null && !"".equals(driver))
            jdbcDriver = driver;
        String db_Url = properties.getProperty("dbUrl");
        if (driver != null && !"".equals(db_Url))
            dbUrl = db_Url;
        String db_UserName = properties.getProperty("dbUserName");
        if (driver != null && !"".equals(db_UserName))
            dbUserName = db_UserName;
        String db_Password = properties.getProperty("dbPassword");
        if (driver != null && !"".equals(db_Password))
            dbPassword = db_Password;
    }


}
