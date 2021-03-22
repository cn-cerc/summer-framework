package cn.cerc.ui.core;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import cn.cerc.ui.mvc.AbstractPage;

import javax.servlet.ServletException;

import cn.cerc.mis.core.Application;
import cn.cerc.mis.core.IForm;
import cn.cerc.mis.language.R;

public class JspFile extends AbstractPage {
    private String jspFile;
    
    public JspFile() {
        super();
    }

    public JspFile(IForm form) {
        super();
        setForm(form);
    }

    public final void add(String id, Object value) {
        put(id, value);
    }

    @Override
    public String execute() throws ServletException, IOException {
        return this.getViewFile();
    }

    public final String getJspFile() {
        return jspFile;
    }

    public final void setJspFile(String jspFile) {
        this.jspFile = jspFile;
    }


    public final String getViewFile() {
        String jspFile = this.getJspFile();
        if (getRequest() == null || jspFile == null) {
            return jspFile;
        }
        if (!jspFile.contains(".jsp")) {
            return jspFile;
        }

        String rootPath = String.format("/WEB-INF/%s/", config.getString(Application.PATH_FORMS, "forms"));
        String fileName = jspFile.substring(0, jspFile.indexOf(".jsp"));
        String extName = jspFile.substring(jspFile.indexOf(".jsp") + 1);
        IForm form = getForm();
        String langCode = R.getLanguageId(form.getHandle());
        
        // 检查是否存在 PC 专用版本的jsp文件
        String newFile = String.format("%s-%s.%s", fileName, "pc", extName);
        if (!this.getForm().getClient().isPhone() && fileExists(rootPath + newFile)) {
            // 检查是否存在相对应的语言版本
            String langFile = String.format("%s-%s-%s.%s", fileName, "pc", langCode, extName);
            if (fileExists(rootPath + langFile)) {
                return langFile;
            }
            return newFile;
        }

        // 检查是否存在相对应的语言版本
        String langFile = String.format("%s-%s.%s", fileName, langCode, extName);
        if (fileExists(rootPath + langFile)) {
            return langFile;
        }

        return jspFile;
    }
    
    private boolean fileExists(String fileName) {
        URL url = AbstractPage.class.getClassLoader().getResource("");
        if (url == null) {
            return false;
        }
        String filepath = url.getPath();
        String appPath = filepath.substring(0, filepath.indexOf("/WEB-INF"));
        String file = appPath + fileName;
        File f = new File(file);
        return f.exists();
    }

}
