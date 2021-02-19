package cn.cerc.mis.other;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.cerc.core.DataSet;
import cn.cerc.core.IHandle;
import cn.cerc.core.TDate;
import cn.cerc.core.Utils;
import cn.cerc.db.mysql.BuildQuery;
import cn.cerc.db.mysql.SqlQuery;
import cn.cerc.mis.core.Application;
import cn.cerc.mis.core.ISystemTable;
import cn.cerc.mis.core.LocalService;

public class BookOptions {
    private static final Logger log = LoggerFactory.getLogger(BookOptions.class);
    private IHandle handle;
    public static final String BookInfo_Set = "_BookInfoSet_";
    public static final String HideLoginInfo = "_HideLoginInfo_";
    public static final String AllowERPSynchro = "AllowERPSynchro";
    public static final String AllowScanBCMode = "AllowScanBCMode";
    public static final String ZZTPY_VERSION = "ZZTPY_VERSION";
    public static final String AllowMallShare = "AllowMallShare";
    public static final String SenderInfo = "SenderInfo";
    public static final String Report = "Report";
    // public static final String ERPSalesDeptCode = "ERPSalesDeptCode";
    public static final String BEDefaultCusCode = "BEDefaultCusCode";
    public static final String BEDefaultVipCard = "BEDefaultVipCard";
    public static final String OEDefaultBusiness = "OEDefaultBusiness";
    public static final String AccInitYearMonth = "AccInitYearMonth";
    public static final String DADefaultSupCode = "DADefaultSupCode";
    public static final String DefaultProcCode = "DefaultProcCode";
    public static final String PurSafetyRateDefault = "PurSafetyRateDefault";
    public static final String CreditLineApproveUser = "CreditLineApproveUser";
    public static final String TranAGApproveUser = "TranAGApproveUser";
    public static final String NoAllowSalesBCToAG = "NoAllowSalesBCToAG";
    public static final String DisablePasswordSave = "DisablePasswordSave";
    public static final String DisableAccountSave = "DisableAccountSave";
    public static final String ReportHead = "ReportHead";
    public static final String ReportTranBCHead = "ReportTranBCHead";
    public static final String ReportTranBCFoot = "ReportTranBCFoot";
    public static final String ReportBarcode = "ReportBarcode";
    public static final String ReportTranBETitle = "ReportTranBETitle";
    public static final String ReportTranBETel = "ReportTranBETel";
    public static final String ReportCheckARFoot = "ReportCheckARFoot";
    // public static final String ERPAppServer = "ERPAppServer";
    // public static final String ERPCorpCode = "ERPCorpCode";
    // public static final String ERPVersion = "ERPVersion";
    public static final String ScanBCAdminControl = "ScanBCAdminControl";
    public static final String ScanBCAdminAccount = "ScanBCAdminAccount";

    // public static final String StockReadFromERP = "StockReadFromERP";
    // public static final String OutUPReadFromERP = "OutUPReadFromERP";
    // public static final String InUPReadFromERP = "InUPReadFromERP";
    // public static final String DAReadFromERP = "DAReadFromERP";
    // public static final String CusInfoReadFromERP = "CusInfoReadFromERP";
    // public static final String ODWriteToERP = "ODWriteToERP";
    // public static final String ODStatusReadFromERP = "ODStatusReadFromERP";
    // public static final String BCWriteToERP = "BCWriteToERP";
    // public static final String BCStatusReadFromERP = "BCStatusReadFromERP";
    // public static final String OtherWriteToERP = "OtherWriteToERP";
    public static final String UpdateInUPFromAA = "UpdateInUPFromAA";
    public static final String EnableStockLessControl = "EnableStockLessControl";
    public static final String EnableSendMailIntro = "EnableSendMailIntro";
    public static final String EnableSendMobileIntro = "EnableSendMobileIntro";
    public static final String EnableNotODToBC = "EnableNotODToBC";
    public static final String EnablePackageNumInput = "EnablePackageNumInput";
    public static final String AllowDiyPartCode = "AllowDiyPartCode";
    public static final String EanbleSalesPromotion = "EanbleSalesPromotion";
    public static final String CostPriceSet = "CostPriceSet";
    public static final String SafetyStockSynPartStock = "SafetyStockSynPartStock";
    public static final String OrdToPurFinal = "OrdToPurFinal";
    public static final String StockToPurFinal = "StockToPurFinal";
    public static final String PurToPurFinal = "PurToPurFinal";
    public static final String EnableCustomerCare = "EnableCustomerCare";
    public static final String EnableStockCWReport = "EnableStockCWReport";
    public static final String AllowAreaControl = "AllowAreaControl";
    public static final String AGAutoAreaBlack = "AGAutoAreaBlack";
    public static final String EnableWHManage = "EnableWHManage";
    public static final String EnableTranDetailCW = "EnableTranDetailCW";
    public static final String EnablePrintCurStock = "EnablePrintCurStock";
    public static final String CusCreditLiit = "CusCreditLiit";
    public static final String EnableApproveFlow = "EnableApproveFlow";
    public static final String EnableSyncERP = "EnableSyncERP";
    public static final String EnableReportSecurity = "EnableReportSecurity";
    public static final String NotPrintListUP = "NotPrintListUP";
    public static final String EnablePrintPartCode = "EnablePrintPartCode";
    public static final String DefaultCWCode = "DefaultCWCode";
    public static final String AvailableStockOption = "AvailableStockOption";
    public static final String ABAndBGDefaultMonthly = "ABAndBGDefaultMonthly";
    public static final String EnableAccBook = "EnableAccBook";
    public static final String UpdateCurrentMonthProfit = "UpdateCurrentMonthProfit";
    public static final String BEDefaultBankAccount = "BEDefaultBankAccount";
    public static final String UpdateTBDateToEffectiveDate = "UpdateTBDateToEffectiveDate";
    public static final String IsViewOldMenu = "IsViewOldMenu";
    public static final String DefaultProfitMargin = "DefaultProfitMargin";
    public static final String OnlineToOfflineMenu = "OnlineToOfflineMenu";
    public static final String OnlineToOfflineMaxScale = "OnlineToOfflineMaxScale";
    public static final String OnlineToOfflineArea = "OnlineToOfflineArea";
    public static final String BMDefaultDeptCode = "BMDefaultDeptCode";
    public static final String SupplyQuotationGrade = "SupplyQuotationGrade";
    public static final String EnableForecastMode = "EnableForecastMode";
    public static final String EnableAutoFinishDA = "EnableAutoFinishDA";
    public static final String EnableABQualityManage = "EnableABQualityManage";
    public static final String EnableWorkPieceToOP = "EnableWorkPieceToOP";
    public static final String StudentFileSupCorpNo = "StudentFileSupCorpNo";
    public static final String EnableAutoMRP = "EnableAutoMRP";

    private static Map<String, String> items = new HashMap<>();

    static {
        // 虚拟参数
        items.put("_BookInfoSet_", "帐套基本资料设置");
        items.put("_HideLoginInfo_", "禁止所有用户在电脑上保存帐号或密码");

        // 基本资料
        items.put(AccInitYearMonth, "财务期初开帐年月");
        items.put(AllowScanBCMode, "允许出货作业时，使用条码枪备货扫描作业模式");
        items.put(Report, "可对报表台头、销售订单页头和页尾、条码标签广告语、小票打印、销售对账页尾进行设置");
        items.put(SenderInfo, "对寄往本公司的收件人姓名、电话、地址进行设置");
        items.put(DisablePasswordSave, "禁止所有用户在电脑上保存密码");
        items.put(DisableAccountSave, "禁止所有用户在电脑上保存帐号");

        // ERP相关
        items.put(AllowERPSynchro, "允许地藤系统与ERP系统交换数据,此功能当前暂支持华软ERP系统");
        // items.put(StockReadFromERP, "从ERP系统中同步当前库存数量、安全库存设置");
        // items.put(OutUPReadFromERP, "从ERP系统中获取出厂价、批发价、零售价");
        // items.put(InUPReadFromERP, "从ERP系统中获取采购进货报价资料");
        // items.put(DAReadFromERP, "从ERP系统中取得采购单据（不含采购价）");
        // items.put(CusInfoReadFromERP, "从ERP系统中读取客户基本资料");
        // items.put(ODWriteToERP, "同步业务订单数据到ERP系统");
        // items.put(ODStatusReadFromERP, "ERP系统中业务订单生效后，地藤系统方可出货");
        // items.put(ERPSalesDeptCode, "同步到ERP的业务订单部门代码");
        // items.put(BCWriteToERP, "同步出货单数据到ERP系统");
        // items.put(BCStatusReadFromERP, "ERP系统中出货单生效后，地藤系统方可备货");
        // items.put(OtherWriteToERP, "同步其它库存变动数据到ERP系统");
        // items.put(ERPAppServer, "ERP主机地址");
        // items.put(ERPCorpCode, "ERP数据库名");
        // items.put(ERPVersion, "与地藤对接ERP版本");
        items.put(ScanBCAdminControl, "扫描状态变更由专人控制");
        items.put(ScanBCAdminAccount, "变更指定操作人员帐号");
        items.put(ReportHead, "报表表头设置");
        items.put(ReportTranBCHead, "销售订单页头备注");
        items.put(ReportTranBCFoot, "销售订单页尾备注");
        items.put(ReportBarcode, "条码标签广告语");
        items.put(ReportTranBETitle, "小票打印报表店家名称");
        items.put(ReportTranBETel, "小票打印报表店家电话");
        items.put(ReportCheckARFoot, "销售对账页尾备注");

        // 系统基本设置
        items.put(AccInitYearMonth, "财务期初开帐年月");
        items.put(UpdateInUPFromAA, "允许在进货时，使用输入的进货价来更新商品基本资料中的进货价");
        items.put(EnableStockLessControl, "启用商品库存不足控制，不允许库存数量出现负数");
        items.put(EnableSendMailIntro, "启用邮件发送功能，在接单及出货时，自动发送邮件于客户");
        items.put(EnableSendMobileIntro, "启动简讯发送功能，在出货等时机点，自动发送简讯（<font color=red>此项额外收费：0.06元/条</font>）");
        items.put(EnableNotODToBC, "启用无订单出货模式，允许直接录入批发出货单");
        items.put(EnablePackageNumInput, "启用包装单位录单,即在录单时可以录入包装的数量");
        items.put(AllowDiyPartCode, "关闭商品自动编号，改为手动录入商品编号");
        items.put(EanbleSalesPromotion, "启用促销包作业模式，用于满足如买M送N，或量大优惠等");
        items.put(BEDefaultCusCode, "快速销售模式时，零售默认客户代码（<font color=red>新版系统将以默认零售会员为主</font>）");
        items.put(BEDefaultVipCard, "登记零售单时，零售默认会员代码");
        items.put(BMDefaultDeptCode, "登记转账单时，银行费用默认部门代码");
        items.put(OEDefaultBusiness, "默认业务人员，用于处理在线订货单客户关联");
        items.put(CostPriceSet, " 成本单价取移动加权价（如打上勾取加权价，如不打上勾取进货价）");
        items.put(SafetyStockSynPartStock, "启用分仓别进行安全库存设置(其会自动同步到商品基本资料档)");
        items.put(OrdToPurFinal, "关闭订单转采购自动审核");
        items.put(StockToPurFinal, "关闭库存转采购自动审核");
        items.put(PurToPurFinal, "关闭手开采购自动审核");
        items.put(DefaultCWCode, "默认仓别代码");
        items.put(ABAndBGDefaultMonthly, "进货时不登记现金付款金额，进货后由财务做付款单登记");
        items.put(BEDefaultBankAccount, "默认柜台银行刷卡账户");

        // 高级参数设置
        items.put(EnableCustomerCare, "启用客户关怀功能，允许一个客户有多个联系人，并登记其生日提醒等资料");
        items.put(EnableStockCWReport, "启用进出仓通知作业模式，需要打印进、出仓通知单");
        items.put(AllowAreaControl, "启用商品区域专卖控制，防范同区域恶性竞争");
        items.put(AGAutoAreaBlack, "将退货单自动设置成销售黑名单（须先启动区域专卖管控）");
        items.put(EnableWHManage, "启用多仓别管理，允许设置多个仓别及使用仓别调拔单");
        items.put(EnableTranDetailCW, "启动库存入、出库单单身仓别管理");
        items.put(EnablePrintCurStock, "打印出仓通知单时打印分仓库存");
        items.put(DADefaultSupCode, "MRP计算时，默认厂商代码");
        items.put(DefaultProcCode, "BOM默认制程代码（仅用于制造业版）");
        items.put(PurSafetyRateDefault, " 设置安全库存采购线默认20%低于此值时，将会生成采购建议值");
        items.put(CusCreditLiit, "启用客户信用额度管理");
        items.put(EnableApproveFlow, "启用单据审核流程管理");
        items.put(CreditLineApproveUser, "超出信用额度审核人员帐号（注：须先启用单据审核流程管理）");
        items.put(TranAGApproveUser, "在无销售单时，进行销货退回时的审核人员帐号（注：须先启用单据审核流程管理）");
        items.put(NoAllowSalesBCToAG, "不允许无销售订单进行退货");
        items.put(EnableSyncERP, "启用华软ERP同步到地藤系统");
        items.put(AvailableStockOption, "可用库存设置，默认可用库存等于当前库存");
        items.put(UpdateCurrentMonthProfit, "在修改进货价后，每晚自动更新本月所有单据的成本价与毛利");
        items.put(UpdateTBDateToEffectiveDate, "单据生效时，单据日期自动等于生效日期");
        items.put(IsViewOldMenu, "是否显示旧版菜单链接（带闪电标识）");
        items.put(DefaultProfitMargin, "网单代发货利润率（<font color=red>需启用O2O模组</font>）");
        items.put(OnlineToOfflineMaxScale, "商家代发允许的最大时间，按分钟计（<font color=red>需启用O2O模组</font>）");
        items.put(OnlineToOfflineArea, "网单代发区域等级范围设置（<font color=red>0、按省 1、按市 2、按县 3、指定市 4、指定县 5、指定镇</font>）");
        items.put(SupplyQuotationGrade, "是否开启采购报价单阶梯报价");
        items.put(EnableForecastMode, "是否开启销售预测管理模式");
        items.put(EnableAutoFinishDA, "是否开启采购单自动结案");
        items.put(EnableABQualityManage, "是否开启进货单商品品质状况管理");
        items.put(EnableWorkPieceToOP, "是否开启员工计件生成生产报工单");
        items.put(EnableAutoMRP, "是否开启自动MRP计算");

        // 安全管控参数
        items.put(EnableReportSecurity, "启用安全报表控制，未确认的单据不允许打印报表");
        items.put(NotPrintListUP, "在商品条码中不打印统一售价");
        items.put(EnablePrintPartCode, "在打印报表时是否打印商品编号");

        // 其它参数
        items.put(ZZTPY_VERSION, "启动【郑州太平洋】专用功能项");
        items.put(AllowMallShare, "是否开放在线商城（允许所有人查看本公司商品信息）");
        items.put(StudentFileSupCorpNo, "设置互联上游账套（助学计划）");
    }

    public BookOptions(IHandle handle) {
        super();
        this.handle = handle;
    }

    // 启用单据签核流程管理
    public static boolean isEnableApproveFlow(IHandle handle) {
        String val = getOption(handle, EnableApproveFlow, "");
        return "on".equals(val);
    }

    public static String getOption(IHandle handle, String ACode) {
        return getOption(handle, ACode, "");
    }

    public static String getOption(IHandle handle, String ACode, String ADefault) {
        if (ACode.equals(AccInitYearMonth)) {
            return getOption2(handle, ACode, ADefault);
        }

        try (MemoryBuffer buff = new MemoryBuffer(BufferType.getVineOptions, handle.getCorpNo(), ACode)) {
            if (buff.isNull()) {
                ISystemTable systemTable = Application.getBean("systemTable", ISystemTable.class);
                BuildQuery f = new BuildQuery(handle);
                f.add("select Value_ from %s ", systemTable.getBookOptions());
                f.byField("CorpNo_", handle.getCorpNo());
                f.byField("Code_", ACode);
                f.open();
                if (!f.getDataSet().eof())
                    buff.setField("Value_", f.getDataSet().getString("Value_"));
                else
                    buff.setField("Value_", ADefault);

            }
            return buff.getString("Value_");
        }
    }

    public static String getOption2(IHandle handle, String ACode, String def) {
        try (MemoryBuffer buff = new MemoryBuffer(BufferType.getVineOptions, handle.getCorpNo(), ACode)) {
            if (buff.isNull() || buff.getString("Value_").equals("")) {
                log.info("reset buffer.");
                LocalService ser = new LocalService(handle, "SvrBookOption");
                if (ser.exec("Code_", ACode) && !ser.getDataOut().eof())
                    buff.setField("Value_", ser.getDataOut().getString("Value_"));
                else
                    buff.setField("Value_", def);

            }
            return buff.getString("Value_");
        }
    }

    public static boolean checkStockNum(IHandle handle, double AStock) {
        if (getOption(handle, EnableStockLessControl, "off").equals("on"))
            return AStock >= 0;
        else
            return true;
    }

    // 是否不允许库存为负数
    public static boolean isEnableStockLessControl(IHandle handle) {
        return getOption(handle, EnableStockLessControl, "off").equals("on");
    }

    // 是否启动与ERP同步
    public static boolean isEnableSyncERP(IHandle handle, DataSet dataIn) {
        if (dataIn.getHead().exists("SyncERPToVine"))
            return false;
        return getEnabled(handle, EnableSyncERP);
    }

    // 启用商品区域专卖控制，防范同区域恶性竞争
    public static boolean isAllowAreaControl(IHandle handle) {
        return getEnabled(handle, AllowAreaControl);
    }

    // 将退货单自动设置成销售黑名单（须先启动区域专卖管控）
    public static boolean isAGAutoAreaBlack(IHandle handle) {
        return getEnabled(handle, AGAutoAreaBlack);
    }

    public static boolean isEnableSendMobileIntro(IHandle handle) {
        return getEnabled(handle, EnableSendMobileIntro);
    }

    // 成本价是否取加权价
    @Deprecated
    public static boolean isCostPriceSet(IHandle handle) {
        return getEnabled(handle, CostPriceSet);
    }

    public static String getAccInitYearMonth(IHandle handle) {
        String result;
        String paramKey = AccInitYearMonth;
        // String result = getOption(owner, paramKey, "201301";
        try (MemoryBuffer buff = new MemoryBuffer(BufferType.getVineOptions, handle.getCorpNo(), paramKey)) {
            if (buff.isNull() || buff.getString("Value_").equals("")) {
                ISystemTable systemTable = Application.getBean("systemTable", ISystemTable.class);
                BuildQuery f = new BuildQuery(handle);
                String corpNo = handle.getCorpNo();
                f.add("select * from %s ", systemTable.getBookOptions());
                f.byField("CorpNo_", corpNo);
                f.byField("Code_", paramKey);
                SqlQuery ds = f.open();
                if (!ds.eof()) {
                    result = ds.getString("Value_");
                    if ("".equals(result)) {
                        result = getBookCreateDate(handle).getYearMonth();
                        ds.edit();
                        ds.setField("Value_", result);
                        ds.post();
                    }
                } else {
                    result = getBookCreateDate(handle).getYearMonth();
                    BookOptions app = new BookOptions(handle);
                    app.appendToCorpOption(corpNo, paramKey, result);
                }
                buff.setField("Value_", result);

            }
            result = buff.getString("Value_");
        }
        // 做返回值复查
        if (result == null || "".equals(result))
            throw new RuntimeException("期初年月未设置，请先到系统参数中设置好后再进行此作业!");
        return result;
    }

    // 从系统帐套中取开帐日期
    private static TDate getBookCreateDate(IHandle handle) {
        ISystemTable systemTable = Application.getBean("systemTable", ISystemTable.class);
        BuildQuery f = new BuildQuery(handle);
        String corpNo = handle.getCorpNo();
        f.byField("CorpNo_", corpNo);
        f.add("select AppDate_ from %s", systemTable.getBookInfo());
        SqlQuery ds = f.open();
        if (ds.size() == 0)
            throw new RuntimeException(String.format("没有找到帐套：%s", corpNo));
        return ds.getDate("AppDate_");

    }

    public static String getParamName(String paramCode) {
        String result = items.get(paramCode);
        if (result == null || "".equals(result))
            throw new RuntimeException("没有注册的帐套参数: " + paramCode);
        return result;
    }

    // 增加账套参数
    public void appendToCorpOption(String corpNo, String paramKey, String def) {
        ISystemTable systemTable = Application.getBean("systemTable", ISystemTable.class);
        SqlQuery cdsTmp = new SqlQuery(handle);
        cdsTmp.add("select * from %s where CorpNo_=N'%s' and Code_='%s' ", systemTable.getBookOptions(), corpNo,
                paramKey);
        cdsTmp.open();
        if (!cdsTmp.eof())
            return;
        String paramName = getParamName(paramKey);
        cdsTmp.append();
        cdsTmp.setField("CorpNo_", corpNo);
        cdsTmp.setField("Code_", paramKey);
        cdsTmp.setField("Name_", paramName);
        cdsTmp.setField("Value_", def);
        cdsTmp.setField("UpdateKey_", Utils.newGuid());
        cdsTmp.post();

    }

    public static boolean getEnabled(IHandle handle, String ACode) {
        String val = getOption(handle, ACode, "");
        return "on".equals(val);
    }

    public static String getDefaultCWCode(IHandle handle) {
        String result = getOption(handle, DefaultCWCode).trim();
        if (result == null || "".equals(result))
            result = "仓库";
        return result;
    }

    public static boolean getEnableWHManage(IHandle handle) {
        return getEnabled(handle, EnableWHManage);
    }

    public static boolean getEnableTranDetailCW(IHandle handle) {
        return getEnabled(handle, EnableTranDetailCW);
    }

    // 仓别控制权限
    public static TWHControl getWHControl(IHandle handle) {
        // 拆装单单头仓别管控
        boolean enableWHManage = BookOptions.getEnableWHManage(handle);
        // 拆装单单身仓别管控
        boolean enableTranDetailCW = BookOptions.getEnableTranDetailCW(handle);
        if (enableWHManage && enableTranDetailCW)
            return TWHControl.whcBody;
        else if (enableWHManage)
            return TWHControl.whcHead;
        else
            return TWHControl.whcNone;
    }

    /*
     * 可用库存
     */
    public static boolean isEnableAvailableStock(IHandle handle) {
        String result = getOption(handle, AvailableStockOption).trim();
        return "1".equals(result);
    }

    // 是否允许设置分仓安全库存(请使用新的函数：isEnableDetailSafeStock);
    @Deprecated
    public static boolean isSafetyStockSynPartStock(IHandle handle) {
        return getEnabled(handle, SafetyStockSynPartStock);
    }

    // 是否允许设置分仓安全库存
    public static boolean isEnableDetailSafeStock(IHandle handle) {
        return getEnabled(handle, SafetyStockSynPartStock);
    }

    public static boolean isEnableAccBook(IHandle handle) {
        return getEnabled(handle, EnableAccBook);
    }

    // 是否在修改进货价后，每晚自动更新本月所有单据的成本价与毛利
    public static boolean isUpdateCurrentMonthProfit(IHandle handle) {
        return getEnabled(handle, UpdateCurrentMonthProfit);
    }

    // 是否禁止所有用户在电脑上保存帐号，禁止后首页不显示账套信息
    public static boolean isDissableAccountSave(IHandle handle) {
        return getEnabled(handle, DisableAccountSave);
    }

    // 是否开启不允许无销售订单进行退货
    public static boolean isAllowSaleBCToAG(IHandle handle) {
        return getEnabled(handle, NoAllowSalesBCToAG);
    }

    // 是否开启单据生效时，单据日期等于生效日期
    public static boolean isUpdateTBDateToEffectiveDate(IHandle handle) {
        return getEnabled(handle, UpdateTBDateToEffectiveDate);
    }

    // 是否开启网上订单菜单
    public static boolean isEnableOnlineToOfflineMenu(IHandle handle) {
        return getEnabled(handle, OnlineToOfflineMenu);
    }
}
