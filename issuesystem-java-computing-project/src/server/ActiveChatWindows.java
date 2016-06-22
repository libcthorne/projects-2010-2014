import java.net.*;
import java.awt.Component;
import java.util.ArrayList;

class ActiveChatWindow
{
	long clientID;
	Socket clientSocket;
	ChatWindowPanel chatPanel;
	Component tabComponent;
}

public class ActiveChatWindows
{
	public static ArrayList<ActiveChatWindow> activeChatWindows = new ArrayList<ActiveChatWindow>();

	public static ActiveChatWindow add( long clientID, Socket clientSocket, ChatWindowPanel chatPanel, Component tabComponent )
	{
		ActiveChatWindow chatWindow = new ActiveChatWindow();
		chatWindow.clientID = clientID;
		chatWindow.clientSocket = clientSocket;
		chatWindow.chatPanel = chatPanel;
		chatWindow.tabComponent = tabComponent;

		Boolean result = activeChatWindows.add( chatWindow );

		System.out.println( "activeChatWindows.add: " + result );
		
		if ( result )
			return chatWindow;
		else
			return null;
	}
	
	public static Boolean remove( ActiveChatWindow window )
	{
		return activeChatWindows.remove( window );
	}
}