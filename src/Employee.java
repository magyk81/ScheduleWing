import java.time.LocalDate;
import java.time.Period;

public class Employee {

    private final String mName;
    private final JobEnum mJob;
    private final boolean mPitLicense, mExpeditorTraining;
    private final LocalDate mBirthday;

    private int[] mCurrPositionSlots = new int[] { -1, -1, -1 };
    private final int[] mPrevPositionSlots = new int[5];

    public Employee(
            String name,
            JobEnum job,
            boolean pitLicense,
            boolean expeditorTraining,
            LocalDate birthday,
            int... prevPositionSlots) {
        mName = name;
        mJob = job;
        mPitLicense = pitLicense;
        mExpeditorTraining = expeditorTraining;
        mBirthday = birthday;

        for (int i = 0; i < mPrevPositionSlots.length; i++) {
            if (i >= prevPositionSlots.length) mPrevPositionSlots[i] = -1;
            else mPrevPositionSlots[i] = prevPositionSlots[i];
        }
    }

    public Employee(String[] vals) {
        this(
                vals[0],
                JobEnum.valueOf(vals[1]),
                vals[2].equals("true"),
                vals[3].equals("true"),
                LocalDate.parse(vals[4]),
                (vals.length > 5) ? Integer.parseInt(vals[5]) : -1,
                (vals.length > 6) ? Integer.parseInt(vals[6]) : -1,
                (vals.length > 7) ? Integer.parseInt(vals[7]) : -1,
                (vals.length > 8) ? Integer.parseInt(vals[8]) : -1,
                (vals.length > 9) ? Integer.parseInt(vals[9]) : -1
        );
    }

    public String getName() { return mName; }

    public int getSeniority() {
        return Period.between(mBirthday, LocalDate.now()).getDays();
    }

    public String toCsvLine() {
        StringBuilder sb = new StringBuilder();
        sb.append(mName);
        sb.append(mJob.name());
        sb.append(mPitLicense ? "true" : "false");
        sb.append(mExpeditorTraining ? "true" : "false");
        sb.append(mBirthday);
        return sb.toString(); }

    public String getInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append(getName());
        sb.append("\nDate of girth: "); sb.append(mBirthday);
        sb.append("\nPIT License: "); sb.append(mPitLicense ? "Yes" : "No");
        sb.append("\nExpeditor Training: "); sb.append(mExpeditorTraining ? "Yes" : "No");

        if (mCurrPositionSlots[1] != -1) sb.append("\nCurrent Positions: ");
        else if (mCurrPositionSlots[0] == -1) sb.append("\nCurrent Position: NONE");
        for (int positionSlot : mCurrPositionSlots) {
            if (positionSlot != -1) {
                sb.append(PositionSlot.getName(positionSlot));
                sb.append(", ");
            } else break;
        }
        if (mCurrPositionSlots[0] != -1) sb.delete(sb.length() - 2, sb.length());

        if (mPrevPositionSlots[1] != -1) sb.append("\nPrevious Positions: ");
        else if (mPrevPositionSlots[0] == -1) sb.append("\nPrevious Position: NONE");
        for (int positionSlot : mPrevPositionSlots) {
            if (positionSlot != -1) {
                sb.append(PositionSlot.getName(positionSlot));
                sb.append(",\n");
            } else break;
        }
        if (mPrevPositionSlots[0] != -1) sb.delete(sb.length() - 2, sb.length());

        return sb.toString();
    }
    @Override
    public String toString() { return getName(); }
}