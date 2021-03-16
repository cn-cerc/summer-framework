package cn.cerc.ui.other;

import java.util.ArrayList;
import java.util.List;

import cn.cerc.ui.core.UICustomComponent;
import cn.cerc.ui.core.UrlRecord;

public class OperaPanel extends UICustomComponent {
    // 操作提示
    private String readme;
    // 基本资料
    private List<String> lines;
    // 相关链接
    private List<UrlRecord> menus;

    public OperaPanel(UICustomComponent owner) {
        super(owner);
        this.setId("right");
    }

    public List<UrlRecord> getMenus() {
        return menus;
    }

    public String getReadme() {
        return readme;
    }

    public void setReadme(String readme) {
        this.readme = readme;
    }

    public void addMenu(UrlRecord item) {
        if (menus == null) {
            menus = new ArrayList<>();
        }
        menus.add(item);
    }

    public UrlRecord addMenu(String caption, String url) {
        UrlRecord item = new UrlRecord(url, caption);
        addMenu(item);
        return item;
    }

    public List<String> getLines() {
        return lines;
    }

    public void addLine(String html) {
        if (lines == null) {
            lines = new ArrayList<>();
        }
        lines.add(html);
    }
}
