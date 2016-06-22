import java.net.*;
import java.io.*;
import javax.swing.JOptionPane;

public class NetReportStatusClient
{
	private class NetReportStatusClientMessageHandler extends BaseNetHandler
	{
		int reportID;

		public Boolean init( Socket socket )
		{
			Boolean sendResult = NetPacketSender.send( socket, NetMessageType.C2M_REPORTSTATUS_REQUEST,
				new NetPacketWriter()
				{
					public void write( DataOutputStream outStream ) throws IOException
					{
						outStream.writeInt( reportID );
					}
				}
			);

			if ( !sendResult )
			{
				System.out.println( "Error sending C2M_REPORTSTATUS_REQUEST to master server" );
				
				setErrorFlag( true );

				return false;
			}
			
			return true;
		}

		public Boolean process( NetMessageType messageType, DataInputStream inStream, final Socket messageSource ) throws IOException
		{
			System.out.println( "NetReportStatusClientMessageHandler.process( " + messageType + " )" );

			switch ( messageType )
			{
				case M2C_REPORTSTATUS:
				{
					System.out.println( "Received M2C_REPORTSTATUS" );

					Boolean validRequest = inStream.readBoolean();

					if ( validRequest )
					{
						ReportStatus reportStatus = ReportStatus.convert( inStream.readShort() );
						String reportStatusNotes = inStream.readUTF();
							
						UIManager.reportStatusWindow.processStatus( reportStatus, reportStatusNotes );
					}
					else
					{
						JOptionPane.showMessageDialog( UIManager.reportStatusWindow.mainFrame, "Invalid report ID specified." );
					}
						
					return validRequest;
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
				UIManager.reportStatusWindow.processError( "There was a connection error with the master server. Please try again later." );
		}

		public NetReportStatusClientMessageHandler( int reportID )
		{
			this.reportID = reportID;
		}
	}

	public NetClient netClient;

	public void destroy()
	{
		System.out.println( "NetChatClient.destroy" );
		
		netClient.destroy();
	}

	public NetReportStatusClient( int reportID )
	{
		netClient = new NetClient( ClientProperties.masterAddress.get(), ClientProperties.masterPort.get(), new NetReportStatusClientMessageHandler( reportID ) );
	}
}