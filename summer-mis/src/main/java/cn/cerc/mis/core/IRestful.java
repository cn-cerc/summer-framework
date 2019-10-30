package cn.cerc.mis.core;

public interface IRestful {

    public void setRestPath(String restPath);

    public String getRestPath();

    // // return String.format("%s:%s:%s:%s", "get", handle.getCorpNo(),
    // "order/od", "tbno/params");
    // // return String.format("%s:%s:%s:%s", "get", "", "order/od",
    // "tbno/params");
    // // return String.format("%s::%s:%s", "get", "order/od",
    // "tbno/params");
    // return null;
}
