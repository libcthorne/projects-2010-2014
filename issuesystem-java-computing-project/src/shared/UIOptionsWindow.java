import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.AbstractAction;
import javax.swing.border.EmptyBorder;

import java.awt.GridLayout;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;

import java.util.ArrayList;

class WindowOption
{
	private class WindowOptionHelpPressed extends AbstractAction
	{
		private String helpString;

		public WindowOptionHelpPressed( String helpString )
		{
			this.helpString = helpString;
		}
		
		public void actionPerformed( ActionEvent event )
		{
			System.out.println( "WindowOptionHelpPressed.actionPerformed" );
			
			JOptionPane.showMessageDialog( UIBaseManager.mainWindow.mainFrame, this.helpString );
		}
	}

	public String optionString;
	public AbstractAction helpAction;
	public AbstractAction optionAction;

	public WindowOption( String optionString, String helpString, AbstractAction optionAction )
	{
		this.optionString = optionString;
		this.helpAction = new WindowOptionHelpPressed( helpString );
		this.optionAction = optionAction;
	}
}

public class UIOptionsWindow
{
	private static final int OPTION_BUTTON_YPADDING = 10;
	private static final int OPTION_BUTTON_WIDTH = 160;
	private static final int OPTION_BUTTON_HEIGHT = 25;
	private static final int OPTION_HELP_BUTTON_WIDTH = 45;
	private static final int OPTION_HELP_BUTTON_HEIGHT = 25;
	private static final int OPTION_WINDOW_XPADDING = 40;
	private static final int OPTION_WINDOW_YPADDING = 58;

	public static void Create( UIBaseWindow baseWindow, String windowTitle, WindowOption[] windowOptions )
	{
		// Option JButtons
		JPanel buttonGridPanel = new JPanel();
		buttonGridPanel.setLayout( new GridLayout( windowOptions.length, 0, 0, OPTION_BUTTON_YPADDING ) );

		// Help JButtons
		JPanel helpButtonGridPanel = new JPanel();
		helpButtonGridPanel.setLayout( new GridLayout( windowOptions.length, 0, 0, OPTION_BUTTON_YPADDING ) );

		for ( WindowOption option : windowOptions )
		{
			JButton actionButton = new JButton( option.optionString );
			actionButton.addActionListener( option.optionAction );
			actionButton.setPreferredSize( new Dimension( OPTION_BUTTON_WIDTH, OPTION_BUTTON_HEIGHT ) );
			actionButton.setFocusPainted( false );
			buttonGridPanel.add( actionButton );

			JButton helpButton = new JButton( "?" );
			helpButton.addActionListener( option.helpAction );
			helpButton.setPreferredSize( new Dimension( OPTION_HELP_BUTTON_WIDTH, OPTION_HELP_BUTTON_HEIGHT ) );	
			helpButton.setFocusPainted( false );
			helpButtonGridPanel.add( helpButton );
		}

		// "Please select an option.." label
		JLabel selectLabel = new JLabel( "Please select an option.." );
		JPanel selectLabelPanel = new JPanel( new BorderLayout() );
		selectLabelPanel.setPreferredSize( new Dimension( OPTION_BUTTON_WIDTH + OPTION_HELP_BUTTON_WIDTH + 15, 15 ) );
		selectLabelPanel.add( selectLabel, BorderLayout.WEST );

		// Buttons panel
		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setBorder( new EmptyBorder( 0, 10, 10, 10 ) );
		buttonsPanel.add( buttonGridPanel );
		buttonsPanel.add( helpButtonGridPanel );

		// Main frame content panel
		JPanel contentPanel = new JPanel();
		contentPanel.add( selectLabelPanel );
		contentPanel.add( buttonsPanel );

		// Setup main frame
		baseWindow.SetupMainFrame( windowTitle,
									OPTION_BUTTON_WIDTH + OPTION_HELP_BUTTON_WIDTH + OPTION_WINDOW_XPADDING,
									windowOptions.length * ( OPTION_BUTTON_YPADDING + OPTION_BUTTON_HEIGHT ) + OPTION_WINDOW_YPADDING,
									contentPanel, null );
	}
}