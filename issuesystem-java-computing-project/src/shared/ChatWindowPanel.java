import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.border.*;

interface ChatWindowEvents
{
	public Boolean sendMessage( String message );
}

public class ChatWindowPanel
{
	private static final int SEND_BUTTON_WIDTH = 80;
	private static final int SEND_BUTTON_HEIGHT = 60;

	public ChatWindowEvents events;
	
	public JPanel contentPanel;

	private JTextPane chatLogTextPanel;
	private final JTextArea chatMessageTextPanel;
	private StyledDocument chatLogStyledDocument;
	private Style messageTextStyle;
	private Style selfNameStyle;

	public String getChatLog()
	{
		return chatLogTextPanel.getText();
	}

	private void insertText( String sourceText, Style sourceStyle, String messageText, Style messageStyle )
	{
		try
		{
			chatLogStyledDocument.insertString( chatLogStyledDocument.getLength(), sourceText + ": ", sourceStyle );
			chatLogStyledDocument.insertString( chatLogStyledDocument.getLength(), messageText + "\n", messageStyle );
		}
		catch ( BadLocationException e )
		{
			System.out.println( "insertText exception: " + e.toString() );
		}
	}

	public void sendInputText()
	{
		String inputText = chatMessageTextPanel.getText();

		if ( inputText.length() <= 0 ) return;
		
		if ( events.sendMessage( inputText ) )
			insertText( "You", selfNameStyle, inputText, messageTextStyle );
		else
			insertText( "Error", selfNameStyle, "Failed to send message.", messageTextStyle );
		
		chatMessageTextPanel.setText( "" );
	}
	
	public void grabFocus()
	{
		chatMessageTextPanel.grabFocus();
	}

	public void receivedMessage( String message )
	{
		System.out.println( "ChatWindowPanel.receivedMessage: " + message );

		insertText( "Peer", selfNameStyle, message, messageTextStyle );
	}
	
	public void processDisconnect()
	{
		insertText( "System", selfNameStyle, "Peer disconnected.", messageTextStyle );
	}

	public ChatWindowPanel( int width, int height, ChatWindowEvents events )
	{
		this.events = events;

		int chatLogWidth = width;
		int chatLogHeight = height - SEND_BUTTON_HEIGHT;

		chatLogTextPanel = new JTextPane();
		chatLogTextPanel.setPreferredSize( new Dimension( chatLogWidth, chatLogHeight ) );
		chatLogTextPanel.setBorder( BorderFactory.createLineBorder( Color.GRAY ) );
		chatLogTextPanel.setEditable( false );

		JScrollPane chatLogTextPanelScrollable = new JScrollPane( chatLogTextPanel );
		chatLogTextPanelScrollable.setPreferredSize( new Dimension( chatLogWidth, chatLogHeight ) );
		// Auto-scroll to bottom when new text is added
		chatLogTextPanelScrollable.getVerticalScrollBar().addAdjustmentListener(
			new AdjustmentListener()
			{
				int previousMaximum = 0;

				public void adjustmentValueChanged( AdjustmentEvent e )
				{
					int currentMaximum = e.getAdjustable().getMaximum();
					
					if ( currentMaximum > previousMaximum )
					{
						e.getAdjustable().setValue( currentMaximum );
						
						previousMaximum = currentMaximum;
					}
				}
			}
		);

		// Style document
		chatLogStyledDocument = chatLogTextPanel.getStyledDocument();

		// Default style
		Style defaultStyle = StyleContext.getDefaultStyleContext().getStyle( StyleContext.DEFAULT_STYLE );
		
		// Style for messages
		messageTextStyle = chatLogStyledDocument.addStyle( "messageText", defaultStyle );

		// Style for own name
		selfNameStyle = chatLogStyledDocument.addStyle( "selfName", defaultStyle );
		StyleConstants.setBold( selfNameStyle, true );
		
		// Chat message field
		chatMessageTextPanel = new JTextArea();
		chatMessageTextPanel.setLineWrap( true );
		chatMessageTextPanel.setDocument( new LimitedSizeDocument( LimitConstants.MAX_CHAT_MESSAGE_CHARACTERS ) );
		chatMessageTextPanel.getInputMap().put( KeyStroke.getKeyStroke( "ENTER" ),
			new AbstractAction()
			{
				public void actionPerformed( ActionEvent event )
				{
					sendInputText();
				}
			}
		);

		JScrollPane chatMessageTextPanelScrollable = new JScrollPane( chatMessageTextPanel );
		chatMessageTextPanelScrollable.setPreferredSize( new Dimension( chatLogWidth - SEND_BUTTON_WIDTH, SEND_BUTTON_HEIGHT ) );
		
		// Send button
		JButton sendButton = new JButton( "Send" );
		sendButton.setFocusPainted( false );
		sendButton.setPreferredSize( new Dimension( SEND_BUTTON_WIDTH, SEND_BUTTON_HEIGHT ) );
		sendButton.addActionListener(
			new AbstractAction()
			{
				public void actionPerformed( ActionEvent event )
				{				
					sendInputText();

					// Set input as focus (because focus is lost when clicking the button)
					chatMessageTextPanel.grabFocus();
				}
			}
		);

		// Message text/send button container
		JPanel messageSendContainer = new JPanel( new FlowLayout( 0, 0, 0 ) );
		messageSendContainer.setPreferredSize( new Dimension( chatLogWidth, SEND_BUTTON_HEIGHT ) );
		messageSendContainer.add( chatMessageTextPanelScrollable );
		messageSendContainer.add( sendButton );

		contentPanel = new JPanel();
		contentPanel.setPreferredSize( new Dimension( width, height + 10 ) ); // Add 10 to height to prevent send button border being cut off
		contentPanel.add( chatLogTextPanelScrollable );
		contentPanel.add( messageSendContainer );
	}
}