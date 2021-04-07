package cn.cerc.mis.tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import cn.cerc.db.core.IHandle;
import cn.cerc.db.mysql.SqlQuery;
import cn.cerc.mis.core.Application;
import cn.cerc.mis.core.ISystemTable;

/**
 * 扫描需要翻译的中文
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ExportChinese {
    private static final Logger log = LoggerFactory.getLogger(ExportChinese.class);
    private final Set<String> items = new TreeSet<>();

    public static void main(String[] args) throws JsonProcessingException {
        ExportChinese ec = new ExportChinese();
        // 扫描指定目录下所有的java文件
        ec.scanFile("C:\\Users\\l1091\\Documents\\iWork\\mimrc\\diteng-platform");

        // 将扫描的结果存入到数据库
        // ec.writeDict(new AppHandle());
        System.out.println(new ObjectMapper().writeValueAsString(ec.getItems()));
    }

    /**
     * 扫描指定路径的java文件
     */
    public void scanFile(String srcPath) {
        // 调用查找文件方法
        List<File> files = loadFiles(new File(srcPath), "java");

        // 循环出文件
        for (File file : files) {
            try (BufferedReader buff = new BufferedReader(new FileReader(file))) {
                String line;
                // 按行读取
                while ((line = buff.readLine()) != null) {
                    String word = getChinese(line);
                    if (word != null) {
                        log.info("{} {}", file.getName(), word);
                        items.add(word);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // 加载具体的文件路径
    private List<File> loadFiles(File path, String suffix) {
        List<File> list = new ArrayList<>();
        File[] files = path.listFiles();
        if (files == null) {
            log.info("{} no any files", path);
            return list;
        }
        for (File item : files) {
            if (item.isFile()) {
                if (suffix.equals(item.getName().substring(item.getName().lastIndexOf(".") + 1))) {
                    list.add(item);
                }
            } else {
                List<File> temps = loadFiles(item, suffix);
                list.addAll(temps);
            }
        }
        return list;
    }

    private static String getChinese(String temp) {
        int index = temp.indexOf("R.asString");
        if (index > -1) {
            String s1 = temp.substring(index);
            if (s1.contains("\"")) {
                String s2 = s1.substring(s1.indexOf("\"") + 1);
                if (s2.contains("\")")) {
                    String s3 = s2.substring(0, s2.indexOf("\")"));
                    if (s3.length() > 0) {
                        return s3;
                    }
                }
            }
        }
        return null;
    }

    /**
     * 写入字典
     */
    public void writeDict(IHandle handle) {
        // FIXME: 此处应该进一步抽象处理
        ISystemTable systemTable = Application.getBeanDefault(ISystemTable.class, null);
        SqlQuery ds = new SqlQuery(handle);
        ds.add("select * from %s", systemTable.getLangDict());
        ds.open();
        for (String text : this.getItems()) {
            if (!ds.locate("cn_", text)) {
                ds.append();
                ds.setField("cn_", text);
                ds.post();
            }
        }
    }

    public Set<String> getItems() {
        return this.items;
    }

}
