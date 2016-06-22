import java.awt.*;
import javax.swing.text.*;

public class LimitedSizeDocument extends PlainDocument
{
	private int maxCharacters;
	
	@Override
	public void insertString( int offset, String str, AttributeSet attr ) throws BadLocationException
	{
		if ( str == null )
			return;

		if ( getLength() == maxCharacters )
		{
			Toolkit.getDefaultToolkit().beep(); // Play beep sound if limit is reached
		}
		else if ( getLength() + str.length() > maxCharacters )
		{
			super.insertString( offset, str.substring( 0, maxCharacters - getLength() ), attr );
			
			Toolkit.getDefaultToolkit().beep(); // Play beep sound if string is trimmed
		}
		else
		{
			super.insertString( offset, str, attr );
		}
	}

	public LimitedSizeDocument( int maxCharacters )
	{
		this.maxCharacters = maxCharacters;
	}
}