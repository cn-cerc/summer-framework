package cn.cerc.mis.core;

import java.io.IOException;

import javax.servlet.ServletException;

public interface IView {
    IForm getForm();

    void setForm(IForm form);

    String execute() throws ServletException, IOException;

}
