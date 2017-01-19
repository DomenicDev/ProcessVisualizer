package de.domenic.psv.gui;

import de.domenic.psv.process.Process;
import de.domenic.psv.listener.GuiListener;
import de.domenic.psv.scheduler.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

/**
 * A graphical representation of the various scheduling algorithms.
 * <p>
 * Created by Domenic on 25.12.2016.
 */
public class Gui extends JFrame {

    private static final String TITLE = "ProcessSchedulingVisualizer by Domenic Cassisi";
    private static final int INIT_SIZE_X = 1200;
    private static final int INIT_SIZE_Y = 700;

    private final Font DEFAULT_FONT = new Font("Calibri", Font.PLAIN, 17);

    private JTable processTable;
    private DefaultTableModel tableModel;
    private JScrollPane tableScrollPane;

    private JPanel mainPanel;
    private JPanel algorithmPanel, processPanel, tablePanel;

    private JComboBox<Algorithm> algorithmBox;

    private JList<Process> processList;
    private DefaultListModel<Process> listModel;
    private JButton addProcessButton, removeProcessButton;

    private JLabel waitingQueue;
    private String readyQueueText = "<html><b>ReadyQueue:</b><html> ";

    private JButton nextRoundButton;
    private JButton resetButton;

    private JLabel statisticsLabel;
    private String initStatsText = "<html><b>Statistics:</b><br/>Waiting Time:<br>Avg. Waiting Time:<br/>Throughput:<br/></html>";

    // key value is the row number in the process table
    private HashMap<Integer, Process> processes = new HashMap<>();

    private GuiListener listener;

    public Gui(GuiListener listener) {
        super(TITLE);
        this.listener = listener;
        setSize(INIT_SIZE_X, INIT_SIZE_Y);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(true);
        setLocationRelativeTo(null);

        initGui();

        setVisible(true);
    }

    private void initGui() {
        initPanels();
        initElements();
        selectNewScheduler(); // just set a standard scheduler for the beginning
    }

    private void initPanels() {
        // initialize the panel which all other sub panels are going to be added to.
        mainPanel = new JPanel();
        mainPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.setAlignmentY(Component.TOP_ALIGNMENT);
        add(mainPanel);

        BoxLayout boxLayout = new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS); // vertical layout
        mainPanel.setLayout(boxLayout);

        // initialize all sub panels
        algorithmPanel = new JPanel();
        algorithmPanel.setBorder(BorderFactory.createTitledBorder("Choose an algorithm"));
        algorithmPanel.setPreferredSize(new Dimension(INIT_SIZE_X, 50));
        mainPanel.add(algorithmPanel);

        processPanel = new JPanel();
        processPanel.setBorder(BorderFactory.createTitledBorder("Add some processes"));
        processPanel.setPreferredSize(new Dimension(INIT_SIZE_X, 150));
        GridLayout gl = new GridLayout(1, 1);
        gl.setVgap(10);
        gl.setHgap(10);
        processPanel.setLayout(gl);
        mainPanel.add(processPanel);

        tablePanel = new JPanel();
        tablePanel.setBorder(BorderFactory.createTitledBorder("Watch the scheduling process"));
        tablePanel.setPreferredSize(new Dimension(INIT_SIZE_X, 600));
        GridLayout gridLayout = new GridLayout(2, 1);
        gridLayout.setHgap(10);
        gridLayout.setVgap(20);
        tablePanel.setLayout(gridLayout);
        mainPanel.add(tablePanel);
    }

    private void initElements() {
        // ---- SCHEDULER-ALGORITHM -------- //
        algorithmBox = new JComboBox<>();
        algorithmBox.setFont(DEFAULT_FONT);

        for (Algorithm algorithm : Algorithm.values()) {
            algorithmBox.addItem(algorithm);
        }
        algorithmBox.setSelectedIndex(0);

        algorithmBox.addActionListener(algorithmListener);
        algorithmPanel.add(algorithmBox);

        // ------ PROCESSES ---------
        processList = new JList<>();
        processList.setFont(DEFAULT_FONT);
        listModel = new DefaultListModel<>();
        processList.setModel(listModel);
        processPanel.add(processList);

        JPanel buttonPanel = new JPanel();
        processPanel.add(buttonPanel);

        addProcessButton = new JButton("Add Process");
        addProcessButton.setFont(DEFAULT_FONT);
        addProcessButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new ProcessCreatorGui(Gui.this);
            }
        });
        buttonPanel.add(addProcessButton);

        removeProcessButton = new JButton("Remove Process");
        removeProcessButton.setFont(DEFAULT_FONT);
        removeProcessButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (processes.size() > 0) {
                    Process p = processes.get(processes.size() - 1);
                    removeProcess(processes.size() - 1, p);
                }
            }
        });
        buttonPanel.add(removeProcessButton);


        // ------- TABLE ------------- //
        JPanel tableProcessControlPanel = new JPanel();
        GridLayout gridLayout = new GridLayout(2, 2);
        gridLayout.setHgap(10);
        gridLayout.setVgap(10);
        tableProcessControlPanel.setLayout(gridLayout);
        tablePanel.add(tableProcessControlPanel);

        waitingQueue = new JLabel(readyQueueText);
        waitingQueue.setFont(DEFAULT_FONT);
        waitingQueue.setBorder(BorderFactory.createBevelBorder(0));
        tableProcessControlPanel.add(waitingQueue);

        statisticsLabel = new JLabel(initStatsText);
        statisticsLabel.setFont(DEFAULT_FONT);
        statisticsLabel.setBorder(BorderFactory.createBevelBorder(0));
        tableProcessControlPanel.add(statisticsLabel);

        nextRoundButton = new JButton("Next Round");
        nextRoundButton.setFont(DEFAULT_FONT);
        tableProcessControlPanel.add(nextRoundButton);

        resetButton = new JButton("Reset");
        resetButton.setFont(DEFAULT_FONT);
        resetButton.addActionListener(e -> {
            listener.onClickReset();
            reset();
        });
        tableProcessControlPanel.add(resetButton);


        nextRoundButton.addActionListener(e -> listener.onClickNextRound());

        processTable = new JTable(1, 20);
        processTable.setDragEnabled(false);

        ProcessTableCellRenderer renderer = new ProcessTableCellRenderer();
        processTable.setDefaultRenderer(Object.class, renderer);

        tableScrollPane = new JScrollPane(processTable);
        tablePanel.add(tableScrollPane);
    }

    private ActionListener algorithmListener = e -> selectNewScheduler();

    private void selectNewScheduler() {
        Algorithm a = algorithmBox.getItemAt(algorithmBox.getSelectedIndex());
        listener.onSchedulerChoose(a);
        reset();
    }

    public void addProcess(Process p) {
        processes.put(processes.size(), p);
        listModel.addElement(p);
        listener.addProcess(p);
        reset();
    }

    private void removeProcess(int key, Process p) {
        if (processes.containsValue(p)) {
            processes.remove(key);
            listModel.removeElement(p);
        }
        listener.removeProcess(p);
        reset();
    }

    private void adjustTable() {
        clearProcessTable();

        tableModel = new DefaultTableModel(this.processes.size(), 30) {

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        processTable.setModel(tableModel);

        setHeaderValues();

        //set processes
        for (int i = 0; i < processes.size(); i++) {
            processTable.setValueAt(processes.get(i).getId() + "", i, 0);
        }

        // repaint, otherwise new table won't be visible
        getContentPane().validate();
        getContentPane().repaint();
    }

    private void setHeaderValues() {
        processTable.getColumnModel().getColumn(0).setHeaderValue("");
        // init header values (0,1,2, ...) for clock time
        for (int i = 0; i < processTable.getColumnCount() - 1; i++) {
            processTable.getColumnModel().getColumn(i + 1).setHeaderValue(i);
        }
    }

    public void setProcesses(ArrayList<Process> process) {
        for (int i = 0; i < process.size(); i++) {
            addProcess(process.get(i));
        }
        adjustTable();
    }

    public void refreshProcessTable(int clockTime) {
        if (tableModel.getColumnCount() <= (clockTime + 1)) { // if necessary, add a new column
            tableModel.addColumn(clockTime);
            setHeaderValues(); // we need to refresh the header values, because they get removed when adding a new column
        }
        Iterator<Entry<Integer, Process>> it = processes.entrySet().iterator();
        while (it.hasNext()) {
            Entry<Integer, Process> e = it.next();
            processTable.setValueAt(e.getValue().getState(), e.getKey(), (clockTime + 1));
        }
    }

    public void clearProcessTable() {
        if (processTable != null) {
            for (int x = 0; x < processTable.getColumnCount(); x++) {
                for (int y = 0; y < processTable.getRowCount(); y++) {
                    processTable.setValueAt(null, y, x);
                }
            }
        }
    }

    public void refreshStatistics(Statistics stats) {
        String s = "<html><b>Statistics:</b><br/>Waiting Time: %s<br>Avg. Waiting Time: %s<br/>Throughput: %s<br/></html>";
        s = String.format(s, stats.getWaitingTime(), stats.getAverageWaitingTime(), stats.getThroughput());
        statisticsLabel.setText(s);
    }

    public void reset() {
        waitingQueue.setText(readyQueueText);
        clearProcessTable();
        adjustTable();
        statisticsLabel.setText(initStatsText);
        listener.onClickReset();
    }

    public void refreshReadyQueue(ArrayList<Process> waitingQueue) {
        String text = readyQueueText;
        for (int i = waitingQueue.size() - 1; i >= 0; i--) {
            text += waitingQueue.get(i).getId() + " --> ";
        }
        this.waitingQueue.setText(text);
    }

}
