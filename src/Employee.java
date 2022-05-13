import java.time.LocalDate;
import java.time.Period;

public class Employee {

    private final String mName;
    private final JobEnum mJob;
    private final boolean mPitLicense, mExpeditorTraining;
    private final LocalDate mBirthday;

    private PositionSlot[] mCurrPositionSlots = new PositionSlot[3];
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

    /**
     * @param position
     * @return the position that was replaced, null if no position was replaced
     */
    public PositionSlot addPosition(PositionSlot position) {
        for (int i = 0; i < mCurrPositionSlots.length; i++) {
            if (mCurrPositionSlots[i] == null) {
                mCurrPositionSlots[i] = position;
                return null;
            }
        }
        PositionSlot replacedPosition = mCurrPositionSlots[mCurrPositionSlots.length - 1];
        replacedPosition.clearEmployee();
        mCurrPositionSlots[mCurrPositionSlots.length - 1] = position;
        return replacedPosition;
    }
    public void removePosition(PositionSlot position) {
        position.clearEmployee();
        for (int i = 0; i < mCurrPositionSlots.length; i++) {
            if (mCurrPositionSlots[i] == position) {
                // Shift them all to the left once.
                for (int j = i; j < mCurrPositionSlots.length - 1; j++) {
                    mCurrPositionSlots[j] = mCurrPositionSlots[j + 1];
                }
                mCurrPositionSlots[mCurrPositionSlots.length - 1] = null;
            }
        }
    }

    public String toCsvLine() {
        StringBuilder sb = new StringBuilder();
        sb.append(mName); sb.append(',');
        sb.append(mJob.name()); sb.append(',');
        sb.append(mPitLicense ? "true" : "false"); sb.append(',');
        sb.append(mExpeditorTraining ? "true" : "false"); sb.append(',');
        sb.append(mBirthday); sb.append(',');
        return sb.toString(); }

    public String getInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append(getName());
        sb.append("\nDate of girth: "); sb.append(mBirthday);
        sb.append("\nPIT License: "); sb.append(mPitLicense ? "Yes" : "No");
        sb.append("\nExpeditor Training: "); sb.append(mExpeditorTraining ? "Yes" : "No");

        if (mCurrPositionSlots[1] != null) {
            sb.append("\nCurrent Positions:\n");
            for (PositionSlot positionSlot : mCurrPositionSlots) {
                if (positionSlot != null) {
                    sb.append("  ");
                    sb.append(positionSlot.getName());
                    sb.append(",\n");
                } else break;
            }
            sb.delete(sb.length() - 2, sb.length());
        }
        else if (mCurrPositionSlots[0] == null) sb.append("\nCurrent Position: NONE");
        else {
            sb.append("\nCurrent Positions:\n  ");
            sb.append(mCurrPositionSlots[0].getName());
        }

        if (mPrevPositionSlots[1] != -1) {
            sb.append("\nPrevious Positions:\n");
            for (int positionSlot : mPrevPositionSlots) {
                if (positionSlot != -1) {
                    sb.append("  ");
                    sb.append(PositionSlot.getName(positionSlot));
                    sb.append(",\n");
                } else break;
            }
            sb.delete(sb.length() - 2, sb.length());
        }
        else if (mPrevPositionSlots[0] == -1) sb.append("\nPrevious Position: NONE");
        else {
            sb.append("\nPrevious Positions:\n  ");
            sb.append(PositionSlot.getName(mPrevPositionSlots[0]));
        }

        return sb.toString();
    }
    @Override
    public String toString() { return getName(); }
}