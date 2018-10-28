package com.wanxp.App;

import com.wanxp.App.connecter.PoolManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;

/**
 * 文件分析器
 */
public class DataAnalysiser {
    private static Logger LOGGER = LogManager.getLogger(DataLoader.class);
    private static String SQL_ANALYSISER_PREFIX = "SELECT nas_port_id, page_type, COUNT(nas_port_id) FROM ";
    private static String SQL_ANALYSISER_SUFFIX = " GROUP BY nas_port_id, page_type ";
    private static int RESULT_BUFFERED_STRING_CAPACITY_SIZE = 4000000;
    private static String FOLDER_NAME = "anlysiseData";
    private String tableName;

    private PoolManager poolManager = PoolManager.getInstance();

    public DataAnalysiser(String tableName) {
        this.tableName = tableName;
        poolManager.initPool();
    }

    /**
     * 分析文件并写入文件
     * @param path
     */
    public void analysiseToFile(String path, Boolean isFirstTable) {
        LOGGER.info("Table " + tableName + " is analysising");
        String hour = tableName.indexOf("pv_hour_") > -1 ? tableName.substring(tableName.length() - 10) : "";
        if (path == null || tableName == null)
            return;
        try {
            ResultSet resultSet = queryPageType();
            String newFilePath = createFilePath(path);
            if (isFirstTable) {
                new File(newFilePath).delete();
            }
            BufferedWriter writer = new BufferedWriter(new FileWriter(newFilePath, true));
            while (resultSet.next()) {
                writer.write(hour
                        + "\t" + resultSet.getString(1)
                        + "\t" + resultSet.getString(2)
                        + "\t" + resultSet.getInt(3)
                        + "\n");
            }
            writer.flush();
            writer.close();
        } catch (Exception e) {
            LOGGER.error(e);
        }
    }

    /**
     * 获取数据库数据
     * @return
     * @throws SQLException
     */
    private ResultSet queryPageType() throws SQLException {
        Connection connection = poolManager.getConnection();
        String sql = SQL_ANALYSISER_PREFIX + tableName + SQL_ANALYSISER_SUFFIX;
        Statement statement = connection.createStatement();
        return statement.executeQuery(sql);
    }

    /**
     * 创建分析的文件
     * @param path
     * @return
     * @throws IOException
     */
    private String createFilePath(String path) throws IOException {
        File file = new File(path + "\\" + FOLDER_NAME);
        String fileName = tableName.indexOf("pv_day_") > -1 ?
                tableName : tableName.substring(0, tableName.length() - 2);
        if (!file.exists()) {
            file.mkdir();
        }
        return file.getPath() + "\\" + fileName + ".txt";
    }


}
