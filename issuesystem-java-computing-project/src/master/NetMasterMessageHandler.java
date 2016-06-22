import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Date;
import java.text.SimpleDateFormat;

public class NetMasterMessageHandler extends BaseNetHandler
{
	// Pending net chat client descriptor

	class PendingNetChatClient
	{
		public String userName;
		public Socket socket;
	}

	// List of pending net chat clients
	private ArrayList<PendingNetChatClient> pendingNetChatClients = new ArrayList<PendingNetChatClient>( LimitConstants.MAX_PENDING_CHAT_REQUESTS );
	// List of connected server sockets
	private ArrayList<Socket> netServerRegister = new ArrayList<Socket>( LimitConstants.MASTER_MAX_SERVER_REGISTER_SIZE );

	// Creates a packet writer for the specified ActiveReport object
	// Used by sendReportToServer(s) as a helper
	private NetPacketWriter newReportPacketWriter( final ActiveReport report )
	{
		return new NetPacketWriter()
		{
			public void write( DataOutputStream outStream ) throws IOException
			{
				outStream.writeInt( report.reportID );
				outStream.writeUTF( report.reportTime );
				outStream.writeUTF( report.userName );
				outStream.writeShort( report.roomNumber );
				outStream.writeShort( report.machineNumber );
				outStream.writeShort( report.problemType.ordinal() );
				outStream.writeUTF( report.additionalDetails );
				outStream.writeShort( report.reportStatus.ordinal() );
				outStream.writeUTF( report.reportStatusNotes );
			}
		};	
	}

	// Sends a report to a specific server
	private Boolean sendReportToServer( ActiveReport report, Socket serverSocket )
	{
		return NetPacketSender.send( serverSocket, NetMessageType.M2S_CREATEREPORT, newReportPacketWriter( report ) );
	}

	// Sends a report to all registered servers
	private void sendReportToServers( ActiveReport report )
	{
		NetPacketWriter netServerPacketWriter = newReportPacketWriter( report );

		for ( Socket serverSocket : netServerRegister )
		{
			// If the server can't be reached, remove it from the register
			if ( !NetPacketSender.send( serverSocket, NetMessageType.M2S_CREATEREPORT, netServerPacketWriter ) )
				netServerRegister.remove( serverSocket );
		}
	}
	
	// Creates a packet writer for the specified PendingNetChatClient object
	// Used by sendChatRequestToServer(s) as a helper
	private NetPacketWriter newChatRequestPacketWriter( final PendingNetChatClient pncc )
	{
		return new NetPacketWriter()
		{
			public void write( DataOutputStream outStream ) throws IOException
			{
				// User identification string ("username, ip:port")
				outStream.writeUTF( pncc.userName + ", " + pncc.socket.getInetAddress().getHostAddress() + ":" + pncc.socket.getPort() );
				// User identification value
				outStream.writeLong( pncc.socket.hashCode() );
			}
		};
	}

	// Sends a chat request to a specific server
	private Boolean sendChatRequestToServer( PendingNetChatClient pncc, Socket serverSocket )
	{
		return NetPacketSender.send( serverSocket, NetMessageType.M2S_CHATREQUEST_CREATE, newChatRequestPacketWriter( pncc ) );
	}
	
	// Sends a chat request info to all registered servers
	private void sendChatRequestToServers( PendingNetChatClient pncc )
	{
		NetPacketWriter netServerPacketWriter = newChatRequestPacketWriter( pncc );

		for ( Socket serverSocket : netServerRegister )
		{
			// If the server can't be reached, remove it from the register
			if ( !NetPacketSender.send( serverSocket, NetMessageType.M2S_CHATREQUEST_CREATE, netServerPacketWriter ) )
				netServerRegister.remove( serverSocket );
		}
	}

	// Initialisation callback
	public Boolean init( Socket socket )
	{
		return true;
	}

	// Message processing callback
	public Boolean process( NetMessageType messageType, DataInputStream inStream, final Socket messageSource ) throws IOException
	{
		System.out.println( "NetMasterMessageHandler.process( " + messageType + " )" );

		switch ( messageType )
		{
			case S2M_REGISTER:
			{
				// Password specified by the server application
				String password = inStream.readUTF();
				System.out.println( "password = " + password + ", serverPassword = " + MasterProperties.serverPassword );

				// Compare networked password with local password
				if ( !password.equals( MasterProperties.serverPassword.get() ) )
				{
					System.out.println( "serverPassword != password" );

					return false;
				}

				// Add server to register
				Boolean result = netServerRegister.add( messageSource );
				System.out.println( "netServerRegister.add: " + result );

				if ( result )
				{
					// Send pending chat requests
					for ( PendingNetChatClient pendingNetChatClient : pendingNetChatClients )
					{
						System.out.println( "Sending server M2S_CHATREQUEST_CREATE for client " + pendingNetChatClient.socket.hashCode() );
						
						if ( !sendChatRequestToServer( pendingNetChatClient, messageSource ) )
							return false;
					}
					
					// Send reports
					for ( ActiveReport report : ActiveReports.list )
					{
						if ( !sendReportToServer( report, messageSource ) )
							return false;
					}
				}
				
				// Terminate connection if the server was not registered
				return result;
			}
			case C2M_CHATREQUEST:
			{				
				System.out.println( "(" + messageSource.toString() + ") Received chat request" );
				System.out.println( "Number of registered servers: " + netServerRegister.size() );

				if ( pendingNetChatClients.size() < LimitConstants.MAX_PENDING_CHAT_REQUESTS )
				{
					String userName = inStream.readUTF();
						
					if ( userName.length() > LimitConstants.MAX_USERNAME_LENGTH )
					{
						System.out.println( "C2M_CHATREQUEST: username too big" );
					
						return false;
					}

					// Store client request
					final PendingNetChatClient pncc = new PendingNetChatClient();
					pncc.userName = userName;
					pncc.socket = messageSource;
					pendingNetChatClients.add( pncc );

					// Broadcast client request
					sendChatRequestToServers( pncc );

					return NetPacketSender.send( messageSource, NetMessageType.M2C_CHATREQUEST_ACK,
						new NetPacketWriter()
						{
							public void write( DataOutputStream outStream ) throws IOException
							{
								outStream.writeBoolean( true );
							}
						}
					);
				}
				else
				{
					return NetPacketSender.send( messageSource, NetMessageType.M2C_CHATREQUEST_ACK,
						new NetPacketWriter()
						{
							public void write( DataOutputStream outStream ) throws IOException
							{
								outStream.writeBoolean( false );
							}
						}
					);
				}
			}
			case C2M_SUBMITREPORT:
			{
				System.out.println( "Received C2M_SUBMITREPORT" );

				// Networked report data

				final String userName = inStream.readUTF();
					
				if ( userName.length() > LimitConstants.MAX_USERNAME_LENGTH )
				{
					System.out.println( "C2M_SUBMITREPORT: username too big" );
						
					return false;
				}
					
				final short roomNumber = inStream.readShort();
				final short machineNumber = inStream.readShort();
				final ReportProblemType problemType = ReportProblemType.convert( inStream.readShort() );

				final String additionalDetails = inStream.readUTF();
					
				if ( additionalDetails.length() > LimitConstants.MAX_ADDITIONAL_DETAILS_CHARACTERS )
				{
					System.out.println( "C2M_SUBMITREPORT: additional details too big" );
						
					return false;
				}

				// Server report data

				final int reportID = ActiveReports.getFreeReportID();
				final String reportTime = new SimpleDateFormat( "yyyy/MM/dd HH:mm:ss" ).format( new Date() );

				final ActiveReport report = ActiveReports.add(
					reportID,
					reportTime,
					userName,
					roomNumber,
					machineNumber,
					problemType,
					additionalDetails,
					ReportStatus.PENDING,
					""
				);
					
				// Create report file
				final Boolean saveResult = ReportFile.save( report );

				// Remove from active list if the file failed to save
				if ( !saveResult )
					ActiveReports.remove( report );

				Boolean clientSendResult = NetPacketSender.send( messageSource, NetMessageType.M2C_SUBMITREPORT_ACK,
					new NetPacketWriter()
					{
						public void write( DataOutputStream outStream ) throws IOException
						{
							outStream.writeBoolean( saveResult );
							
							if ( saveResult )
								outStream.writeInt( reportID );
						}
					}
				);

				// Forward report to servers if the file was successfully created
				if ( saveResult )
				{
					System.out.println( "Forwarding report to " + netServerRegister.size() + " servers" );

					sendReportToServers( report );
				}

				return clientSendResult;
			}
			case C2M_REPORTSTATUS_REQUEST:
			{
				int reportID = inStream.readInt();
					
				System.out.println( "C2M_REPORTSTATUS_REQUEST: " + reportID );
					
				final ActiveReport report = ActiveReports.getReportFromID( reportID );

				if ( report != null )
				{
					NetPacketSender.send( messageSource, NetMessageType.M2C_REPORTSTATUS,
						new NetPacketWriter()
						{
							public void write( DataOutputStream outStream ) throws IOException
							{
								outStream.writeBoolean( true );
								outStream.writeShort( report.reportStatus.ordinal() );
								outStream.writeUTF( report.reportStatusNotes );
							}
						}
					);					
				}
				else
				{
					NetPacketSender.send( messageSource, NetMessageType.M2C_REPORTSTATUS,
						new NetPacketWriter()
						{
							public void write( DataOutputStream outStream ) throws IOException
							{
								outStream.writeBoolean( false );
							}
						}
					);
				}
					
				return true;
			}
			case S2M_CHATREQUEST_RESPONSE:
			{
				long clientID = inStream.readLong();
				final int chatServerPort = inStream.readInt();
				final int initiationValue = inStream.readInt();

				System.out.println( "Server responded to client with ID " + clientID );

				for ( PendingNetChatClient client : pendingNetChatClients )
				{
					System.out.println( "Pending client = " + client.socket.getInetAddress().getHostAddress() + ":" + client.socket.getPort() );

					if ( client.socket.hashCode() == clientID )
					{
						NetPacketSender.send( client.socket, NetMessageType.M2C_CHATREQUEST_RESPONSE,
							new NetPacketWriter()
							{
								public void write( DataOutputStream outStream ) throws IOException
								{
									outStream.writeUTF( messageSource.getInetAddress().getHostAddress() );
									outStream.writeInt( chatServerPort );
									outStream.writeInt( initiationValue );
								}
							}
						);

						return true;
					}
				}
				
				// If this point is reached, it means the chat request was not found,
				// however, this does not warrant terminating the connection.

				System.out.println( "Received unknown chat response" );

				return true;
			}
			case S2M_UPDATEREPORT_STATUS:
			{
				int reportID = inStream.readInt();
				ReportStatus reportStatus = ReportStatus.convert( inStream.readShort() );
				String reportStatusNotes = inStream.readUTF();
					
				if ( reportStatusNotes.length() > LimitConstants.MAX_REPORT_STATUS_NOTES_CHARACTERS )
				{
					System.out.println( "S2M_UPDATEREPORT_STATUS: notes too big" );
					
					return false;
				}

				System.out.println( "S2M_UPDATEREPORT_STATUS: " + reportID + ", " + reportStatus.toString() + ", " + reportStatusNotes );

				final ActiveReport report = ActiveReports.getReportFromID( reportID );

				// Invalid report ID
				if ( report == null )
				{
					System.out.println( "S2M_UPDATEREPORT_STATUS: invalid report ID" );
						
					return false;
				}

				report.reportStatus = reportStatus;
				report.reportStatusNotes = reportStatusNotes;

				// Update report file
				ReportFile.save( report );

				NetPacketWriter netServerPacketWriter = new NetPacketWriter()
				{
					public void write( DataOutputStream outStream ) throws IOException
					{
						outStream.writeInt( report.reportID );
						outStream.writeShort( report.reportStatus.ordinal() );
						outStream.writeUTF( report.reportStatusNotes );
					}
				};
					
				for ( Socket serverSocket : netServerRegister )
				{
					if ( serverSocket != messageSource )
					{
						// If the server can't be reached, remove it from the register
						if ( !NetPacketSender.send( serverSocket, NetMessageType.M2S_UPDATEREPORT_STATUS, netServerPacketWriter ) )
							netServerRegister.remove( serverSocket );
					}
				}

				return true;
			}
			default:
			{
				setErrorFlag( true );

				return false;
			}
		}
	}

	// Shutdown callback.
	public void shutdown( final Socket socket )
	{
		System.out.println( "Master server handler shutting down" );

		Boolean serverRegisterResult = netServerRegister.remove( socket );
		System.out.println( "Removing registered server: " + serverRegisterResult );
		
		// If the socket was a registered server then there's no point
		// in checking if it was a pending net chat client.
		if ( serverRegisterResult )
			return;
			
		Boolean pendingClientResult = false;

		for ( PendingNetChatClient pncc : pendingNetChatClients )
		{
			if ( pncc.socket == socket )
			{
				pendingNetChatClients.remove( pncc );
				pendingClientResult = true;
				break;
			}
		}
		
		System.out.println( "Removing pending net chat client: " + pendingClientResult );

		if ( pendingClientResult )
		{
			System.out.println( "Informing servers of cancelled pending chat request (registered count=" + netServerRegister.size() + ")" );

			NetPacketWriter netPacketWriter = new NetPacketWriter()
			{
				public void write( DataOutputStream outStream ) throws IOException
				{
					outStream.writeLong( socket.hashCode() );
				}
			};

			for ( Socket serverSocket : netServerRegister )
			{
				if ( !NetPacketSender.send( serverSocket, NetMessageType.M2S_CHATREQUEST_REMOVE, netPacketWriter ) )
					netServerRegister.remove( serverSocket );
			}
		}
	}
}