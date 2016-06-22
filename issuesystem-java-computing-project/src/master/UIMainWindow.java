import javax.swing.*;
import javax.swing.border.*;

import java.awt.*;
import java.awt.event.*;

import java.net.*;

public class UIMainWindow extends UIBaseWindow
{
	// Listeners

	private class ShowLocalIPButtonPressed extends AbstractAction
	{
		public void actionPerformed( ActionEvent event )
		{
			System.out.println( "ShowLocalIPButtonPressed.actionPerformed" );
			
			try
			{
				InetAddress addr = InetAddress.getLocalHost();
				String hostname = addr.getHostName();
				
				JOptionPane.showMessageDialog( null, "local address: " + addr.toString() + " / " + hostname );
			}
			catch ( UnknownHostException e )
			{
			}
		}
	}

	public UIMainWindow()
	{
		WindowOption[] windowOptions = {
			new WindowOption( "Show Local IP", "", new ShowLocalIPButtonPressed() )
		};

		UIOptionsWindow.Create( this, "Issue Master", windowOptions );
	}
}