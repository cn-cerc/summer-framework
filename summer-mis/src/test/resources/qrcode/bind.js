function bind() {
    $.ajax({
        url : "FrmSecurity.bind",
        dataType : 'json',
        type : 'post',
        cache : false,
        async : false,
        data : {},
        success : function(data) {
            if (data.result) {
                sendCode(data.qrcode);
            } else {
                showMsg(data.message);
            }
        },
        error : function() {
            showMsg("网络异常");
        }
    });
}

function sendCode(qrcode) {
    var browser = new ClientProxy();
    if (!browser.active
            && (browser.device != 'iphone' || browser.device != 'android')) {
        showMsg("仅支持安卓系统和苹果系统");
        return;
    }
    browser.req = {
        "qrcode" : qrcode
    };
    browser.send("qrcode", function(resp) {
    });
}
