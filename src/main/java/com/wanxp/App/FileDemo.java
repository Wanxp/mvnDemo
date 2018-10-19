package com.wanxp.App;

import java.io.*;

/**
 * 文件读取测试
 */
public class FileDemo {

    public static void main(String[] args) {

    }

    public static void readFileByLines(String fileName) throws IOException {
        File file = new File(fileName);
        BufferedReader reader = null;
        reader = new BufferedReader(new FileReader(file));
        int line = 1;
        String tempString = null;
        while ((tempString = reader.readLine()) != null) {
            tempString.replaceAll("Ip-address", "")
                    .replaceAll("pageType","")
                    .replaceAll("datetime","")
                    .replaceAll("username","")
                    .replaceAll("customName","");

        }
    }
}
