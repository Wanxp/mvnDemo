package com.wanxp;

import com.wanxp.App.DataAnalysiser;
import com.wanxp.App.DataLoader;
import junit.framework.TestCase;

import java.io.IOException;

public class DataAnalysiserTest extends TestCase {
    /**
     * 测试新增文件夹以及获取路径
     */
    public void testCreateFile() {
        DataAnalysiser dataAnalysiser = new DataAnalysiser("pv_day_20160319");
//        try {
//            System.out.println(dataAnalysiser.createFilePath("C:\\Users\\hugh\\Downloads\\数据"));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }
}
