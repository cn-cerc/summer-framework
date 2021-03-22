package cn.cerc.ui.mvc;

import cn.cerc.core.ClassResource;
import cn.cerc.core.DataSet;
import cn.cerc.core.IUserLanguage;
import cn.cerc.core.Record;
import cn.cerc.db.core.ServerConfig;
import cn.cerc.db.oss.OssConnection;
import cn.cerc.mis.core.IPage;
import cn.cerc.mis.core.LocalService;
import cn.cerc.mis.core.RedirectPage;
import cn.cerc.mis.language.R;
import cn.cerc.mis.other.BufferType;
import cn.cerc.mis.other.MemoryBuffer;
import cn.cerc.ui.SummerUI;
import cn.cerc.ui.fields.ButtonField;
import cn.cerc.ui.fields.DateTimeField;
import cn.cerc.ui.fields.ItField;
import cn.cerc.ui.fields.StringField;
import cn.cerc.ui.fields.UploadField;
import cn.cerc.ui.grid.AbstractGrid;
import cn.cerc.ui.grid.PhoneGrid;
import cn.cerc.ui.page.UIPageSearch;
import cn.cerc.ui.page.upload.FileUploadBasePage;
import cn.cerc.ui.parts.UIFormHorizontal;
import cn.cerc.ui.parts.UIHeader;
import cn.cerc.ui.parts.UISheetHelp;
import cn.cerc.ui.parts.UIToolbar;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * 文件上传实现Form
 */
public class FileUploadPage extends FileUploadBasePage implements IUserLanguage {
    private final ClassResource res = new ClassResource(this, SummerUI.ID);

    @Override
    public IPage exec() {
        UIPageSearch jspPage = new UIPageSearch(this);
        UIHeader header = jspPage.getHeader();
        Map<String, String> menus = getMenuPath();
        if (!menus.isEmpty()) {
            for (String key : menus.keySet()) {
                header.addLeftMenu(key, R.asString(this, menus.get(key)));
            }
        }

        setName(R.asString(this, getPageTitle()));
        header.setPageTitle(R.asString(this, getPageTitle()));

        jspPage.addScriptFile("../imgZoom/imgAlert.js");
        jspPage.addCssFile("../imgZoom/bigImg.css");

        UIToolbar toolbar = jspPage.getToolBar();
        UISheetHelp section1 = new UISheetHelp(toolbar);
        section1.setCaption(res.getString(1, "操作提示"));
        section1.addLine(res.getString(2, "所支持上传的文件类型："), getSuportTypes());
        section1.addLine(getSuportTypes());
        section1.addLine(res.getString(3, "单文件上传最大为：%s B"), getMaxSize());
        section1.addLine(res.getString(4, "文件名长度最大为：%s 个字"), getMaxNameLength());
        section1.addLine(res.getString(5, "是否支持多文件上传：%s"), isMultiple() ? "Yes" : "No");

        try (MemoryBuffer buff = new MemoryBuffer(BufferType.getUserForm, getUserCode(), getAction())) {
            String tb = getTb();
            String tbNo = getTbNo();
            buff.setField("tbNo", tbNo);
            buff.setField("tb", tb);

            UIFormHorizontal upload = jspPage.createSearch(buff);
            upload.setSearchTitle(res.getString(6, "上传文件"));
            upload.setAction(getAction() + "?page=1");
            upload.setCssClass("search sales-search");
            upload.setEnctype("multipart/form-data");
            UploadField uploadField = new UploadField(upload, res.getString(7, "请选择文件"), "file");
            uploadField.setMultiple(isMultiple());

            new ButtonField(upload.getButtons(), res.getString(8, "确认上传"), "submit", "upload");
            upload.readAll();

            LocalService svr = new LocalService(this, "SvrFileUpload.search");
            svr.getDataIn().getHead().setField("tbNo", tbNo);
            if (!svr.exec()) {
                jspPage.setMessage(svr.getMessage());
                return jspPage;
            }

            ServerConfig config = ServerConfig.getInstance();
            String ossSite = config.getProperty("oss.site") + "/";

            AbstractGrid gird = jspPage.createGrid(jspPage.getContent(), svr.getDataOut());
            ItField it = new ItField(gird);
            it.setWidth(1);
            StringField fileFld = new StringField(gird, res.getString(9, "文件名"), "Name_", 3);
            fileFld.createText((record, html) -> {
                String name = record.getString("Name_");
                // 手机端不预览图片
                if (!getClient().isPhone() && (name.endsWith(".jpg") || name.endsWith(".png") || name.endsWith(".jpeg")
                        || name.endsWith(".gif") || name.endsWith(".bmp"))) {
                    html.print("<a href='javascript:showImages(\"%s\")'>%s</a>", ossSite + record.getString("Path_"), name);
                } else {
                    html.print(name);
                }
            });
            StringField sizeFld = new StringField(gird, res.getString(10, "文件大小"), "Size_", 2);
            sizeFld.createText((record, html) -> {
                html.print(record.getString("Size_") + " B");
            });
            DateTimeField dateFld = new DateTimeField(gird, res.getString(11, "上传时间"), "AppDate_", 4);

            StringField opearFld = new StringField(gird, res.getString(12, "操作"), "Path_", 4);
            opearFld.createText((record, html) -> {
                html.print("<a href='%s?name=%s&link=%s&page=3'>%s</a>", getAction(), record.getString("Name_"),
                        ossSite + record.getString("Path_"), res.getString(13, "下载"));
                html.print(" | ");
                html.print(
                        "<a href='%s?tbNo=%s&name=%s&page=2' onclick=\"if(confirm('%s？')==false)return false;\">%s</a>",
                        getAction(), tbNo, record.getString("Name_"), res.getString(14, "确认删除"), res.getString(15, "删除"));
            });

            // 手机版
            if (gird instanceof PhoneGrid) {
                PhoneGrid phoneGrid = (PhoneGrid) gird;
                phoneGrid.addLine().addItem(it, fileFld, sizeFld);
                phoneGrid.addLine().addItem(dateFld, opearFld);
            }

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
                buff.setField("msg", res.getString(16, "请限定所支持的文件类型"));
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
                    // 以字节为单位
                    if (item.getSize() > singleMaxSize) {
                        buff.setField("msg", String.format(res.getString(17, "文件过大！单个文件最大不能超过%s"), singleMaxSize));
                        return jspPage;
                    }

                    int pointIndex = item.getName().lastIndexOf(".");
                    if (pointIndex > maxNameLength) {
                        buff.setField("msg", String.format(res.getString(18, "文件名过长！单个文件最大不能超过%s个字"), maxNameLength));
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
                buff.setField("msg", res.getString(19, "请选择文件！"));
                return jspPage;
            }

            if (!svr.exec()) {
                buff.setField("msg", svr.getMessage());
                return jspPage;
            }
            buff.setField("msg", res.getString(20, "上传成功！"));
            return jspPage;
        } catch (FileUploadException | IOException e) {
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
                buff.setField("msg", svr.getMessage());
            } else {
                buff.setField("msg", res.getString(21, "删除成功！"));
            }
            return jspPage;
        }
    }

    /**
     * @return 下载文件
     */
    @Override
    public IPage download() {
        String fileName = getRequest().getParameter("name");
        String fileLink = getRequest().getParameter("link");
        HttpServletResponse response = getResponse();
        response.setContentType("multipart/form-data");
        try {
            response.setHeader("Content-Disposition", "attachment;fileName=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8.name()));
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }

        try (ServletOutputStream out = response.getOutputStream(); InputStream inputStream = doGetByStream(fileLink)) {
            int b = 0;
            byte[] buffer = new byte[1024];
            while ((b = inputStream.read(buffer)) != -1) {
                out.write(buffer, 0, b);
            }
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getLanguageId() {
        return R.getLanguageId(getHandle());
    }
}
