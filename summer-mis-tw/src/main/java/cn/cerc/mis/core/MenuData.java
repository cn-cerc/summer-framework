package cn.cerc.mis.core;

import com.google.gson.Gson;

public class MenuData {
    // 頁麵類名代碼，用於css定位
    private String id = "";
    // 頁面標題
    private String caption;
    // 頁面描述
    private String describe;
    private boolean security;
    private String parent = "";
    // 頁面圖標名稱
    private String image;
    // 授權權限代碼
    private String proccode;
    // 適用版本
    private String versions;
    // 頁面版面代碼，用於排版定位
    private String pageNo = "000";
    private boolean web; // 是否支持Web調用
    private boolean win; // 是否支持Window調用
    private boolean phone; // 是否支持phone調用
    private boolean hide; // 是否隱藏菜單
    private String process; // Web化進度
    private boolean folder; // 是否為目錄結構
    private boolean custom; // 是否客制化菜單
    private String funcCode;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        if (id.indexOf('.') > -1)
            throw new RuntimeException("error id: " + id);
        this.id = id;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        if (caption != null) {
            String[] captions = caption.split("\\\\");
            this.caption = captions[captions.length - 1];
        } else
            this.caption = "";
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public boolean isSecurity() {
        return security;
    }

    public void setSecurity(boolean security) {
        this.security = security;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    @Deprecated // 此函數不再需要
    public String getClazz() {
        return "";
    }

    @Deprecated // 此函數不再需要
    public void setClazz(String clazz) {
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getProccode() {
        return proccode;
    }

    public void setProccode(String proccode) {
        this.proccode = proccode;
    }

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public String getVersions() {
        return versions;
    }

    public void setVersions(String versions) {
        this.versions = versions;
    }

    @Deprecated // 請改使用 getPageNo
    public String getFormNo() {
        return pageNo;
    }

    public String getPageNo() {
        return pageNo;
    }

    @Deprecated // 請改使用 setPageNo
    public void setFormNo(String pageNo) {
        this.pageNo = pageNo;
    }

    public void setPageNo(String pageNo) {
        this.pageNo = pageNo;
    }

    public boolean isWeb() {
        return web;
    }

    public void setWeb(boolean web) {
        this.web = web;
    }

    public boolean isWin() {
        return win;
    }

    public void setWin(boolean win) {
        this.win = win;
    }

    public boolean getHide() {
        return hide;
    }

    public void setHide(boolean hide) {
        this.hide = hide;
    }

    public String getProcess() {
        return process;
    }

    public void setProcess(String process) {
        this.process = process;
    }

    public boolean getFolder() {
        return folder;
    }

    public void setFolder(boolean folder) {
        this.folder = folder;
    }

    public boolean getCustom() {
        return custom;
    }

    public void setCustom(boolean custom) {
        this.custom = custom;
    }

    public String getFuncCode() {
        return funcCode;
    }

    public void setFuncCode(String funcCode) {
        this.funcCode = funcCode;
    }

    public boolean isPhone() {
        return phone;
    }

    public void setPhone(boolean phone) {
        this.phone = phone;
    }

}