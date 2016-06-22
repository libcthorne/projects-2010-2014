import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.border.*;

public class UIChatManagerWindow extends UIBaseWindow
{
	private static final int WINDOW_WIDTH = 480;
	private static final int WINDOW_HEIGHT = 440;
	
	private JPanel chatRequests;
	private JTabbedPane tabbedChatPane;
	private NetChatServer netChatServer;

	private class ChatManagerWindowListener extends WindowAdapter
	{
		public void windowClosing( WindowEvent e )
		{
			System.out.println( "ChatManagerWindowListener.windowClosing" );

			UIManager.chatManagerWindow.destroy();
		}
	}

	private class AcceptChatRequestButtonPressed extends AbstractAction
	{
		private NetChatRequest netChatRequest;

		public void actionPerformed( ActionEvent event )
		{
			System.out.println( "Sending response for request " + netChatRequest.id );

			NetPacketSender.send( IssueSystemManager.netMasterClient.clientSocket, NetMessageType.S2M_CHATREQUEST_RESPONSE,
				new NetPacketWriter()
				{
					public void write( DataOutputStream outStream ) throws IOException
					{
						outStream.writeLong( netChatRequest.id );
						outStream.writeInt( ServerProperties.chatHostPort.get() );
						outStream.writeInt( ChatInitiationValues.generate() );
					}
				}
			);
		}

		public AcceptChatRequestButtonPressed( NetChatRequest netChatRequest )
		{
			this.netChatRequest = netChatRequest;
		}
	}

	public void reloadChatRequests()
	{
		System.out.println( "reloadChatRequests()" );

		chatRequests.removeAll();

		if ( PendingChatRequests.pendingChatRequests.size() > 0 )
		{
			for ( NetChatRequest netChatRequest : PendingChatRequests.pendingChatRequests )
			{
				JPanel chatRequest = new JPanel( new BorderLayout() );
				chatRequest.setPreferredSize( new Dimension( 360, 25 ) );

				JLabel chatRequestText = new JLabel( netChatRequest.name );
				chatRequestText.setPreferredSize( new Dimension( 200, 20 ) );
				chatRequestText.setBorder( new EmptyBorder( 5, 10, 0, 0 ) );

				JPanel chatRequestButtons = new JPanel( new FlowLayout() );
				chatRequestButtons.setBorder( new EmptyBorder( 0, 0, 0, 12 ) );
				JButton chatRequestAccept = new JButton( "Accept" );
				chatRequestAccept.setPreferredSize( new Dimension( 120, 20 ) );
				chatRequestAccept.addActionListener( new AcceptChatRequestButtonPressed( netChatRequest ) );
				
				chatRequestButtons.add( chatRequestAccept );

				chatRequest.add( chatRequestText, BorderLayout.WEST );
				chatRequest.add( chatRequestButtons, BorderLayout.EAST );

				chatRequests.add( chatRequest );
			}
		}
		else
		{
			JLabel noChatRequestsLabel = new JLabel( "There are currently no pending chat requests." );
			chatRequests.add( noChatRequestsLabel );
		}

		chatRequests.revalidate();
	}

	public void createChatTab( long clientID, String clientInfo, final Socket clientSocket )
	{
		Dimension tabbedChatPaneSize = tabbedChatPane.getPreferredSize();
		int chatWindowWidth = tabbedChatPaneSize.width - 30;
		int chatWindowHeight = tabbedChatPaneSize.height - 85;

		final ChatWindowPanel chatWindow = new ChatWindowPanel( chatWindowWidth, chatWindowHeight,
			new ChatWindowEvents()
			{
				public Boolean sendMessage( String message )
				{
					System.out.println( "ChatWindowEvents.sendChatMessage: " + message );
					return netChatServer.sendMessage( clientSocket, message );
				}
			}
		);

		JPanel chatOptions = new JPanel( new FlowLayout() );
		chatOptions.setPreferredSize( new Dimension( chatWindowWidth, 30 ) );

		int chatOptionButtonWidth = chatWindowWidth / 2 - 5;
		int chatOptionButtonHeight = 25;

		JButton exitChatButton  = new JButton( "Close" );
		exitChatButton.setPreferredSize( new Dimension( chatOptionButtonWidth, chatOptionButtonHeight ) );
		chatOptions.add( exitChatButton );

		JButton saveChatButton = new JButton( "Save" );
		saveChatButton.setPreferredSize( new Dimension( chatOptionButtonWidth, chatOptionButtonHeight ) );
		saveChatButton.addActionListener(
			new AbstractAction()
			{
				public void actionPerformed( ActionEvent event )
				{
					FileDialog saveChatLogDialog = new FileDialog( mainFrame, "Save Chat Log", FileDialog.SAVE );
					saveChatLogDialog.setVisible( true );
					
					System.out.println( "User chose " + saveChatLogDialog.getDirectory() + saveChatLogDialog.getFile() );

					try
					{
						BufferedWriter chatLogWriter = new BufferedWriter( new FileWriter( saveChatLogDialog.getDirectory() + saveChatLogDialog.getFile() ) );
						chatLogWriter.write( chatWindow.getChatLog() );
						chatLogWriter.close();
					}
					catch ( IOException e )
					{
						System.out.println( "Exception writing chat log file: " + e.toString() );
					}
				}
			}
		);

		chatOptions.add( saveChatButton );

		JPanel chatOptionsContainer = new JPanel( new BorderLayout() );
		chatOptionsContainer.setPreferredSize( new Dimension( chatWindowWidth, 30 ) );
		chatOptionsContainer.add( chatOptions, BorderLayout.WEST );

		JPanel chatTab = new JPanel();
		chatTab.setPreferredSize( new Dimension( chatWindowWidth, WINDOW_HEIGHT ) );
		chatTab.add( chatOptionsContainer );	
		chatTab.add( chatWindow.contentPanel );	

		Component tabComponent = tabbedChatPane.add( "Client: " + clientInfo, chatTab );
		tabbedChatPane.setSelectedComponent( tabComponent );

		final ActiveChatWindow activeChatWindow = ActiveChatWindows.add( clientID, clientSocket, chatWindow, tabComponent );
		
		exitChatButton.addActionListener(
			new AbstractAction()
			{
				public void actionPerformed( ActionEvent event )
				{
					System.out.println( "ExitChatButtonPressed.actionPerformed" );
					UIManager.chatManagerWindow.removeChatTab( activeChatWindow );
				}
			}
		);
	}
	
	public void processDisconnect( long clientID )
	{
		for ( ActiveChatWindow chatWindow : ActiveChatWindows.activeChatWindows )
		{
			System.out.println( chatWindow.clientID + " == " + clientID );
			
			if ( chatWindow.clientID == clientID )
			{
				chatWindow.chatPanel.processDisconnect();

				// Remove the window here just incase a connection with the same IP and port
				// is initiated while the tab is still open. (i.e. when the client causes the disconnect)
				System.out.println( "Removing active window: " + ActiveChatWindows.remove( chatWindow ) );
				
				break;
			}
		}
	}

	public void removeChatTab( ActiveChatWindow chatWindow )
	{
		try
		{
			chatWindow.clientSocket.close();
		}
		catch ( IOException e )
		{
			System.out.println( "removeChatTabByID failed to close clientSocket: " + e.toString() );
		}

		System.out.println( "Removing chat tab" );

		tabbedChatPane.remove( chatWindow.tabComponent );
	}

	public void removeChatTabByID( long clientID )
	{
		for ( ActiveChatWindow chatWindow : ActiveChatWindows.activeChatWindows )
		{
			System.out.println( chatWindow.clientID + " == " + clientID );
			
			if ( chatWindow.clientID == clientID )
			{
				removeChatTab( chatWindow );

				break;
			}
		}
	}

	public void connect()
	{
		netChatServer = new NetChatServer();

		if ( !netChatServer.netServer.isValid() )
		{
			System.out.println( "Failed to create net chat server!" );
			
			JOptionPane.showMessageDialog( mainFrame, "Failed to create net chat server!" );

			destroy();

			return;
		}

		reloadChatRequests();
	}

	public void destroy()
	{
		UIManager.chatManagerWindow = null;

		if ( netChatServer != null )
			netChatServer.destroy();

		super.destroy();
	}

	public UIChatManagerWindow()
	{
		JPanel chatRequestsContainer = new JPanel();		
		JScrollPane chatRequestsContainerScrollPane = new JScrollPane( chatRequestsContainer );
		chatRequestsContainerScrollPane.setPreferredSize( new Dimension( WINDOW_WIDTH - 20, WINDOW_HEIGHT - 40 - 40 ) ); // Second -40 for scrollbar
		chatRequestsContainerScrollPane.setBorder( new EmptyBorder( 0, 5, 0, 5 ) );
		chatRequestsContainerScrollPane.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_ALWAYS ); 

		chatRequests = new JPanel();
		chatRequests.setLayout( new GridLayout( 0, 1, 0, 5 ) );
		chatRequestsContainer.add( chatRequests );

		JPanel chatRequestsTab = new JPanel();
		chatRequestsTab.add( chatRequestsContainerScrollPane );

		tabbedChatPane = new JTabbedPane();
		tabbedChatPane.setPreferredSize( new Dimension( WINDOW_WIDTH - 20, WINDOW_HEIGHT - 40 ) );
		tabbedChatPane.add( "Pending", chatRequestsTab );
		tabbedChatPane.setTabLayoutPolicy( JTabbedPane.SCROLL_TAB_LAYOUT );

		JPanel contentPanel = new JPanel();
		contentPanel.add( tabbedChatPane );

		SetupMainFrame( "Chat Manager", WINDOW_WIDTH, WINDOW_HEIGHT, contentPanel, new ChatManagerWindowListener() );
	}
}