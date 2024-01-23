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

public class InfoPanel extends JPanel {
    private JTextField uStartTextField, uEndTextField, nowTextField, dChooseTextField;
    private JButton uChooseStartButton, uChooseEndButton, dChooseButton;
    private JPopupMenu locationMenu;
    private JList<String> locationList;
    private SimpleGraph map;
    private User user;
    private Driver driver;
    private String[] locations;
    private boolean uIsChoosingStart, uIsChoosingEnd, dIsChoosing;
    private Database db;

    InfoPanel(SimpleGraph map, Database db) {
        this.map = map;
        this.db = db;
        HashMap<String, Location> locationsList = map.getLocations();
        this.locations = new String[locationsList.size()];
        locationsList.keySet().toArray(locations);
    }

    public void initPanel() {
        // JPanel userPanel = new JPanel(new BorderLayout());
        setLayout(new BorderLayout());
        // Top panel for the title
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel welcomeLabel = new JLabel("Welcome, User");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24)); // Set a larger font
        titlePanel.add(welcomeLabel);
        // userPanel.add(titlePanel, BorderLayout.NORTH);
        add(titlePanel, BorderLayout.NORTH);

        // Middle panel for choosing locaiton
        JPanel middlePanel = new JPanel(new GridLayout(3, 1, 0, 10));

        JPanel startPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel startLabel = new JLabel("Choose your start location: ");
        startPanel.add(startLabel);
        uStartTextField = new JTextField(20);
        uStartTextField.getDocument().addDocumentListener(new TextFieldListener());
        startPanel.add(uStartTextField);
        uChooseStartButton = new JButton("Choose On Map");
        uChooseStartButton.addActionListener(new ChooseListener());
        startPanel.add(uChooseStartButton);

        middlePanel.add(startPanel);

        // -----
        JPanel endPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel endLabel = new JLabel("Choose your end location: ");
        endPanel.add(endLabel);
        uEndTextField = new JTextField(20);
        uEndTextField.getDocument().addDocumentListener(new TextFieldListener());
        endPanel.add(uEndTextField);
        uChooseEndButton = new JButton("Choose On Map");
        uChooseEndButton.addActionListener(new ChooseListener());
        endPanel.add(uChooseEndButton);
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

        // userPanel.add(middlePanel, BorderLayout.CENTER);
        add(middlePanel, BorderLayout.CENTER);

        // Bottom panel for the submit button
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton submitButton = new JButton("Start My Ryde!");
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (user != null) {
                    if (displayRideInfo()) {
                        System.out.println(user);
                        user.setStart(map.getLocation(uStartTextField.getText()));
                        user.setEnd(map.getLocation(uEndTextField.getText()));
                        System.out.println(user);
                        // driver thread
                        updateUser();
                    }
                }
            }
        });
        bottomPanel.add(submitButton);
        add(bottomPanel, BorderLayout.SOUTH);
        // userPanel.add(bottomPanel, BorderLayout.SOUTH);
        // add(userPanel);
    }

    public void initDriverPanel() {
        // JPanel driverPanel = new JPanel(new BorderLayout());
        setLayout(new BorderLayout());

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel welcomeLabel = new JLabel("Welcome, Driver"); // Changed the label for the driver
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titlePanel.add(welcomeLabel);
        // driverPanel.add(titlePanel, BorderLayout.NORTH);
        add(titlePanel, BorderLayout.NORTH);

        JPanel middlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel locatLabel = new JLabel("Choose your current location: ");
        middlePanel.add(locatLabel);
        dChooseTextField = new JTextField(20);
        dChooseTextField.getDocument().addDocumentListener(new TextFieldListener());
        middlePanel.add(dChooseTextField);

        JPanel locationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        locationList = new JList<>(locations);
        locationList.addListSelectionListener(new ListListener());
        locationMenu = new JPopupMenu();
        locationMenu.add(new JScrollPane(locationList));
        locationMenu.setFocusable(false);
        locationPanel.add(locationMenu);
        middlePanel.add(locationPanel);

        // Adding the missing line to add the dChooseButton
        dChooseButton = new JButton("Choose On Map");
        dChooseButton.addActionListener(new ChooseListener());
        middlePanel.add(dChooseButton);
        JButton confirmButton = new JButton("Confirm Location");
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (driver != null) {
                    driver.setCurrentLocation(map.getLocation(dChooseTextField.getText()));
                }
            }
        });
        middlePanel.add(confirmButton);
        add(middlePanel, BorderLayout.CENTER);

        // driverPanel.add(locatPanel);
        // add(driverPanel);
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
            if (user != null) {
                if (txtDocument == uStartTextField.getDocument()) {
                    nowTextField = uStartTextField;
                } else if (txtDocument == uEndTextField.getDocument()) {
                    nowTextField = uEndTextField;
                }
            } else if (driver != null) {
                nowTextField = dChooseTextField;
            }
            String input = nowTextField.getText().toLowerCase();
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
                locationMenu.show(nowTextField, 0, nowTextField.getHeight());
            } else {
                locationMenu.setVisible(false);
            }
            uIsChoosingStart = false;
            uIsChoosingEnd = false;
            setButtonStatus();
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
                                nowTextField.setText(locationList.getSelectedValue());
                                locationMenu.setVisible(false);
                                uIsChoosingStart = false;
                                uIsChoosingEnd = false;
                                dIsChoosing = false;
                                setButtonStatus();
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
            if (event.getSource() == uChooseStartButton) {
                uIsChoosingStart = true;
                uIsChoosingEnd = false;
            } else if (event.getSource() == uChooseEndButton) {
                uIsChoosingEnd = true;
                uIsChoosingStart = false;
            } else {
                dIsChoosing = true;
            }
            setButtonStatus();
            repaint();
        }
    }

    public void setUser(User user) {
        this.user = user;
        this.driver = null;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
        this.user = null;
    }

    public void setButtonStatus() {
        if (user != null) {
            uChooseEndButton.setVisible(!uIsChoosingEnd);
            uChooseStartButton.setVisible(!uIsChoosingStart);
        } else {
            dChooseButton.setVisible(!dIsChoosing);
        }
    }

    public void finishChoose(Location location) {
        System.out.println(location);
        System.out.println(user);
        System.out.println(driver);
        if (uIsChoosingStart) {
            uStartTextField.setText(location.getName());
        } else if (uIsChoosingEnd) {
            uEndTextField.setText(location.getName());
        } else {
            dChooseTextField.setText(location.getName());
        }
        uIsChoosingStart = false;
        uIsChoosingEnd = false;
        dIsChoosing = false;
        locationMenu.setVisible(false);
        setButtonStatus();
        repaint();
        System.out.println(user);
        System.out.println(driver);
    }

    private boolean displayRideInfo() {
        Location startLocation = map.getLocation(uStartTextField.getText());
        Location endLocation = map.getLocation(uEndTextField.getText());

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

            int optionResult = JOptionPane.showConfirmDialog(this, optionPanel, "Select an Option",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            while (!carpoolButton.isSelected() && !singleButton.isSelected()) {
                JOptionPane.showMessageDialog(this, "Please select an option.", "Error", JOptionPane.ERROR_MESSAGE);
                optionResult = JOptionPane.showConfirmDialog(this, optionPanel, "Select an Option",
                        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

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
        // Thread driverThread = new Thread(new DriverThread(db, driverId));
        db.getDriver(10010100).assignRyder(this.user);
        // driverThread.start();
    }

}
