<?php
session_start();

function getRequest($key){
    if(array_key_exists($key, $_GET)){
        return $_GET[$key];
    }else if(array_key_exists($key, $_GET)){
        return $_POST[$key];
    }else{
        return null;
    }
}
    
class JayunAPI{ //聚安基础调用工具
    private $jayunHost = "http://www.jayun.site";
    private $appKey = "1r625g3wNX91";
    private $appSecret = "532baeaef5544a34a5336fef19bc29b1";
    private $debug = "113.91.143.195"; //正式使用时，此处须置为null

    private $curl;
    private $result = false;
    private $message = null;
        
    function __construct(){
        $this->curl = new Curl();
        $this->curl->addParameter("appKey", $this->appKey);
    }
    
    public function getHost(){
        return $this->host;
    }
    
    //增加一个参数
    public function put($key, $value){
        $this->curl->addParameter($key, $value);
    }
    
    public function post($serviceUrl){
        $this->result = false;
        $this->message = null;
        try {
            $reqUrl = "$this->jayunHost/api/$serviceUrl";
            $result = $this->curl->doPost($reqUrl);
            $json = json_decode($result);
            if (array_key_exists('data', $json)) {
                $this->data = $json->data;
            }
            if (array_key_exists('result', $json)) {
                if (array_key_exists("message", $json)) {
                    $this->message = $json->message;
                }
                $this->result = $json->result;
            } else {
                $this->message = result;
            }
        } catch (Exception $e) {
            log_error("请求的网址不存在，或服务暂停使用中");
            $this->message = $e->getMessage();
        }
    }
    
    public function registerServer(){ //向服务器注册当前应用主机
        if ($this->appKey == null) {
            log_error("jayun.appKey 未设置，无法自动注册 ");
            return;
        }
        if ($this->appSecret == null) {
            log_error("jayun.appSecret 未设置，无法自动注册 ");
            return;
        }
        
        try {
            $reqUrl = "$this->jayunHost/api/server.register";
            $curl = new Curl();
            $curl->addParameter("appKey", $this->appKey);
            $curl->addParameter("appSecret", $this->appSecret);
            $result = $curl->doPost($reqUrl);
            $json = json_decode($result);
            if (array_key_exists('result', $json)) {
                $this->result = $json->result;
                if (array_key_exists("message", $json)) {
                    if ($json->result == 0) {
                        log_error($json->message);
                    }
                }
            } else {
                $this->message = result;
            }
        } catch (Exception $e) {
            $this->message = "请求的网址不存在，或服务暂停使用中";
        }
        return $this->result;
    }
    
    private function log_error($message){
        $this->message = $message;
    }
    
    public function getMessage(){
        return $this->message;
    }
    
    public function isResult(){
        return $this->result;
    }
    
    public function getRemoteIP(){
        return !empty($this->debug) ? $this->debug : $_SERVER['HTTP_HOST'];
    }
}

class Curl{ //工具类，用于以post方式向聚安服务呼叫
    private $debug = true; //正式运行时，此值应该设置为false
    private $params = [];
    
    public function addParameter($key, $value){
        $this->params[$key] = $value;	
    }
    
    public function doPost($url){
        if (empty($url)) {
            return  '{"result":false,"message":"调用参数错误，url is null"}';
        }
        
        $ch = curl_init(); //初始化curl
        curl_setopt($ch, CURLOPT_URL, $url); //抓取指定网页
        curl_setopt($ch, CURLOPT_HEADER, 0); //设置header
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1);//要求结果为字符串且输出到屏幕上
        curl_setopt($ch, CURLOPT_POST, 1); //post提交方式
        curl_setopt($ch, CURLOPT_POSTFIELDS, http_build_query($this->params));
        $data = curl_exec($ch);//运行curl
        curl_close($ch);

        if($this->debug){
            print("<div>debug.data: ");
            print_r($data);
            print("</div>");
        }else {
            print_r($data);
        }
        return $data;
    }
}

class JayunMessage{ //用于新用户注册及验证
    private $sendVoice = false;
    private $message = null;
    
    public function setMessage($message){
        $this->message = $message;
    }
    
    public function getMessage(){
        return $this->message;
    }
        
    //注册时向新用户发送验证码
    public function requestRegister($mobile){
        $api = new JayunAPI();
        $api->put("ip", $api->getRemoteIP());
        $api->put("mobile", $mobile);
        $api->put("sendMode", "sms");
        if ($this->sendVoice)
            $api->put("sendVoice", "true");
        $api->post("message.requestRegister");
        $this->setMessage($api->getMessage());
        return $api->isResult();
    }

    //新用户在收到验证码后，确认是否正确
    public function checkRegister($mobile, $verifyCode){
        $api = new JayunAPI();
        $api->put("ip", $api->getRemoteIP());
        $api->put("mobile", empty($mobile) ? "" : $mobile);
        $api->put("verifyCode", empty($verifyCode) ? "" : $verifyCode);
        $api->post("message.checkRegister");
        $this->setMessage($api->getMessage());
        return $api->isResult();
    }

    public function sendMessage($user, $templateId, $args){
        $api = new JayunAPI();
        $api->put("ip", $api->getRemoteIP());
        $api->put("user", $user);
        $api->put("templateId", $templateId);
        
        $length=count($args);
        for($x=0; $x<$length; $x++) {
            $api->put("arg".$x, $args[$x]);
          }

        $api->post("message.sendMessage");
        $this->setMessage($api->getMessage());
        return $api->isResult();
    }
}

class JayunSecurity{
    private $message = null;
    private $sendVoice = false;

    public function setMessage($message){
        $this->message = $message;
    }
    
    public function getMessage(){
        return $this->message;
    }
    
    /**
     * 向聚安云平台注册用户资料，以及所关联手机号讯息，后续会增加更多的讯息用于登记
     * 
     * @param user   应用的用户账号，并非聚安云的帐号
     * @param mobile 手机号
     * @return true:成功，若失败可用getMessage取得错误信息
     */
    public function register($user, $mobile) {
        $api = new JayunAPI();
        $api->put("ip", $api->getRemoteIP());
        $api->put("user", $user);
        $api->put("mobile", $mobile);
        $api->post("security.register");
        $this->setMessage($api->getMessage());
        return $api->isResult();
    }
    
    /**
     * 检测用户当前使用的IP以及设备是否是安全，不安全的原因可能有：未认证的IP、设备，或未许可的时间段
     * 
     * @param user 应用的用户账号，并非聚安云的帐号r
     * @return true:成功，若失败可用getMessage取得错误信息
     */
    public function isSecurity($user) {
        $api = new JayunAPI();
        $api->put("ip", $api->getRemoteIP());
        $api->put("user", $user);
        $api->put("deviceId", $this->getDeviceId());
        $api->post("security.isSecurity");
        $this->setMessage($api->getMessage());
        return $api->isResult();
    }
    
    /**
     * 发送验证码，请求校验
     * 
     * @param user 应用的用户账号，并非聚安云的帐号
     * @return true 成功，若失败可用getMessage取得错误信息
     */
    public function requestVerify($user) {
        $api = new JayunAPI();
        $api->put("ip", $api->getRemoteIP());
        $api->put("user", $user);
        $api->put("deviceId", $this->getDeviceId());
        $api->put("sendMode", "sms");
        if ($this->sendVoice)
            $api->put("sendVoice", "true");
        $api->post("security.requestVerify");
        $this->setMessage($api->getMessage());
        return $api->isResult();
    }
    
    /**
     * 检测验证码
     * 
     * @param user       应用的用户账号，并非聚安云的帐号
     * @param verifyCode 验证码
     * @return true:成功，若失败可用getMessage取得错误信息
     */
    public function checkVerify($user, $verifyCode) {
        $api = new JayunAPI();
        $api->put("ip", $api->getRemoteIP());
        $api->put("user", $user);
        $api->put("verifyCode", $verifyCode);
        $api->put("deviceId", $this->getDeviceId());
        $api->post("security.checkVerify");
        $this->setMessage($api->getMessage());
        return $api->isResult();
    }
    
    /**
     * 环境安全检测及校验安全码，一般配合 php 使用文件
     * 
     * @param user 用户账号
     * @return true:成功，若失败可用getMessage取得错误信息
     */
    public function checkEnvironment($user) {
        $securityValue = getRequest("securityCode");
        if (!empty($securityValue)) {
            return $this->checkVerify($user, $securityValue);
        } else {
            return $this->isSecurity($user);
        }
    }

    //取得当前用户的设备码
    private function getDeviceId(){
        if(array_key_exists('deviceId', $_SESSION)){
            return $_SESSION["deviceId"];
        }else{
            return "";
        }
    }
}
?>