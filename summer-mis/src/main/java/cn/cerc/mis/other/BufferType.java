package cn.cerc.mis.other;

public enum BufferType {
    // 0.测试专用
    test,
    // 1.取得用户基本资料
    getAccount,
    // 2.取得用户生命值, 已停用
    getSessionViability,
    // 3.取得会话用户基本资料
    getSessionInfo,
    // 4.取得财务开帐年月
    getAccInitYearMonth,
    // 5.取得公司设置参数
    getVineOptions,
    // 6.异步作业请求ID
    getAsyncRecord,
    // 7.取得指定类别的服务器IP
    getServerCount,
    // 8.取得单别选项
    getTBOptions,
    // 9.取得客户简称
    getCusName,
    // 10.取得厂商简称
    getSupName,
    // 11.取得部门名称
    getDeptName,
    // 12.取得用户基本认证资料
    getSessionBase,
    // 13.取得用户选项
    getUserOption,
    // 14.取得公司别资料
    getOurInfo,
    // 15.取得商品大类资料
    getClass1List,
    // 16.取得客户端设备类别
    getDeviceInfo,
    // 17.采购订单
    getPurOrder,
    // 18.取得库别基本资料
    getStockCWList,
    // 19.取得用户表单参数
    getUserForm,
    // 20.取得ExportKey
    getExportKey,
    // 21.对象缓存
    getObject,
    // 22.数据表缓存
    getGrid,
    // 23.全局缓存
    getGlobal,
    // 24.专用于单据复制
    getTicket,
    // 25.订购菜单缓存
    getCusMenu,
    // 26.总库存缓存
    getPartStock,
    // 27.分仓库存缓存
    getStockNum
}
