package cn.cerc.db.mysql;

public enum UpdateMode {
    /**
     * 严谨模式: 根据主键+所有变动字段形成where条件
     **/
    strict,

    /**
     * 宽松模式：仅根据主键更新，一般临时使用
     **/
    loose,
}
