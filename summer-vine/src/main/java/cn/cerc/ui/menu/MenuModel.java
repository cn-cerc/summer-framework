package cn.cerc.ui.menu;

public class MenuModel {

    private String code;
    private String name;
    private String module;

    private String verlist;
    private String procCode;

    private int status;
    private String deadline;
    private double price;

    private boolean security;
    private boolean hide;
    private boolean win;
    private boolean web;
    private boolean phone;
    private boolean custom;// 需要购买
    private int orderType;// 订购类型

    private String remark;
    private int menuIconType;// 图标类型
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getModule() {
        return module;
    }
    public void setModule(String module) {
        this.module = module;
    }
    public String getVerlist() {
        return verlist;
    }
    public void setVerlist(String verlist) {
        this.verlist = verlist;
    }
    public String getProcCode() {
        return procCode;
    }
    public void setProcCode(String procCode) {
        this.procCode = procCode;
    }
    public int getStatus() {
        return status;
    }
    public void setStatus(int status) {
        this.status = status;
    }
    public String getDeadline() {
        return deadline;
    }
    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }
    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }
    public boolean isSecurity() {
        return security;
    }
    public void setSecurity(boolean security) {
        this.security = security;
    }
    public boolean isHide() {
        return hide;
    }
    public void setHide(boolean hide) {
        this.hide = hide;
    }
    public boolean isWin() {
        return win;
    }
    public void setWin(boolean win) {
        this.win = win;
    }
    public boolean isWeb() {
        return web;
    }
    public void setWeb(boolean web) {
        this.web = web;
    }
    public boolean isPhone() {
        return phone;
    }
    public void setPhone(boolean phone) {
        this.phone = phone;
    }
    public boolean isCustom() {
        return custom;
    }
    public void setCustom(boolean custom) {
        this.custom = custom;
    }
    public int getOrderType() {
        return orderType;
    }
    public void setOrderType(int orderType) {
        this.orderType = orderType;
    }
    public String getRemark() {
        return remark;
    }
    public void setRemark(String remark) {
        this.remark = remark;
    }
    public int getMenuIconType() {
        return menuIconType;
    }
    public void setMenuIconType(int menuIconType) {
        this.menuIconType = menuIconType;
    }

}
