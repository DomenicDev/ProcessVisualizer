package de.domenic.psv.scheduler;

import de.domenic.psv.process.Process;

/**
 * The common "First Come First Served" algorithm is implemented here.
 *
 * Created by Domenic on 25.12.2016.
 */
public class FirstComeFirstServe extends AbstractScheduler {

    @Override
    public void sortProcessIntoReadyQueue(Process p) {
        readyQueue.add(p);
    }

}
