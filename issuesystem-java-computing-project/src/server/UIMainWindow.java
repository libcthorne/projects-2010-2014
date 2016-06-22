import javax.swing.*;
import javax.swing.border.*;

import java.awt.*;
import java.awt.event.*;

public class UIMainWindow extends UIBaseWindow
{
	private class OpenChatManagerButtonPressed extends AbstractAction
	{
		public void actionPerformed( ActionEvent event )
		{
			System.out.println( "OpenChatManagerButtonPressed.actionPerformed" );

			if ( UIManager.chatManagerWindow == null )
			{
				UIManager.chatManagerWindow = new UIChatManagerWindow();
				UIManager.chatManagerWindow.connect();
			}
			else
			{
				UIManager.chatManagerWindow.mainFrame.setState( Frame.NORMAL );
				UIManager.chatManagerWindow.mainFrame.requestFocus();
			}
		}
	}

	private class OpenReportManagerButtonPressed extends AbstractAction
	{
		public void actionPerformed( ActionEvent event )
		{
			System.out.println( "OpenReportManagerButtonPressed.actionPerformed" );
			
			if ( UIManager.reportManagerWindow == null )
			{
				UIManager.reportManagerWindow = new UIReportManagerWindow();
			}
			else
			{
				UIManager.reportManagerWindow.mainFrame.setState( Frame.NORMAL );
				UIManager.reportManagerWindow.mainFrame.requestFocus();
			}
		}
	}

	public UIMainWindow()
	{
		WindowOption[] windowOptions = {
			new WindowOption( "Report Manager", "The report manager is used to view, update and edit issues submitted by the client.", new OpenReportManagerButtonPressed() ),
			new WindowOption( "Chat Manager", "The chat manager is used to accept chat requests and talk to clients.", new OpenChatManagerButtonPressed() )
		};

		UIOptionsWindow.Create( this, "Issue Manager", windowOptions );
	}
}