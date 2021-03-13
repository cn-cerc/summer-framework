package cn.cerc.mis.excel.input;

import cn.cerc.core.DataSet;
import cn.cerc.core.Record;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ImportExcelFile {
    private HttpServletRequest request;
    private List<FileItem> uploadFiles;
    private DataSet dataSet;

    public int init() throws UnsupportedEncodingException {
        dataSet = new DataSet();
        // 处理文件上传
        DiskFileItemFactory factory = new DiskFileItemFactory();
        // 设置最大缓存
        factory.setSizeThreshold(5 * 1024);
        ServletFileUpload upload = new ServletFileUpload(factory);
        try {
            uploadFiles = upload.parseRequest(request);
        } catch (FileUploadException e) {
            // e.printStackTrace();
            uploadFiles = null;
            return 0;
        }
        if (uploadFiles != null) {
            for (int i = 0; i < uploadFiles.size(); i++) {
                FileItem fileItem = uploadFiles.get(i);
                if (fileItem.isFormField()) {
                    // 普通数据
                    String val = new String(fileItem.getString().getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
                    dataSet.getHead().setField(fileItem.getFieldName(), val);
                } else {
                    // 文件数据
                    if (fileItem.getSize() > 0) {
                        Record rs = dataSet.append().getCurrent();
                        rs.setField("_FieldNo", i);
                        rs.setField("_FieldName", fileItem.getFieldName());
                        rs.setField("_ContentType", fileItem.getContentType());
                        rs.setField("_FileName", fileItem.getName());
                        rs.setField("_FileSize", fileItem.getSize());
                    }
                }
            }
        }
        dataSet.first();
        return dataSet.size();
    }

    public FileItem getFile(Record record) {
        int fileNo = record.getInt("_FieldNo");
        return uploadFiles.get(fileNo);
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public ImportExcelFile setRequest(HttpServletRequest request) {
        this.request = request;
        return this;
    }

    public DataSet getDataSet() {
        return dataSet;
    }

    public void setDataSet(DataSet dataSet) {
        this.dataSet = dataSet;
    }
}
