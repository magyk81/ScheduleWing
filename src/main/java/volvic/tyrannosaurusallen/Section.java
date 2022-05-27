package volvic.tyrannosaurusallen;

public class Section {

    private String mName;
    private boolean mIsMailhandler, mIsClerk;

    public Section(String name, boolean isClerk) {
        mName = name;
        mIsMailhandler = !isClerk;
        mIsClerk = isClerk;
    }
}