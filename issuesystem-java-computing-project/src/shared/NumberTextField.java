import java.awt.Toolkit;
import java.lang.NumberFormatException;
import javax.swing.*;
import javax.swing.text.*;

public class NumberTextField extends JTextField
{
	private int maxDigits;

	class NumberDocument extends PlainDocument
	{
		private Boolean isValidString( String str )
		{
			for ( int i = 0; i < str.length(); i++ )
			{
				char c = str.charAt( i );

				// Ignore whitespace and letters
				// Ignore digits when the maximum has been reached
				if ( Character.isWhitespace( c ) || ( !Character.isDigit( c ) && !Character.isIdentifierIgnorable( c ) ) )	
					return false;
			}
			
			return true;
		}

		public void insertString( int offset, String str, AttributeSet attr ) throws BadLocationException
		{
			if ( str == null )
				return;
				
			if ( getLength() == maxDigits )
			{
				Toolkit.getDefaultToolkit().beep(); // Play beep sound if max digits reached
				
				return;
			}
			else if ( getLength() + str.length() > maxDigits )
			{
				str = str.substring( 0, maxDigits - getLength() );
				
				Toolkit.getDefaultToolkit().beep(); // Play beep sound if input was trimmed
			}
			
			if ( isValidString( str ) )
				super.insertString( offset, str, attr );
			else
				Toolkit.getDefaultToolkit().beep(); // Play beep sound on invalid input
		}
	}
	
	public int getIntegerValue()
	{
		try
		{
			return Integer.parseInt( this.getText() );
		}
		catch ( NumberFormatException e )
		{
			return -1;
		}
	}
 
	protected Document createDefaultModel()
	{
		return new NumberDocument();
	}
	
	public NumberTextField( int maxDigits )
	{
		this.maxDigits = maxDigits;
	}
}