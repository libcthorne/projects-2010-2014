import java.io.*;
import java.net.*;

interface INetHandler
{
	public Boolean init( Socket socket );
	public Boolean process( NetMessageType messageType, DataInputStream inStream, final Socket messageSource ) throws IOException;
	public void shutdown( Socket socket );
	public Boolean getErrorFlag();
	public void setErrorFlag( Boolean error );
}

class BaseNetHandler implements INetHandler
{
	private Boolean errorFlag = false;

	public Boolean init( Socket socket )
	{
		System.out.println( "Base init called" );

		return false;
	}

	public Boolean process( NetMessageType messageType, DataInputStream inStream, final Socket messageSource ) throws IOException
	{
		System.out.println( "Base process called" );

		return false;
	}
	
	public void shutdown( Socket socket )
	{
		System.out.println( "Base shutdown called" );
	}

	public Boolean getErrorFlag()
	{
		return errorFlag;
	}

	public void setErrorFlag( Boolean error )
	{
		errorFlag = error;
	}
}

public class NetHandlerThread
{
	private Thread netHandlerThread;

	public NetHandlerThread( final Socket socket, final INetHandler handler )
	{
		if ( handler.init( socket ) )
		{
			Runnable netHandler = new Runnable()
			{
				public void run()
				{
					try
					{
						DataInputStream netMessageReader = new DataInputStream( socket.getInputStream() );

						while ( true )
						{
							System.out.println( "(" + handler + ") Waiting for message.." );
							
							// Read net message type
							NetMessageType messageType = NetMessageType.convert( netMessageReader.readShort() );

							if ( messageType != null )
							{
								if ( !handler.process( messageType, netMessageReader, socket ) )
								{
									System.out.println( "(" + socket.toString() + ") handler.process( " + messageType + " ) returned false, terminating connection." );

									break;
								}
							}
							else
							{
								System.out.println( "(" + handler + ") Received unknown type, disconnecting" );

								handler.setErrorFlag( true );

								break;
							}
						}
						
						// Shutdown socket handler
						handler.shutdown( socket );

						// Close packet reader stream
						netMessageReader.close();
					}
					catch ( EOFException e )
					{
						System.out.println( "(" + handler + ") Socket stream terminated" );

						// Shutdown by peer
						handler.shutdown( socket );
					}
					catch ( SocketException e )
					{
						System.out.println( "(" + handler + ") Socket exception: " + e.toString() );
						
						// Shutdown caused by an error if the socket is still open
						if ( !socket.isClosed() )
							handler.setErrorFlag( true );

						handler.shutdown( socket );
					}
					catch ( IOException e )
					{
						System.out.println( "(" + handler + ") IO exception: " + e.toString() );

						// Shutdown with error flag
						handler.setErrorFlag( true );
						handler.shutdown( socket );
					}
					
					try
					{
						if ( !socket.isClosed() )
							socket.close();
					}
					catch ( IOException closeException )
					{
						System.out.println( "(" + handler + ") Error closing socket" );
					}
				}
			};

			netHandlerThread = new Thread( netHandler );
			netHandlerThread.start();
		}
	}
}