package de.domenic.psv.scheduler;

import de.domenic.psv.process.Process;

/**
 * The strategy "ShortestJobFirst" is implemented here (non-preemptive).
 *
 * Created by Domenic on 27.12.2016.
 */
public class ShortestJobFirst extends AbstractScheduler {

    @Override
    public void sortProcessIntoReadyQueue(Process p) {
        // first process
        if (readyQueue.size() == 0) {
            readyQueue.add(p);
        } else {
            // find the right position in the waiting list
            int length = p.getCurrentBurst().getLength();
            for (int i = readyQueue.size()-1; i >= 0; i--) {
                Process p2 = readyQueue.get(i);
                int p2BurstLength = p2.getCurrentBurst().getLength();
                if (p2BurstLength <= length) {
                    readyQueue.add((i+1), p);
                    return;
                }
            }
            // this process is the shortest one
            readyQueue.add(0, p);
        }
    }

}
