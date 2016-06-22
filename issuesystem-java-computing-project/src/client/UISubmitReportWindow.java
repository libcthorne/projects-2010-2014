import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.*;

import java.awt.*;
import java.awt.event.*;

import java.net.*;
import java.io.*;

public class UISubmitReportWindow extends UIBaseWindow
{
	private class NetSubmitReportMessageHandler extends BaseNetHandler
	{
		public Boolean init( Socket socket )
		{
			return true;
		}

		public Boolean process( NetMessageType messageType, DataInputStream inStream, final Socket messageSource ) throws IOException
		{
			switch ( messageType )
			{
				case M2C_SUBMITREPORT_ACK:
				{
					Boolean success = inStream.readBoolean();

					System.out.println( "M2C_SUBMITREPORT_ACK, success = " + success );

					if ( success )
					{
						int reportID = inStream.readInt();

						JOptionPane.showMessageDialog( UIManager.submitReportWindow.mainFrame, "Report sent successfully! Your report ID is " + reportID + ".\nWrite this down if you wish to track the status of your report." );
						
						UIManager.submitReportWindow.destroy();
					}
					else
					{
						JOptionPane.showMessageDialog( UIManager.submitReportWindow.mainFrame, "Your report could not be saved. Please try again later." );
					}

					// Terminate connection in all cases
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
				JOptionPane.showMessageDialog( UIManager.submitReportWindow.mainFrame, "There was a connection error with the master server. Please try again later." );
		}
	}

	private static final int WINDOW_WIDTH = 320;
	private static final int WINDOW_HEIGHT = 314;

	public JComboBox problemTypeComboBox;
	public NumberTextField roomNumberTextField;
	public NumberTextField machineNumberTextField;
	public HintTextArea additionalDetailsTextArea;	

	public void sendReport()
	{
		if ( roomNumberTextField.getText().length() <= 0 )
		{
			JOptionPane.showMessageDialog( mainFrame, "Please enter a room number." );

			return;
		}

		if ( machineNumberTextField.getText().length() <= 0 )
		{
			JOptionPane.showMessageDialog( mainFrame, "Please enter a machine number." );

			return;
		}

		final String userName = System.getProperty( "user.name" );
		final short roomNumber = (short)roomNumberTextField.getIntegerValue();
		final short machineNumber = (short)machineNumberTextField.getIntegerValue();
		final ReportProblemType problemType = (ReportProblemType)problemTypeComboBox.getSelectedItem();
		final String additionalDetails = additionalDetailsTextArea.getText();
	
		System.out.println( "User name: " + userName );
		System.out.println( "Room number: " + roomNumber );
		System.out.println( "Machine number: " + machineNumber );
		System.out.println( "Problem type: " + problemType.toString() );
		System.out.println( "Additional details: " + additionalDetails );

		NetClient netClient = new NetClient( ClientProperties.masterAddress.get(), ClientProperties.masterPort.get(), new NetSubmitReportMessageHandler() );
			
		if ( netClient.isValid() )
		{
			System.out.println( "Connected to master server." );
				
			Boolean sendResult = NetPacketSender.send( netClient.clientSocket, NetMessageType.C2M_SUBMITREPORT,
				new NetPacketWriter()
				{
					public void write( DataOutputStream outStream ) throws IOException
					{
						outStream.writeUTF( userName );
						outStream.writeShort( roomNumber );
						outStream.writeShort( machineNumber );
						outStream.writeShort( problemType.ordinal() );
						outStream.writeUTF( additionalDetails );
					}
				}
			);

			if ( !sendResult )
			{
				System.out.println( "Error sending C2M_SUBMITREPORT to master server." );
				JOptionPane.showMessageDialog( mainFrame, "Error sending report." );
			}
		}
		else
		{
			System.out.println( "Failed to connect to master server." );
			netClient.destroy();
		}	
	}

	public void destroy()
	{
		System.out.println( "UISubmitReportWindow.destroy" );

		UIManager.submitReportWindow = null;

		super.destroy();
	}

	public UISubmitReportWindow()
	{
		// Room number input

		JLabel roomNumberLabel = new JLabel( "Room Number:" );
		roomNumberLabel.setPreferredSize( new Dimension( 100, 25 ) );

		roomNumberTextField = new NumberTextField( 4 );
		roomNumberTextField.setPreferredSize( new Dimension( 75, 25 ) );

		JButton roomNumberButton = new JButton( "Use Current" );
		roomNumberButton.setFocusPainted( false );
		roomNumberButton.setPreferredSize( new Dimension( 105, 25 ) );
		
		if ( ClientProperties.roomNumber.get() == null )
		{
			roomNumberButton.setEnabled( false );
		}
		else
		{
			roomNumberButton.addActionListener(
				new AbstractAction()
				{
					public void actionPerformed( ActionEvent event )
					{
						roomNumberTextField.setText( ClientProperties.roomNumber.get().toString() );
					}
				}
			);
		}

		// Machine number input

		JLabel machineNumberLabel = new JLabel( "Machine Number:" );
		machineNumberLabel.setPreferredSize( new Dimension( 100, 25 ) );

		machineNumberTextField = new NumberTextField( 4 );
		machineNumberTextField.setPreferredSize( new Dimension( 75, 25 ) );

		JButton machineNumberButton = new JButton( "Use Current" );
		machineNumberButton.setFocusPainted( false );
		machineNumberButton.setPreferredSize( new Dimension( 105, 25 ) );

		if ( ClientProperties.machineNumber.get() == null )
		{
			machineNumberButton.setEnabled( false );
		}
		else
		{
			machineNumberButton.addActionListener(
				new AbstractAction()
				{
					public void actionPerformed( ActionEvent event )
					{
						machineNumberTextField.setText( ClientProperties.machineNumber.get().toString() );
					}
				}
			);
		}

		// Additional details input

		additionalDetailsTextArea = new HintTextArea( 11 );
		additionalDetailsTextArea.setLineWrap( true );
		additionalDetailsTextArea.setDocument( new LimitedSizeDocument( LimitConstants.MAX_ADDITIONAL_DETAILS_CHARACTERS ) );
		additionalDetailsTextArea.initHintText( "Enter any additional details here...", "" );

		JScrollPane additionalDetailsTextAreaScrollPane = new JScrollPane( additionalDetailsTextArea );

		// Send button

		JButton sendButton = new JButton( "Send Report" );
		sendButton.setFocusPainted( false );
		sendButton.addActionListener(
			new AbstractAction()
			{
				public void actionPerformed( ActionEvent e )
				{
					System.out.println( "sendButton.actionPerformed" );

					sendReport();
				}
			}
		);

		// Problem type input
		
		JLabel problemTypeLabel = new JLabel( "Problem Type: " );
		problemTypeLabel.setPreferredSize( new Dimension( 100, 25 ) );
		problemTypeComboBox = new JComboBox();
		
		for ( ReportProblemType problemType : ReportProblemType.values() )
		{
			problemTypeComboBox.addItem( problemType );
		}

		// Layout

		JPanel infoGridPanel = new JPanel( new GridBagLayout() );
		GridBagConstraints infoGridBagConstraints = new GridBagConstraints();

		infoGridBagConstraints.insets = new Insets( 5, 0, 0, 5 );
		infoGridBagConstraints.gridx = 0;
		infoGridBagConstraints.gridy = 0;
		infoGridPanel.add( roomNumberLabel, infoGridBagConstraints );
		
		infoGridBagConstraints.insets = new Insets( 5, 0, 0, 5 );
		infoGridBagConstraints.gridx = 1;
		infoGridBagConstraints.gridy = 0;
		infoGridPanel.add( roomNumberTextField, infoGridBagConstraints );
		
		infoGridBagConstraints.insets = new Insets( 5, 0, 0, 0 );
		infoGridBagConstraints.gridx = 2;
		infoGridBagConstraints.gridy = 0;
		infoGridPanel.add( roomNumberButton, infoGridBagConstraints );
		
		infoGridBagConstraints.insets = new Insets( 10, 0, 0, 5 );
		infoGridBagConstraints.gridx = 0;
		infoGridBagConstraints.gridy = 1;
		infoGridPanel.add( machineNumberLabel, infoGridBagConstraints );
		
		infoGridBagConstraints.insets = new Insets( 10, 0, 0, 5 );
		infoGridBagConstraints.gridx = 1;
		infoGridBagConstraints.gridy = 1;
		infoGridPanel.add( machineNumberTextField, infoGridBagConstraints );
		
		infoGridBagConstraints.insets = new Insets( 10, 0, 0, 0 );
		infoGridBagConstraints.gridx = 2;
		infoGridBagConstraints.gridy = 1;
		infoGridPanel.add( machineNumberButton, infoGridBagConstraints );
		
		infoGridBagConstraints.insets = new Insets( 10, 0, 0, 0 );
		infoGridBagConstraints.gridx = 0;
		infoGridBagConstraints.gridy = 2;
		infoGridPanel.add( problemTypeLabel, infoGridBagConstraints );

		infoGridBagConstraints.insets = new Insets( 10, 0, 0, 0 );
		infoGridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
		infoGridBagConstraints.gridwidth = 2;
		infoGridBagConstraints.gridx = 1;
		infoGridBagConstraints.gridy = 2;
		infoGridPanel.add( problemTypeComboBox, infoGridBagConstraints );

		infoGridBagConstraints.insets = new Insets( 10, 0, 0, 0 );
		infoGridBagConstraints.fill = GridBagConstraints.HORIZONTAL	;
		infoGridBagConstraints.gridwidth = 3;
		infoGridBagConstraints.ipady = 100;
		infoGridBagConstraints.gridx = 0;
		infoGridBagConstraints.gridy = 3;
		infoGridPanel.add( additionalDetailsTextAreaScrollPane, infoGridBagConstraints );

		infoGridBagConstraints.insets = new Insets( 10, 0, 0, 0 );
		infoGridBagConstraints.gridwidth = 3;
		infoGridBagConstraints.ipady = 5;
		infoGridBagConstraints.gridx = 0;
		infoGridBagConstraints.gridy = 5;
		infoGridPanel.add( sendButton, infoGridBagConstraints );
		
		// Create window

		JPanel contentPanel = new JPanel();
		contentPanel.add( infoGridPanel );
		SetupMainFrame( "Submit Report", WINDOW_WIDTH, WINDOW_HEIGHT, contentPanel,
			new WindowAdapter()
			{
				public void windowClosing( WindowEvent e )
				{
					System.out.println( "SubmitReportWindowListener.windowClosing" );

					destroy();
				}
			}
		);
	}
}