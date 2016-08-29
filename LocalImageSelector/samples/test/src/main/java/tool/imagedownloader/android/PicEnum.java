package tool.imagedownloader.android;

/**
 * Created by liuchonghui on 16/6/2.
 */
public enum PicEnum {

    PIC_0("Autumn", "This is Autumn", "http://wallpaper.cdn.pandora.xiaomi.com/thumbnail/webp/w1080/MiTv/08dca4796ddb26cbfb1d7d27a7850879fd841db78"),
    PIC_1("Paris", "This is Paris", "http://wallpaper.cdn.pandora.xiaomi.com/thumbnail/webp/w1080/MiTv/0bb8654a9b11447f6341df83518721e1836160df6"),
    PIC_2("Candle", "This is Candle", "http://wallpaper.cdn.pandora.xiaomi.com/thumbnail/webp/w1080/MiTv/00b459584ef6a441303e0f26c684c050facd19c4b"),
    PIC_3("Book", "This is Book", "http://wallpaper.cdn.pandora.xiaomi.com/thumbnail/webp/w1080/MiTv/0d2aa5eb53866ea2d5b67aeb7d7a337229f4120b4"),
    PIC_4("Coast", "This is Coast", "http://wallpaper.cdn.pandora.xiaomi.com/thumbnail/webp/w1080/MiTv/04bb25ed378722f947a595945bd283ecc6e414f9f"),
    PIC_5("Boat", "This is Boat", "http://wallpaper.cdn.pandora.xiaomi.com/thumbnail/webp/w1080/MiTv/0e4cf4d1de916fcd19b2e9d520ba74ae55a41581d"),
    PIC_6("Car", "This is Car", "http://wallpaper.cdn.pandora.xiaomi.com/thumbnail/webp/w1080/MiTv/0925c24e16b6d42c8208eff1d70b61a331aa56005"),
    PIC_7("Perfume", "This is Perfume", "http://wallpaper.cdn.pandora.xiaomi.com/thumbnail/webp/w1080/MiTv/02121f57ca1b548863c33bba511c19bbfd69cbe4b"),
    PIC_8("Art", "This is Art", "http://wallpaper.cdn.pandora.xiaomi.com/thumbnail/webp/w1080/MiTv/06aec5126e269fb4ed9cbcacf4741ef1ef743ab5a");

    PicEnum(String title, String content, String url) {
        this.title = title;
        this.content = content;
        this.url = url;
    }

    public final String url;
    public final String title;
    public final String content;

    public String getUrl() {
        return this.url;
    }

    public String getTitle() {
        return this.title;
    }

    public String getContent() {
        return this.content;
    }

    public static String get(int position) {
        int size = values().length;
        return values()[position % size].getUrl();
    }

    public static PicEnum peek(int position) {
        int size = values().length;
        return values()[position % size];
    }

    public static String getTitle(int position) {
        int size = values().length;
        return values()[position % size].getTitle();
    }

    public static String getContent(int position) {
        int size = values().length;
        return values()[position % size].getContent();
    }
}
