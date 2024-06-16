package logic;

public class Soundboard {
    private Theme theme;

    public Soundboard(Theme theme) {
        this.theme = theme;
    }

    public Soundboard() {
        this.theme = Theme.SPACE;
    }

    public Theme getTheme() {
        return theme;
    }

    public void setTheme(Theme theme) {
        this.theme = theme;
    }

    public void playBGM(){

    }
    public void winSound(){

    }
    public void loseSound(){

    }
    public void popSound(){

    }
}
