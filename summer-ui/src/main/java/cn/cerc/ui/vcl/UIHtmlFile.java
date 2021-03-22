package cn.cerc.ui.vcl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.parts.UIComponent;

public class UIHtmlFile extends UIComponent {
    private String fileName;

    public UIHtmlFile() {
        super();
    }

    public UIHtmlFile(UIComponent owner) {
        super(owner);
    }

    @Override
    public void output(HtmlWriter html) {
        if (fileName == null) {
            html.println("fileName is null.");
            return;
        }
        // 加载项目文件配置
        InputStream file = UIHtmlFile.class.getResourceAsStream(fileName);

        if (file != null) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(file, StandardCharsets.UTF_8));
            try {
                String text = null;
                while ((text = reader.readLine()) != null) {
                    html.println(text);
                }
            } catch (IOException e) {
                html.println(e.getMessage());
            }
        } else {
            html.println("%s doesn't exist.", fileName);
        }
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

}
