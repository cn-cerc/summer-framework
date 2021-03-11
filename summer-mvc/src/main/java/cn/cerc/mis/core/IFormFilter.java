package cn.cerc.mis.core;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Deprecated
//TODO IFormFilter 写法不容易配置，应改进
public interface IFormFilter {
    boolean doFilter(HttpServletResponse resp, String formId, String funcCode) throws IOException;
}
