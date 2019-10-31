<!DOCTYPE html>
<html>
    <head>
        <meta charset="utf-8" />
        <title>聚安SDK范例</title>
    </head>
    <?php
        require_once "JayunSDK.php";
        $mobile = "13927470636"; //测试用手机号
        $verifyCode = "313402"; //依实际收到的进行设置
        $user = "testUser"; //应用中的帐号
        $templateId = "SMS_129745895";//此处对应指定模板编号
        $args = array("聚安","小米手机","广东深圳");
        $_SESSION['deviceId'] = 'webclient'; //此处应取手机的唯一ID
    ?>
    <body>

    <div>
        <h3>设置的变量（使用前须进行修改）：</h3>
        mobile: <?php echo $mobile ?></br>
        verifyCode: <?php echo $verifyCode ?></br>
        user: <?php echo $user ?></br>
        templateId: <?php echo $templateId ?></br>
        args: 
        <?php $length=count($args);
        for($x=0; $x<$length; $x++) {
            echo $args[$x] .', ';
          }
        ?>
        </br>
        deviceId: <?php echo $_SESSION['deviceId'] ?></br>
    </div>
    <div>
        <h3>功能选择区(注册)：</h3>
        <a href="JayunDemo.php?action=registerServer">注册当前服务器</a></br>
        <a href="JayunDemo.php?action=requestRegister">新用户申请注册</a></br>
        <a href="JayunDemo.php?action=checkRegister">验证新用户的验证码</a></br>
        <a href="JayunDemo.php?action=userRegister">绑定用户帐号与手机号</a></br>
    </div>
    
    <div>
        <h3>功能选择区(日常)：</h3>
        <a href="JayunDemo.php?action=isSecurity">判断环境是否安全</a></br>
        <a href="JayunDemo.php?action=requestVerify">若环境不安全时，请求检验码</a></br>
        <a href="JayunDemo.php?action=checkVerify">校验验证码</a></br>
        <a href="JayunDemo.php?action=checkEnvironment">整合使用：判断是否安全</a></br>
    </div>

    <div>
        <h3>功能选择区(进阶)：</h3>
        <a href="JayunDemo.php?action=sendMessage">发送指定模板消息</a></br>
    </div>
    
    <div>
        <h3>执行结果区：</h3>
    <?php
        $action = getRequest('action');
        switch($action){
        case "registerServer":
            $api = new JayunAPI();
            $result = $api->registerServer($mobile);
            echo $result ? "注册成功" : $api->getMessage();
            break;
        case "requestRegister":
            $api = new JayunMessage();
            $result = $api->requestRegister($mobile);
            echo $result ? "已向手机号 $mobile 发送验证码" : $api->getMessage();
            break;
        case "checkRegister":
            $api = new JayunMessage();
            $result = $api->checkRegister($mobile, $verifyCode);
            echo $result ? "验证通过" : $api->getMessage();
            break;
        case "sendMessage":
            $api = new JayunMessage();
            $result = $api->sendMessage($user, $templateId, $args);
            echo $result ? "指定模板短信发送成功" : $api->getMessage();
            break;
        case "userRegister": 
            $api = new JayunSecurity();
            $result = $api->register($user, $mobile);
            echo $result ? "关联成功" : $api->getMessage();
            break;
        case "isSecurity":
            $api = new JayunSecurity();
            $result = $api->isSecurity($user);
            echo $result ? "环境安全" : $api->getMessage();
            break;
        case "requestVerify":
            $api = new JayunSecurity();
            $result = $api->requestVerify($user);
            echo $result ? "已向用户手机发送验证码" : $api->getMessage();
            break;
        case "checkVerify":
            $api = new JayunSecurity();
            $result = $api->checkVerify($user, $verifyCode);
            echo $result ? "验证通过" : $api->getMessage();
            break;
        case "checkEnvironment":
            $api = new JayunSecurity();
            $result = $api->checkEnvironment($user);
            echo $result ? "当前环境安全" : $api->getMessage();
            break;
        default:
            echo $action . " 请选择操作";
        }
    ?>
    </div>
    </body>
</html>
