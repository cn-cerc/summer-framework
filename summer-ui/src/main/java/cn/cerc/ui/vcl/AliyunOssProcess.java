package cn.cerc.ui.vcl;

public final class AliyunOssProcess {
    private int height = 0;
    private int width = 0;
    private int quality = 0;

    public AliyunOssProcess() {

    }

    public AliyunOssProcess(int width, int quality) {
        super();
        this.width = width;
        this.quality = quality;
    }

    public String getCommand() {
        StringBuffer sb = new StringBuffer();
        if (this.width > 0 && this.height > 0)
            sb.append(String.format("/resize,w_%d,h_%d", this.width, this.height));
        else if (this.width > 0)
            sb.append("/resize,w_" + width);
        else if (this.height > 0)
            sb.append("/resize,h_" + height);
        if (this.quality > 0 && this.quality < 100)
            sb.append("/quality,q_" + quality);
        return sb.toString();
    }

    public AliyunOssProcess setWidth(int width) {
        this.width = width;
        return this;
    }

    public AliyunOssProcess setHeight(int height) {
        this.height = height;
        return this;
    }

    public AliyunOssProcess setQuality(int quality) {
        this.quality = quality;
        return this;
    }

    public int getQuality() {
        return quality;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

}
