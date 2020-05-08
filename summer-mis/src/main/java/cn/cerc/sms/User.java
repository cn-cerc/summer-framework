package cn.cerc.sms;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class User {
    private String name;
    private String number;

    public User(String name, String number) {
        this.name = name;
        this.number = number;
    }
}
