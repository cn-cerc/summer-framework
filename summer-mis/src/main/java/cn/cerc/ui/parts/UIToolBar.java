package cn.cerc.ui.parts;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cn.cerc.ui.core.Component;
import cn.cerc.ui.core.HtmlWriter;

public class UIToolBar extends UIComponent {
    private List<UISheet> sheets = new ArrayList<>();

    public UIToolBar(UIComponent owner) {
        super(owner);
        this.setId("rightSide");
    }

    @Override
    @Deprecated
    public void setOwner(Component owner) {
        super.setOwner(owner);
    }

    @Override
    public void output(HtmlWriter html) {
        html.print("\n<aside role='toolBar' id='%s'", this.getId());
        if (isEmpty()) {
            html.print(" style='display:none'");
        }
        html.println(">");
        html.println("<div style='overflow-y: auto; height: 100%;'>");

        if (sheets.size() > 0) {
            // 分组归类
            Map<String, List<UISheet>> items = new LinkedHashMap<>();
            for (UISheet sheet : sheets) {
                List<UISheet> list = items.get(sheet.getGroup());
                if (list == null) {
                    list = new ArrayList<>();
                    items.put(sheet.getGroup(), list);
                }
                list.add(sheet);
            }
            // 分组输出：标题
            int groupNo = 0;
            html.println("<ul role='toolGroup'>");
            for (String groupCaption : items.keySet()) {
                html.println("<ui data-id='group%d'>%s</ui>", groupNo++, groupCaption);
            }
            html.print("</ul>");
            // 分组输出：内容
            groupNo = 0;
            for (String group : items.keySet()) {
                html.println(String.format("<div role='toolSheet' id='group%d'>", groupNo++));
                List<UISheet> list = items.get(group);
                for (UISheet sheet : list) {
                    html.print(sheet.toString());
                }
                html.println("</div>");
            }
        } else {
            super.output(html);
        }
        html.println("</div>");
        html.print("</aside>");
    }

    private boolean isEmpty() {
        return sheets.size() == 0 && this.getComponents().size() == 0;
    }

    public List<UISheet> getSheets() {
        return sheets;
    }

    public int size() {
        return sheets.size();
    }

    public UISheet addSheet() {
        UISheet sheet = new UISheet(this);
        sheets.add(sheet);
        return sheet;
    }
}
