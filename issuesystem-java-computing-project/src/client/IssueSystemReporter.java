import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class IssueSystemReporter
{
	public static void main( String[] args )
	{
		// Load properties
		if ( !ClientProperties.load() )
		{
			System.out.println( "ClientProperties.load() failed" );
			JOptionPane.showMessageDialog( null, "Failed to load client properties. Shutting down." );
			System.out.println( "Shutting down." );

			return;
		}

		// Initialize UI
		UIManager.load();
	}
}