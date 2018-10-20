package com.wanxp.App;

import java.io.*;

/**
 * 文件读取测试
 */
public class FileDemo {

    public static void main(String[] args) throws IOException {
        readFileByLines("D:\\BaiduYunDownload\\20160319\\portal.log.2016031923.txt");
    }

    public static void readFileByLines(String fileName) throws IOException {
        File file = new File(fileName);
        BufferedReader reader = null;
        reader = new BufferedReader(new FileReader(file));
        int line = 1;
        String tempString = null;
        File writename = new File("D:\\BaiduYunDownload\\20160319\\test\\output.txt");
        writename.createNewFile();
        BufferedWriter out = new BufferedWriter(new FileWriter(writename));
        while ((tempString = reader.readLine()) != null) {
            tempString = tempString.replaceAll("Ip-address", " ")
                    .replaceAll("pageType"," ")
                    .replaceAll("datetime"," ")
                    .replaceAll("username"," ")
                    .replaceAll("customName"," ");
            out.write(tempString);
        }
        out.flush();
        out.close();
    }
}
