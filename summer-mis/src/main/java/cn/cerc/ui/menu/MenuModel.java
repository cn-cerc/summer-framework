package cn.cerc.ui.menu;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
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
    private boolean order;// 需要购买
    private int orderType;// 订购类型

    private String remark;
    private int menuIconType;// 图标类型

}
