import javax.swing.*;
import java.awt.*;

public class LimitDocumentTest
{
	public static void main( String[] args )
	{
		JTextArea testTextArea = new JTextArea();
		testTextArea.setPreferredSize( new Dimension( 300, 300 ) );
		testTextArea.setLineWrap( true );
		testTextArea.setDocument( new LimitedSizeDocument( 10 ) ); // 10 character limit

		// Content panel
		JPanel contentPanel = new JPanel();
		contentPanel.add( testTextArea );

		// Main window
		JFrame mainFrame = new JFrame( "Limit Document Test" );
		mainFrame.setSize( new Dimension( 400, 400 ) );
		mainFrame.setVisible( true );
		mainFrame.setContentPane( contentPanel );
	}
}