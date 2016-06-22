import java.io.*;
import java.net.*;
import javax.swing.JOptionPane;

public class NetServerMessageHandler extends BaseNetHandler
{
	public Boolean init( Socket socket )
	{		
		return NetPacketSender.send( socket, NetMessageType.S2M_REGISTER,
			new NetPacketWriter()
			{
				public void write( DataOutputStream outStream ) throws IOException
				{
					outStream.writeUTF( ServerProperties.serverPassword.get() );
				}
			}
		);
	}

	public Boolean process( NetMessageType messageType, DataInputStream inStream, final Socket messageSource ) throws IOException
	{
		System.out.println( "NetServerMessageHandler.process: " + messageType.toString() );

		switch ( messageType )
		{
			case M2S_CHATREQUEST_CREATE:
			{
				String clientInfo = inStream.readUTF();
				System.out.print( "clientInfo=" + clientInfo + ", " );
				long clientID = inStream.readLong();
				System.out.println( "clientID=" + clientID );

				PendingChatRequests.add( clientInfo, clientID );

				return true;
			}
			case M2S_CHATREQUEST_REMOVE:
			{
				long clientID = inStream.readLong();
				System.out.println( "clientID=" + clientID );
					
				Boolean result = PendingChatRequests.removeByID( clientID );

				System.out.println( "PendingChatRequests.removeByID: " + result );

				return true;
			}
			case M2S_CREATEREPORT:
			{
				ActiveReport report = ActiveReports.add(
					// Report ID
					inStream.readInt(),
					// Report time
					inStream.readUTF(),
					// Reporter user name
					inStream.readUTF(),
					// Report room number
					inStream.readShort(),
					// Report machine number
					inStream.readShort(),
					// Report problem type
					ReportProblemType.convert( inStream.readShort() ),
					// Additional details
					inStream.readUTF(),
					// Report status
					ReportStatus.convert( inStream.readShort() ),
					// Report status notes
					inStream.readUTF()
				);
					
				if ( report != null && UIManager.reportManagerWindow != null )
					UIManager.reportManagerWindow.addFilteredReportRow( report );

				return report != null;
			}
			case M2S_UPDATEREPORT_STATUS:
			{
				int reportID = inStream.readInt();
				ReportStatus reportStatus = ReportStatus.convert( inStream.readShort() );
				String reportStatusNotes = inStream.readUTF();
					
				ActiveReport report = ActiveReports.getReportFromID( reportID );
					
				// Invalid report ID
				if ( report == null )
				{
					System.out.println( "M2S_UPDATEREPORT_STATUS: null report" );
					
					return false;
				}
				
				// Update report
				report.reportStatus = reportStatus;
				report.reportStatusNotes = reportStatusNotes;

				// Update report manager if open
				if ( UIManager.reportManagerWindow != null )
				{
					UIManager.reportManagerWindow.updateReportRowStatus( report );

					for ( UIReportStatusWindow window : UIManager.reportManagerWindow.reportStatusWindows )
					{
						if ( window.report.reportID == report.reportID )
						{
							JOptionPane.showMessageDialog( window.mainFrame, "Warning! Another technician has just made changes to the status of this report.\nSaving your own changes will overwrite their's.\nYou are advised to open a new status window to view the changes made before you update." );
							
							break;
						}
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

	public void shutdown( Socket socket )
	{
		JOptionPane.showMessageDialog( null, "Connection error with master server. Shutting down." );

		System.exit( 0 );
	}
}