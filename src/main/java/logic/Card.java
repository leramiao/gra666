package logic;

public class Card {
    private final Suit suit;
    private final Color color;
    private final int value;
    /*
    A - 11 pkt.
    10 - 10 pkt.
    K - 4 pkt.
    Q - 3 pkt.
    J - 2 pkt.
    ???? 9 = 0pkt.
    */

    public Card(Suit suit, Color color, int value) {
        this.suit = suit;
        this.color = color;
        this.value = value;
    }

    public Suit getSuit() {
        return suit;
    }

    public Color getColor() {
        return color;
    }

    public int getValue() {
        return value;
    }

    public String getSpriteFilename(){
        return "media/cards/"+suit.name()+value+".png";
    }
}
