package cn.cerc.mis.other;

public class UserNotFindException extends Exception {
    private static final long serialVersionUID = -356897945745530968L;
    private String userCode;

    public UserNotFindException(String userCode) {
        super("找不到用户帐号：" + userCode);
        this.userCode = userCode;
    }

    public String getUserCode() {
        return userCode;
    }
}
