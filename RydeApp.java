import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Font;

public class RydeApp extends JFrame {

    public RydeApp() {
        initializeUI();
    }

    private void initializeUI() {
        setTitle("RYDE App");

        // Set the frame size to approximately 3/4 of the screen size
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int width = screenSize.width * 3 / 4;
        int height = screenSize.height * 3 / 4;
        setSize(width, height);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create panel
        JPanel panel = new JPanel(new BorderLayout());

        // Create labels and buttons
        JLabel titleLabel = new JLabel("Welcome to RYDE", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));

        JButton soloButton = new JButton("Ride Alone");
        JButton carpoolButton = new JButton("Carpool");

        // Set preferred size for buttons
        soloButton.setPreferredSize(new Dimension(150, 50));
        carpoolButton.setPreferredSize(new Dimension(150, 50));

        // Add action listeners to the buttons
        soloButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(RydeApp.this, "You have chosen to ride alone.");
            }
        });

        carpoolButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(RydeApp.this, "You have chosen carpooling.");
            }
        });

        // Create panel for buttons
        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 0, 20));
        buttonPanel.add(soloButton);
        buttonPanel.add(carpoolButton);

        // Create panel for the welcome message
        JPanel messagePanel = new JPanel();
        messagePanel.add(titleLabel);

        // Add components to the main panel
        panel.add(messagePanel, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        // Add the main panel to the frame
        add(panel);

        // Make the frame visible
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RydeApp());
    }
}