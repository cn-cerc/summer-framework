package cn.cerc.mis.other;

import cn.cerc.core.ClassResource;

public class UserNotFindException extends Exception {
    private static final ClassResource res = new ClassResource("summer-mvc", UserNotFindException.class);

    private static final long serialVersionUID = -356897945745530968L;
    private String userCode;

    public UserNotFindException(String userCode) {
        super(String.format(res.getString(1, "找不到用户帐号：%s"), userCode));
        this.userCode = userCode;
    }

    public String getUserCode() {
        return userCode;
    }
}
