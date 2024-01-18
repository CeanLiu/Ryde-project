import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

public class Interface extends JFrame {
    final int MAX_X = (int) getToolkit().getScreenSize().getWidth();
    final int MAX_Y = (int) getToolkit().getScreenSize().getHeight();

    JFrame frame;
    JPanel welcomePanel, userLoginPanel, userPanel;
    JButton driveButton, rideButton;

    public Interface() {
        // Set the size to 3/4 of the screen
        frame = new JFrame("RYDE");
        frame.setSize(MAX_X * 3 / 4, MAX_Y * 3 / 4);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridBagLayout());
        frame.setLocationRelativeTo(null);

        // ------------------------------------------------------------------------------------------------------------------
        // #region
        welcomePanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Create the title label with a larger font
        JLabel titleLabel = new JLabel("Welcom To RYDE");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24)); // Larger font
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2; // Span across two columns
        gbc.insets = new Insets(0, 20, 20, 20); // Increased top margin
        welcomePanel.add(titleLabel, gbc);

        // Create the left button with a larger size
        driveButton = new JButton("I am a Driver");
        driveButton.setFont(new Font("Arial", Font.PLAIN, 18)); // Larger font
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1; // Reset grid width
        gbc.insets = new Insets(100, 20, 30, 10); // Increased bottom margin, decreased right margin
        welcomePanel.add(driveButton, gbc);

        // Create the right button with a larger size
        rideButton = new JButton("I am a Ryder");
        rideButton.setFont(new Font("Arial", Font.PLAIN, 18)); // Larger font
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.insets = new Insets(100, 100, 30, 20); // Increased bottom margin, decreased left margin
        rideButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                goLoginPage(event.getSource() == driveButton);
            }
        });
        welcomePanel.add(rideButton, gbc);
        frame.add(welcomePanel);
        // #endregion
        // -----------------------------------------------------------------------------------------------------------------

        JLabel promptLabel = new JLabel("What is your phone number?");
        JTextField phoneNumberTextField = new JTextField();
        setPlaceholder(phoneNumberTextField, "Enter your phone number"); // Set placeholder text

        JButton continueButton = new JButton("Continue");

        Font labelFont = promptLabel.getFont();
        promptLabel.setFont(new Font(labelFont.getName(), Font.PLAIN, 24));

        Font textFont = phoneNumberTextField.getFont();
        phoneNumberTextField.setFont(new Font(textFont.getName(), Font.PLAIN, 24));

        Font buttonFont = continueButton.getFont();
        continueButton.setFont(new Font(buttonFont.getName(), Font.PLAIN, 24));

        // Set layout manager
        userLoginPanel = new JPanel();
        userLoginPanel.setLayout(new GridLayout(3, 1,10,10));

        // Add components to the frame
        userLoginPanel.add(promptLabel);
        userLoginPanel.add(phoneNumberTextField);
        userLoginPanel.add(continueButton);
        userLoginPanel.setVisible(false);

        // Add action listener to the continue button
        frame.add(userLoginPanel);
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
        //userPanel.setVisible(false);
    }

    public void goLoginPage(boolean isDriver) {
        initialize();
        if (isDriver) {
            // driverLogin.setVisible(true);
        } else {
            userLoginPanel.setVisible(true);
        }

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Interface example = new Interface();
        });
    }
}
