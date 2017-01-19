package de.domenic.psv;

import de.domenic.psv.gui.Gui;
import de.domenic.psv.listener.GuiListener;
import de.domenic.psv.process.Burst;
import de.domenic.psv.process.Process;
import de.domenic.psv.scheduler.AbstractScheduler;
import de.domenic.psv.listener.SchedulerListener;
import de.domenic.psv.scheduler.Algorithm;

import java.util.ArrayList;
import java.util.List;

/**
 * The application starts here.
 *
 * Created by Domenic on 25.12.2016.
 */
public class Main implements GuiListener, SchedulerListener {

    private AbstractScheduler scheduler;
    private Gui gui;

    public static void main(String[] args) {
        new Main();
    }

    public Main() {
        ArrayList<Process> ps = getTestProcesses();

        gui = new Gui(this);
        gui.setProcesses(ps);

    }

    private static ArrayList<Process> getTestProcesses() {

        ArrayList<Process> processes = new ArrayList<>();

        Process p1 = new Process();
        p1.addBurst(new Burst(Burst.BurstType.CPU, 6));
        p1.addBurst(new Burst(Burst.BurstType.IO, 7));
        p1.addBurst(new Burst(Burst.BurstType.CPU, 2));
        p1.setArrivalTime(0);

        Process p2 = new Process();
        p2.addBurst(new Burst(Burst.BurstType.CPU, 4));
        p2.addBurst(new Burst(Burst.BurstType.IO, 2));
        p2.addBurst(new Burst(Burst.BurstType.CPU, 1));
        p2.setArrivalTime(0);

        Process p3 = new Process();
        p3.addBurst(new Burst(Burst.BurstType.CPU, 8));
        p3.addBurst(new Burst(Burst.BurstType.IO, 4));
        p3.addBurst(new Burst(Burst.BurstType.CPU, 4));
        p3.setArrivalTime(2);

        processes.add(p1);
        processes.add(p2);
        processes.add(p3);

        return processes;
    }

    @Override
    public void onClickNextRound() {
        if (scheduler.hasRemainingProcesses()) {
            scheduler.nextRound();
        }
    }

    @Override
    public void onClickReset() {
        scheduler.reset();
    }

    @Override
    public void onSchedulerChoose(Algorithm chosenAlgorithm) {
        List<Process> processList = new ArrayList<>();
        if (scheduler != null) {
            scheduler.reset();
            processList.addAll(scheduler.getProcesses());
        }
        Class<? extends AbstractScheduler> c = chosenAlgorithm.getScheduler();
        try {
            scheduler = c.newInstance();
            scheduler.setSchedulerListener(this);
            scheduler.getProcesses().addAll(processList);
        } catch (InstantiationException | IllegalAccessException e1) {
            e1.printStackTrace();
        }
    }

    @Override
    public void addProcess(Process process) {
        if (!scheduler.getProcesses().contains(process)) {
            scheduler.addProcess(process);
        }
    }

    @Override
    public void removeProcess(Process process) {
        if (scheduler.getProcesses().contains(process)) {
            scheduler.getProcesses().remove(process);
        }
    }

    @Override
    public void onRoundEnd(int time) {
        gui.refreshProcessTable(time);
        gui.refreshReadyQueue(scheduler.getReadyQueue());
        gui.refreshStatistics(scheduler.getStatistics());
    }
}
