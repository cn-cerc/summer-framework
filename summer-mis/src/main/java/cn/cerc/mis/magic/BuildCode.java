package cn.cerc.mis.magic;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BuildCode {
    private static final Logger log = LoggerFactory.getLogger(BuildCode.class);
    private String packageName;
    private String classPath;
    private String tableCode;
    private File file;
    private FileWriter fw;
    BufferedWriter bw;

    public String getClassPath() {
        return classPath;
    }

    public void setClassPath(String classPath) {
        this.classPath = classPath;
    }

    public String getTableCode() {
        return tableCode;
    }

    public void setTableCode(String tableCode) {
        this.tableCode = tableCode;
    }

    public void createFile() {
        try {
            String srcFile = this.classPath + "\\db\\" + this.tableCode + ".java";
            log.info("create {}", srcFile);
            file = new File(srcFile);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
            }
            file.createNewFile();

            fw = new FileWriter(file, true);
            bw = new BufferedWriter(fw);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void closeFile() {
        try {
            bw.flush();
            bw.close();
            fw.close();
            file = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createDefineClass() throws IOException {
        TableDefine table = DatabaseDefine.getTable(tableCode);
        bw.write(String.format("package %s.db;", this.packageName));
        bw.newLine();
        bw.write("import cn.cerc.mis.magic.DatabaseDefine;");
        bw.newLine();
        bw.write("import cn.cerc.mis.magic.FieldDefine;");
        bw.newLine();
        bw.write("import cn.cerc.mis.magic.TableDefine;");
        bw.newLine();
        bw.write("");
        bw.newLine();
        bw.write("/**");
        bw.newLine();
        bw.write("* 范例表");
        bw.newLine();
        bw.write("*/");
        bw.newLine();
        bw.write(String.format("public class %s {", table.getCode()));
        bw.newLine();
        bw.write("    /**");
        bw.newLine();
        bw.write(String.format("    * %s", table.getComment()));
        bw.newLine();
        bw.write("    */");
        bw.newLine();
        bw.write(String.format("    public static String _table = \"%s\";", table.getCode()));
        bw.newLine();
        bw.write("    /**");
        bw.newLine();
        bw.write("    * 表对象");
        bw.newLine();
        bw.write("    */");
        bw.newLine();
        bw.write("    public static TableDefine _define = DatabaseDefine.getTable(_table);");
        for (FieldDefine field : table) {
            bw.newLine();
            bw.write("    /**");
            bw.newLine();
            bw.write(String.format("    * %s", field.getName()));
            bw.newLine();
            bw.write("    */");
            bw.newLine();
            bw.write(String.format("    public static FieldDefine %s = DatabaseDefine.getField(_table + \".%s\");",
                    field.getCode(), field.getCode()));
        }
        bw.newLine();
        bw.write("}");
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

}
