import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.*;

public class UILiveChatWindow extends UIBaseWindow
{
	private static final int WINDOW_WIDTH = 290;
	private static final int WINDOW_HEIGHT = 440;
	private static final int CHAT_XPADDING = 30;
	private static final int CHAT_YPADDING = 90;

	private JLabel connectStatusLabel;
	private ChatWindowPanel chatWindow;

	public NetChatClient netChatClient;
	public NetChatRequestClient netChatRequestClient;

	private class LiveChatWindowListener extends WindowAdapter
	{
		public void windowClosing( WindowEvent e )
		{
			System.out.println( "LiveChatWindowListener.windowClosing" );

			UIManager.liveChatWindow.destroy();
		}
	}
	
	public void createChat( String technicianName )
	{
		JLabel chatPeerNameLabel = new JLabel( "Technician Name: " + technicianName );
		JPanel chatPeerNamePanel = new JPanel( new BorderLayout() );
		chatPeerNamePanel.setPreferredSize( new Dimension( WINDOW_WIDTH - CHAT_XPADDING, 30 ) );
		chatPeerNamePanel.add( chatPeerNameLabel, BorderLayout.WEST );

		chatWindow = new ChatWindowPanel( WINDOW_WIDTH - CHAT_XPADDING, WINDOW_HEIGHT - CHAT_YPADDING,
			new ChatWindowEvents()
			{
				public Boolean sendMessage( String message )
				{
					System.out.println( "ChatWindowEvents.sendChatMessage: " + message );
					return netChatClient.sendMessage( message );			
				}
			}
		);

		JPanel contentPanel = new JPanel();
		contentPanel.add( chatPeerNamePanel );
		contentPanel.add( chatWindow.contentPanel );

		SetupMainFrame( "Live Chat", WINDOW_WIDTH, WINDOW_HEIGHT, contentPanel, new LiveChatWindowListener() );
		
		chatWindow.grabFocus();
	}

	public void processAck( Boolean response )
	{
		if ( response )
		{
			connectStatusLabel.setText( "Waiting for technician..." );
		}
		else
		{
			JOptionPane.showMessageDialog( mainFrame, "The master server is currently busy. Please try again later." );

			destroy();
		}
	}
	
	public void processResponse( String serverIP, int serverPort, int initiationValue )
	{
		netChatRequestClient.destroy();
		netChatRequestClient = null;

		netChatClient = new NetChatClient( serverIP, serverPort, initiationValue );

		if ( !netChatClient.netClient.isValid() )
		{
			JOptionPane.showMessageDialog( null, "Failed to connect to master server." );
			
			destroy();
			
			return;
		}

		connectStatusLabel.setText( "Loading..." );
	}
	
	public void processMessage( String message )
	{
		chatWindow.receivedMessage( message );
	}
	
	public void processDisconnect( Boolean error )
	{
		processError( "Disconnected from server." );

		destroy();
	}

	public void connect()
	{
		netChatRequestClient = new NetChatRequestClient();	
	}

	public void destroy()
	{
		UIManager.liveChatWindow = null;

		System.out.println( "UILiveChatWindow.destroy" );

		if ( netChatRequestClient != null )
		{
			System.out.println( "netChatRequestClient.destroy()" );
			netChatRequestClient.destroy();
		}

		if ( netChatClient != null )
		{
			System.out.println( "netChatClient.destroy()" );
			netChatClient.destroy();
		}

		super.destroy();
	}

	public UILiveChatWindow()
	{
		// Connecting spinner
		ImageIcon loadingImage = new ImageIcon( "resources/shared/loading-spinner.gif" );
		JLabel loadingImagePanel = new JLabel( loadingImage );

		JPanel contentPanel = new JPanel( new GridBagLayout() );
		contentPanel.setPreferredSize( new Dimension( WINDOW_WIDTH, WINDOW_HEIGHT ) );
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		contentPanel.add( loadingImagePanel, gbc );
		gbc.insets = new Insets( 10, 0, 0, 0 );
		gbc.gridx = 0;
		gbc.gridy = 1;
		connectStatusLabel = new JLabel( "Connecting..." );
		contentPanel.add( connectStatusLabel, gbc );

		SetupMainFrame( "Live Chat", WINDOW_WIDTH, WINDOW_HEIGHT, contentPanel, new LiveChatWindowListener() );
	}
}