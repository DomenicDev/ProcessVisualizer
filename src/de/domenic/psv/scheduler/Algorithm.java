package de.domenic.psv.scheduler;

/**
 * Created by Domenic on 07.01.2017.
 */
public enum Algorithm {

    FCFS(FirstComeFirstServe.class),
    SJF(ShortestJobFirst.class),
    SJF_P(ShortestJobFirstPreemptive.class),
    RoundRobin(de.domenic.psv.scheduler.RoundRobin.class);

    Algorithm(Class<? extends AbstractScheduler> c) {
        this.scheduler = c;
    }

    private Class<? extends AbstractScheduler> scheduler;

    public Class<? extends AbstractScheduler> getScheduler() {
        return scheduler;
    }

    @Override
    public String toString() {
        return this.name();
    }
}
