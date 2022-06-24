package tcb;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class IOTest {
    List<String> strings = new ArrayList<>();

    public static void main(String[] args) {
        IOTest ioTest = new IOTest();
        ioTest.readLineFile("gps.txt");
        ioTest.writeLineFile("a.txt");
    }

    public void readLineFile(String filename) {
        try {
            FileInputStream in = new FileInputStream(filename);
            InputStreamReader inReader = new InputStreamReader(in, "UTF-8");
            BufferedReader bufReader = new BufferedReader(inReader);
            String line = null;
            int i = 1;
            while ((line = bufReader.readLine()) != null) {
                if (line.startsWith("latitude")) {
                    this.strings.add(line.split(": ")[1]+","+bufReader.readLine().split(": ")[1]);
                }
            }
            bufReader.close();
            inReader.close();
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("读取" + filename + "出错！");
        }
    }

    public void writeLineFile(String filename) {
        try {
            FileOutputStream out = new FileOutputStream(filename);
            OutputStreamWriter outWriter = new OutputStreamWriter(out, "UTF-8");
            BufferedWriter bufWrite = new BufferedWriter(outWriter);
            for (int i = 0; i < this.strings.size(); i++) {
                bufWrite.write(this.strings.get(i) + "\r\n");
            }
            bufWrite.close();
            outWriter.close();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("读取" + filename + "出错！");
        }
    }

}
