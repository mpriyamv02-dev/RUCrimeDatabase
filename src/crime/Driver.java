package crime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.Timer;

import edu.rutgers.cs112.LL.LLNode;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;

public class Driver extends JFrame {

    private RUCrimeDatabase database;
    
    private JPanel mainPanel;
    private JPanel topPanel; 
    private DatabasePanel databasePanel; 
    private InfoPanel infoPanel; 

    private Timer actionTimer;
    private static final int TIMEOUT = 4000; // 5 seconds timeout

    public Driver() {
        this.setTitle("RU Crime Database");
        this.setSize(600, 600);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null); 

        this.database = new RUCrimeDatabase();

        this.mainPanel = new JPanel();
        this.add(mainPanel);

        this.mainPanel.setLayout(new BorderLayout());
        
        this.createTopPanel(); 
        this.databasePanel = new DatabasePanel();
        this.mainPanel.add(databasePanel, BorderLayout.CENTER); 


        this.infoPanel = new InfoPanel();
        this.mainPanel.add(infoPanel, BorderLayout.SOUTH); 
 
        this.revalidate();
        this.repaint();
        this.setVisible(true);
    }

    private void createTopPanel() {
        this.topPanel = new JPanel();
        this.mainPanel.add(topPanel, BorderLayout.NORTH);
 
        JComboBox<String> fileComboBox = new JComboBox<>(new String[] {
            "NB2025July.csv",
            "NB2025June.csv",
            "NB2025May.csv",
            "NB2025Combined.csv"
        });
        topPanel.add(fileComboBox);
        
        JButton loadButton = new JButton("buildIncidentTable()");
        topPanel.add(loadButton);

        loadButton.addActionListener(e -> {
            String selectedFile = (String) fileComboBox.getSelectedItem();
            runStudentCode(() -> { 
                try { 
                    if (loadButton.getText().equals("buildIncidentTable()")) {
                        database.buildIncidentTable(selectedFile);   
                        loadButton.setText("buildIncidentTable() and join()");
                    } else if (loadButton.getText().equals("buildIncidentTable() and join()")) {
                        RUCrimeDatabase newDatabase = new RUCrimeDatabase();
                        newDatabase.buildIncidentTable(selectedFile);
                        database.join(newDatabase);
                    } 
                    this.databasePanel.update();
                    this.infoPanel.refresh();
                    fileComboBox.removeItem(selectedFile);
                    if (fileComboBox.getItemCount() == 0) {
                        topPanel.removeAll(); 
                    } 
                    mainPanel.revalidate();
                    mainPanel.repaint();
                } catch (Exception ex) {
                    SwingUtilities.invokeLater(() -> 
                        JOptionPane.showMessageDialog(this, "Error loading file: " + ex.getMessage(), "File Load Error", JOptionPane.ERROR_MESSAGE)
                    );
                } 
            }); 
         });  
    } 

    /**
     * RUN STUDENT CODE 
     */
    private void runStudentCode(Runnable task) {
        this.runStudentCode(Executors.callable(task));
    }

    private <T> void runStudentCode(Callable<T> task) {  
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    task.call();
                } catch (Exception e) {
                    SwingUtilities.invokeLater(() ->  e.printStackTrace());
                    System.exit(0);
                }
                this.done();
                actionTimer = null;
                return null;
            } 
        }; 
        actionTimer = new Timer(TIMEOUT, e -> {
            if (!worker.isDone()) {
                worker.cancel(true);
                actionTimer = null;
                int choice = JOptionPane.showConfirmDialog( null, 
                    "Solution code is taking more time than expected -- if you're not using the debugger right now, there may be an infinite loop.\nWould you like to close the Driver?", 
                    "Warning", 
                    JOptionPane.YES_NO_OPTION, 
                    JOptionPane.WARNING_MESSAGE
                );
                if (choice == JOptionPane.YES_OPTION) {
                    this.dispose(); 
                    System.exit(0);
                }
            }
        });
        actionTimer.start();
        worker.execute(); 
    }

    /**
     * DATABASE PANEL CLASS
     */
    private class DatabasePanel extends JScrollPane {
 
        private JPanel contentPanel;

        public DatabasePanel() {  
            this.contentPanel = new JPanel();
            this.contentPanel.setLayout(new BoxLayout(this.contentPanel, BoxLayout.Y_AXIS));
            this.setViewportView(this.contentPanel);

            this.update();
        }

        public void update() { 
            this.remove(this.contentPanel);
            this.contentPanel = new JPanel();
            this.contentPanel.setLayout(new BoxLayout(this.contentPanel, BoxLayout.Y_AXIS));
            this.setViewportView(this.contentPanel);

            int count = 1;
            for (int i = 0; i < database.getIncidentTable().length; i++) {
                JPanel indexSpacer = new JPanel();
                indexSpacer.setLayout(new BoxLayout(indexSpacer, BoxLayout.Y_AXIS)); 
                JLabel indexLabel = new JLabel("Index " + i + ":");
                JSeparator sep = new JSeparator(); 
                indexSpacer.add(indexLabel);
                indexSpacer.add(sep);
                this.contentPanel.add(indexSpacer);

                LLNode<Incident> row = database.getIncidentTable()[i];
                for (LLNode<Incident> node = row; node != null; node = node.getNext()) {
                    IncidentPanel ip = new IncidentPanel(count++, node.getData());
                    ip.setAlignmentX(Component.LEFT_ALIGNMENT);
                    ip.setMaximumSize(new Dimension(Integer.MAX_VALUE, ip.getPreferredSize().height));
                    this.contentPanel.add(ip);
                }
            }
 
  
            this.revalidate();
            this.repaint();
        }
    }

    /**
     * INCIDENT PANEL CLASS
     */
    private class IncidentPanel extends JPanel {

        private Incident incident;
        private JButton detailsButton;

        private void showIncidentDetails() { 
            JDialog dialog = new JDialog(Driver.this, "Incident Details", true);
            dialog.setSize(420, 300);
            dialog.setLocationRelativeTo(Driver.this);
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.setBorder(javax.swing.BorderFactory.createEmptyBorder(10,10,10,10));
            String[][] rows = {
                {"Incident Number", incident.getIncidentNumber()},
                {"Nature", incident.getNature()},
                {"Report Date", String.valueOf(incident.getReportDate())},
                {"Occurrence Date", String.valueOf(incident.getOccurrenceDate())},
                {"Location", incident.getLocation()},
                {"Disposition", incident.getDisposition()},
                {"General Location", incident.getGeneralLocation()}
            };
            for (int i = 0; i < rows.length; i++) {
                String header = rows[i][0];
                String value = rows[i][1];
                javax.swing.JLabel lbl = new javax.swing.JLabel(
                    "<html><div style='text-align:center; font-size:14pt'><b>" + header + ":</b> " + value + "</div></html>"
                );
                lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
                panel.add(lbl);
                if (i < rows.length - 1) {
                    panel.add(javax.swing.Box.createRigidArea(new Dimension(0,6)));
                    javax.swing.JSeparator sep = new javax.swing.JSeparator(); 
                    panel.add(sep);
                    panel.add(javax.swing.Box.createRigidArea(new Dimension(0,6)));
                }
            }
            dialog.add(panel);
            dialog.setVisible(true);
        }


        public IncidentPanel(int index, Incident incident) { 
            this.incident = incident;
            this.setLayout(new BorderLayout()); 

            this.detailsButton = new JButton("View Info");
            this.detailsButton.addActionListener(e -> {
                showIncidentDetails();
            });
            this.add(detailsButton, BorderLayout.WEST);

            String indexStr = "" + index;
            if (index < 100) indexStr = "0" + indexStr;
            if (index < 10) indexStr = "0" + indexStr;
            String incidentStr = incident.toString();
            String[] words = incidentStr.split(" "); 
            float fontSize = 15.0f;
            if (words.length < 3) { 
                JLabel label = new JLabel(incidentStr);
                label.setHorizontalAlignment(JLabel.LEFT); 
                label.setFont(label.getFont().deriveFont(fontSize));
                this.add(new JLabel("| " + indexStr + " | " + label), BorderLayout.CENTER); 
            } else {
                String header = words[0];
                String number = words[1];
                String crime = String.join(" ", java.util.Arrays.copyOfRange(words, 2, words.length)); 
                String label =  "<html>| " + indexStr + " | <b>" + header + "</b>     |     <b>" + number + "</b>     |     <b>" + crime  + "</html>";
                JLabel jLabel = new JLabel(label);
                jLabel.setHorizontalAlignment(JLabel.LEFT);
                jLabel.setFont(jLabel.getFont().deriveFont(fontSize));
                this.add(jLabel, BorderLayout.CENTER); 
            }
            this.setAlignmentX(Component.LEFT_ALIGNMENT);
            Dimension pref = this.getPreferredSize();
            this.setMaximumSize(new Dimension(Integer.MAX_VALUE, pref.height));
        }
    }

    /**
     * INFO PANEL CLASS
     */
    private class InfoPanel extends JPanel {
        
        private JPanel numIncidentsPanel;
        private JLabel numIncidentsLabel;

        private JPanel deleteIncidentPanel;
        private JLabel deleteIncidentLabel;
        private JComboBox<String> deleteIncidentComboBox;
        private JButton deleteIncidentButton;

        private JPanel topKLocationsPanel;
        private JLabel topKLocationsLabel;
        private JSpinner kSpinner;
        private JButton topKLocationsButton;

        private JPanel natureBreakdownPanel;
        private JLabel natureBreakdownLabel;
        private JButton natureBreakdownButton;
        
        public InfoPanel() {
            this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

            this.numIncidentsPanel = new JPanel();
            this.numIncidentsLabel = new JLabel("Total Incidents: " + database.numberOfIncidents());
            this.numIncidentsPanel.add(this.numIncidentsLabel);
            this.add(this.numIncidentsPanel);

            this.deleteIncidentPanel = new JPanel();
            this.deleteIncidentLabel = new JLabel("Delete Incident by Number: ");
            this.deleteIncidentComboBox = new JComboBox<>();
            for (int i = 0; i < database.numberOfIncidents(); i++) {
                LLNode<Incident> row = database.getIncidentTable()[i];
                for (LLNode<Incident> node = row; node != null; node = node.getNext()) {
                    this.deleteIncidentComboBox.addItem(node.getData().getIncidentNumber());
                }
            }
            this.deleteIncidentButton = new JButton("Delete");
            deleteIncidentButton.addActionListener(e -> { 
                String incidentNumber = (String) deleteIncidentComboBox.getSelectedItem();
                if (incidentNumber != null && !incidentNumber.trim().isEmpty()) {
                    runStudentCode(() -> {
                        database.deleteIncident(incidentNumber.trim());
                        SwingUtilities.invokeLater(() -> {
                            databasePanel.update();
                            infoPanel.refresh();
                        });
                    });
                }
            });
            this.deleteIncidentPanel.add(this.deleteIncidentLabel);
            this.deleteIncidentPanel.add(this.deleteIncidentComboBox);
            this.deleteIncidentPanel.add(this.deleteIncidentButton);
            this.add(this.deleteIncidentPanel);

            this.topKLocationsPanel = new JPanel();
            this.topKLocationsLabel = new JLabel("Top K Locations: ");
            this.topKLocationsButton = new JButton("Show");
            this.kSpinner = new JSpinner();
            this.kSpinner.setModel(new javax.swing.SpinnerNumberModel(3, 1, 10, 1));
            this.topKLocationsButton.addActionListener(e -> { 
                int k = (Integer) kSpinner.getValue();
                runStudentCode(() -> {
                    ArrayList<String> topK = database.topKLocations(k);
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(this, "Top " + k + " Locations:\n" + String.join("\n", topK), "Top K Locations", JOptionPane.INFORMATION_MESSAGE);
                    });
                });
            });
            this.topKLocationsPanel.add(this.topKLocationsLabel);
            this.topKLocationsPanel.add(this.kSpinner);
            this.topKLocationsPanel.add(this.topKLocationsButton);
            this.add(this.topKLocationsPanel);

            this.natureBreakdownPanel = new JPanel();
            this.natureBreakdownLabel = new JLabel("Nature Breakdown: ");
            this.natureBreakdownButton = new JButton("Show");
            this.natureBreakdownButton.addActionListener(e -> { 
                runStudentCode(() -> {
                    HashMap<Category, Double> breakdown = database.natureBreakdown();
                    if (breakdown == null) {
                        SwingUtilities.invokeLater(() -> {
                            JOptionPane.showMessageDialog(this, "Nature breakdown data is null.", "Nature Breakdown", JOptionPane.INFORMATION_MESSAGE);
                        });
                        return;
                    }
                    StringBuilder sb = new StringBuilder();
                    for (Category category : breakdown.keySet()) {
                        sb.append(category).append(": ").append(breakdown.get(category)).append("\n");
                    }
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(this, sb.toString(), "Nature Breakdown", JOptionPane.INFORMATION_MESSAGE);
                    });
                });
            });
            this.natureBreakdownPanel.add(this.natureBreakdownLabel);
            this.natureBreakdownPanel.add(this.natureBreakdownButton);
            this.add(this.natureBreakdownPanel);
        }

        public void refresh() {
            this.numIncidentsLabel.setText("Total Incidents: " + database.numberOfIncidents());

            this.deleteIncidentComboBox.removeAllItems();
            for (int i = 0; i < database.getIncidentTable().length; i++) {
                LLNode<Incident> row = database.getIncidentTable()[i];
                for (LLNode<Incident> node = row; node != null; node = node.getNext()) {
                    this.deleteIncidentComboBox.addItem(node.getData().getIncidentNumber());
                }
            }

            this.revalidate();
            this.repaint();
        }
    }

    /**
     * MAIN METHOD
     */
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                Driver frame = new Driver();
                frame.setVisible(true);
            }
        });
    }

}