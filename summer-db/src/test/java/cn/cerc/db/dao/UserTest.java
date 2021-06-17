package cn.cerc.db.dao;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class UserTest {
    @Column(name = "ID_")
    public String id;
    @Column(name = "Code_")
    public String code;
    @Column(name = "Name_")
    public String name;
    @Column(name = "Mobile_")
    public String mobile;
}