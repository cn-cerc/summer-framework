package cn.cerc.ui.core;

import org.junit.Test;

public class UrlRecordTest {

    @Test
    public void test() {
        UrlRecord url = new UrlRecord.Builder("TFrmUserMenu").name("菜单设置").put("module", "TBase")
                .put("menuCode", "TFrmPartInfo").build();
        System.out.println(url.getUrl());
    }

}