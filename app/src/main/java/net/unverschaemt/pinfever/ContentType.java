package net.unverschaemt.pinfever;

/**
 * Created by D060338 on 29.05.2015.
 */
public enum ContentType {
    JSON(0),
    FORM_DATA(1);

    private final int type;

    private ContentType(int type) {
        this.type = type;
    }

    public int getValue() {
        return this.type;
    }
}
