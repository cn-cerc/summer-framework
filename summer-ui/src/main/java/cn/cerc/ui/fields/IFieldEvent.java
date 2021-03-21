package cn.cerc.ui.fields;

//动作设置
public interface IFieldEvent {
    String getOninput();

    Object setOninput(String oninput);

    String getOnclick();

    String getOnclick(String onclick);
}
