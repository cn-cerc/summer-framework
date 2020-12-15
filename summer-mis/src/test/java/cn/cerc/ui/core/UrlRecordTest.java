package cn.cerc.ui.core;

import org.junit.Test;

public class UrlRecordTest {

    @Test
    public void encode() {
        /*
         * http://127.0.0.1/911001/TFrmPartImage.listPartImages?partCode=ZKYXBSB-A+-L
         */
        String str = "http://127.0.0.1/911001/TFrmPartImage.listPartImages";
        UrlRecord url = new UrlRecord();
        url.setSite(str);
        url.putParam("partCode", "ZKYXBSB-A+-L");
        System.out.println(url.getUrl());
    }

    @Test
    public void test() {
        UrlRecord url = new UrlRecord.Builder("TFrmUserMenu")
                .name("菜单设置")
                .title("这是系统菜单")
                .put("module", "TBase")
                .put("menuCode", "TFrmPartInfo")
                .put("标记", "中国")
                .build();
        System.out.println(url.getUrl());
    }

}