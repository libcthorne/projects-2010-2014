import javax.swing.JOptionPane;
import java.net.*;

public class IssueSystemMaster
{
	public static NetServer netServer;

	public static void main( String[] args )
	{
		// Load properties
		if ( !MasterProperties.load() )
		{
			System.out.println( "Error reading properties, exiting." );

			return;	
		}

		// Read report files
		ReportFiles.loadAll();

		// Create master server
		netServer = new NetServer( MasterProperties.hostAddress.get(), MasterProperties.hostPort.get(), new NetMasterMessageHandler() );
	
		if ( netServer.isValid() )
		{
			Boolean useUI = true;

			for ( String s : args )
			{
				if ( s.equals( "-no-ui" ) )
				{
					useUI = false;

					break;
				}
			}
			
			if ( useUI )
				UIManager.load();
		}
		else
		{
			JOptionPane.showMessageDialog( null, "Failed to create net server" );
		}
	}
}