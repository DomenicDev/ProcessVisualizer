package de.domenic.psv.gui;

import de.domenic.psv.process.Burst;
import de.domenic.psv.process.Process;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * This gui will help to create a process.
 * You also can add various bursts here.
 *
 * Created by Domenic on 27.12.2016.
 */
public class ProcessCreatorGui extends JDialog {

    private Gui gui;

    private JPanel mainPanel;
    private JComboBox<Integer> priorityBox;
    private JTextField arrivalField;

    private Process p;

    public ProcessCreatorGui(Gui gui) {
        this.gui = gui;
        this.p = new Process();

        setTitle("Create a new process");
        setSize(400, 300);
        setModal(true);
        setResizable(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        mainPanel = new JPanel();
        mainPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.setAlignmentY(Component.TOP_ALIGNMENT);
        add(mainPanel);

        BoxLayout boxLayout = new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS); // vertical layout
        mainPanel.setLayout(boxLayout);

        initDataPanel();
        initBurstPanel();
        initEnterProcessPanel();

        setVisible(true);
    }


    private void initDataPanel() {
        JPanel dataPanel = new JPanel();
        mainPanel.add(dataPanel);
        GridLayout gridLayout = new GridLayout();
        gridLayout.setColumns(2);
        gridLayout.setRows(2);
        dataPanel.setLayout(gridLayout);

        JLabel priorityLabel = new JLabel("Priority");
        dataPanel.add(priorityLabel);

        priorityBox = new JComboBox<>();
        for (int i = 0; i < 5; i++) {
            priorityBox.addItem(i);
        }
        dataPanel.add(priorityBox);

        JLabel arrivalLabel = new JLabel("Arrival Time");
        dataPanel.add(arrivalLabel);

        arrivalField = new JTextField();
        arrivalField.setText("0");
        dataPanel.add(arrivalField);
    }

    private void initBurstPanel() {
        JPanel burstPanel = new JPanel();
        mainPanel.add(burstPanel);

        BoxLayout boxLayout = new BoxLayout(burstPanel, BoxLayout.PAGE_AXIS);
        burstPanel.setLayout(boxLayout);

        DefaultListModel<Burst> model = new DefaultListModel<>();

        JList<Burst> burstList = new JList<>(model);
        burstPanel.add(burstList);

        JPanel burstButtonPanel = new JPanel();
        burstPanel.add(burstButtonPanel);

        JButton addBurst = new JButton("Add Burst");
        addBurst.addActionListener(e -> {
            JDialog dialog = new JDialog(ProcessCreatorGui.this);
            dialog.setSize(100,120);
            dialog.setModal(true);
            dialog.setResizable(false);
            dialog.setLocationRelativeTo(null);

            JPanel contentPanel = new JPanel();
            contentPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            contentPanel.setAlignmentY(Component.TOP_ALIGNMENT);
            dialog.add(contentPanel);

            BoxLayout b = new BoxLayout(contentPanel, BoxLayout.PAGE_AXIS);
            contentPanel.setLayout(b);

            JComboBox<Burst.BurstType> typeBox = new JComboBox<>();
            for (Burst.BurstType type : Burst.BurstType.values()) typeBox.addItem(type);
            contentPanel.add(typeBox);

            JTextField lengthField = new JTextField();
            lengthField.setText(1+"");
            contentPanel.add(lengthField);

            JButton enterButton = new JButton("Enter");
            enterButton.addActionListener(e1 -> {
                Integer length = Integer.parseInt(lengthField.getText());
                if (length <= 0) {
                    JOptionPane.showMessageDialog(null, "Length must not be less than or equal to 0");
                    return;
                }
                Burst burst = new Burst(typeBox.getItemAt(typeBox.getSelectedIndex()), length);
                ProcessCreatorGui.this.p.addBurst(burst);
                dialog.dispose();

                model.addElement(burst);
            });
            contentPanel.add(enterButton);
            dialog.setVisible(true);
        });
        burstButtonPanel.add(addBurst);

        JButton removeBurst = new JButton("Remove Burst");
        removeBurst.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (p.getBursts().size() > 0) {
                    int index = p.getBursts().size()-1;
                    Burst b = p.getBursts().get(index);
                    model.remove(index);
                    p.removeBurst(b);
                }
            }
        });
        burstButtonPanel.add(removeBurst);
    }


    private void initEnterProcessPanel() {
        JPanel enterPanel = new JPanel();
        mainPanel.add(enterPanel);

        JButton enterButton = new JButton("Create Process");
        enterButton.addActionListener(e -> {
            p.setPriority(priorityBox.getItemAt(priorityBox.getSelectedIndex()));
            int arrivalTime = Integer.parseInt(arrivalField.getText());
            if (arrivalTime < 0) {
                JOptionPane.showMessageDialog(null, "Arrival Time must not be less than 0");
                return;
            }
            p.setArrivalTime(arrivalTime);
            if (p.getBursts().size() == 0) {
                JOptionPane.showMessageDialog(null, "A process needs to have at least 1 burst!");
                return;
            }
            ProcessCreatorGui.this.gui.addProcess(p);
            dispose();
        });
        enterPanel.add(enterButton);
    }

}
