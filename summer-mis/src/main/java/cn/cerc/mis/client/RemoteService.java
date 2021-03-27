package cn.cerc.mis.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import cn.cerc.core.ClassConfig;
import cn.cerc.core.ClassResource;
import cn.cerc.core.DataSet;
import cn.cerc.core.Record;
import cn.cerc.core.Utils;
import cn.cerc.db.core.Curl;
import cn.cerc.db.core.IHandle;
import cn.cerc.mis.SummerMIS;
import cn.cerc.mis.core.Application;
import cn.cerc.mis.core.RequestData;
import cn.cerc.mis.core.SystemBufferType;
import cn.cerc.mis.other.MemoryBuffer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RemoteService implements IServiceProxy {
    private static final ClassResource res = new ClassResource(RemoteService.class, SummerMIS.ID);
    private static final ClassConfig config = new ClassConfig(RemoteService.class, SummerMIS.ID);

    private final IHandle handle;

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
    }

    @Deprecated
    public static RemoteService create(IHandle handle, String bookNo) {
        return new RemoteService(handle, bookNo);
    }

    @Deprecated
    public RemoteService(IHandle handle, String bookNo) {
        this.handle = handle;
        this.token = Application.getToken(handle);

        this.host = getApiHost(bookNo);
        this.path = bookNo;
    }

    public static String getApiHost(String bookNo) {
        String result = config.getString(String.format("remote.host.%s", bookNo), "");
        if (!"".equals(result))
            return result;
        return config.getString(String.format("remote.host.%s", ServiceFactory.BOOK_PUBLIC), "");
    }

    @Override
    public boolean exec(Object... args) {
        if (args.length > 0) {
            Record headIn = getDataIn().getHead();
            if (args.length % 2 != 0) {
                throw new RuntimeException(res.getString(1, "传入的参数数量必须为偶数！"));
            }
            for (int i = 0; i < args.length; i = i + 2) {
                headIn.setField(args[i].toString(), args[i + 1]);
            }
        }
        log.debug(this.service);
        if (Utils.isEmpty(this.service)) {
            this.setMessage(res.getString(2, "服务代码不允许为空"));
            return false;
        }
        if (Utils.isEmpty(this.token)) {
            this.setMessage(res.getString(3, "token 不允许为空"));
            return false;
        }
        if (Utils.isEmpty(this.host)) {
            this.setMessage(res.getString(4, "host 不允许为空"));
            return false;
        }

        try {
            Curl curl = new Curl();
            curl.put("dataIn", getDataIn().getJSON());
            curl.put(RequestData.TOKEN, this.token);
            log.debug("url {}", this.getUrl());
            log.debug("params {}", curl.getParameters());

            String response = curl.doPost(this.getUrl());
            log.debug("response {}", response);

            if (response == null) {
                log.warn("url {}", this.getUrl());
                log.warn("params {}", curl.getParameters());
                this.setMessage(res.getString(5, "远程服务异常"));
                return false;
            }

            ObjectMapper mapper = new ObjectMapper();
            JsonNode json = mapper.readTree(response);
            if (json.get("message") != null) {
                this.setMessage(json.get("message").asText());
            }

            if (json.has("data")) {
                String dataJson = json.get("data").asText();
                if (dataJson != null) {
                    this.getDataOut().setJSON(dataJson);
                }
            }
            return json.get("result").asBoolean();
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
    public RemoteService setService(String service) {
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

    public String getExportKey() {
        String tmp = "" + System.currentTimeMillis();
        try (MemoryBuffer buff = new MemoryBuffer(SystemBufferType.getExportKey, handle.getUserCode(), tmp)) {
            buff.setField("data", this.getDataIn().getJSON());
        }
        return tmp;
    }

    @Override
    public DataSet getDataOut() {
        if (dataOut == null) {
            dataOut = new DataSet();
        }
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
        if (dataIn == null) {
            dataIn = new DataSet();
        }
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
