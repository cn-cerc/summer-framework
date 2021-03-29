package cn.cerc.mis.other;

import cn.cerc.mis.core.IBufferKey;

public enum BufferType implements IBufferKey {
    // 1.取得用户基本资料
    getAccount,
    // 2.取得用户生命值, 已停用
    getSessionViability,
    // 3.取得财务开帐年月
    getAccInitYearMonth,
    // 4.取得公司设置参数
    getVineOptions,
    // 5.异步作业请求ID
    getAsyncRecord,
    // 6.取得指定类别的服务器IP
    getServerCount,
    // 7.取得单别选项
    getTBOptions,
    // 8.取得客户简称
    getCusName,
    // 9.取得厂商简称
    getSupName,
    // 10.取得部门名称
    getDeptName,
    // 11.取得用户选项
    getUserOption,
    // 12.取得公司别资料
    getOurInfo,
    // 13.取得商品大类资料
    getClass1List,
    // 14.采购订单
    getPurOrder,
    // 15.取得库别基本资料
    getStockCWList,
    // 16.取得用户表单参数
    getUserForm,
    // 17.数据表缓存
    getGrid,
    // 18.专用于单据复制
    getTicket,
    // 19.订购菜单缓存
    getOrderMenu,
    // 20.总库存缓存
    getPartStock,
    // 21.分仓库存缓存
    getStockNum;

    @Override
    public int getStartingPoint() {
        return 1000;
    }

    @Override
    public int getMinimumNumber() {
        return 1;
    }

    @Override
    public int getMaximumNumber() {
        return 99;
    }
}
