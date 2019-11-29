package cn.cerc.mis.other;

import java.io.Serializable;

public class DataRecordSample implements Serializable {
    private static final long serialVersionUID = 4744827168403991038L;
    private String id;
    private String corpNo;
    private String code;
    private String name;
    private String headImgAdd;
    private int showInUP;
    private int showOutUP;
    private int showWholesaleUP;
    private int showBottomUP;
    private String qq;
    private String mobile;
    private String email;
    private String lastRemindDate;
    private String roleCode;
    private String proxyUsers;
    private boolean enabled;
    private boolean admin;

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

    public int getShowInUP() {
        return showInUP;
    }

    public void setShowInUP(int showInUP) {
        this.showInUP = showInUP;
    }

    public int getShowOutUP() {
        return showOutUP;
    }

    public void setShowOutUP(int showOutUP) {
        this.showOutUP = showOutUP;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getLastRemindDate() {
        return lastRemindDate;
    }

    public void setLastRemindDate(String lastRemindDate) {
        this.lastRemindDate = lastRemindDate;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public void setRoleCode(String roleCode) {
        this.roleCode = roleCode;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCorpNo() {
        return corpNo;
    }

    public void setCorpNo(String corpNo) {
        this.corpNo = corpNo;
    }

    public String getProxyUsers() {
        return proxyUsers;
    }

    public void setProxyUsers(String proxyUsers) {
        this.proxyUsers = proxyUsers;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getShowWholesaleUP() {
        return showWholesaleUP;
    }

    public void setShowWholesaleUP(int showWholesaleUP) {
        this.showWholesaleUP = showWholesaleUP;
    }

    public int getShowBottomUP() {
        return showBottomUP;
    }

    public void setShowBottomUP(int showBottomUP) {
        this.showBottomUP = showBottomUP;
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public String getHeadImgAdd() {
        return headImgAdd;
    }

    public void setHeadImgAdd(String headImgAdd) {
        this.headImgAdd = headImgAdd;
    }
}
