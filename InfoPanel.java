import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;

import javax.swing.BoxLayout;
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
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.Document;

public class InfoPanel extends JPanel {
    JPanel displayPanel, dButtonPanel, bottomPanel;
    JButton uChooseStartButton, uChooseEndButton, dChooseButton, driveButton, confirmButton;
    private JTextField uStartTextField, uEndTextField, nowTextField, dChooseTextField;
    private JPopupMenu locationMenu;
    private JList<String> locationList;
    private SimpleGraph map;
    private Client client;
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

    public void initUserPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel welcomeLabel = new JLabel("Welcome, User");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titlePanel.add(welcomeLabel);
        add(titlePanel);

        // Middle panel for choosing location
        JPanel middlePanel = new JPanel();
        middlePanel.setLayout(new BoxLayout(middlePanel, BoxLayout.Y_AXIS));

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

        displayPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        middlePanel.add(displayPanel);

        add(middlePanel);

        JPanel locationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        locationList = new JList<>(locations);
        locationList.addListSelectionListener(new ListListener());
        locationMenu = new JPopupMenu();
        locationMenu.add(new JScrollPane(locationList));
        locationMenu.setFocusable(false);
        locationPanel.add(locationMenu);

        add(locationPanel);

        // Bottom panel for the submit button
        bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton submitButton = new JButton("Start My Ryde!");
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (client instanceof User) {
                    Location start = map.getLocation(uStartTextField.getText());
                    Location end = map.getLocation(uEndTextField.getText());
                    if (displayRideInfo(start, end)) {
                        ((User) client).setStart(start);
                        ((User) client).setEnd(end);
                        ((User) client).send("request:" + ((User) client).requestInfo());
                        ((User) client).updateGUI();
                        db.saveDatabase();
                    }
                }
            }
        });
        bottomPanel.add(submitButton);
        add(bottomPanel);
    }

    public void initDriverPanel() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel welcomeLabel = new JLabel("Welcome, Driver");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titlePanel.add(welcomeLabel);
        add(titlePanel, BorderLayout.NORTH);

        JPanel middlePanel = new JPanel(new GridLayout(6, 0, 0, 0));

        JPanel locationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel locatLabel = new JLabel("Choose your current location: ");
        locationPanel.add(locatLabel);
        dChooseTextField = new JTextField(20);
        dChooseTextField.getDocument().addDocumentListener(new TextFieldListener());
        locationPanel.add(dChooseTextField);

        locationList = new JList<>(locations);
        locationList.addListSelectionListener(new ListListener());
        locationMenu = new JPopupMenu();
        locationMenu.add(new JScrollPane(locationList));
        locationMenu.setFocusable(false);
        locationPanel.add(locationMenu);

        middlePanel.add(locationPanel);

        // Using GridBagLayout for better control
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        dChooseButton = new JButton("Choose On Map");
        dChooseButton.addActionListener(new ChooseListener());
        buttonPanel.add(dChooseButton);
        confirmButton = new JButton("Confirm Location");
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (client instanceof Driver) {
                    ((Driver) client).setCurrent(map.getLocation(dChooseTextField.getText()));
                    ((Driver) client).updateGUI();
                    db.saveDatabase();
                }
            }
        });
        buttonPanel.add(confirmButton);
        middlePanel.add(buttonPanel);
        add(middlePanel);

        displayPanel = new JPanel();
        displayPanel.setLayout((new BoxLayout(displayPanel, BoxLayout.Y_AXIS)));
        add(displayPanel);

        dButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        driveButton = new JButton("Start Drive");
        driveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (client instanceof Driver) {
                    ((Driver) client).setDrive(true);
                    ((Driver) client).send("moveDriver:" + ((Driver) client).getNumber() + ","
                            + ((Driver) client).getCurrent() + "," + ((Driver) client).getCurrent().getX() + ","
                            + ((Driver) client).getCurrent().getY() + "," + ((Driver) client).getDirectionAngle());
                    ((Driver) client).updateGUI();
                    db.saveDatabase();
                }
            }
        });
        dButtonPanel.add(driveButton);
        dButtonPanel.setVisible(false);
        add(dButtonPanel);
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public void setButtonStatus() {
        if (client instanceof User) {
            uChooseEndButton.setVisible(!uIsChoosingEnd);
            uChooseStartButton.setVisible(!uIsChoosingStart);
        } else if (client instanceof Driver) {
            dChooseButton.setVisible(!dIsChoosing);
        }
    }

    public void finishChoose(Location location) {
        if (uIsChoosingStart) {
            uStartTextField.setText(location.getName());
        } else if (uIsChoosingEnd) {
            uEndTextField.setText(location.getName());
        } else if (dIsChoosing) {
            dChooseTextField.setText(location.getName());
        } else {
            return;
        }
        uIsChoosingStart = false;
        uIsChoosingEnd = false;
        dIsChoosing = false;
        locationMenu.setVisible(false);
        setButtonStatus();
        repaint();
    }

    //for drivers, display the user request based on their choice(carpool/alone)
    public void createRequest(Color color, String txt, String carpool) {
        displayInfo(color, txt);
        HashMap<Long, User> requestList = db.getUsers();
        if (!carpool.equals("none")) {
            for (User user : requestList.values()) {
                if (!user.hasDriver() && user.isDoneChoose()) {
                    JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
                    JLabel request = new JLabel(user.acceptInfo());
                    JButton accept = new JButton("Accept User");
                    accept.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            if (client instanceof Driver) {
                                ((Driver) client).assignRyder(user);
                                user.setDriver((Driver) client);
                                ((Driver) client).send("accept:" + user.getNumber() + ","
                                        + ((Driver) client).getNumber() + "," + ((Driver) client).getCurrent());
                                db.saveDatabase();
                            }
                        }
                    });
                    if (carpool.equals("carpool")) {
                        if (!user.isAlone()) {
                            panel.add(request);
                            panel.add(accept);
                            displayPanel.add(panel);
                        }
                    } else {
                        panel.add(request);
                        panel.add(accept);
                        displayPanel.add(panel);
                    }

                }
            }
            revalidate();
            repaint();
        }
    }
    //displays the information about the driver/ryder and their drier/ryders
    public void displayInfo(Color color, String txt) {
        displayPanel.removeAll();
        JPanel textPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JTextArea infoArea = new JTextArea(txt);
        infoArea.setForeground(color);
        textPanel.add(infoArea);
        displayPanel.add(textPanel);
        if (client instanceof User) {
            bottomPanel.setVisible(false);
        }
        revalidate();
        repaint();
    }

    public void setLocationText(String start, String end) {
        uStartTextField.setText(start);
        uEndTextField.setText(end);
        uStartTextField.setEditable(false);
        uEndTextField.setEditable(false);
        uChooseEndButton.setVisible(false);
        uChooseStartButton.setVisible(false);
        locationMenu.setVisible(false);
    }

    public void setLocationText(String currentLocation) {
        dChooseTextField.setText(currentLocation);
        dChooseTextField.setEditable(false);
        dChooseButton.setVisible(false);
        locationMenu.setVisible(false);
    }

    public void resetDisplay() {
        if (client instanceof User) {
            uStartTextField.setText("");
            uEndTextField.setText("");
            uStartTextField.setEditable(true);
            uEndTextField.setEditable(true);
            uChooseStartButton.setVisible(true);
            uChooseEndButton.setVisible(true);
        } else {
            dChooseTextField.setText("");
            dChooseTextField.setEditable(true);
            dChooseButton.setVisible(true);
            confirmButton.setVisible(true);
        }
        locationMenu.setVisible(false);
        displayPanel.removeAll();
    }

    private boolean displayRideInfo(Location start, Location end) {
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
            JRadioButton singleButton = new JRadioButton("I want to Ride Alone");
            optionPanel.add(singleButton);
            buttonGroup.add(singleButton);

            int optionResult = JOptionPane.showConfirmDialog(this, optionPanel, "Select your Ryde Option",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (optionResult == JOptionPane.CANCEL_OPTION) {
                return false;
            } else {
                while (!carpoolButton.isSelected() && !singleButton.isSelected()) {
                    if (optionResult == JOptionPane.OK_OPTION) {
                        JOptionPane.showMessageDialog(this, "Please select an option.", "Error",
                                JOptionPane.ERROR_MESSAGE);
                        optionResult = JOptionPane.showConfirmDialog(this, optionPanel, "Select your Ryde Option",
                                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                    } else {
                        return false;
                    }
                }
                if (client instanceof User) {
                    if (carpoolButton.isSelected()) {
                        ((User) client).setChoice(false);
                        info = info + "Going Carpool\n" + "Price: $" +((User) client).getPrice(start,end,map);
                    } else if (singleButton.isSelected()) {
                        ((User) client).setChoice(true);
                        info = info + "Going Alone\n" + "Price: $" + ((User) client).getPrice(start,end,map);
                    }
                }

                JOptionPane.showMessageDialog(this, info, "Ryde Information", JOptionPane.INFORMATION_MESSAGE);
                return true;
            }
        }
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
            if (client instanceof User) {
                if (txtDocument == uStartTextField.getDocument()) {
                    nowTextField = uStartTextField;
                } else if (txtDocument == uEndTextField.getDocument()) {
                    nowTextField = uEndTextField;
                }
            } else if (client instanceof Driver) {
                nowTextField = dChooseTextField;
            }
            String input = nowTextField.getText().toLowerCase();
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
}