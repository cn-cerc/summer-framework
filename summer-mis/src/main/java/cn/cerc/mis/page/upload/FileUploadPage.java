package cn.cerc.mis.page.upload;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import cn.cerc.core.DataSet;
import cn.cerc.core.Record;
import cn.cerc.db.core.ServerConfig;
import cn.cerc.db.oss.OssConnection;
import cn.cerc.mis.core.IPage;
import cn.cerc.mis.core.LocalService;
import cn.cerc.mis.language.R;
import cn.cerc.mis.other.BufferType;
import cn.cerc.mis.other.MemoryBuffer;
import cn.cerc.mis.page.RedirectPage;
import cn.cerc.ui.fields.ButtonField;
import cn.cerc.ui.fields.DateTimeField;
import cn.cerc.ui.fields.ItField;
import cn.cerc.ui.fields.StringField;
import cn.cerc.ui.fields.UploadField;
import cn.cerc.ui.grid.AbstractGrid;
import cn.cerc.ui.page.UIPageSearch;
import cn.cerc.ui.parts.UIFormHorizontal;
import cn.cerc.ui.parts.UIHeader;
import cn.cerc.ui.parts.UISheetHelp;
import cn.cerc.ui.parts.UIToolBar;

/**
 * 文件上传实现Form
 */
public class FileUploadPage extends FileUploadBasePage {

    @Override
    public IPage exec() {
        UIPageSearch jspPage = new UIPageSearch(this);
        UIHeader top = jspPage.getHeader();
        Map<String, String> menus = getMenuPath();
        if (!menus.isEmpty()) {
            for (String key : menus.keySet()) {
                top.addLeftMenu(key, R.asString(this, menus.get(key)));
            }
        }

        setCaption(R.asString(this, getPageTitle()));
        top.setPageTitle(R.asString(this, getPageTitle()));

        jspPage.addScriptFile("../imgZoom/imgAlert.js");
        jspPage.addCssFile("../imgZoom/bigImg.css");

        UIToolBar right = jspPage.getToolBar();
        UISheetHelp section1 = new UISheetHelp(right);
        section1.setCaption(R.asString(this, "操作提示"));
        section1.addLine(R.asString(this, "所支持上传的文件类型："), getSuportTypes());
        section1.addLine(getSuportTypes());
        section1.addLine(R.asString(this, "单文件上传最大为：%s B"), getMaxSize());
        section1.addLine(R.asString(this, "文件名长度最大为：%s 个字"), getMaxNameLength());
        section1.addLine(R.asString(this, "是否支持多文件上传：%s"), isMultiple() ? "Yes" : "No");

        try (MemoryBuffer buff = new MemoryBuffer(BufferType.getUserForm, getUserCode(), getAction())) {
            String tb = getTb();
            String tbNo = getTbNo();
            buff.setField("tbNo", tbNo);
            buff.setField("tb", tb);

            UIFormHorizontal upload = jspPage.createSearch(buff);
            upload.setSearchTitle(R.asString(this, "上传文件"));
            upload.setAction(getAction() + "?page=1");
            upload.setCssClass("search sales-search");
            upload.setEnctype("multipart/form-data");
            UploadField uploadField = new UploadField(upload, R.asString(this, "请选择文件"), "file");
            uploadField.setMultiple(isMultiple());

            new ButtonField(upload.getButtons(), R.asString(this, "确认上传"), "submit", "upload");
            upload.readAll();

            LocalService svr = new LocalService(this, "SvrFileUpload.search");
            svr.getDataIn().getHead().setField("tbNo", tbNo);
            if (!svr.exec()) {
                jspPage.setMessage(R.asString(this, svr.getMessage()));
                return jspPage;
            }

            ServerConfig config = ServerConfig.getInstance();
            String ossSite = config.getProperty("oss.site") + "/";

            AbstractGrid gird = jspPage.createGrid(jspPage.getContent(), svr.getDataOut());
            new ItField(gird).setWidth(1);
            new StringField(gird, R.asString(this, "文件名"), "Name_", 3).createText((record, html) -> {
                String name = record.getString("Name_");
                if (name.endsWith(".jpg") || name.endsWith(".png") || name.endsWith(".jpeg") || name.endsWith(".gif")
                        || name.endsWith(".bmp")) {
                    html.print("<a href='javascript:showImages(\"%s\")'>%s</a>", ossSite + record.getString("Path_"),
                            R.asString(this, name));
                } else {
                    html.print(name);
                }
            });
            new StringField(gird, R.asString(this, "文件大小"), "Size_", 2).createText((record, html) -> {
                html.print(record.getString("Size_")+" B");
            });
            new DateTimeField(gird, R.asString(this, "上传时间"), "AppDate_", 4);

            new StringField(gird, R.asString(this, "操作"), "Path_", 4).createText((record, html) -> {
                html.print("<a href='%s?name=%s&link=%s&page=3'>%s</a>", getAction(), record.getString("Name_"),
                        ossSite + record.getString("Path_"), R.asString(this, "下载"));
                html.print(" | ");
                html.print(
                        "<a href='%s?tbNo=%s&name=%s&page=2' onclick=\"if(confirm('%s？')==false)return false;\">%s</a>",
                        getAction(), tbNo, record.getString("Name_"), R.asString(this, "确认删除"), R.asString(this, "删除"));
            });

            String msg = buff.getString("msg");
            if (msg != null && !"".equals(msg)) {
                jspPage.setMessage(msg);
                buff.setField("msg", "");
            }
        }
        return jspPage;
    }

    @Override
    public IPage upload() {
        RedirectPage jspPage = new RedirectPage(this, getAction());
        try (MemoryBuffer buff = new MemoryBuffer(BufferType.getUserForm, getUserCode(), getAction())) {
            String tb = getTb();
            String tbNo = getTbNo();

            long singleMaxSize = getMaxSize();
            String uploadPage = getUploadPath();
            String typeStr = getSuportTypes();
            int maxNameLength = getMaxNameLength();

            DiskFileItemFactory factory = new DiskFileItemFactory();
            // 设置最大缓存
            factory.setSizeThreshold(5 * 1024);
            ServletFileUpload upload = new ServletFileUpload(factory);

            if (typeStr == null || "".equals(typeStr)) {
                buff.setField("msg", String.format(R.asString(this, "请限定所支持的文件类型"), maxNameLength));
                return jspPage;
            }

            List<FileItem> uploadFiles = upload.parseRequest(getRequest());
            OssConnection oss = (OssConnection) getProperty(OssConnection.sessionId);

            LocalService svr = new LocalService(this, "SvrFileUpload.append");
            DataSet dsIn = svr.getDataIn();
            Record headIn = svr.getDataIn().getHead();
            headIn.setField("tb", tb);
            headIn.setField("tbNo", tbNo);

            for (FileItem item : uploadFiles) {
                if (item != null && !item.isFormField() && item.getSize() > 0 && isSurpots(item.getName())) {
                    if (item.getSize() / 1000 > singleMaxSize) {
                        buff.setField("msg", String.format(R.asString(this, "文件过大！单个文件最大不能超过%s"), singleMaxSize));
                        return jspPage;
                    }

                    int pointIndex = item.getName().lastIndexOf(".");
                    if (pointIndex > maxNameLength) {
                        buff.setField("msg", String.format(R.asString(this, "文件名过长！单个文件最大不能超过%s个字"), maxNameLength));
                        return jspPage;
                    }

                    String currentFile = uploadPage + "/" + item.getName();
                    oss.upload(currentFile, item.getInputStream());

                    Record fileInfo = new Record();
                    fileInfo.setField("name", item.getName());
                    fileInfo.setField("path", currentFile);
                    fileInfo.setField("size", item.getSize());
                    dsIn.append(fileInfo);
                }
            }

            if (dsIn.eof()) {
                buff.setField("msg", R.asString(this, "请选择文件！"));
                return jspPage;
            }

            if (!svr.exec()) {
                buff.setField("msg", R.asString(this, svr.getMessage()));
                return jspPage;
            }
            buff.setField("msg", R.asString(this, "上传成功！"));
            return jspPage;
        } catch (FileUploadException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jspPage;
    }

    @Override
    public IPage delete() {
        RedirectPage jspPage = new RedirectPage(this, getAction());
        try (MemoryBuffer buff = new MemoryBuffer(BufferType.getUserForm, getUserCode(), getAction())) {
            String tbNo = getTbNo();
            String name = getRequest().getParameter("name");
            LocalService svr = new LocalService(this, "SvrFileUpload.delete");
            svr.getDataIn().getHead().setField("tbNo", tbNo).setField("name", name);
            if (!svr.exec()) {
                buff.setField("msg", R.asString(this, svr.getMessage()));
            } else {
                buff.setField("msg", R.asString(this, "删除成功！"));
            }
            return jspPage;
        }
    }

    /**
     * 下载文件
     * 
     * @param fileName 文件名
     * @param fileLink 文件路径
     */
    @Override
    public IPage download() {
        String fileName = getRequest().getParameter("name");
        String fileLink = getRequest().getParameter("link");
        HttpServletResponse response = getResponse();
        response.setContentType("multipart/form-data");
        try {
            response.setHeader("Content-Disposition",
                    "attachment;fileName=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8.name()));
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }

        try (ServletOutputStream out = response.getOutputStream(); InputStream inputStream = doGetByStream(fileLink)) {
            int b = 0;
            byte[] buffer = new byte[512];
            while (b != -1) {
                b = inputStream.read(buffer);
                out.write(buffer, 0, b);
            }
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
