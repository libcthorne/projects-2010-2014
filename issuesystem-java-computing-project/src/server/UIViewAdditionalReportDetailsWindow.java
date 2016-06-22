import javax.swing.*;

import java.awt.*;
import java.awt.event.*;

public class UIViewAdditionalReportDetailsWindow extends UIBaseWindow
{
	private static final int WINDOW_WIDTH = 450;
	private static final int WINDOW_HEIGHT = 350;

	public ActiveReport report;

	public UIViewAdditionalReportDetailsWindow( ActiveReport report )
	{
		System.out.println( "Creating ViewAdditionalReportDetailsWindow for reportID " + report.reportID );

		// Additional details

		JTextArea additionalDetailsTextArea = new JTextArea( report.additionalDetails );
		JScrollPane additionalDetailsTextAreaScr = new JScrollPane( additionalDetailsTextArea );
		additionalDetailsTextArea.setPreferredSize( new Dimension( WINDOW_WIDTH - 20, WINDOW_HEIGHT - 40 ) );
		additionalDetailsTextArea.setLineWrap( true );
		additionalDetailsTextArea.setEditable( false );

		// Create window

		JPanel contentPanel = new JPanel();
		contentPanel.add( additionalDetailsTextAreaScr );

		final UIViewAdditionalReportDetailsWindow thisWindow = this;

		SetupMainFrame( "Report #" + report.reportID + " - Additional Details", WINDOW_WIDTH, WINDOW_HEIGHT, contentPanel,
			new WindowAdapter()
			{
				public void windowClosing( WindowEvent e )
				{
					System.out.println( "View Additional Details: windowClosing()" );

					// Remove from active window list
					UIManager.reportManagerWindow.viewAdditionalReportDetailsWindows.remove( thisWindow );

					// Destroy window
					destroy();
				}
			}
		);
	}
}