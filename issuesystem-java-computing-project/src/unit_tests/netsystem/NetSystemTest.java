import java.io.*;
import java.net.*;

class NetSystemTestClientHandler extends BaseNetHandler
{
	public Boolean init( Socket socket )
	{
		System.out.println( "NetSystemTestClientHandler.init(" + socket + ")" );

		return true;
	}
		
	public Boolean process( NetMessageType messageType, DataInputStream inStream, final Socket messageSource ) throws IOException
	{
		System.out.println( "NetSystemTestClientHandler.process( " + messageType + " )" );

		switch ( messageType )
		{
			case S2C_TEST:
			{
				String testStr = inStream.readUTF();
				System.out.println( "C2S_TEST: testStr=" + testStr );
				Integer testInt = inStream.readInt();
				System.out.println( "C2S_TEST: testInt=" + testInt );
					
				return true;
			}
			default:
			{
				setErrorFlag( true );

				return false;
			}
		}
	}

	public void shutdown( Socket socket )
	{
		System.out.println( "NetSystemTestClientHandler.shutdown(" + socket + "), error=" + getErrorFlag() );
	}
}

class NetSystemTestServerHandler extends BaseNetHandler
{
	public Boolean init( Socket socket )
	{
		System.out.println( "NetSystemTestServerHandler.init(" + socket + ")" );

		return true;
	}
		
	public Boolean process( NetMessageType messageType, DataInputStream inStream, final Socket messageSource ) throws IOException
	{
		System.out.println( "NetSystemTestServerHandler.process( " + messageType + " )" );

		switch ( messageType )
		{
			case C2S_TEST:
			{
				String testStr = inStream.readUTF();
				System.out.println( "C2S_TEST: testStr=" + testStr );
				Integer testInt = inStream.readInt();
				System.out.println( "C2S_TEST: testInt=" + testInt );
					
				return true;
			}
			default:
			{
				setErrorFlag( true );

				return false;
			}
		}
	}

	public void shutdown( Socket socket )
	{
		System.out.println( "NetSystemTestServerHandler.shutdown(" + socket + "), error=" + getErrorFlag() );
	}
}

public class NetSystemTest
{
	private static void invalidLocalAddressTest()
	{
		NetServer netServer = new NetServer( "1.1.1.1", 1, new NetSystemTestClientHandler() );
		System.out.println( "valid=" + netServer.isValid() );	
	}

	private static void validLocalAddressTest()
	{
		NetServer netServer = new NetServer( "localhost", 6000, new NetSystemTestClientHandler() );
		System.out.println( "valid=" + netServer.isValid() );
	}

	private static void invalidClientToServerTest()
	{
		NetClient netClient = new NetClient( "1.1.1.1", 80, new NetSystemTestClientHandler() );
		System.out.println( "valid=" + netClient.isValid() );
	}

	private static void validClientToServerTest()
	{
		NetServer netServer = new NetServer( "192.168.0.9", 6000, new NetSystemTestServerHandler() );
		
		if ( netServer.isValid() )
		{
			NetClient netClient = new NetClient( "192.168.0.9", 6000, new NetSystemTestClientHandler() );
			System.out.println( "valid=" + netClient.isValid() );
		}
		else
		{
			System.out.println( "test error: invalid server" );
		}
	}

	private static void unhandledNetMessageTest()
	{
		NetServer netServer = new NetServer( "localhost", 6000, new NetSystemTestServerHandler() );
		
		if ( netServer.isValid() )
		{
			NetClient netClient = new NetClient( "localhost", 6000, new NetSystemTestClientHandler() );
			
			if ( netClient.isValid() )
			{
				Boolean sendResult = NetPacketSender.send( netClient.clientSocket, NetMessageType.C2S_UNHANDLED_TEST_MESSAGE, null );

				System.out.println( "client packet sent: " + sendResult );
			}
			else
			{
				System.out.println( "test error: invalid client" );
			}
		}
		else
		{
			System.out.println( "test error: invalid server" );
		}
	}
	
	private static void validNetMessageTest()
	{
		NetServer netServer = new NetServer( "localhost", 6000, new NetSystemTestServerHandler() );
		
		if ( netServer.isValid() )
		{
			NetClient netClient = new NetClient( "localhost", 6000, new NetSystemTestClientHandler() );
			
			if ( netClient.isValid() )
			{
				Boolean sendResult = NetPacketSender.send( netClient.clientSocket, NetMessageType.C2S_TEST,
					new NetPacketWriter()
					{
						public void write( DataOutputStream outStream ) throws IOException
						{
							outStream.writeUTF( "AAA" );
							outStream.writeInt( 100 );
						}						
					}
				);

				System.out.println( "client packet sent: " + sendResult );
			}
			else
			{
				System.out.println( "test error: invalid client" );
			}
		}
		else
		{
			System.out.println( "test error: invalid server" );
		}
	}
	
	private static void clientToServerDisconnectTest()
	{
		NetServer netServer = new NetServer( "localhost", 6000, new NetSystemTestServerHandler() );
		
		if ( netServer.isValid() )
		{
			NetClient netClient = new NetClient( "localhost", 6000, new NetSystemTestClientHandler() );
			
			if ( netClient.isValid() )
			{	
				try
				{
					System.out.println( "Sleeping.." );
					Thread.currentThread().sleep( 1000 ); // Sleep for one second
					System.out.println( "Disconnect." );
				}
				catch ( InterruptedException e )
				{
					System.out.println( "Sleep interrupted" );
				}
				
				try
				{
					netClient.clientSocket.close();
				}
				catch ( IOException e )
				{
					System.out.println( "Disconnect failed" );
				}
			}
			else
			{
				System.out.println( "test error: invalid client" );
			}
		}
		else
		{
			System.out.println( "test error: invalid server" );
		}
	}
	
	private static void clientToServerAbnormalDisconnectTest()
	{
		NetServer netServer = new NetServer( "192.168.0.9", 6000, new NetSystemTestServerHandler() );
		
		if ( netServer.isValid() )
		{
			NetClient netClient = new NetClient( "192.168.0.9", 6000, new NetSystemTestClientHandler() );
			
			if ( netClient.isValid() )
			{
				System.out.println( "Pending adapter disconnect." );
			}
			else
			{
				System.out.println( "test error: invalid client" );
			}
		}
		else
		{
			System.out.println( "test error: invalid server" );
		}
	}


	public static void main( String[] args )
	{
		validClientToServerTest();
	}
}