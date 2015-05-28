package net.unverschaemt.pinfever;

/**
 * Created by D060338 on 28.05.2015.
 */
public enum RequestMethod {
    GET(0),
    POST(1);

    private final int method;

    private RequestMethod(int method) {
        this.method = method;
    }

    public int getValue() {
        return this.method;
    }
}