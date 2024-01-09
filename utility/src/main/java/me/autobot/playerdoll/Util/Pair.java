package me.autobot.playerdoll.Util;

public final class Pair <A,B> {
    private final A A;
    private final B B;
    public Pair(A A, B B) {
        this.A = A;
        this.B = B;
    }
    public A getA() {
        return A;
    }
    public B getB() {
        return B;
    }
}
