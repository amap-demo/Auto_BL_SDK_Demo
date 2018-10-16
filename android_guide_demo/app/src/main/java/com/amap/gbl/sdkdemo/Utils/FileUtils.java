package com.amap.gbl.sdkdemo.Utils;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by zed.qzq on 2018/3/16.
 */

public class FileUtils {

    public static byte[] File2byte(File filePath) {
        byte[] buffer = null;
        try {
            FileInputStream fis = new FileInputStream(filePath);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
    }



    public static final String TAG = FileUtils.class.getSimpleName();
    public static String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/amapauto20/bllog/"; //性能测试路径


    /**
     * 检查 文件 / 文件夹 是否存在
     *
     * @param filepath 路径
     * @return
     */
    public boolean checkFileExists(String filepath) {
        File file = new File(path + filepath);
        return file.exists();
    }

    /**
     * 创建文件夹
     *
     * @param dirpath 路径
     * @return dir
     */
    public static File createDIR(String dirpath) {
        File dir = new File(path + dirpath);
        boolean b = dir.mkdirs();
        Log.d(TAG, "createDIR: path = " + dir.getAbsolutePath() + ",  b =  " + b);
        return dir;
    }

    /**
     * 创建文件
     *
     * @param filepath 路径
     * @return file对象
     * @throws IOException
     */
    public static File createFile(String filepath) throws IOException {
        File file = new File(path + filepath);
        boolean b = file.createNewFile();
        Log.d(TAG, "createFile: path = " + file.getAbsolutePath() + " , b = " + b);
        return file;
    }

    /**
     * 写文件
     *
     * @param msg             写入的内容
     * @param filePathAndName 路径及文件地址
     * @param append          是否是追加模式
     */
    public static void writeToFile(String msg, String filePathAndName, boolean append) {
        Log.i(TAG, "开始写文件");
        File dir = new File(path);
        if (!dir.exists()) {
            boolean mkdirs = dir.mkdirs();
            Log.i(TAG, "writeToFile: mkdirs = " + mkdirs);
        }

        FileWriter fileWriter = null;
        BufferedWriter bufferedWriter = null;
        try {
            File file = new File(path + filePathAndName);
            if (!file.exists()) {
                file.createNewFile();
            }
            fileWriter = new FileWriter(file, append);
            bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(msg + "\r\n");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bufferedWriter != null) {
                    bufferedWriter.close();
                }
                if (fileWriter != null) {
                    fileWriter.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }

    /**
     * 清除测试数据
     * 文件夹 sdcard/function/文件夹
     */
    public static void cleanCache() {
        File mapEngineLogDir = new File(path);
        if (mapEngineLogDir.listFiles() == null || mapEngineLogDir.listFiles().length == 0) {
            return;
        }
        for (File files : mapEngineLogDir.listFiles()) {
            files.delete();
        }
    }
}
