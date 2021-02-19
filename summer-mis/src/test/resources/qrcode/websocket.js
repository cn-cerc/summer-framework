var websocket = null;
$(function() {
    var socketUrl = $("#socketUrl").val();

    // 判断当前浏览器是否支持WebSocket
    if ('WebSocket' in window) {
        // app服务器地址
        websocket = new WebSocket(socketUrl);
    }

    // 连接发生错误的回调方法
    websocket.onerror = function() {
        setMessageInnerHTML("WebSocket连接发生错误");
    };

    // 接收到消息的回调方法
    websocket.onmessage = function(event) {
        setMessageInnerHTML(event.data);
    }

    // 监听窗口关闭事件，当窗口关闭时，主动去关闭websocket连接，防止连接还没断开就关闭窗口，server端会抛异常。
    window.onbeforeunload = function() {
        closeWebSocket();
    }

    // 将消息显示在网页上
    function setMessageInnerHTML(innerHTML) {
        var data = JSON.parse(innerHTML);
        if (data.result == true) {
            window.location.href = data.url + "?msg=" + data.message;
        } else {
            window.location.href = "FrmSecurity?msg=" + data.message;
        }
    }

    // 关闭WebSocket连接
    function closeWebSocket() {
        websocket.close();
    }

    // 发送消息
    function send() {
        var message = document.getElementById('text').value;
        websocket.send(message);
    }
});
