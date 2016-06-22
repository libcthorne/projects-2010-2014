import java.net.*;
import java.io.*;

public class NetServer
{
	public ServerSocket serverSocket;
	private Boolean socketValid;

	public Boolean isValid()
	{
		return socketValid;
	}
	
	public void destroy()
	{
		if ( !socketValid )
			return;

		try
		{
			serverSocket.close();
			
			socketValid = false;
		}
		catch ( IOException e )
		{
			System.out.println( "NetServer.destroy exception: " + e.toString() );
		}
	}

	public NetServer( final String address, final int port, final BaseNetHandler handler )
	{
		Runnable netServerRunnable = new Runnable()
		{
			public void run()
			{
				try
				{
					if ( address != null )
					{
						// Bind to specified IP/adapter
						System.out.println( "NetServer.createServer: address=" + address + ", port=" + port );
						serverSocket = new ServerSocket( port, 0, InetAddress.getByName( address ) );
					}
					else
					{
						// Bind to default network adapter
						System.out.println( "NetServer.createServer: port=" + port );
						serverSocket = new ServerSocket( port );
					}

					socketValid = true;

					while ( true )
					{
						System.out.println( "Running net listener" );

						Socket clientSocket;

						try
						{
							clientSocket = serverSocket.accept();

							NetHandlerThread netHandlerThread = new NetHandlerThread( clientSocket, handler );
						}
						catch ( IOException e )
						{
							System.out.println( "serverSocket.accept exception: " + e.toString() );
	
							break;
						}
					}
				}
				catch ( IOException e )
				{
					System.out.println( "NetServer() exception: " + e.toString() );
				}
				
				socketValid = false;
			}
		};
		
		Thread netServerThread = new Thread( netServerRunnable );
		netServerThread.start();
		
		// Hold up main thread until the state of the server is determined
		while ( socketValid == null && netServerThread.isAlive() ) {}
	}
}