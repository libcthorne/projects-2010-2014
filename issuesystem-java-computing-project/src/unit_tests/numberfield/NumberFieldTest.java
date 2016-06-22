import javax.swing.*;
import java.awt.*;

public class NumberFieldTest
{
	public static void main( String[] args )
	{
		// 4 digit limit
		NumberTextField testField = new NumberTextField( 4 );
		testField.setPreferredSize( new Dimension( 100, 50 ) );

		// Content panel
		JPanel contentPanel = new JPanel();
		contentPanel.add( testField );

		// Main window
		JFrame mainFrame = new JFrame( "Number Field Test" );
		mainFrame.setSize( new Dimension( 400, 100 ) );
		mainFrame.setVisible( true );
		mainFrame.setContentPane( contentPanel );
	}
}