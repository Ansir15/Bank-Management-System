package system.bankingapp.util;

import javax.swing.*;
import java.awt.*;

public class InputValidator {

    public static class ValidationException extends Exception {
        public ValidationException(String message) {
            super(message);
        }
    }

    public static void showError(Component parent, String message) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(parent), "Input Error", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(380, 160);
        dialog.setLocationRelativeTo(parent);
        dialog.setResizable(false);

        JLabel icon = new JLabel(UIManager.getIcon("OptionPane.warningIcon"));
        icon.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 5));

        JLabel msg = new JLabel("<html><body style='width:230px'>" + message + "</body></html>");
        msg.setBorder(BorderFactory.createEmptyBorder(15, 5, 15, 15));

        JButton ok = new JButton("OK");
        ok.setPreferredSize(new Dimension(80, 30));
        ok.addActionListener(e -> dialog.dispose());

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottom.add(ok);

        dialog.add(icon, BorderLayout.WEST);
        dialog.add(msg, BorderLayout.CENTER);
        dialog.add(bottom, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    public static String requireNonEmpty(Component parent, String value, String fieldName) throws ValidationException {
        if (value == null || value.trim().isEmpty()) {
            showError(parent, "<b>" + fieldName + "</b> field is required. Please enter a valid value.");
            throw new ValidationException(fieldName + " is empty");
        }
        return value.trim();
    }

    public static double requireDouble(Component parent, String value, String fieldName) throws ValidationException {
        try {
            double d = Double.parseDouble(value.trim());
            if (d < 0) {
                showError(parent, "<b>" + fieldName + "</b> cannot be negative.");
                throw new ValidationException(fieldName + " is negative");
            }
            return d;
        } catch (NumberFormatException e) {
            showError(parent, "<b>" + fieldName + "</b> must be a valid number (e.g. 1000.00).");
            throw new ValidationException(fieldName + " is not a number");
        }
    }

    public static int requireInt(Component parent, String value, String fieldName) throws ValidationException {
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            showError(parent, "<b>" + fieldName + "</b> must be a whole number.");
            throw new ValidationException(fieldName + " is not an integer");
        }
    }

    public static String requireMinLength(Component parent, String value, String fieldName, int min) throws ValidationException {
        requireNonEmpty(parent, value, fieldName);
        if (value.trim().length() < min) {
            showError(parent, "<b>" + fieldName + "</b> must be at least " + min + " characters long.");
            throw new ValidationException(fieldName + " too short");
        }
        return value.trim();
    }

    public static String requireAlphanumeric(Component parent, String value, String fieldName) throws ValidationException {
        requireNonEmpty(parent, value, fieldName);
        if (!value.trim().matches("[a-zA-Z0-9]+")) {
            showError(parent, "<b>" + fieldName + "</b> must contain only letters and numbers.");
            throw new ValidationException(fieldName + " not alphanumeric");
        }
        return value.trim();
    }
}