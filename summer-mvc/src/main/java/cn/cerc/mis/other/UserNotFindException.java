package cn.cerc.mis.other;

import cn.cerc.core.ClassResource;
import cn.cerc.mis.SummerMIS;

public class UserNotFindException extends Exception {
    private static final ClassResource res = new ClassResource(UserNotFindException.class, SummerMIS.ID);

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
