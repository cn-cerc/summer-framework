package cn.cerc.ui.core;

import javax.servlet.http.HttpServletRequest;

import cn.cerc.core.Utils;
import cn.cerc.mis.core.IRequestOwner;

public class RequestReader {

    private HttpServletRequest request;

    public RequestReader(IRequestOwner owner) {
        this.request = owner.getRequest();
    }

    public boolean hasValue(INameOwner owner) {
        String value = request.getParameter(owner.getName());
        return Utils.isNotEmpty(value);
    }

    public String getString(INameOwner owner, String defaultValue) {
        String result = request.getParameter(owner.getName());
        return result != null ? result : defaultValue;
    }

    public int getInt(INameOwner owner, int defaultValue) {
        return Integer.parseInt(getString(owner, String.valueOf(defaultValue)));
    }

}
