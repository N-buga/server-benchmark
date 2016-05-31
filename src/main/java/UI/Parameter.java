package UI;

/**
 * Created by n_buga on 30.05.16.
 */
public class Parameter {
    private int from;
    private int to;
    private int step;
    private boolean isChangeable;

    public Parameter(boolean isChangeable) {
        from = 0;
        to = 0;
        step = 0;
        this.isChangeable = isChangeable;
    }

    public Parameter(int from, int to, int step, boolean isChangeable) {
        this.from = from;
        this.to = to;
        this.step = step;
        this.isChangeable = isChangeable;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public void setTo(int to) {
        this.to = to;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public int getFrom() {
        return from;
    }

    public int getTo() {
        return to;
    }

    public int getStep() {
        return step;
    }

    public boolean isChangeable() {
        return isChangeable;
    }

    public void setChangeable(boolean f) {
        isChangeable = f;
    }
}
