package cn.cerc.mis.client;

import cn.cerc.core.DataSet;
import cn.cerc.core.IHandle;
import cn.cerc.core.Record;
import cn.cerc.core.Utils;
import cn.cerc.db.core.Curl;
import cn.cerc.db.core.LocalConfig;
import cn.cerc.mis.config.ApplicationProperties;
import cn.cerc.mis.core.RequestData;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;

@Slf4j
public class RemoteService implements IServiceProxy {

    private String host;
    private String path;
    private String service;
    private String token;

    private DataSet dataIn;
    private DataSet dataOut;
    private String message;

    public RemoteService() {
        LocalConfig localConfig = LocalConfig.getInstance();
        this.host = localConfig.getProperty("remote.host", ApplicationProperties.Local_Host);
    }

    public RemoteService(IHandle handle, String bookNo, String service) {
        this(bookNo, service);
        this.token = ApplicationProperties.getToken(handle);
    }

    public RemoteService(String bookNo, String service) {
        LocalConfig localConfig = LocalConfig.getInstance();
        this.host = localConfig.getProperty("remote.host", ApplicationProperties.Local_Host);
        this.path = bookNo;
        this.service = service;
    }

    @Override
    public boolean exec(Object... args) {
        if (args.length > 0) {
            Record headIn = getDataIn().getHead();
            if (args.length % 2 != 0)
                throw new RuntimeException("传入的参数数量必须为偶数！");
            for (int i = 0; i < args.length; i = i + 2)
                headIn.setField(args[i].toString(), args[i + 1]);
        }

        try {
            Curl curl = new Curl();
            curl.putParameter("service", this.service);
            curl.putParameter("dataIn", getDataIn().getJSON());
            if (Utils.isNotEmpty(this.token)) {
                curl.putParameter(RequestData.TOKEN, this.token);
            }

            String response = curl.doPost(this.getUrl());
            log.info("response {}", response);
            if (response == null) {
                return false;
            }

            JSONObject json = JSONObject.fromObject(response);
            if (json.get("message") != null) {
                this.setMessage(json.getString("message"));
            }

            if (json.containsKey("dataOut")) {
                String dataJson = json.getString("dataOut");
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
        return String.format("%s/%s/proxyService", this.host, this.path);
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

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

}
