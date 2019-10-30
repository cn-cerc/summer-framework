package cn.cerc.mis.excel.input;

public class ColumnValidateException extends Exception {
    private String title;
    private String value;
    private int row;
    private int col;

    public ColumnValidateException(String message) {
        super(message);
    }

    private static final long serialVersionUID = 4218729854214897220L;

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
