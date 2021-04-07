package cn.cerc.mis.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import cn.cerc.core.ClassResource;
import cn.cerc.core.DataSet;
import cn.cerc.core.Record;
import cn.cerc.core.Utils;
import cn.cerc.db.core.Curl;
import cn.cerc.db.core.IHandle;
import cn.cerc.mis.SummerMIS;
import cn.cerc.mis.core.RequestData;
import cn.cerc.mis.core.SystemBuffer;
import cn.cerc.mis.other.MemoryBuffer;

public abstract class RemoteService implements IServiceProxy {
    private static final Logger log = LoggerFactory.getLogger(RemoteService.class);
    private static final ClassResource res = new ClassResource(RemoteService.class, SummerMIS.ID);
    private IHandle handle;
    private String service;
    private DataSet dataIn;
    private DataSet dataOut;
    private String message;
    private String token;

    public RemoteService(IHandle handle) {
        this.handle = handle;
    }

    protected void initDataIn(Object... args) {
        if (args.length > 0) {
            Record headIn = getDataIn().getHead();
            if (args.length % 2 != 0) {
                throw new RuntimeException(res.getString(1, "传入的参数数量必须为偶数！"));
            }
            for (int i = 0; i < args.length; i = i + 2) {
                headIn.setField(args[i].toString(), args[i + 1]);
            }
        }
    }

    protected boolean executeService(String site) {
        log.debug(this.service);
        if (Utils.isEmpty(this.service)) {
            this.setMessage(res.getString(2, "服务代码不允许为空"));
            return false;
        }

        try {
            Curl curl = new Curl();
            curl.put("dataIn", getDataIn().getJSON());
            curl.put(RequestData.TOKEN, this.token);
            log.debug("url {}", site);
            log.debug("params {}", curl.getParameters());

            String response = curl.doPost(site);
            log.debug("response {}", response);

            if (response == null) {
                log.warn("url {}", site);
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

    public IHandle getHandle() {
        return handle;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Deprecated
    public RemoteService() {

    }

    @Deprecated
    public String getExportKey() {
        String tmp = "" + System.currentTimeMillis();
        try (MemoryBuffer buff = new MemoryBuffer(SystemBuffer.User.ExportKey, handle.getUserCode(), tmp)) {
            buff.setField("data", this.getDataIn().getJSON());
        }
        return tmp;
    }

}
