package cn.cerc.mis.rds;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StubHandle {

    // FIXME 此处应该使用ClassConfig
    public static final String DefaultBook = "999001";
    public static final String DefaultUser = DefaultBook + "01";
    public static final String DefaultProduct = "999001000001";
    public static final String password = "123456";
    public static final String machineCode = "T800";

    // 生产部
    public static final String DefaultDept = "10050001";

}
