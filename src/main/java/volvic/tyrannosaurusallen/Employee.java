package volvic.tyrannosaurusallen;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

public class Employee {

    private final String mName;
    private final JobEnum mJob;
    private final boolean mPitLicense, mExpeditorTraining;
    private final LocalDate mGirthday;
    private DayOfWeek[] mDaysOff;

    private PositionSlot[] mCurrPositionSlots = new PositionSlot[3];
    private final int[] mPrevPositionSlots = new int[5];

    public Employee(
            String name,
            JobEnum job,
            boolean pitLicense,
            boolean expeditorTraining,
            LocalDate birthday,
            String daysOff,
            int... prevPositionSlots) {
        mName = name.trim();
        mJob = job;
        mPitLicense = pitLicense;
        mExpeditorTraining = expeditorTraining;
        mGirthday = birthday;

        for (int i = 0; i < mPrevPositionSlots.length; i++) {
            if (i >= prevPositionSlots.length) mPrevPositionSlots[i] = -1;
            else mPrevPositionSlots[i] = prevPositionSlots[i];
        }

        final char[] daysOffChars = daysOff.toCharArray();
        mDaysOff = new DayOfWeek[daysOffChars.length];
        for (int i = 0; i < daysOffChars.length; i++) {
            switch (daysOffChars[i]) {
                case 'M' -> mDaysOff[i] = DayOfWeek.MONDAY;
                case 'T' -> mDaysOff[i] = DayOfWeek.TUESDAY;
                case 'W' -> mDaysOff[i] = DayOfWeek.WEDNESDAY;
                case 'R' -> mDaysOff[i] = DayOfWeek.THURSDAY;
                case 'F' -> mDaysOff[i] = DayOfWeek.FRIDAY;
                case 'S' -> mDaysOff[i] = DayOfWeek.SATURDAY;
                case 'U' -> mDaysOff[i] = DayOfWeek.SUNDAY;
            }
        }
    }

    public Employee(String[] vals) {
        this(
                vals[0],
                JobEnum.valueOf(vals[1]),
                vals[2].equals("true"),
                vals[3].equals("true"),
                vals[4].equals("???") ? null : LocalDate.parse(vals[4]),
                vals[5],
                (vals.length > 6) ? Integer.parseInt(vals[6]) : -1,
                (vals.length > 7) ? Integer.parseInt(vals[7]) : -1,
                (vals.length > 8) ? Integer.parseInt(vals[8]) : -1,
                (vals.length > 9) ? Integer.parseInt(vals[9]) : -1,
                (vals.length > 10) ? Integer.parseInt(vals[10]) : -1
        );
    }

    public String getName() { return mName; }

    public int getSeniority() {
        if (mGirthday == null) return 0; // Unknown day of conversion means ZERO seniority ^_^
        return Period.between(mGirthday, LocalDate.now()).getDays();
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

    public void applyAsPrev() {
        List<Integer> newPrevSlots = new ArrayList<Integer>();
        for (int i = 0; i < mCurrPositionSlots.length; i++) {
            if (newPrevSlots.size() >= mPrevPositionSlots.length) break;
            if (mCurrPositionSlots[i] != null) newPrevSlots.add(mCurrPositionSlots[i].getId());
        }
        for (int i = 0; i < mPrevPositionSlots.length; i++) {
            if (newPrevSlots.size() >= mPrevPositionSlots.length) break;
            if (mPrevPositionSlots[i] != -1) newPrevSlots.add(mPrevPositionSlots[i]);
        }
        for (int i = 0; i < mPrevPositionSlots.length; i++) {
            if (newPrevSlots.size() <= i) break;
            mPrevPositionSlots[i] = newPrevSlots.get(i);
        }
    }

    public String toCsvLine() {
        StringBuilder sb = new StringBuilder("    ");
        sb.append(mName); sb.append(',');
        sb.append(mJob.name()); sb.append(',');
        sb.append(mPitLicense ? "true" : "false"); sb.append(',');
        sb.append(mExpeditorTraining ? "true" : "false"); sb.append(',');
        sb.append((mGirthday == null) ? "???" : mGirthday); sb.append(',');
        sb.append(getDaysOffString(false)); sb.append(',');
        for (int positionSlot : mPrevPositionSlots) { sb.append(positionSlot); sb.append(','); }
        return sb.toString(); }

    public String getInfo() {
        StringBuilder sb = new StringBuilder();
        sb.append(getName());
        sb.append("\nDate of girth: "); sb.append((mGirthday == null) ? "???" : mGirthday);
        sb.append("\nPIT License: "); sb.append(mPitLicense ? "Yes" : "No");
        sb.append("\nExpeditor Training: "); sb.append(mExpeditorTraining ? "Yes" : "No");
        sb.append("\nDays Off:\n"); sb.append(getDaysOffString(true));

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

    private String getDaysOffString(boolean spelledOut) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mDaysOff.length; i++) {
            switch (mDaysOff[i]) {
                case MONDAY -> sb.append(spelledOut ? "  Monday" : 'M');
                case TUESDAY -> sb.append(spelledOut ? "  Tuesday" : 'T');
                case WEDNESDAY -> sb.append(spelledOut ? "  Wednesday" : 'W');
                case THURSDAY -> sb.append(spelledOut ? "  Thursday" : 'R');
                case FRIDAY -> sb.append(spelledOut ? "  Friday" : 'F');
                case SATURDAY -> sb.append(spelledOut ? "  Saturday" : 'S');
                case SUNDAY -> sb.append(spelledOut ? "  Sunday" : 'U');
            }
            if (spelledOut && i < mDaysOff.length - 1) sb.append('\n');
        }
        return sb.toString();
    }
}
