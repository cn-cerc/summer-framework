package cn.cerc.mis.page.upload;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;

import cn.cerc.mis.core.AbstractForm;
import cn.cerc.mis.core.IPage;

/**
 * 文件上传基类
 */
public abstract class FileUploadBasePage extends AbstractForm {

    /**
     * 支持的类型
     */
    private String suportTypes = ".jpg,.bmp,.png";
    /**
     * 是否开启多文件上传，默认关闭
     */
    private boolean multiple = false;
    /**
     * 单文件最大上传大小，默认5M
     */
    private long maxSize = 5 * 1024 * 1024;
    /**
     * 单别
     */
    private String tb;
    /**
     * 单号
     */
    private String tbNo;

    /**
     * 上传路径
     */
    private String uploadPath;

    /**
     * 文件名最大长度，默认20
     */
    private int maxNameLength = 20;

    /**
     * 列表地址
     */
    private String action;

    /**
     * 执行方法（默认列表页，1上传，2,删除，3下载）
     */
    private int doMethod;

    /**
     * 标题
     */
    private String pageTitle = "附件管理";
    /**
     * 导航栏
     */
    protected Map<String, String> menuPath = new LinkedHashMap<>();

    @Override
    public IPage execute() {
        if (getPageNo() == 1) {
            return upload();
        } else if (getPageNo() == 2) {
            return delete();
        } else if (getPageNo() == 3) {
            return download();
        }
        return exec();
    }

    /**
     * 列表页
     * 
     * @param params
     * @return
     */
    public abstract IPage exec();

    /**
     * 文件上传
     * 
     * @return
     */
    public abstract IPage upload();

    /**
     * 文件删除
     * 
     * @return
     */
    public abstract IPage delete();

    /**
     * 文件下载
     * 
     * @return
     */
    public abstract IPage download();

    public String getTb() {
        return tb;
    }

    public void setTb(String tb) {
        this.tb = tb;
    }

    public String getTbNo() {
        return tbNo;
    }

    public void setTbNo(String tbNo) {
        this.tbNo = tbNo;
    }

    public Map<String, String> getMenuPath() {
        return menuPath;
    }

    public void setMenuPath(Map<String, String> menuPath) {
        this.menuPath = menuPath;
    }

    public String getSuportTypes() {
        return suportTypes;
    }

    public void setSuportTypes(String suportTypes) {
        this.suportTypes = suportTypes;
    }

    public boolean isMultiple() {
        return multiple;
    }

    public void setMultiple(boolean multiple) {
        this.multiple = multiple;
    }

    public long getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(long maxSize) {
        this.maxSize = maxSize;
    }

    public String getUploadPath() {
        return uploadPath;
    }

    public void setUploadPath(String uploadPath) {
        this.uploadPath = uploadPath;
    }

    public int getMaxNameLength() {
        return maxNameLength;
    }

    public void setMaxNameLength(int maxNameLength) {
        this.maxNameLength = maxNameLength;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public int getPageNo() {
        return doMethod;
    }

    public void setPageNo(int pageNo) {
        this.doMethod = pageNo;
    }

    /**
     * 请求文件
     * 
     * @param link 文件链接
     * @return
     */
    protected InputStream doGetByStream(String link) {
        try {
            URL url = new URL(link);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
            return conn.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 判断是否支持该文件类型
     * 
     * @param fileName 文件名
     * @return
     */
    protected boolean isSurpots(String fileName) {
        String[] types = getSuportTypes().split("[,，]");
        for (String type : types) {
            if (fileName.endsWith(type)) {
                return true;
            }
        }
        return false;
    }

    public String getPageTitle() {
        return pageTitle;
    }

    public void setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
    }

}
