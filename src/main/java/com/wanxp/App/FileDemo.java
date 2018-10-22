package com.wanxp.App;

import java.io.*;
import java.util.Arrays;
import java.util.LinkedList;

/**
 * 文件读取测试
 */
public class FileDemo {

    public static void main(String[] args) throws IOException {

//        readFileByLines("/home/wanxp/IdeaProjects/mvnDemo/static_source/source");
        readFileInPathByLines("/home/wanxp/IdeaProjects/mvnDemo/static_source/source/");
    }

    public static void readFileByLines(String fileName) throws IOException {
        File file = new File(fileName);
        readFileByLines(file);
    }

    public static void readFileInPathByLines(String pathName) throws IOException {
        File file = new File(pathName);
        LinkedList<File> files =  new LinkedList<>();
        File[] filesArray = file.listFiles();
       Arrays.asList(filesArray).forEach(x -> {
           if (x .isFile() )
               files.add(x);
        });
       files.forEach(x -> {
           try {
               int[] lengths = readFileByLines(x);
               Arrays.sort(lengths);
               int j = 1;
               for (int i = 0; i < lengths.length-1;i++) {
                   if (lengths[i] == lengths[i+1]) {
                       j++;
                   }else {
                       System.out.println(lengths[i] + "  count : " + j);
                       j = 1;
                   }
               }
               System.out.println("--------------------------------------fileEnd-----------------------------------------");
               System.out.println("--------------------------------------fileEnd-----------------------------------------");
               System.out.println("--------------------------------------fileEnd-----------------------------------------");
               System.out.println("--------------------------------------fileEnd-----------------------------------------");
           } catch (IOException e) {
               e.printStackTrace();
           }
       });

    }


    public static int[] readFileByLines(File file) throws IOException {
        BufferedReader reader = null;
        reader = new BufferedReader(new FileReader(file));
        int line = 1;
        String tempString = null;
        File writename = new File("/home/wanxp/IdeaProjects/mvnDemo/static_source/target/" + file.getName()  + ".txt");
        writename.createNewFile();
        BufferedWriter out = new BufferedWriter(new FileWriter(writename));
        int[] lengths =new int[1261360];
        int i=0;
        while ((tempString = reader.readLine()) != null) {
//            if (tempString.length() > 200) continue;;
            tempString = tempString.replaceAll("Ip-address", " ")
                    .replaceAll("pageType"," ")
                    .replaceAll("datetime"," ")
                    .replaceAll("username"," ")
                    .replaceAll("customName"," ");
            out.write(tempString + "\n");
            lengths[i] = tempString.length();
            i++;
        }
        out.flush();
        out.close();
        return lengths;
    }

}
