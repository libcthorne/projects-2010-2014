import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;
import javax.swing.border.*;

public class UIReportStatusWindow extends UIBaseWindow
{
	private static final int WINDOW_WIDTH = 280;
	private static final int WINDOW_HEIGHT = 305;
	
	private JLabel reportStatusLabel;
	private JLabel reportNotesLabel;
	private JTextArea reportNotesTextArea;
	
	public void processStatus( ReportStatus status, String notes )
	{
		reportStatusLabel.setEnabled( true );
		reportNotesLabel.setEnabled( true );

		reportStatusLabel.setText( "Report Status: " + status );
		
		if ( notes.length() == 0 )
		{
			reportNotesTextArea.setEnabled( false );
			reportNotesTextArea.setText( "N/A" );
		}
		else
		{
			reportNotesTextArea.setEnabled( true );
			reportNotesTextArea.setText( notes );
		}
	}

	public void destroy()
	{
		UIManager.reportStatusWindow = null;
		
		super.destroy();
	}

	public UIReportStatusWindow()
	{
		final UIReportStatusWindow thisWindow = this;

		// Report ID selector
		
		JPanel reportSelector = new JPanel( new FlowLayout() );
		
		JLabel reportIDLabel = new JLabel( "Report ID:" );
		reportIDLabel.setPreferredSize( new Dimension( 60, 25 ) );

		final NumberTextField reportIDField = new NumberTextField( 10 );
		reportIDField.setPreferredSize( new Dimension( 100, 25 ) );

		JButton reportIDViewButton = new JButton( "View" );
		reportIDViewButton.setPreferredSize( new Dimension( 80, 25 ) );
		reportIDViewButton.addActionListener(
			new AbstractAction()
			{
				public void actionPerformed( ActionEvent event )
				{
					int reportID = reportIDField.getIntegerValue();

					System.out.println( "View: " + reportID );
					
					if ( reportID == -1 )
					{
						JOptionPane.showMessageDialog( thisWindow.mainFrame, "Please enter a report ID." );
						
						return;
					}
					
					NetReportStatusClient netReportStatusClient = new NetReportStatusClient( reportID );
				}
			}	
		);

		reportSelector.add( reportIDLabel );
		reportSelector.add( reportIDField );
		reportSelector.add( reportIDViewButton );

		// 1px black line separator

		JPanel lineSeparator = new JPanel();
		lineSeparator.setPreferredSize( new Dimension( WINDOW_WIDTH, 1 ) );
		lineSeparator.setBorder( BorderFactory.createLineBorder( Color.LIGHT_GRAY, 1 ) );

		// Report status

		JPanel reportStatus = new JPanel( new BorderLayout() );
		reportStatus.setPreferredSize( new Dimension( WINDOW_WIDTH - 30, 25 ) );

		reportStatusLabel = new JLabel( "Report Status:" );
		reportStatusLabel.setEnabled( false );

		reportStatus.add( reportStatusLabel, BorderLayout.WEST );

		// Report notes

		reportNotesLabel = new JLabel( "Report Notes:" );
		reportNotesLabel.setPreferredSize( new Dimension( WINDOW_WIDTH - 30, 25 ) );
		reportNotesLabel.setEnabled( false );

		reportNotesTextArea = new JTextArea();
		reportNotesTextArea.setFont( new Font( reportNotesTextArea.getFont().getName(), Font.PLAIN, 11 ) );
		reportNotesTextArea.setLineWrap( true );
		reportNotesTextArea.setEditable( false );
		reportNotesTextArea.setEnabled( false );

		JScrollPane reportNotesTextAreaScrollPane = new JScrollPane( reportNotesTextArea );
		reportNotesTextAreaScrollPane.setPreferredSize( new Dimension( WINDOW_WIDTH - 30, 155 ) );

		// Create window

		JPanel contentPanel = new JPanel();
		contentPanel.add( reportSelector );
		contentPanel.add( lineSeparator );
		contentPanel.add( reportStatus );
		contentPanel.add( reportNotesLabel );
		contentPanel.add( reportNotesTextAreaScrollPane );

		SetupMainFrame( "Report Status", WINDOW_WIDTH, WINDOW_HEIGHT, contentPanel,
			new WindowAdapter()
			{
				public void windowClosing( WindowEvent e )
				{
					System.out.println( "Status: windowClosing()" );

					destroy();
				}
			}
		);
	}
}