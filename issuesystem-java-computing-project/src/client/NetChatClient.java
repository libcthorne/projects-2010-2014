import java.net.*;
import java.io.*;
import javax.swing.JOptionPane;

public class NetChatClient
{
	private class NetChatClientMessageHandler extends BaseNetHandler
	{
		public Boolean init( Socket socket )
		{
			return NetPacketSender.send( socket, NetMessageType.C2S_CHAT_INIT,
				new NetPacketWriter()
				{
					public void write( DataOutputStream outStream ) throws IOException
					{
						outStream.writeInt( initiationValue );
						outStream.writeUTF( System.getProperty( "user.name" ) );
					}
				}
			);
		}
		
		public Boolean process( NetMessageType messageType, DataInputStream inStream, final Socket messageSource ) throws IOException
		{
			System.out.println( "NetChatClientMessageHandler.process( " + messageType + " )" );

			switch ( messageType )
			{
				case S2C_CHAT_INIT:
				{
					String technicianName = inStream.readUTF();
						
					// Verify technician name isn't too large
					if ( technicianName.length() > LimitConstants.MAX_USERNAME_LENGTH )
					{
						System.out.println( "S2C_CHAT_INIT: username too big" );
						
						return false;
					}

					// Create chat window
					UIManager.liveChatWindow.mainFrame.dispose();
					UIManager.liveChatWindow.createChat( technicianName );
							
					return true;
				}
				case S2C_CHAT_MESSAGE:
				{
					System.out.println( "Received S2C_CHAT_MESSAGE" );
						
					String chatMessage = inStream.readUTF();

					if ( chatMessage.length() > LimitConstants.MAX_CHAT_MESSAGE_CHARACTERS )
					{
						System.out.println( "S2C_CHAT_MESSAGE: message too big" );
						
						return false;
					}

					System.out.println( "chatMessage=" + chatMessage );

					UIManager.liveChatWindow.processMessage( chatMessage );
						
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
			if ( UIManager.liveChatWindow != null )
				UIManager.liveChatWindow.processDisconnect( getErrorFlag() );
		}
	}

	public NetClient netClient;
	private int initiationValue;

	public Boolean sendMessage( final String message )
	{
		return NetPacketSender.send( netClient.clientSocket, NetMessageType.C2S_CHAT_MESSAGE,
			new NetPacketWriter()
			{
				public void write( DataOutputStream outStream ) throws IOException
				{
					outStream.writeUTF( message );
				}
			}
		);
	}
	
	public void destroy()
	{
		System.out.println( "NetChatClient.destroy" );
		
		netClient.destroy();
	}
	
	public NetChatClient( String serverIP, int serverPort, int initiationValue )
	{
		this.initiationValue = initiationValue;

		System.out.println( "Initiating chat with " + serverIP + ":" + serverPort );

		netClient = new NetClient( serverIP, serverPort, new NetChatClientMessageHandler() );

		System.out.println( "Initiated chat with " + serverIP + ":" + serverPort );
	}
}