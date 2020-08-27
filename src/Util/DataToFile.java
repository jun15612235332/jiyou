package Util;

import java.io.*;

/**
 * @Des 将数据转换成文件保存
 * @Author SunXianJun
 * @Date 2020/8/25 11:14
 */
public class DataToFile {

    public static void main(String[] args) {
        String dirname = "D:\\sunxianjun\\test";
        //将数据写到dirname目录下
        File file = new File(dirname);
        if (!file.exists()) {
            file.mkdirs();
        }

        String filename = "test1.txt";
        //创建文件
        FileOutputStream f = null;
        OutputStreamWriter streamWriter =null;
        try {
            f = new FileOutputStream(filename);
            streamWriter = new OutputStreamWriter(f);
            streamWriter.write("数据转换成文件");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                streamWriter.close();
                f.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
