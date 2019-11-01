package cn.cerc.mis.core;

public interface IRestful {

    public String getRestPath();

    public void setRestPath(String restPath);

    // // return String.format("%s:%s:%s:%s", "get", handle.getCorpNo(),
    // "order/od", "tbno/params");
    // // return String.format("%s:%s:%s:%s", "get", "", "order/od",
    // "tbno/params");
    // // return String.format("%s::%s:%s", "get", "order/od",
    // "tbno/params");
    // return null;
}
