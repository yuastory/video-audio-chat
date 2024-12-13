package common;

import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JTextField;
import javax.swing.border.Border;

public class CustomJTextField extends JTextField implements FocusListener
{
    private static final long serialVersionUID = -4203367228252300935L;
    private final String      hint;
    private boolean           showingHint;

    public CustomJTextField()
    {
        hint = "";
    }
    
    @Override
    public void setBorder(Border border)
    {
        
        super.setBorder(SharedPreferences.BORDER_TEXT_FIELD);
//        super.setBorder(border);
    }

    public CustomJTextField(final String hint)
    {
        super(hint);
        setForeground(SharedPreferences.COLOR_GRAY);
        this.hint = hint;
        this.showingHint = true;
        super.addFocusListener(this);
    }

    @Override
    public void focusGained(FocusEvent e)
    {
        if (this.getText().isEmpty())
        {
            setForeground(Color.BLACK);
            super.setText("");
            showingHint = false;
        }
    }

    @Override
    public void focusLost(FocusEvent e)
    {
        if (this.getText().isEmpty())
        {
            setForeground(SharedPreferences.COLOR_GRAY);
            super.setText(hint);
            showingHint = true;
        }
    }

    @Override
    public String getText()
    {
        return showingHint ? "" : super.getText();
    }
}