package volvic.tyrannosaurusallen;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class PositionSlot {

    public final static int WIDTH = 60, HEIGHT = WIDTH / 2;
    private final static List<PositionSlot> sPositionSlots = new ArrayList<>();

    private final int mId;
    private final boolean mIsLabel;
    private final Line[] mBorder = new Line[4];
    private Employee mEmployee;
    private Rectangle mRect;
    private Text mText;
    private String mTitle, mSubtitle;

    public PositionSlot(
            CraftEnum craft,
            int xPos,
            int yPos,
            String subtitle,
            Text title,
            Consumer<PositionSlot> consoomer) {

        mIsLabel = false;
        init(xPos, yPos);
        mRect.setFill(craft.getColor());
        mSubtitle = subtitle;
        mTitle = title.getText();
        mId = sPositionSlots.size();
        sPositionSlots.add(this);

        mRect.setOnMouseClicked(event -> { consoomer.accept(this); });
        mText.setOnMouseClicked(event -> { consoomer.accept(this); });
    }

    public PositionSlot(int xPos, int yPos, String subtitle) {
        mIsLabel = true;
        init(xPos, yPos);
        mText.setText(subtitle.replace(' ', '\n'));
        if (!mText.getText().contains("\n")) {
            mText.setFont(Font.font(15));
            mText.setY(mRect.getY() + HEIGHT / 1.5);
        }
        mId = -1;
    }

    public static String getName(int idx) {
        return sPositionSlots.get(idx).getName();
    }

    public int getId() { return mId; }
    public Employee getEmployee() { return mEmployee; }
    public void setEmployee(Employee employee) {
        if (mIsLabel) return;
        mEmployee = employee;
        mText.setText(employee.getName().replace(' ', '\n'));
    }

    public void clearEmployee() {
        if (mIsLabel) return;
        mEmployee = null;
        mText.setText("");
    }

    public void addTo(ObservableList<Node> children) {
        if (!mIsLabel) children.add(mRect);
        children.add(mText);
        children.addAll(mBorder);
    }

    public int getTop() { return (int) mRect.getY(); }
    public int getBottom() { return (int) mRect.getY() + HEIGHT; }
    public int getRight() { return (int) mRect.getX() + WIDTH; }

    public String getName() {
        StringBuilder sb = new StringBuilder();
        sb.append(mTitle);
        if (mSubtitle != null) {
            sb.append(" [");
            sb.append(mSubtitle);
            sb.append("]");
        }
        return sb.toString();
    }

    private void init(int xPos, int yPos) {
        mRect = new Rectangle();
        mRect.setX(xPos);
        mRect.setY(yPos);
        mRect.setWidth(WIDTH);
        mRect.setHeight(HEIGHT);
        mBorder[0] = new Line(xPos, yPos, xPos + WIDTH, yPos);
        mBorder[1] = new Line(xPos + WIDTH, yPos, xPos + WIDTH, yPos + HEIGHT);
        mBorder[2] = new Line(xPos + WIDTH, yPos + HEIGHT, xPos, yPos + HEIGHT);
        mBorder[3] = new Line(xPos, yPos + HEIGHT, xPos, yPos);
        for (Line border : mBorder) { border.setFill(Color.BLACK); }
        mText = new Text();
        mText.setTextAlignment(TextAlignment.CENTER);
        mText.setFont(Font.font(10));
        mText.setX(mRect.getX() + WIDTH / 6.0);
        mText.setY(mRect.getY() + HEIGHT / 3.0);
    }
}
