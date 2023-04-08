package autochoosecourse;

import java.awt.Font;
import javax.swing.JLabel;

public class SetLabelFont extends JLabel{
    
    public SetLabelFont(String text) {
        setText(text);
        setFont(new Font("微軟正黑體", Font.BOLD, 18));
    }
}
