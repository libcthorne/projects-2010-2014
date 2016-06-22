import java.io.*;
import java.net.*;

public class NetChatServer
{
	private class NetChatServerMessageHandler extends BaseNetHandler
	{
		public Boolean init( Socket socket )
		{
			return true;
		}

		public Boolean process( NetMessageType messageType, DataInputStream inStream, final Socket messageSource ) throws IOException
		{
			System.out.println( "NetChatServerMessageHandler.process( " + messageType + " )" );

			switch ( messageType )
			{
				case C2S_CHAT_INIT:
				{
					// Client identifier
					long clientID = messageSource.hashCode();
					
					// Stream contents
					int initiationValue = inStream.readInt();
					String clientName = inStream.readUTF();

					// Verify the initiation value is valid
					if ( !ChatInitiationValues.exists( initiationValue ) )
					{
						System.out.println( "C2S_CHAT_INIT: invalid initiation value" );
					
						return false;					
					}

					// Remove initiation value
					ChatInitiationValues.invalidate( initiationValue );

					// Verify name isn't too large
					if ( clientName.length() > LimitConstants.MAX_USERNAME_LENGTH )
					{
						System.out.println( "C2S_CHAT_INIT: username too big" );
					
						return false;
					}
					
					// Send response
					Boolean sendResult = NetPacketSender.send( messageSource, NetMessageType.S2C_CHAT_INIT,
						new NetPacketWriter()
						{
							public void write( DataOutputStream outStream ) throws IOException
							{
								outStream.writeUTF( System.getProperty( "user.name" ) );
							}
						}
					);
					
					// Create chat tab on success
					if ( sendResult )
						UIManager.chatManagerWindow.createChatTab( clientID, clientName, messageSource );

					return sendResult;
				}
				case C2S_CHAT_MESSAGE:
				{
					// Client identifier
					long clientID = messageSource.hashCode();

					// Stream contents
					String chatMessage = inStream.readUTF();
					
					// Verify message isn't too large
					if ( chatMessage.length() > LimitConstants.MAX_CHAT_MESSAGE_CHARACTERS )
					{
						System.out.println( "C2S_CHAT_MESSAGE: Message received exceeds MAX_CHAT_MESSAGE_CHARACTERS" );

						return false;
					}

					System.out.println( "Received message from client " + clientID + ": " + chatMessage );

					// Find window and insert message
					for ( ActiveChatWindow chatWindow : ActiveChatWindows.activeChatWindows )
					{
						System.out.println( chatWindow.clientID + " == " + clientID );

						if ( chatWindow.clientID == clientID )
						{
							System.out.println( "Found active chat window" );
								
							chatWindow.chatPanel.receivedMessage( chatMessage );
								
							return true;
						}
					}
					
					// Terminate the connection if the ID given was invalid
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
			System.out.println( "NetChatServerMessageHandler shutdown, error = " + getErrorFlag() );
			
			if ( UIManager.chatManagerWindow != null )
				UIManager.chatManagerWindow.processDisconnect( socket.hashCode() );
		}
	}
	
	public NetServer netServer;
	
	public Boolean sendMessage( final Socket clientSocket, final String message )
	{
		System.out.println( "NetChatServer.sendMessage( " + clientSocket.hashCode() + ", " + message + ")" );

		return NetPacketSender.send( clientSocket, NetMessageType.S2C_CHAT_MESSAGE,
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
		System.out.println( "NetChatServer.destroy" );
		
		for ( ActiveChatWindow chatWindow : ActiveChatWindows.activeChatWindows )
		{
			try
			{
				chatWindow.clientSocket.close();
			}
			catch ( IOException e )
			{
				System.out.println( "Failed closing NetChatServer clientSocket" );
			}
		}
		
		ActiveChatWindows.activeChatWindows.clear();
		
		netServer.destroy();
	}
	
	public NetChatServer()
	{
		netServer = new NetServer( ServerProperties.chatHostAddress.get(), ServerProperties.chatHostPort.get(), new NetChatServerMessageHandler() );
	}
}