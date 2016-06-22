import java.awt.event.WindowAdapter;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class UIBaseWindow
{
	public JFrame mainFrame;

	public void processError( String message )
	{
		System.out.println( "processError( " + message + " )" );

		JOptionPane.showMessageDialog( mainFrame, message );

		destroy();
	}

	public void SetupMainFrame( String title, int width, int height, JPanel content, WindowAdapter listener )
	{
		mainFrame = new JFrame( title ); // Create main window
		
		if ( listener != null )
			mainFrame.addWindowListener( listener ); // Use custom listener
		else
			mainFrame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE ); // Use default listener
	
		mainFrame.setSize( width, height ); // Set window size
		mainFrame.setContentPane( content ); // Set content panel
		mainFrame.setLocationRelativeTo( null ); // Center window
		mainFrame.setResizable( false ); // Disable resizing
		mainFrame.setVisible( true ); // Make the window visible
	}
	
	public void destroy()
	{
		mainFrame.dispose(); // Free mainFrame
	}
}