package cn.cerc.db.dao;

import cn.cerc.core.SearchKey;
import cn.cerc.core.SpecialNum;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity(name = "s_person")
public class StubPerson implements BigRecord {

    private static final long serialVersionUID = -3899392767135463003L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_")
    private long id;

    @Column(name = "code_")
    @SearchKey
    private String code;

    @Column(name = "name_")
    private String name;

    @SpecialNum
    @Column(name = "num_")
    private int num;

    public StubPerson() {

    }

    public StubPerson(String code) {
        this.code = code;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    @Override
    public void mergeValue(BigRecord base, BigRecord record) {
        // before:
        // this.num = 54
        // base.num = 50
        // record.num = 52
        this.num += ((StubPerson) record).num - ((StubPerson) base).num;
        // after: this.num = 56
    }

    @Override
    public Object getDiffValue(String field, BigRecord record) {
        if ("num".equals(field)) {
            // before: this.num = 56, record.num = 50
            return this.num - ((StubPerson) record).num;
            // after: return = 6
        } else {
            throw new RuntimeException("not support field: " + field);
        }
    }
}
