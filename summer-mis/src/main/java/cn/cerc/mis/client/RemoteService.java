package cn.cerc.mis.client;

import cn.cerc.core.DataSet;
import cn.cerc.core.IHandle;
import cn.cerc.core.Record;
import cn.cerc.core.Utils;
import cn.cerc.db.core.Curl;
import cn.cerc.db.core.LocalConfig;
import cn.cerc.mis.config.ApplicationProperties;
import cn.cerc.mis.core.RequestData;
import cn.cerc.mis.other.BufferType;
import cn.cerc.mis.other.MemoryBuffer;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;

@Slf4j
public class RemoteService implements IServiceProxy {
    private IHandle handle;

    private String host;
    private String path;
    private String service;
    private String token;

    private DataSet dataIn;
    private DataSet dataOut;
    private String message;

    private String buffKey;

    public RemoteService(IHandle handle) {
        this.handle = handle;
        this.token = ApplicationProperties.getToken(handle);
    }

    public RemoteService(IHandle handle, String bookNo, String service) {
        this.handle = handle;
        this.token = ApplicationProperties.getToken(handle);

        LocalConfig localConfig = LocalConfig.getInstance();
        this.host = localConfig.getProperty("remote.host", ApplicationProperties.Local_Host);
        this.path = bookNo;

        this.service = service;
    }

    public RemoteService(IHandle handle, String bookNo) {
        this.handle = handle;
        this.token = ApplicationProperties.getToken(handle);

        LocalConfig localConfig = LocalConfig.getInstance();
        this.host = localConfig.getProperty("remote.host", ApplicationProperties.Local_Host);
        this.path = bookNo;
    }

    @Override
    public boolean exec(Object... args) {
        if (args.length > 0) {
            Record headIn = getDataIn().getHead();
            if (args.length % 2 != 0) {
                throw new RuntimeException("传入的参数数量必须为偶数！");
            }
            for (int i = 0; i < args.length; i = i + 2) {
                headIn.setField(args[i].toString(), args[i + 1]);
            }
        }
        log.info(this.service);
        if (Utils.isEmpty(this.service)) {
            this.setMessage("服务代码不允许为空");
            return false;
        }
        if (Utils.isEmpty(this.token)) {
            this.setMessage("token 不允许为空");
            return false;
        }
        if (Utils.isEmpty(this.host)) {
            this.setMessage("host 不允许为空");
            return false;
        }

        try {
            Curl curl = new Curl();
            curl.putParameter("dataIn", getDataIn().getJSON());
            curl.putParameter(RequestData.TOKEN, this.token);
            log.info("url {}", this.getUrl());
            log.info("params {}", curl.getParameters());

            String response = curl.doPost(this.getUrl());
            log.info("response {}", response);

            if (response == null) {
                log.warn("url {}", this.getUrl());
                log.warn("params {}", curl.getParameters());
                this.setMessage("远程服务异常");
                return false;
            }

            JSONObject json = JSONObject.fromObject(response);
            if (json.get("message") != null) {
                this.setMessage(json.getString("message"));
            }

            if (json.containsKey("data")) {
                String dataJson = json.getString("data");
                if (dataJson != null) {
                    this.getDataOut().setJSON(dataJson);
                }
            }
            return json.getBoolean("result");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            if (e.getCause() != null) {
                setMessage(e.getCause().getMessage());
            } else {
                setMessage(e.getMessage());
            }
            return false;
        }
    }

    public String getUrl() {
        return String.format("%s/%s/proxyService?service=%s", this.host, this.path, this.service);
    }

    @Override
    public String getService() {
        return service;
    }

    @Override
    public IServiceProxy setService(String service) {
        this.service = service;
        return this;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String getExportKey() {
        String tmp = "" + System.currentTimeMillis();
        try (MemoryBuffer buff = new MemoryBuffer(BufferType.getExportKey, handle.getUserCode(), tmp)) {
            buff.setField("data", this.getDataIn().getJSON());
        }
        return tmp;
    }

    @Override
    public DataSet getDataOut() {
        if (dataOut == null)
            dataOut = new DataSet();
        return dataOut;
    }

    protected void setDataOut(DataSet dataOut) {
        this.dataOut = dataOut;
    }

    public String getHost() {
        return this.host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    @Override
    public DataSet getDataIn() {
        if (dataIn == null)
            dataIn = new DataSet();
        return dataIn;
    }

    public void setDataIn(DataSet dataIn) {
        this.dataIn = dataIn;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getBuffKey() {
        return buffKey;
    }

    public void setBuffKey(String buffKey) {
        this.buffKey = buffKey;
    }

}
