package de.domenic.psv.listener;

import de.domenic.psv.process.Burst;
import de.domenic.psv.process.Process;

/**
 * Interface which covers all the events a process can trigger.
 *
 * Created by Domenic on 25.12.2016.
 */
public interface ProcessEventListener {

    /**
     * When a burst is done, this method is called.
     * @param p the specific process
     * @param doneBurst the done burst
     * @param nextBurst the next burst
     */
    void onBurstDone(Process p, Burst doneBurst, Burst nextBurst);

}
