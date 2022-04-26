import java.time.LocalDate;
import java.time.Period;

public class Employee {

    private final String mName;
    private final JobEnum mJob;
    private final boolean mPitLicense, mExpeditorTraining;
    private final LocalDate mBirthday;

    public Employee(
            String name,
            JobEnum job,
            boolean pitLicense,
            boolean expeditorTraining,
            LocalDate birthday) {
        mName = name;
        mJob = job;
        mPitLicense = pitLicense;
        mExpeditorTraining = expeditorTraining;
        mBirthday = birthday;
    }

    public Employee(String[] vals) {
        this(
                vals[0],
                JobEnum.valueOf(vals[1]),
                vals[2].equals("true"),
                vals[3].equals("true"),
                LocalDate.parse(vals[4])
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
}
