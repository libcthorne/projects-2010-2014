import java.net.*;
import java.io.*;

public class NetClient
{
	public Socket clientSocket;
	private Boolean socketValid;

	public Boolean isValid()
	{
		return socketValid;
	}

	public void destroy()
	{
		System.out.println( "NetClient.destroy" );

		try
		{
			System.out.println( "Closing NetClient socket" );
			clientSocket.close();
			System.out.println( "Closed NetClient socket" );
		}
		catch ( IOException e )
		{
			System.out.println( "Exception in NetClient.destroy" );
		}

		socketValid = false;
	}
	
	public NetClient( String serverIP, int serverPort, BaseNetHandler handler )
	{
		clientSocket = new Socket();

		try
		{
			// Attempt to connect with 5 second timeout
			clientSocket.connect( new InetSocketAddress( serverIP, serverPort ), 5000 );
			
			NetHandlerThread netHandlerThread = new NetHandlerThread( clientSocket, handler );

			socketValid = true;
		}
		catch ( IOException e )
		{
			System.out.println( "NetClient() exception: " + e.toString() );
			
			socketValid = false;
			
			handler.setErrorFlag( true );
			handler.shutdown( clientSocket );
		}
	}
}