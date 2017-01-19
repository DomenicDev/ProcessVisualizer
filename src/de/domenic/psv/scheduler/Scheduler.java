package de.domenic.psv.scheduler;

import de.domenic.psv.process.Burst;
import de.domenic.psv.process.Process;

/**
 * Created by Domenic on 25.12.2016.
 */
public interface Scheduler {

    /**
     * Schedule the processes and define which process gets access to the CPU.
     * The Scheduler algorithm should be implemented here.
     */
    void schedule();

    /**
     * This is called when a process needs to be inserted in the ready queue.
     * @param p the process to insert in the ready queue.
     */
    void sortProcessIntoReadyQueue(Process p);

    /**
     * This method is called when a process has done a CPU-Burst.
     * @param p the process which has the done the CPU-Burst.
     * @param nextBurst the next burst of this process or null if there is none.
     */
    void onCpuBurstDone(Process p, Burst nextBurst);

    /**
     * This method is called when a process has done an IO-Burst.
     * @param p the process which has done the IO-Burst
     * @param nextBurst the next burst of this process or null if there is none.
     */
    void onIOBurstDone(Process p, Burst nextBurst);

    /**
     * Is called when a process arrives.<br/>
     * <b>Note that the state of the process is already set to either "Ready" or "Waiting".</b>
     * Use this method to insert the process into the waiting queue.
     * @param p the arrived process
     */
    void onProcessArrive(Process p);
}
