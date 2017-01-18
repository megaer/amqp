package com.palmaplus.data.amqp.test;

import java.io.*;

/**
 * Created by jiabing.zhu on 2016/10/22.
 */
public class CreateCSV {
    public void createCsvFile(String hx,String hy,String zx, String zy){
        System.out.println("hx :" + hx + ", " + "hy: " + hy + ", " + "zx: " + zx + ", " + "zy:" + zy);
        String outPutPath = "D:/create/";
        File file = new File(outPutPath);
        if (!file.exists()) {
            file.mkdir();
        }
        BufferedWriter csvFileOutputStream = null;
        File csvFile = null;
        csvFile = new File(outPutPath + "test.csv");
        try {
            csvFileOutputStream = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(csvFile), "GBK"), 1024);
            csvFileOutputStream.write(hx);
            csvFileOutputStream.write(",");
            csvFileOutputStream.write(hy);
            csvFileOutputStream.write(",");
            csvFileOutputStream.write(zx);
            csvFileOutputStream.write(",");
            csvFileOutputStream.write(zy);
            csvFileOutputStream.write(",");
            csvFile.createNewFile();
        } catch (IOException e){
            e.printStackTrace();
        } finally {
            try {
                csvFileOutputStream.flush();
                csvFileOutputStream.close();
            } catch (IOException e) {
                System.out.println(e);
            }
        }
    }
}
