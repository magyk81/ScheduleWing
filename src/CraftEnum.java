import javafx.scene.paint.Color;

public enum CraftEnum {
    MAILHANDLER {
        @Override
        public Color getColor() {return Color.PEACHPUFF;}
    },
    CLERK {
        @Override
        public Color getColor() {return Color.LIGHTBLUE;}
    },
    EITHER;

    public Color getColor() { return Color.LIGHTGREEN; }
}
