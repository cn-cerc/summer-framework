package cn.cerc.mis.tools;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import cn.cerc.core.IHandle;
import cn.cerc.core.TDateTime;
import cn.cerc.core.Utils;
import cn.cerc.db.mysql.SqlQuery;
import cn.cerc.mis.core.Application;
import cn.cerc.mis.core.ISystemTable;

public class DirectoryTest {
    private IHandle handle;

    @Before
    public void setUp() throws Exception {
        handle = Application.getHandle();
    }

    @Test
    @Ignore
    public void test() {
        Directory dir = new Directory();
        dir.setOnFilter(file -> {
            // 列出所有的java文件
            return file.getName().endsWith(".java");
        });

        int count = 0;
        if (dir.list("C:\\Users\\10914\\Documents\\iWork\\ufamily\\src\\main") > 0) {
            for (String fileName : dir.getFiles()) {
                StringList src = new StringList();
                src.loadFromFile(fileName);
                for (String line : src.getItems()) {
                    String text = processString(line);
                    if (text != null) {
                        count += WriteLine(text);
                    }
                }
            }
        } else {
            System.out.println("没有找到任何目录与文件");
        }
        System.out.println(count);
    }

    private int WriteLine(String text) {
        if (text.length() > 150) {
            System.err.println(text);
            return 0;
        }
        ISystemTable systemTable = Application.getBean("systemTable", ISystemTable.class);
        SqlQuery dsLang = new SqlQuery(handle);
        dsLang.add("select * from %s", systemTable.getLanguage());
        dsLang.add("where key_='%s' and lang_='en'", text);
        dsLang.open();
        if (dsLang.eof()) {
            System.out.println(text);
            dsLang.append();
            dsLang.setField("key_", Utils.safeString(text));
            dsLang.setField("lang_", "en");
            dsLang.setField("value_", "");
            dsLang.setField("supportAndroid_", false);
            dsLang.setField("supportIphone_", false);
            dsLang.setField("enable_", true);
            dsLang.setField("updateUser_", handle.getUserCode());
            dsLang.setField("updateDate_", TDateTime.Now());
            dsLang.setField("createUser_", handle.getUserCode());
            dsLang.setField("createDate_", TDateTime.Now());
            dsLang.post();
            return 1;
        }
        return 0;
    }

    private String processString(String text) {
        String flag = "R.asString(this,";
        int start = text.indexOf(flag);
        if (start == -1) {
            return null;
        }

        String s1 = text.substring(flag.length() + start);
        if (s1.indexOf("\"") == -1) {
            return null;
        }
        s1 = s1.substring(s1.indexOf("\"") + 1);
        if (s1.indexOf("\"") == -1) {
            return null;
        }
        s1 = s1.substring(0, s1.indexOf("\""));
        return s1;
    }

}
