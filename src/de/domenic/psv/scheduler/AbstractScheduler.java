package de.domenic.psv.scheduler;

import de.domenic.psv.process.Burst;
import de.domenic.psv.process.Process;
import de.domenic.psv.listener.ProcessEventListener;
import de.domenic.psv.listener.SchedulerListener;

import java.util.ArrayList;

/**
 * A basic implementation for all Scheduler strategies.
 * Extend this class to implement the specific scheduler strategy.
 *
 * Created by Domenic on 23.12.2016.
 */
public abstract class AbstractScheduler implements Scheduler, ProcessEventListener {

    private int clockTime = -1;
    private ArrayList<Process> processes = new ArrayList<>();
    protected ArrayList<Process> readyQueue = new ArrayList<>();
    private SchedulerListener schedulerListener; // one listener is enough, no need for an array or a list
    private Statistics stats = new Statistics();

    /**
     * Start next round for all processes.
     * If a {@link SchedulerListener} is attached, the method <code>onRoundEnd(clockTime)</code> is called at the end.
     */
    public final void nextRound() {
        // the order of the calls is very important !!! Do not change this in any way !!!
        clockTime++; // increment clock time

        letProcessesWait(); // all waiting processes will increment their process/program counter
        checkForIncomingProcesses(); // look for incoming processes
        nextRoundForRunningProcess(); // call nextRound() for the process with the state "Running"

        schedule(); // call the scheduling algorithm for the specific scheduler
        refreshStatistics();

        if (schedulerListener != null) {
            schedulerListener.onRoundEnd(clockTime);
        }

    }

    private void refreshStatistics() {
        for (Process p : readyQueue) {
            stats.incWaitingTime();
        }
        stats.setTime((clockTime+1));
    }

    /**
     * Look for incoming processes and set their state depending on their first burst to either "Ready" or "Waiting".
     */
    private void checkForIncomingProcesses() {
        for (int i = 0; i < processes.size(); i++) {
            Process p = processes.get(i);
            if (p.getState() == Process.State.NotStarted && clockTime >= p.getArrivalTime()) {
                Burst b = p.getCurrentBurst();
                if (b.getType() == Burst.BurstType.IO) {
                    p.setState(Process.State.Waiting);
                } else if (b.getType() == Burst.BurstType.CPU) {
                    p.setState(Process.State.Ready);
                }
                onProcessArrive(p);
            }
        }
    }

    public ArrayList<Process> getProcesses() {
        return this.processes;
    }

    public ArrayList<Process> getReadyQueue() {
        return this.readyQueue;
    }

    public Process getRunningProcess() {
        for (Process p : processes) {
            if (p.getState() == Process.State.Running) {
                return p;
            }
        }
        return null;
    }

    private void letProcessesWait() {
        for (Process p : processes) {
            if (p.getState() == Process.State.Waiting) {
                p.nextRound();
            }
        }
    }

    /**
     * Will reset all processes and the timer. The waiting queue is cleared as well.
     */
    public void reset() {
        for (Process p : processes) {
            p.reset();
        }
        clockTime = -1;
        readyQueue.clear();
        stats.reset();
    }



    /**
     * Check if there is a process running.
     * @return true if a process has the state "Running".
     */
    public boolean isProcessRunning() {
        for (Process p : processes) {
            if (p.getState() == Process.State.Running) {
                return true;
            }
        }
        return false;
    }

    /**
     * Add a process to this scheduler.
     * @param p the process to add
     */
    public void addProcess(Process p) {
        if (p != null) {
            processes.add(p);
            p.setEventListener(this);
        }
    }

    /**
     * Check whether there are remaining processes (which are not terminated yet).
     * @return true if there is at least one process, which is not terminated, else return false if all are terminated.
     */
    public boolean hasRemainingProcesses() {
        for (Process p : processes) {
            if (p.getState() != Process.State.Terminated) {
                return true;
            }
        }
        return false;
    }

    /**
     * Is called at the beginning of every round.
     */
    private void nextRoundForRunningProcess() {
        Process runningProcess = getRunningProcess();
        if (runningProcess != null) {
            runningProcess.nextRound();
        }
    }

    @Override
    public void onProcessArrive(Process p) {
        if (p.getState() == Process.State.Ready) {
            sortProcessIntoReadyQueue(p);
        }
    }

    /**
     * Default implementation for every scheduling strategy.
     * Override this method to implement a custom algorithm if necessary.<br/>
     * <b>Do not call Process.nextRound() from here!!! Just set the states of the processes!</b>
     * <br/>
     * {@inheritDoc}
     */
    @Override
    public void schedule() {
        if (!isProcessRunning()) {
            if (readyQueue.size() > 0) {
                Process p = readyQueue.get(0);
                setRunningProcess(p);
            }
        }
    }

    /**
     * Will set the state of the given process to "Ready" and will sort this process back into the ready queue.
     * @param p the process which has to wait/get ready
     */
    public void setProcessReady(Process p) {
        p.setState(Process.State.Ready);
        sortProcessIntoReadyQueue(p);
    }

    /**
     * This method will set the state of the given process to "Running"
     * and will remove this process from the ready queue.
     * @param p the process which shall run on the CPU
     */
    protected final void setRunningProcess(Process p) {
        p.setState(Process.State.Running);
        readyQueue.remove(p);
    }

    @Override
    public void onIOBurstDone(Process p, Burst nextBurst) {
        if (nextBurst.getType() == Burst.BurstType.CPU) {
            p.setState(Process.State.Ready);
            sortProcessIntoReadyQueue(p);
        }
    }

    @Override
    public void onCpuBurstDone(Process p, Burst nextBurst) {
        if (nextBurst.getType() == Burst.BurstType.IO) {
            p.setState(Process.State.Waiting);
        }
    }

    @Override
    public void onBurstDone(Process p, Burst doneBurst, Burst nextBurst) {
        // if there isn't a next burst, this process is terminated
        if (nextBurst == null) {
            p.setState(Process.State.Terminated);
            stats.incDoneProcesses();
            return;
        }
        // else look which type of burst this was
        if (doneBurst.getType() == Burst.BurstType.CPU) {
            onCpuBurstDone(p, nextBurst);
        } else if (doneBurst.getType() == Burst.BurstType.IO) {
            onIOBurstDone(p, nextBurst);
        }
    }

    public Statistics getStatistics() {
        return stats;
    }

    public SchedulerListener getSchedulerListener() {
        return schedulerListener;
    }

    public void setSchedulerListener(SchedulerListener schedulerListener) {
        this.schedulerListener = schedulerListener;
    }
}
