package cn.cerc.ui.vcl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import cn.cerc.ui.core.HtmlWriter;
import cn.cerc.ui.parts.UIComponent;

public class UIHtmlFile extends UIComponent {
    private String fileName;
    private Map<String, Object> items = new HashMap<>();

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
                    String result = text;
                    for (String key : this.items.keySet()) {
                        String flag = String.format("${%s}", key);
                        if (text.contains(flag)) {
                            result = text.replace(flag, String.valueOf(items.get(key)));
                        }
                    }
                    html.println(result);
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

    public UIHtmlFile setFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public UIHtmlFile addItem(String key, Object value) {
        items.put(key, value);
        return this;
    }

    public Map<String, Object> getItems() {
        return items;
    }
}
