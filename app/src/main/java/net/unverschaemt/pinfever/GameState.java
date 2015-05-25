package net.unverschaemt.pinfever;

/**
 * Created by D060338 on 25.05.2015.
 */
public enum GameState {
    ACTIVE(0),
    COMPLETED(1),
    REJECTED(2);

    private final int state;

    private GameState(int state) {
        this.state = state;
    }

    public int getValue() {
        return this.state;
    }
}
