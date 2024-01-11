import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Interface extends JFrame {

    private JTextField phoneNumberField;
    private JTextField pickupLocationField;
    private JTextField destinationField;

    private JButton confirmRideButton;

    private JLabel routeLabel;
    private JLabel uberLocationLabel;
    private JLabel userLocationLabel;

    private Timer timer;

    private double uberLatitude = 37.7749; // Initial Uber location (example)
    private double uberLongitude = -122.4194;

    public Interface() {
        setTitle("Ride Interface");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());

        initializeComponents();

        timer = new Timer(5000, e -> updateUberLocation());
    }

    private void initializeComponents() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        phoneNumberField = new JTextField();
        pickupLocationField = new JTextField();
        destinationField = new JTextField();

        confirmRideButton = new JButton("Confirm Ride");
        confirmRideButton.addActionListener(e -> confirmRide());

        routeLabel = new JLabel("Planned Route: ");
        uberLocationLabel = new JLabel("Uber Location: ");
        userLocationLabel = new JLabel("User Location: ");

        gbc.gridx = 0;
        gbc.gridy = 0;
        addLabelAndField("Phone Number:", phoneNumberField, gbc);

        gbc.gridy++;
        addLabelAndField("Pickup Location:", pickupLocationField, gbc);

        gbc.gridy++;
        addLabelAndField("Destination:", destinationField, gbc);

        gbc.gridy++;
        gbc.gridwidth = 2;
        add(confirmRideButton, gbc);

        gbc.gridy++;
        addLabelAndField("Planned Route:", routeLabel, gbc);

        gbc.gridy++;
        addLabelAndField("Uber Location:", uberLocationLabel, gbc);

        gbc.gridy++;
        addLabelAndField("User Location:", userLocationLabel, gbc);
    }

    private void addLabelAndField(String labelText, JComponent field, GridBagConstraints gbc) {
        gbc.gridx = 0;
        gbc.gridwidth = 1;
        add(new JLabel(labelText), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(field, gbc);
    }

    private void confirmRide() {
        String phoneNumber = phoneNumberField.getText();
        String pickupLocation = pickupLocationField.getText();
        String destination = destinationField.getText();

        double distance = calculateDistance(pickupLocation, destination);
        double payment = distance * 2.5; // Example rate per kilometer

        routeLabel.setText("Planned Route: " + pickupLocation + " to " + destination);
        JOptionPane.showMessageDialog(this, "Ride confirmed!\nPayment: $" + payment, "Confirmation", JOptionPane.INFORMATION_MESSAGE);

        timer.start();
    }

    private void updateUberLocation() {
        uberLatitude += 0.001;
        uberLongitude += 0.001;

        uberLocationLabel.setText("Uber Location: " + uberLatitude + ", " + uberLongitude);
    }

    private double calculateDistance(String startLocation, String endLocation) {
        // Simulate distance calculation (example)
        return 10.0; // Distance in kilometers (example)
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Interface().setVisible(true));
    }
}
