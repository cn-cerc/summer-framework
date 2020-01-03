package cn.cerc.sms;

import lombok.Getter;
import lombok.Setter;

public abstract class CustomSMS {

    @Setter
    @Getter
    private String message;

    @Setter
    @Getter
    private String templateText;

    public abstract boolean send(String mobile, String templateId, String... args);

}
