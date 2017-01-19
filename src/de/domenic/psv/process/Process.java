package de.domenic.psv.process;

import de.domenic.psv.listener.ProcessEventListener;

import java.util.ArrayList;

/**
 * A simple process implementation, which contains the most important parameter about the process.
 *
 * Created by Domenic on 23.12.2016.
 */
public class Process {

    private static int idCounter; // id counter

    private int id;
    private int priority;
    private int arrivalTime;
    private int totalProcessTime;
    private int programCounter;

    private ArrayList<Burst> bursts = new ArrayList<>();

    private State state = State.NotStarted;

    private ProcessEventListener eventListener;

    /**
     * This enum contains all the possible states a process can have.
     */
    public enum State {

        /**
         * Process has not been started or is not arrived yet.
         */
        NotStarted,

        /**
         * Process is currently using the CPU (CPU-Burst)
         */
        Running,

        /**
         * Process is waiting for some In-/Output (IO-Burst)
         */
        Waiting,

        /**
         * Process is ready to use the CPU
         */
        Ready,

        /**
         * This process is terminated.
         */
        Terminated

    }

    /**
     * Initialize a process with default values.
     */
    public Process() {
        this.id = idCounter;
        idCounter++;

        this.priority = 1;
        this.programCounter = 0;
        this.arrivalTime = 0;
    }


    /**
     * Adds the given burst to this process
     * @param burst the burst to add
     */
    public void addBurst(Burst burst) {
        if (burst != null) {
            getBursts().add(burst);
            totalProcessTime = getTotalProcessTime() + burst.getLength();
        }
    }

    /**
     * If the given burst belongs to this process, the burst will be removed.
     * @param burst the burst to remove
     */
    public void removeBurst(Burst burst) {
        if (burst != null) {
            if (getBursts().contains(burst)) {
                getBursts().remove(burst);
                totalProcessTime = getTotalProcessTime() - burst.getLength();
            }
        }
    }

    /**
     * Will increment the program counter.
     */
    public void nextRound() {
        Burst b1 = getCurrentBurst();
        programCounter++;
        Burst b2 = getCurrentBurst();
        if (b1 != null) {
            if (b1 != b2) {
                eventListener.onBurstDone(this, b1, b2);
            }
        }
    }

    /**
     * This method returns the current burst of this process or null if it has
     * none or is at the end
     *
     * @return the current burst
     */
    public Burst getCurrentBurst() {
        // if at the beginning return the first burst
        if (getProgramCounter() == 0) {
            return getBursts().get(0);
        } else {
            // go find the current burst by adding length of bursts to time
            int time = 0;
            for (Burst b : getBursts()) {
                time += b.getLength();
                if (getProgramCounter() < time) { // e.g. [...CPU(4,5,6)...] , so length
                    // is 3 --> time += 3, e.g. here 7,
                    // if processTime < 7 return this
                    // burst
                    return b;
                }
            }
        }
        return null;
    }

    /**
     * Will reset the process state to "NotStarted" and will reset the program counter
     */
    public void reset() {
        state = State.NotStarted;
        programCounter = 0;
    }

    @Override
    public String toString() {
        String s = "ID: " + getId() + " ";
        for (Burst b : bursts) {
            s += b.getType() + "("+b.getLength()+"), ";
        }
        s += "Pr: " + priority + ", " + "AT: " + arrivalTime;
        return s;
    }

    // ------- GETTER AND SETTER METHODS -------------- //

    public int getId() { return id; }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(int arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public int getTotalProcessTime() {
        return totalProcessTime;
    }

    public void setProgramCounter(int counter) { this.programCounter = counter; }

    public int getProgramCounter() {
        return programCounter;
    }

    public ArrayList<Burst> getBursts() {
        return bursts;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public ProcessEventListener getEventListener() {
        return eventListener;
    }

    public void setEventListener(ProcessEventListener eventListener) {
        this.eventListener = eventListener;
    }

}
