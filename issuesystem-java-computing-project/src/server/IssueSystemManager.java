import java.net.*;
import java.io.*;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class IssueSystemManager
{
	public static NetClient netMasterClient;

	public static void main( String[] args )
	{
		// Load properties
		if ( !ServerProperties.load() )
		{
			System.out.println( "ServerProperties.load() failed" );
			JOptionPane.showMessageDialog( null, "Failed to load server properties. Shutting down." );
			System.out.println( "Shutting down." );

			return;	
		}

		// Connect to master server

		System.out.println( "Connecting to master server..." );
		
		netMasterClient = new NetClient( ServerProperties.masterAddress.get(), ServerProperties.masterPort.get(), new NetServerMessageHandler() );

		System.out.println( "Connected to master server." );

		UIManager.load();
	}
}