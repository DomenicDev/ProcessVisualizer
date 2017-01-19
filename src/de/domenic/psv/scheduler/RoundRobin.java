package de.domenic.psv.scheduler;

import de.domenic.psv.process.Process;

/**
 * The preemptive scheduling strategy Round Robin.
 * <p>
 * Created by Domenic on 27.12.2016.
 */
public class RoundRobin extends AbstractScheduler {

    private int timeCounter = 0;
    private int timePerProcess = 3;

    /**
     * To implement RoundRobin we need to override this method.
     * Basically we look if a process has spend the allowed time on the CPU and if so, we add this process
     * back to the ready queue and let the next process use the CPU.
     */
    @Override
    public void schedule() {
        Process runningProcess = getRunningProcess();
        if (runningProcess == null && readyQueue.size() > 0) {
            setRunningProcess(readyQueue.get(0));
            timeCounter = 0;
        } else {
            // look if the currently running process has achieved the limit of time to spend on the CPU
            if (timeCounter >= timePerProcess) {
                if (readyQueue.size() > 0) {

                    // add currently running process back into ready queue
                    if (runningProcess != null) {
                        setProcessReady(runningProcess);
                    }

                    // let the next process use the CPU
                    Process newProcess = readyQueue.get(0);
                    setRunningProcess(newProcess);
                }
                timeCounter = 0; // reset timer counter
            }
        }
        timeCounter++;
    }

    @Override
    public void sortProcessIntoReadyQueue(Process p) {
        readyQueue.add(p);
    }

}
