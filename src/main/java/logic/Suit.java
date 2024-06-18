package logic;

public enum Suit {
    DIAMOND,
    CLUB,
    HEART,
    SPADE;
    public Color getColor(){
        if (this.equals(DIAMOND) ||this.equals(HEART))
            return Color.RED;
        return Color.BLACK;
    }
}
