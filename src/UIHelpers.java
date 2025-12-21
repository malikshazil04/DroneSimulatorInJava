import javax.swing.*;
import java.awt.*;

public class UIHelpers {
    public static final Color SKY_BLUE = new Color(135, 206, 235);
    public static final Color DARK_BLUE = new Color(15, 23, 42);
    public static final Color TEXT_DARK = new Color(15, 23, 42);
    public static final Font LABEL_FONT = new Font("Arial", Font.BOLD, 13);
    public static final Dimension FIELD_SIZE = new Dimension(80, 25);
    public static final Dimension BUTTON_SIZE = new Dimension(100, 30);

    public static void styleLabel(JLabel label) {
        label.setFont(LABEL_FONT);
        label.setForeground(TEXT_DARK);
    }

    public static void styleTextField(JTextField field) {
        field.setPreferredSize(FIELD_SIZE);
        field.setMinimumSize(FIELD_SIZE);
        field.setMaximumSize(FIELD_SIZE);
        field.setFont(new Font("Arial", Font.PLAIN, 12));
        field.setBackground(Color.WHITE);
        field.setForeground(TEXT_DARK);
    }

    public static void styleButton(JButton button) {
        button.setPreferredSize(BUTTON_SIZE);
        button.setMinimumSize(BUTTON_SIZE);
        button.setMaximumSize(BUTTON_SIZE);
    }

    public static void styleComboBox(JComboBox<?> combo) {
        combo.setFont(new Font("Arial", Font.PLAIN, 12));
        combo.setForeground(TEXT_DARK);
        combo.setBackground(Color.WHITE);
        combo.setPreferredSize(FIELD_SIZE);
    }
}
