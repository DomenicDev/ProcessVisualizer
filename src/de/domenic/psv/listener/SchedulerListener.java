package de.domenic.psv.listener;

/**
 * Created by Domenic on 25.12.2016.
 */
public interface SchedulerListener {

    /**
     * This method is called when a round has been executed.
     * @param time of the done round
     */
    void onRoundEnd(int time);

}
