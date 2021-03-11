package cn.cerc.mis.services;

import lombok.Getter;
import lombok.Setter;

@Deprecated
//FIXME BookInfoRecord 应该从框架中移出
@Getter
@Setter
public class BookInfoRecord {
    private int status;
    private String code;
    private String shortName;
    private String name;
    private int corpType;
    private String address;
    private String tel;
    private String fastTel;
    private String managerPhone;
    private String startHost;
    private String contact;
    private boolean authentication;
    private String industry;
    private String currency;
    private String email;
    private String fax;
}
