package zandbak;

import net.miginfocom.swing.MigLayout;
import javax.swing.*;
import java.awt.*;

public class MigLayoutFontTest {
    public static void main(String[] args) {
        // Maak een JFrame
        JFrame frame = new JFrame("MigLayout Font Test  © Copyright 2025");
        frame.setLayout(new MigLayout("wrap 2")); // 2 kolommen

        // Nieuw lettertype
        Font customFont = new Font("Arial", Font.PLAIN, 18);

        // Componenten maken met aangepast lettertype
        JLabel label = new JLabel("© Copyright 2025");
        label.setFont(customFont);

        JButton button = new JButton("Klik hier");
        button.setFont(customFont);

        JTextField textField = new JTextField("Voer tekst in", 15);
        textField.setFont(customFont);

        // Componenten toevoegen aan layout
        frame.add(label);
        frame.add(button);
        frame.add(textField, "span, growx"); // "span" maakt het veld breder

        // Basis instellingen voor het venster
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 200);
        frame.setVisible(true);
    }
}

