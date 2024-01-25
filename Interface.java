import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.UIManager;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
public class Interface extends JFrame {
    final int MAX_X = (int) getToolkit().getScreenSize().getWidth();
    final int MAX_Y = (int) getToolkit().getScreenSize().getHeight();

    private BufferedImage mapImage;
    private Database db;
    private JFrame frame;
    private JPanel welcomePanel, loginPanel, driverLoginPanel;
    private JTextField dCapacityTextField;
    private JLabel dCapacityLabel;
    private JSplitPane splitPane;
    private JButton driveButton, rideButton;
    private MapPanel mapPanel;
    private InfoPanel infoPanel;
    private SimpleGraph map;
    private boolean isDriver;

    public Interface(SimpleGraph map, String imageFile) {
        db = new Database(map,this);
        this.map = map;
        try {
            mapImage = ImageIO.read(new File(imageFile));
        } catch (IOException ex) {
            System.out.println("No Image Found");
        }
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void runGUI() {
        // Set the size to 3/4 of the screen
        frame = new JFrame("RYDE");
        frame.setSize(MAX_X *3/4, MAX_Y *3/4);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridBagLayout());
        frame.setLocationRelativeTo(null);
        GridBagConstraints frameGBC = new GridBagConstraints();
        // ------------------------------------------------------------------------------------------------------------------
        // #region
        welcomePanel = new JPanel(new GridBagLayout());
        GridBagConstraints startGBC = new GridBagConstraints();

        // Create the title label with a larger font
        JLabel titleLabel = new JLabel("Welcome To RYDE");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24)); // Larger font
        startGBC.gridx = 0;
        startGBC.gridy = 0;
        startGBC.gridwidth = 2; // Span across two columns
        startGBC.insets = new Insets(0, 20, 20, 20); // Increased top margin
        welcomePanel.add(titleLabel, startGBC);

        // Create the left button with a larger size
        driveButton = new JButton("I am a Driver");
        driveButton.setFont(new Font("Arial", Font.PLAIN, 18)); // Larger font
        startGBC.gridx = 0;
        startGBC.gridy = 1;
        startGBC.gridwidth = 1; // Reset grid width
        startGBC.insets = new Insets(100, 20, 30, 10); // Increased bottom margin, decreased right margin
        driveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                isDriver = true;
                goLoginPage();
            }
        });
        welcomePanel.add(driveButton, startGBC);

        // Create the right button with a larger size
        rideButton = new JButton("I am a Ryder");
        rideButton.setFont(new Font("Arial", Font.PLAIN, 18)); // Larger font
        startGBC.gridx = 1;
        startGBC.gridy = 1;
        startGBC.insets = new Insets(100, 100, 30, 20); // Increased bottom margin, decreased left margin
        rideButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                isDriver = false;
                goLoginPage();
            }
        });

        welcomePanel.add(rideButton, startGBC);
        frame.add(welcomePanel, frameGBC);
        // #endregion
        // -----------------------------------------------------------------------------------------------------------------

        // #region
        loginPanel = new JPanel();
        loginPanel.setLayout(new GridLayout(5, 1, 10, 10));

        dCapacityLabel = new JLabel("What is your car's maximum capacity?");
        dCapacityLabel.setFont(new Font(dCapacityLabel.getFont().getName(), Font.PLAIN, 24));
        loginPanel.add(dCapacityLabel);
        dCapacityLabel.setVisible(false);

        dCapacityTextField = new JTextField();
        setPlaceholder(dCapacityTextField, "Enter your car's capacity");
        dCapacityTextField.setFont(new Font(dCapacityTextField.getFont().getName(), Font.PLAIN, 24));
        loginPanel.add(dCapacityTextField);
        dCapacityTextField.setVisible(false);

        JLabel phoneLabel = new JLabel("What is your phone number?");
        phoneLabel.setFont(new Font(phoneLabel.getFont().getName(), Font.PLAIN, 24));
        loginPanel.add(phoneLabel);

        JTextField phoneNumberTextField = new JTextField();
        setPlaceholder(phoneNumberTextField, "Enter your phone number"); // Set placeholder text
        phoneNumberTextField.setFont(new Font(phoneNumberTextField.getFont().getName(), Font.PLAIN, 24));
        loginPanel.add(phoneNumberTextField);

        JButton continueButton = new JButton("Continue");
        continueButton.setFont(new Font(continueButton.getFont().getName(), Font.PLAIN, 24));
        continueButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                try {
                    Long phoneNum = Long.parseLong(phoneNumberTextField.getText().trim());
                    if(isDriver){
                        int capacity  =  Integer.parseInt(dCapacityTextField.getText().trim());
                        goDriverPage(phoneNum, capacity);
                    }else{
                        goUserPage(phoneNum);
                    }
                } catch (NullPointerException e) {
                    System.out.println("Please fill in your phone number");

                }
            }
        });
        loginPanel.add(continueButton);
        loginPanel.setVisible(false);
        frame.add(loginPanel, frameGBC);

        // #endregion
        // ----------------------------------------------------------------------------------------------

        infoPanel = new InfoPanel(map, db);
        mapPanel = new MapPanel(mapImage, map, infoPanel);

        splitPane = new JSplitPane(1);
        splitPane.setLeftComponent(infoPanel);
        splitPane.setRightComponent(mapPanel);
        splitPane.setDividerLocation(600);

        frameGBC.weightx = 1.0;
        frameGBC.weighty = 1.0;
        frameGBC.fill = GridBagConstraints.BOTH;
        frameGBC.anchor = GridBagConstraints.NORTHWEST;
        splitPane.setVisible(false);
        frame.add(splitPane, frameGBC);

        frame.setVisible(true);

    }

    public InfoPanel getInfoPanel() {
        return infoPanel;
    }

    public MapPanel getMapPanel() {
        return mapPanel;
    }

    private void setPlaceholder(JTextField textField, String placeholder) {
        textField.setForeground(Color.GRAY);
        textField.setText(placeholder);

        textField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (textField.getText().equals(placeholder)) {
                    textField.setText("");
                    textField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (textField.getText().isEmpty()) {
                    textField.setForeground(Color.GRAY);
                    textField.setText(placeholder);
                }
            }
        });
    }

    public void initialize() {
        welcomePanel.setVisible(false);
        loginPanel.setVisible(false);
        splitPane.setVisible(false);
    }

    public void goLoginPage() {
        initialize();
        if (isDriver) {
            dCapacityLabel.setVisible(true);
            dCapacityTextField.setVisible(true);
        }
        loginPanel.setVisible(true);
    }

    public void goUserPage(Long phoneNum) {
        initialize();
        db.addUser(this, phoneNum);
        Thread userThread = new Thread(new UserThread(db, phoneNum));
        userThread.start();
        infoPanel.setClient(db.getUser(phoneNum));
        infoPanel.initUserPanel();
        splitPane.setVisible(true);
        db.getUser(phoneNum).updateGUI();
        repaint();
    }

    public void goDriverPage(long phoneNum, int capacity) {
        initialize();
        db.addDriver(this, phoneNum, capacity);
        Thread userThread = new Thread(new DriverThread(db, phoneNum));
        userThread.start();
        infoPanel.setClient(db.getDriver(phoneNum));
        infoPanel.initDriverPanel();
        splitPane.setVisible(true);
        db.getDriver(phoneNum).updateGUI();
        repaint();

    }
}
