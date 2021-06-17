package cn.cerc.mis.core;

import com.google.gson.Gson;

/**
 * 菜单元数据
 */
public class MenuMetadata {
    // 页面类名代码，用于css定位
    private String id = "";
    // 页面标题
    private String name;
    // 页面描述
    private String describe;
    private boolean security;
    private String parent = "";
    // 页面图标名称
    private String image;
    // 授权权限代码
    private String proccode;
    // 适用版本
    private String versions;
    // 页面版面代码，用于排版定位
    private String pageNo = "000";

    private boolean web; // 是否支持Web调用
    private boolean phone; // 是否支持phone调用
    private boolean win; // 是否支持Window调用

    private boolean hide; // 是否隐藏菜单
    private boolean folder; // 是否为目录结构
    private boolean custom; // 是否客制化菜单
    private String funcCode;
    private String module;
    private String group;

    private int child;// 子项数量

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        if (id.indexOf('.') > -1) {
            throw new RuntimeException("error id: " + id);
        }
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name != null) {
            String[] titles = name.split("\\\\");
            this.name = titles[titles.length - 1];
        } else {
            this.name = "";
        }
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

    @Deprecated // 此函数不再需要
    public String getClazz() {
        return "";
    }

    @Deprecated // 此函数不再需要
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

    @Deprecated // 请改使用 getPageNo
    public String getFormNo() {
        return pageNo;
    }

    @Deprecated // 请改使用 setPageNo
    public void setFormNo(String pageNo) {
        this.pageNo = pageNo;
    }

    public String getPageNo() {
        return pageNo;
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

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public int getChild() {
        return child;
    }

    public void setChild(int child) {
        this.child = child;
    }

}
