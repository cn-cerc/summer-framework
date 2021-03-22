package cn.cerc.ui.fields;

//自动完成（默认为 off）
public interface IFieldAutocomplete {

    boolean isAutocomplete();

    Object setAutocomplete(boolean autocomplete);
}
