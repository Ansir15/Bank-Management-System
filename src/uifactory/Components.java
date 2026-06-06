package uifactory;

import javax.swing.*;
import java.awt.*;

public class Components {
    public static JButton createButton(String text, Color bgcolor, int fontSize){
     JButton button=new JButton(text);
     button.setBackground(bgcolor);
     button.setFont(new Font("Segoe UI",Font.PLAIN,fontSize));
     button.setForeground(new Color(255,255,255));
     return button;
    }
    public static JLabel createLabel(String text,int fontSize,int fontWeight){
        JLabel label=new JLabel(text);
        label.setFont(new Font("Segoe UI",fontWeight,fontSize));
        return label;
    }
}
