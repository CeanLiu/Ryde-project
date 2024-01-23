import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.Document;

public class UserPanel extends JPanel {
    private JTextField startTextField, endTextField, currentTextField;
    private JButton startChooseButton, endChooseButton, submitButton;
    private JPopupMenu locationMenu;
    private JList<String> locationList;
    private SimpleGraph map;
    private User user;
    private String[] locations;
    private boolean isChoosingStart, isChoosingEnd;
    private Database db;
    private ArrayList<Driver> drivers = new ArrayList<>();

    UserPanel(SimpleGraph map, Database db) {
        this.map = map;
        this.db = db;
        HashMap<String, Location> locationsList = map.getLocations();
        this.locations = new String[locationsList.size()];
        locationsList.keySet().toArray(locations);
        initPanel();
    }

    public void initPanel() {
        setLayout(new BorderLayout());

        // Top panel for the title
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel welcomeLabel = new JLabel("Welcome, User");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24)); // Set a larger font
        titlePanel.add(welcomeLabel);

        // Middle panel for choosing locaiton
        JPanel middlePanel = new JPanel(new GridLayout(3, 1, 0, 10));

        JPanel startPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel startLabel = new JLabel("Choose your start location: ");
        startPanel.add(startLabel);
        startTextField = new JTextField(20);
        startTextField.getDocument().addDocumentListener(new TextFieldListener());
        startPanel.add(startTextField);
        startChooseButton = new JButton("Choose On Map");
        startChooseButton.addActionListener(new ChooseListener());
        startPanel.add(startChooseButton);

        middlePanel.add(startPanel);

        // -----
        JPanel endPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel endLabel = new JLabel("Choose your end location: ");
        endPanel.add(endLabel);
        endTextField = new JTextField(20);
        endTextField.getDocument().addDocumentListener(new TextFieldListener());
        endPanel.add(endTextField);
        endChooseButton = new JButton("Choose On Map");
        endChooseButton.addActionListener(new ChooseListener());
        endPanel.add(endChooseButton);
        middlePanel.add(endPanel);

        JPanel locationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        locationList = new JList<>(locations);
        locationList.addListSelectionListener(new ListListener());
        locationMenu = new JPopupMenu();
        locationMenu.add(new JScrollPane(locationList));
        locationMenu.setFocusable(false);
        locationPanel.add(locationMenu);
        middlePanel.add(locationPanel);

        // Add panels to the main panel
        add(titlePanel, BorderLayout.NORTH);
        add(middlePanel, BorderLayout.CENTER);

        // Bottom panel for the submit button
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        submitButton = new JButton("Start My Ryde!");
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (displayRideInfo()) {
                    System.out.println(user);
                    user.setStart(map.getLocation(startTextField.getText()));
                    user.setEnd(map.getLocation(endTextField.getText()));
                    System.out.println(user);
                    // driver thread
                    updateUser();
                }
            }
        });
        bottomPanel.add(submitButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    public class TextFieldListener implements DocumentListener {
        @Override
        public void insertUpdate(DocumentEvent e) {
            updateList(e.getDocument());
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            updateList(e.getDocument());
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            updateList(e.getDocument());
        }

        private void updateList(Document txtDocument) {
            DefaultListModel<String> listModel = new DefaultListModel<String>();
            if (txtDocument == startTextField.getDocument()) {
                currentTextField = startTextField;
            } else {
                currentTextField = endTextField;
            }
            String input = currentTextField.getText().toLowerCase();
            System.out.println(input);
            for (String location : locations) {
                boolean isStart = true;
                for (int i = 0; i < input.length(); i++) {
                    if (location.length() < input.length()) {
                        isStart = false;
                        break;
                    }
                    char inputChar = input.charAt(i);
                    char locationChar = location.toLowerCase().charAt(i);
                    if (inputChar != locationChar) {
                        isStart = false;
                        break;
                    }
                }
                if (isStart) {
                    listModel.addElement(location);
                }
            }
            locationList.setModel(listModel);
            if (listModel.getSize() > 0) {
                locationMenu.show(currentTextField, 0, currentTextField.getHeight());
            } else {
                locationMenu.setVisible(false);
            }
            isChoosingStart = false;
            isChoosingEnd = false;
            startChooseButton.setVisible(true);
            endChooseButton.setVisible(true);
        }
    }

    public class ListListener implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            try {
                if (!e.getValueIsAdjusting()) {
                    // invokeLater will postpones execution until the thread is not occupied
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            int selectedIndex = locationList.getSelectedIndex();
                            if (selectedIndex != -1) {
                                currentTextField.setText(locationList.getSelectedValue());
                                locationMenu.setVisible(false);
                                isChoosingStart = false;
                                isChoosingEnd = false;
                                startChooseButton.setVisible(true);
                                endChooseButton.setVisible(true);
                            }
                        }
                    });
                }
            } catch (IllegalStateException ex) {
                ex.printStackTrace();
            }
        }
    }

    public class ChooseListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent event) {
            if (event.getSource() == startChooseButton) {
                isChoosingStart = true;
                isChoosingEnd = false;
                startChooseButton.setVisible(false);
                endChooseButton.setVisible(true);
            } else {
                isChoosingEnd = true;
                isChoosingStart = false;
                endChooseButton.setVisible(false);
                startChooseButton.setVisible(true);
            }
            repaint();
        }
    }

    public void setUser(Client user) {
        this.user = (User) user;
    }

    public void finishChoose(Location location) {
        System.out.println(location);
        System.out.println(user);
        if (isChoosingStart) {
            startTextField.setText(location.getName());
        } else if (isChoosingEnd) {
            endTextField.setText(location.getName());
        }
        isChoosingStart = false;
        isChoosingEnd = false;
        locationMenu.setVisible(false);
        startChooseButton.setVisible(true);
        endChooseButton.setVisible(true);
        repaint();
        System.out.println(user);
    }

    private boolean displayRideInfo() {
        Location startLocation = map.getLocation(startTextField.getText());
        Location endLocation = map.getLocation(endTextField.getText());

        if (startLocation == null || endLocation == null) {
            JOptionPane.showMessageDialog(this, "Please choose valid start and end locations.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        } else if (startLocation.equals(endLocation)) {
            JOptionPane.showMessageDialog(this, "Start Location and End Location cannot be the Same.", "Error",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        } else {
            // Additional information about start and end locations
            String info = "Start Location: " + startLocation.getName() + "\n" + "End Location: " + endLocation.getName()
                    + "\n";

            // Create a dialog for option selection
            JPanel optionPanel = new JPanel(new GridLayout(2, 1));
            ButtonGroup buttonGroup = new ButtonGroup();

            JRadioButton carpoolButton = new JRadioButton("I want to Carpool");
            optionPanel.add(carpoolButton);
            buttonGroup.add(carpoolButton);
            JRadioButton singleButton = new JRadioButton("I want to ride alone");
            optionPanel.add(singleButton);
            buttonGroup.add(singleButton);

            int optionResult = JOptionPane.showConfirmDialog(this, optionPanel, "Select an Option", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                while (!carpoolButton.isSelected() && !singleButton.isSelected()) {
                    JOptionPane.showMessageDialog(this, "Please select an option.", "Error", JOptionPane.ERROR_MESSAGE);
                    optionResult = JOptionPane.showConfirmDialog(this, optionPanel, "Select an Option",JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                    if (optionResult != JOptionPane.OK_OPTION) {
                        return false;
                    }
                }

                if (carpoolButton.isSelected()) {
                    info = info + "Going Carpool\n" + "Price: $30";
                } else if (singleButton.isSelected()) {
                    info = info + "Going Alone\n" + "Price: $45";
                }

                JOptionPane.showMessageDialog(this, info, "Ryde Information", JOptionPane.INFORMATION_MESSAGE);
                return true;
        }
    }

    public void updateUser() {
        int driverId = 10010100;
        db.addDriver(driverId, 5);
        Thread driverThread = new Thread(new DriverThread(db, driverId));
        db.getDriver(10010100).assignRyder(this.user);
        driverThread.start();
    }

}
