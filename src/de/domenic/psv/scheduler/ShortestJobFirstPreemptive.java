package de.domenic.psv.scheduler;

import de.domenic.psv.process.Process;

/**
 * Created by Domenic on 07.01.2017.
 */
public class ShortestJobFirstPreemptive extends ShortestJobFirst {

    @Override
    public void schedule() {
        super.schedule();
        // look, if there is a process in the ready queue which actually has a shorter CPU-Burst than the process
        // which is currently running on the CPU.
        Process runningProcess = getRunningProcess();
        if (runningProcess != null && readyQueue.size() > 0) {
            Process nextProcess = readyQueue.get(0);
            int runningBurst = runningProcess.getCurrentBurst().getLength();
            int newBurst = nextProcess.getCurrentBurst().getLength();
            if (newBurst < runningBurst) {
                setProcessReady(runningProcess);
                setRunningProcess(nextProcess);
            }
        }
    }
}
