import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;

public class UIReportStatusWindow extends UIBaseWindow
{
	private static final int WINDOW_WIDTH = 280;
	private static final int WINDOW_HEIGHT = 285;
	
	public ActiveReport report;
	private JComboBox reportStatusComboBox;
	private HintTextArea reportNotesTextArea;

	public void sendStatusUpdate()
	{
		final ReportStatus reportStatus = (ReportStatus)reportStatusComboBox.getSelectedItem();

		Boolean sendResult = NetPacketSender.send( IssueSystemManager.netMasterClient.clientSocket, NetMessageType.S2M_UPDATEREPORT_STATUS,
			new NetPacketWriter()
			{
				public void write( DataOutputStream outStream ) throws IOException
				{
					outStream.writeInt( report.reportID );
					outStream.writeShort( reportStatus.ordinal() );
					outStream.writeUTF( reportNotesTextArea.getText() );
				}
			}
		);

		if ( sendResult )
		{
			report.reportStatus = reportStatus;
			report.reportStatusNotes = reportNotesTextArea.getText();
			
			UIManager.reportManagerWindow.updateReportRowStatus( report );

			JOptionPane.showMessageDialog( mainFrame, "Status updated." );

			UIManager.reportManagerWindow.reportStatusWindows.remove( this );

			destroy();
		}
		else
		{
			JOptionPane.showMessageDialog( mainFrame, "Failed to update status." );
		}
	}

	public UIReportStatusWindow( ActiveReport report )
	{
		this.report = report;

		// Report status

		JPanel reportStatus = new JPanel( new BorderLayout() );
		reportStatus.setPreferredSize( new Dimension( WINDOW_WIDTH - 30, 25 ) );
		
		JLabel reportStatusLabel = new JLabel( "Report Status:" );
	
		reportStatusComboBox = new JComboBox();
		reportStatusComboBox.setPreferredSize( new Dimension( 150, 25 ) );

		for ( ReportStatus status : ReportStatus.values() )
			reportStatusComboBox.addItem( status );

		reportStatusComboBox.setSelectedItem( report.reportStatus );

		reportStatus.add( reportStatusLabel, BorderLayout.WEST );
		reportStatus.add( reportStatusComboBox, BorderLayout.EAST );

		// Report notes

		JLabel reportNotesLabel = new JLabel( "Notes:" );
		reportNotesLabel.setPreferredSize( new Dimension( WINDOW_WIDTH - 30, 25 ) );

		reportNotesTextArea = new HintTextArea( 11 );
		reportNotesTextArea.setLineWrap( true );
		reportNotesTextArea.setDocument( new LimitedSizeDocument( LimitConstants.MAX_REPORT_STATUS_NOTES_CHARACTERS ) );
		reportNotesTextArea.initHintText( "Enter any notes here...", report.reportStatusNotes );

		JScrollPane reportNotesTextAreaScrollPane = new JScrollPane( reportNotesTextArea );
		reportNotesTextAreaScrollPane.setPreferredSize( new Dimension( WINDOW_WIDTH - 30, 155 ) );

/*
		// Report status log

		JLabel reportStatusLogLabel = new JLabel( "Status Log:" );
		reportStatusLogLabel.setPreferredSize( new Dimension( WINDOW_WIDTH - 30, 25 ) );

		JTextArea reportStatusLogTextArea = new JTextArea( "hi" );
		reportStatusLogTextArea.setLineWrap( true );
		reportStatusLogTextArea.setText( "[01/02/03 12:00] Chris changed status from Pending to Under Investigation.\n[02/02/03 12:00] greg changed status from Under Investigation to Resolved." );
		reportStatusLogTextArea.setEditable( false );

		JScrollPane reportStatusLogTextAreaScrollPane = new JScrollPane( reportStatusLogTextArea );
		reportStatusLogTextAreaScrollPane.setPreferredSize( new Dimension( WINDOW_WIDTH - 30, 155 ) );
*/

		// Update button
		
		JButton updateButton = new JButton( "Update" );
		updateButton.setPreferredSize( new Dimension( WINDOW_WIDTH - 30, 25 ) );
		updateButton.addActionListener(
			new AbstractAction()
			{
				public void actionPerformed( ActionEvent e )
				{
					sendStatusUpdate();
				}
			}
		);
		
		// Create window

		JPanel contentPanel = new JPanel();
		contentPanel.add( reportStatus );
		contentPanel.add( reportNotesLabel );
		contentPanel.add( reportNotesTextAreaScrollPane );
		//contentPanel.add( reportStatusLogLabel );
		//contentPanel.add( reportStatusLogTextAreaScrollPane );
		contentPanel.add( updateButton );

		final UIReportStatusWindow thisWindow = this;

		SetupMainFrame( "Report #" + report.reportID + " - Status", WINDOW_WIDTH, WINDOW_HEIGHT, contentPanel,
			new WindowAdapter()
			{
				public void windowClosing( WindowEvent e )
				{
					System.out.println( "Status: windowClosing()" );

					// Remove from active window list
					UIManager.reportManagerWindow.reportStatusWindows.remove( thisWindow );

					// Destroy window
					destroy();
				}
			}
		);
	}
}