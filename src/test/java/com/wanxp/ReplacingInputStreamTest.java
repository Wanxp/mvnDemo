package com.wanxp;

import com.wanxp.App.LoadDataToSQL;
import com.wanxp.App.connecter.PoolManager;
import com.wanxp.App.connecter.file.ReplacingInputStream;
import junit.framework.TestCase;

import java.io.*;
import java.sql.SQLException;
import java.util.Arrays;

public class ReplacingInputStreamTest extends TestCase {
    private static byte[] STRING_BEING_REPLACED_BYTES_1 = "Ip-address".getBytes();
    private static byte[] STRING_BEING_REPLACED_BYTES_2 = "pageType".getBytes();
    private static byte[] STRING_BEING_REPLACED_BYTES_3 = "datetime".getBytes();
    private static byte[] STRING_BEING_REPLACED_BYTES_4 = "username".getBytes();
    private static byte[] STRING_BEING_REPLACED_BYTES_5 = "customName".getBytes();
    private static byte[] STRING_REPLACE_TO_BYTES_1 = " ".getBytes();

    public void testInputReplace() {
        long start = System.currentTimeMillis();
        File pathFile = new File("C:\\Users\\hugh\\Downloads\\数据");
        File[] files = pathFile.listFiles();
        //初始化连接池从数据库中获取数据库连接
        Arrays.stream(files).filter(x -> x.isFile()).forEach(file -> {
                    try {
                        System.out.println("File " + file.getName() + " is handle");
                        File newFile = new File("C:\\Users\\hugh\\Downloads\\xin");
                        BufferedWriter out = new BufferedWriter(new FileWriter(newFile));
                        FileInputStream inputStream = new FileInputStream(file);
                        InputStream is =
                                new ReplacingInputStream(
                                        new ReplacingInputStream(
                                                new ReplacingInputStream(
                                                        new ReplacingInputStream(
                                                                new ReplacingInputStream(
                                                                        inputStream,
                                                                        STRING_BEING_REPLACED_BYTES_1, STRING_REPLACE_TO_BYTES_1 ),
                                                                STRING_BEING_REPLACED_BYTES_2, STRING_REPLACE_TO_BYTES_1 ),
                                                        STRING_BEING_REPLACED_BYTES_3, STRING_REPLACE_TO_BYTES_1 ),
                                                STRING_BEING_REPLACED_BYTES_4, STRING_REPLACE_TO_BYTES_1 ),
                                        STRING_BEING_REPLACED_BYTES_5, STRING_REPLACE_TO_BYTES_1 );

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        );
        System.out.println("总耗时: " + (System.currentTimeMillis() - start));

    }
}
