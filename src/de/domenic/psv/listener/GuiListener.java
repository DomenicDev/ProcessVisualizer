package de.domenic.psv.listener;

import de.domenic.psv.process.Process;
import de.domenic.psv.scheduler.Algorithm;

/**
 * Created by Domenic on 26.12.2016.
 */
public interface GuiListener {

    void onClickNextRound();

    void onClickReset();

    void onSchedulerChoose(Algorithm chosenAlgorithm);

    void addProcess(Process process);

    void removeProcess(Process process);
}
