import java.net.*;
import java.io.*;
import javax.swing.JOptionPane;

public class NetChatRequestClient
{
	private class NetChatRequestClientMessageHandler extends BaseNetHandler
	{
		public Boolean init( Socket socket )
		{
			Boolean sendResult = NetPacketSender.send( socket, NetMessageType.C2M_CHATREQUEST,
				new NetPacketWriter()
				{
					public void write( DataOutputStream outStream ) throws IOException
					{
						outStream.writeUTF( System.getProperty( "user.name" ) );
					}
				}
			);

			if ( !sendResult )
			{
				System.out.println( "Error sending C2M_CHATREQUEST to master server" );
				
				setErrorFlag( true );

				return false;
			}
			
			return true;
		}

		public Boolean process( NetMessageType messageType, DataInputStream inStream, final Socket messageSource ) throws IOException
		{
			System.out.println( "NetChatRequestClientMessageHandler.process( " + messageType + " )" );

			switch ( messageType )
			{
				case M2C_CHATREQUEST_ACK:
				{
					System.out.println( "Received M2C_CHATREQUEST_ACK" );

					Boolean response = inStream.readBoolean();

					UIManager.liveChatWindow.processAck( response );

					return response;
				}
				case M2C_CHATREQUEST_RESPONSE:
				{
					String serverIP = inStream.readUTF();
					int serverPort = inStream.readInt();
					int initiationValue = inStream.readInt();

					UIManager.liveChatWindow.processResponse( serverIP, serverPort, initiationValue );

					return false;
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
			if ( getErrorFlag() )
				UIManager.liveChatWindow.processError( "There was a connection error with the master server. Please try again later." );
		}
	}

	public NetClient netClient;

	public void destroy()
	{
		System.out.println( "NetChatClient.destroy" );
		
		netClient.destroy();
	}
	
	public NetChatRequestClient()
	{
		netClient = new NetClient( ClientProperties.masterAddress.get(), ClientProperties.masterPort.get(), new NetChatRequestClientMessageHandler() );
	}
}