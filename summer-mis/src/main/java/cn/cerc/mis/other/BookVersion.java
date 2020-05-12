package cn.cerc.mis.other;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public enum BookVersion {
    // 服务商
    ctService("admin"),
    // 专业版
    ctProfession("professional"),
    // 标准版
    ctStandard("standard"),
    // 基础版
    ctBasic("base"),
    // 旗舰版
    ctUltimate("ultimate"),
    // 普及版
    ctFree("free"),
    // 高级版
    ctAdvanced("advanced"),
    // ERP
    erp("erp"),
    // 所有版本, 仅权限判断专用，后须移除！
    ctAll("ctAll");

    private String code;

    BookVersion(String code) {
        this.code = code;
    }

    public String getVersionList() {
        String versions;
        switch (this) {
            case ctService:
                versions = "0,";
                break;
            case ctFree:
                versions = "5,";
                break;
            case ctBasic:
                versions = "5,3,";
                break;
            case ctStandard:
                versions = "5,3,2,";
                break;
            case ctProfession:
                versions = "5,3,2,1,";
                break;
            case ctAdvanced:
                versions = "5,3,2,1,6,";
                break;
            case ctUltimate:
                versions = "5,3,2,1,6,4,";
                break;
            case erp:
                versions = "7,";
                break;
            default:
                versions = null;
                break;
        }
        return versions;
    }

    public int getType(BookVersion version) {
        return version.ordinal();
    }

    public String getCode() {
        return this.code;
    }

    /**
     * 根据角标获取版本号
     */
    public static Map<Integer, String> getIndex() {
        Map<Integer, String> items = new LinkedHashMap<>();
        for (BookVersion k : BookVersion.values()) {
            if (k == ctAll) {
                continue;
            }
            items.put(k.ordinal(), k.getCode());
        }
        return items;
    }

    public static void main(String[] args) throws JsonProcessingException {
        Map<Integer, String> items = BookVersion.getIndex();
        System.out.println(new ObjectMapper().writeValueAsString(items));
    }

}