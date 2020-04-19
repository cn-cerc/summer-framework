package cn.cerc.core;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity(name = "s_user")
@Select("select * from s_userInfo")
public class StubUser {

    @Id
    @Column(name = "ID_")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @SearchKey
    @Column(name = "code_")
    private String code;

    @Column(name = "name_")
    private String name;

    public static void main(String[] args) {
        ClassFactory.printDebugInfo(StubUser.class);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
