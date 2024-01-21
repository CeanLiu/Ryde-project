import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextField;

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
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Interface extends JFrame {
    final int MAX_X = (int) getToolkit().getScreenSize().getWidth();
    final int MAX_Y = (int) getToolkit().getScreenSize().getHeight();


    private BufferedImage mapImage;
    JFrame frame;
    JPanel welcomePanel, userLoginPanel, jPanel;
    JSplitPane splitPane;
    JButton driveButton, rideButton;
    MapPanel mapPanel;
    UserPanel userPanel;
    SimpleGraph map;
    Client client;
    private boolean isDriver;

    public Interface(SimpleGraph map, String imageFile){
        this.map = map;
         try {

            // Load the image that will be shown in the panel
            mapImage = ImageIO.read(new File(imageFile));

        } catch (IOException ex) {
            System.out.println("No Image Found");
        }
    }

    public void runGUI() {
        // Set the size to 3/4 of the screen
        frame = new JFrame("RYDE");
        frame.setSize(MAX_X * 3 / 4, MAX_Y * 3 / 4);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridBagLayout());
        frame.setLocationRelativeTo(null);
        GridBagConstraints frameGBC = new GridBagConstraints();


        // ------------------------------------------------------------------------------------------------------------------
        // #region
        welcomePanel = new JPanel(new GridBagLayout());
        GridBagConstraints startGBC = new GridBagConstraints();

        // Create the title label with a larger font
        JLabel titleLabel = new JLabel("Welcom To RYDE");
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
        welcomePanel.add(driveButton, startGBC);

        // Create the right button with a larger size
        rideButton = new JButton("I am a Ryder");
        rideButton.setFont(new Font("Arial", Font.PLAIN, 18)); // Larger font
        startGBC.gridx = 1;
        startGBC.gridy = 1;
        startGBC.insets = new Insets(100, 100, 30, 20); // Increased bottom margin, decreased left margin
        rideButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                if(event.getSource() == driveButton){
                    isDriver = true;
                }else{
                    isDriver = false;
                }
                goLoginPage(isDriver);
            }
        });

        welcomePanel.add(rideButton, startGBC);
        frame.add(welcomePanel, frameGBC);
        // #endregion
        // -----------------------------------------------------------------------------------------------------------------

        // #region
        userLoginPanel = new JPanel();
        userLoginPanel.setLayout(new GridLayout(3, 1, 10, 10));

        JLabel promptLabel = new JLabel("What is your phone number?");
        Font labelFont = promptLabel.getFont();
        promptLabel.setFont(new Font(labelFont.getName(), Font.PLAIN, 24));
        userLoginPanel.add(promptLabel);

        JTextField phoneNumberTextField = new JTextField();
        setPlaceholder(phoneNumberTextField, "Enter your phone number"); // Set placeholder text
        Font textFont = phoneNumberTextField.getFont();
        phoneNumberTextField.setFont(new Font(textFont.getName(), Font.PLAIN, 24));
        userLoginPanel.add(phoneNumberTextField);

        JButton continueButton = new JButton("Continue");
        Font buttonFont = continueButton.getFont();
        continueButton.setFont(new Font(buttonFont.getName(), Font.PLAIN, 24));
        continueButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                try{
                Long phoneNum = Long.parseLong(phoneNumberTextField.getText().trim());
                goUserPage(phoneNum);
                }catch(NullPointerException e){
                    System.out.println("Please fill in your phone number");
                    
                        
                }
            }
        });
        userLoginPanel.add(continueButton);
        userLoginPanel.setVisible(false);
        frame.add(userLoginPanel,frameGBC);

        // #endregion
        // ----------------------------------------------------------------------------------------------
       
       
        userPanel = new UserPanel(map);
        try {

            // Load the image that will be shown in the panel
            BufferedImage image = ImageIO.read(new File("mapImage.png"));
            mapPanel = new MapPanel(image,map,userPanel);
            //mapPanel.setBorder(BorderFactory.createLineBorder(Color.black));

        } catch (IOException ex) {
            
        }

        splitPane = new JSplitPane(1);
        splitPane.setLeftComponent(userPanel);
        splitPane.setRightComponent(mapPanel);



        //MouseClickListener mouseClickListener = new MouseClickListener();
        //mapPanel.addMouseListener(mouseClickListener);
        //userPanel.setVisible(false);
        //frameGBC.anchor = GridBagConstraints.NORTHWEST;

        
        // Add the mouse click listener to the panel
        frameGBC.weightx = 1.0;
        frameGBC.weighty = 1.0;
        frameGBC.fill = GridBagConstraints.BOTH;
        frameGBC.anchor = GridBagConstraints.NORTHWEST;
        splitPane.setVisible(false);
        splitPane.setDividerLocation(500);
        frame.add(splitPane,frameGBC);
        

        frame.setVisible(true);
        

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
        userLoginPanel.setVisible(false);
        splitPane.setVisible(false);
    }

    public void goLoginPage(boolean isDriver) {
        initialize();
        if (isDriver) {
            // driverLogin.setVisible(true);
        } else {
            userLoginPanel.setVisible(true);
        }

    }

    public void goUserPage(Long phoneNum) {
        initialize();
        if(!isDriver){
            client = new User(phoneNum);
        }
        userPanel.setUser(client);
        splitPane.setVisible(true);

    }

    public static void main(String[] args) {
        SimpleGraph hi= new SimpleGraph();
        Interface bruh = new Interface(hi,"mapImage.png");
        bruh.runGUI();
    }
}
