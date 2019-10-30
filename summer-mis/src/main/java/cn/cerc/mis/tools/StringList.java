package cn.cerc.mis.tools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class StringList {
    private List<String> items = new ArrayList<>();
    private String message;

    public String getMessage() {
        return message;
    }

    public List<String> getItems() {
        return items;
    }

    // 按照行读取文件内容
    public boolean loadFromFile(String fileName) {
        items.clear();

        File f = new File(fileName);
        if (!f.exists()) {
            this.message = "file not exists";
            return false;
        }
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF-8"));
            try {
                String line;
                while ((line = br.readLine()) != null) {
                    items.add(line);
                }
            } finally {
                br.close();
            }
            return true;
        } catch (Exception e) {
            this.message = e.getMessage();
            e.printStackTrace();
            return false;
        }
    }

    public boolean saveToFile(String fileName) {
        File file = new File(fileName);
        try {
            file.createNewFile();
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            for (String line : items) {
                writer.write(line);
                writer.write(System.lineSeparator());
            }
            writer.close();
            return true;
        } catch (IOException e) {
            this.message = e.getMessage();
            e.printStackTrace();
            return false;
        }
    }

    public void add(String line) {
        items.add(line);
    }
}
