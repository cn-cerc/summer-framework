package cn.cerc.mis.other;

public enum BookVersion {
    // 服务商
    ctService,
    // 专业版
    ctProfession,
    // 标准版
    ctStandard,
    // 基础版
    ctBasic,
    // 旗舰版
    ctUltimate,
    // 普及版
    ctFree,
    // 高级版
    ctAdvanced,
    // 所有版本, 仅权限判断专用，后须移除！
    ctAll;

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
        default:
            versions = null;
            break;
        }
        return versions;
    }
}