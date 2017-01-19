package de.domenic.psv.scheduler;

/**
 * In here you can collect some statistics data to later evaluate them.
 *
 * Created by Domenic on 30.12.2016.
 */
public class Statistics {

    private int waitingTime;
    private int doneProcesses;
    private int time;

    /**
     * Set the time which has passed yet.
     * @param time  passed time
     */
    public void setTime(int time) {
        this.time = time;
    }

    /**
     * Increment the waiting time
     */
    public void incWaitingTime() {
        waitingTime++;
    }

    /**
     * Increment the amount of terminated processes.
     */
    public void incDoneProcesses() {
        doneProcesses++;
    }

    /**
     * @return the average waiting time (waitingTime / passedTime)
     */
    public float getAverageWaitingTime() {
        return ((float) waitingTime / time );
    }

    /**
     * @return how long all processes had to wait
     */
    public int getWaitingTime() {
        return waitingTime;
    }

    /**
     * @return how many processes have been finished in the passed time.
     */
    public float getThroughput() {
        return (float) doneProcesses / (float)time;
    }

    /**
     * Reset the statistics data.
     */
    public void reset() {
        waitingTime = 0;
        doneProcesses = 0;
        time = 0;
    }

}
