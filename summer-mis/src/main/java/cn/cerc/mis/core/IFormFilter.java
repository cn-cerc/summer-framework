package cn.cerc.mis.core;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Deprecated
public interface IFormFilter {
    boolean doFilter(HttpServletResponse resp, String formId, String funcCode) throws IOException;
}
