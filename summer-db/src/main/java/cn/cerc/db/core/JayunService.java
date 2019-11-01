package cn.cerc.db.core;

/**
 * 专用于聚安云调用
 */
public class JayunService extends MicroService {
    public JayunService() {
        super();
        this.setHost("http://www.jayun.site");
        this.setPath("/api");
        this.setPort(80);
    }

    public static void main(String[] args) {
        JayunService service = new JayunService();
        service.putParameter("appKey", "asdfsdf");
        System.out.println(service.post("server.getIP"));
        System.out.println(service.isResult());
        System.out.println(service.getMessage());
        System.out.println(service.getResponseContent());
        System.out.println(service.getResponse());
    }
}
