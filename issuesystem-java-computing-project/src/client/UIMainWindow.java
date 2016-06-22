import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.AbstractAction;
import javax.swing.border.EmptyBorder;

import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

public class UIMainWindow extends UIBaseWindow
{
	private class SubmitReportButtonPressed extends AbstractAction
	{
		public void actionPerformed( ActionEvent event )
		{
			if ( UIManager.submitReportWindow == null )
			{
				UIManager.submitReportWindow = new UISubmitReportWindow();
			}
			else
			{
				UIManager.submitReportWindow.mainFrame.setState( Frame.NORMAL );
				UIManager.submitReportWindow.mainFrame.requestFocus();
			}
		}
	}
	
	private class ReportStatusButtonPressed extends AbstractAction
	{
		public void actionPerformed( ActionEvent event )
		{
			if ( UIManager.reportStatusWindow == null )
			{
				UIManager.reportStatusWindow = new UIReportStatusWindow();
			}
			else
			{
				UIManager.reportStatusWindow.mainFrame.setState( Frame.NORMAL );
				UIManager.reportStatusWindow.mainFrame.requestFocus();
			}
		}	
	}
	
	private class StartLiveChatButtonPressed extends AbstractAction
	{
		public void actionPerformed( ActionEvent event )
		{
			if ( UIManager.liveChatWindow == null )
			{
				UIManager.liveChatWindow = new UILiveChatWindow();
				UIManager.liveChatWindow.connect();
			}
			else
			{
				UIManager.liveChatWindow.mainFrame.setState( Frame.NORMAL );
				UIManager.liveChatWindow.mainFrame.requestFocus();
			}
		}
	}

	public UIMainWindow()
	{
		WindowOption[] windowOptions = {
			new WindowOption( "Submit Report", "Submits an issue report to the technicians.", new SubmitReportButtonPressed() ),
			new WindowOption( "Report Status", "Allows you to view the status of a report you have submitted.", new ReportStatusButtonPressed() ),
			new WindowOption( "Live Chat", "Opens a request for a live chat with a technician. Use this if you need immediate assistance.", new StartLiveChatButtonPressed() )
		};
		
		UIOptionsWindow.Create( this, "Issue Reporter", windowOptions );
	}
}