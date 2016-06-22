import javax.swing.*;

import java.awt.*;
import java.awt.event.*;

 class HintTextArea extends JTextArea
{
	private Boolean hintTextShowing;
	private String hintText;
	private int fontSize;
	public String initialText;
	
	@Override
	public String getText()
	{
		if ( hintTextShowing )
			return "";

		return super.getText();
	}
	
	private void disableHintText()
	{
		setText( "" );
		
		setFont( new Font( getFont().getName(), Font.PLAIN, fontSize ) );
		
		hintTextShowing = false;
	}
	
	private void enableHintText()
	{
		setText( hintText );

		setFont( new Font( getFont().getName(), Font.BOLD, fontSize ) );
		
		hintTextShowing = true;
	}

	public void initHintText( String hintText, String initialText )
	{
		this.hintText = hintText;
		this.initialText = initialText;
		
		if ( initialText.length() > 0 )
		{
			disableHintText();

			setText( initialText );
		}
		else
		{
			enableHintText();
		}
	}

	public HintTextArea( final int fontSize )
	{
		this.hintText = "";
		this.initialText = "";
		this.fontSize = fontSize;

		final HintTextArea thisWindow = this;

		addFocusListener(
			new FocusListener()
			{
				public void focusGained( FocusEvent e )
				{
					if ( hintTextShowing )
						disableHintText();
				}

				public void focusLost( FocusEvent e )
				{
					// Reset hint text if there is no input
					if ( !hintTextShowing && thisWindow.getText().length() <= 0 )
						enableHintText();
				}
			}
		);
	}
}