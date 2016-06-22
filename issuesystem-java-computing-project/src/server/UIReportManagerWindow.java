import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

import java.awt.*;
import java.awt.event.*;

import java.util.ArrayList;

public class UIReportManagerWindow extends UIBaseWindow
{
	private static final int REPORTS_TABLE_WIDTH = 850; // Width of reports table
	private static final int REPORTS_TABLE_HEIGHT = 300; // Height of reports table
	private static final int REPORTS_WINDOW_PADDINGX = 30; // Padding (x) for window
	private static final int REPORTS_WINDOW_PADDINGY = 48; // Padding (y) for window
	private static final int OPERATION_BUTTON_COUNT = 2; // Number of operation buttons (i.e. view status, view additional details)
	private static final int OPERATION_BUTTON_WIDTH = 200;
	private static final int OPERATION_BUTTON_HEIGHT = 25;
	private static final int WINDOW_WIDTH = REPORTS_TABLE_WIDTH + REPORTS_WINDOW_PADDINGX;
	private static final int WINDOW_HEIGHT = REPORTS_TABLE_HEIGHT + ( OPERATION_BUTTON_HEIGHT * OPERATION_BUTTON_COUNT ) + REPORTS_WINDOW_PADDINGY + 40;
	
	// Report table

	private JTable reportsTable;
	private DefaultTableModel reportsTableModel;
	
	// Report filter variables
	
	private Boolean reportFilterEnabled = false;
	private ReportProblemType reportProblemTypeFilter;
	private ReportStatus reportStatusFilter;
	private int roomNumberFilter;

	// Selected report option windows

	public ArrayList<UIReportStatusWindow> reportStatusWindows = new ArrayList<UIReportStatusWindow>();
	public ArrayList<UIViewAdditionalReportDetailsWindow> viewAdditionalReportDetailsWindows = new ArrayList<UIViewAdditionalReportDetailsWindow>();

	// Adds the specified report to the report table, checking the filter if it's enabled
	public void addFilteredReportRow( ActiveReport report )
	{
		// Check filter
		if ( reportFilterEnabled )
		{
			if ( reportProblemTypeFilter != null && reportProblemTypeFilter != report.problemType )
				return;

			if ( reportStatusFilter != null && reportStatusFilter != report.reportStatus )
				return;

			if ( roomNumberFilter != -1 && roomNumberFilter != report.roomNumber )
				return;
		}

		// Add row to table
		reportsTableModel.addRow( new Object[]{ report.reportID, report.reportTime, report.userName, report.roomNumber, report.machineNumber, report.problemType.toString(), report.reportStatus.toString(), report.additionalDetails.length() > 0 ? "Yes" : "No" } );	
	}
	
	// Updates the status of a report's row if present
	public Boolean updateReportRowStatus( ActiveReport report )
	{
		for ( int i = 0; i < reportsTable.getRowCount(); i++ )
		{
			int rowReportID = (Integer)reportsTable.getValueAt( i, 0 );
			
			if ( rowReportID == report.reportID )
			{
				// Update status column for specified report row
				// 2nd column from the end -> columns - 2
				reportsTable.setValueAt( report.reportStatus.toString(), i, reportsTable.getColumnCount() - 2 );
				
				// Row found and updated
				return true;
			}
		}
		
		// Row not found
		return false;
	}

	// Returns the report object for the selected report row
	public ActiveReport getSelectedReport()
	{
		int selectedRow = reportsTable.getSelectedRow();

		// No row selected = -1
		if ( selectedRow == -1 )
			return null;

		// Return report object from ID
		return ActiveReports.getReportFromID( (Integer)reportsTable.getValueAt( selectedRow, 0 ) );
	}

	public void destroy()
	{
		System.out.println( "UIReportManagerWindow.destroy" );
		
		// Remove any open report status windows
		System.out.println( "Destroying " + reportStatusWindows.size() + " active UPR windows" );
		for ( UIReportStatusWindow window : reportStatusWindows )
			window.destroy();

		// Remove any open additional details windows
		System.out.println( "Destroying " + viewAdditionalReportDetailsWindows.size() + " active VARD windows" );
		for ( UIViewAdditionalReportDetailsWindow window : viewAdditionalReportDetailsWindows )
			window.destroy();
		
		super.destroy();
	}

	public UIReportManagerWindow()
	{
		// Create reports panel	

		JPanel reportsPanel = new JPanel( new BorderLayout() );
		reportsPanel.setBorder( new EmptyBorder( 5, 0, 0, 0 ) ); // 10 padding from top
		reportsPanel.setPreferredSize( new Dimension( REPORTS_TABLE_WIDTH, REPORTS_TABLE_HEIGHT ) );

		// Create table model

		reportsTableModel = new DefaultTableModel()
		{
			// Disable user cell editing		
			public boolean isCellEditable( int rowIndex, int mColIndex )
			{
				return false;
			}
			
			// Determine column classes for sorting
			public Class getColumnClass( int columnIndex )
			{
				switch ( columnIndex )
				{
				case 0:
				case 3:
				case 4:
					return Integer.class;
				case 5:
					return ReportProblemType.class;
				case 6:
					return ReportStatus.class;
				default:
					return String.class;
				}
			}
		};

		// Set column headers	

		Object[] columnHeaders = { "Report ID", "Report Date", "Reporter Name", "Room Number", "Machine Number", "Problem Type", "Status", "Additional Details" };
		reportsTableModel.setColumnIdentifiers( columnHeaders );

		// Add rows
		
		for ( ActiveReport report : ActiveReports.list )
			addFilteredReportRow( report );

		// Create table

		reportsTable = new JTable( reportsTableModel );
		reportsTable.setSelectionMode( ListSelectionModel.SINGLE_SELECTION ); // Disallow multiple row selection
		reportsTable.setAutoCreateRowSorter( true ); // Enable row sorter
		reportsTable.getTableHeader().setReorderingAllowed( false ); // Disallow column reordering
		reportsTable.getRowSorter().toggleSortOrder( 0 ); // Sort using report ID by default
		reportsPanel.add( new JScrollPane( reportsTable ) );

		// Options label	

		JLabel optionsLabel = new JLabel( "<html><u>Selected Report Options</u></html>" );
		optionsLabel.setPreferredSize( new Dimension( OPERATION_BUTTON_WIDTH, 25 ) );
		
		// Selected report status button

		JButton reportStatusButton = new JButton( "View/Update Status" );
		reportStatusButton.setPreferredSize( new Dimension( OPERATION_BUTTON_WIDTH, OPERATION_BUTTON_HEIGHT ) );
		reportStatusButton.setFocusPainted( false );
		reportStatusButton.addActionListener(
			new AbstractAction()
			{
				public void actionPerformed( ActionEvent event )
				{
					System.out.println( "reportStatusButton.actionPerformed" );
					
					ActiveReport report = getSelectedReport();

					if ( report == null )
					{
						JOptionPane.showMessageDialog( mainFrame, "You have not selected a report." );

						return;
					}

					reportStatusWindows.add( new UIReportStatusWindow( getSelectedReport() ) );
				}
			}
		);

		// Selected report additional details button

		JButton viewAdditionalReportDetailsButton = new JButton( "View Additional Details" );
		viewAdditionalReportDetailsButton.setPreferredSize( new Dimension( OPERATION_BUTTON_WIDTH, OPERATION_BUTTON_HEIGHT ) );
		viewAdditionalReportDetailsButton.setFocusPainted( false );
		viewAdditionalReportDetailsButton.addActionListener(
			new AbstractAction()
			{
				public void actionPerformed( ActionEvent event )
				{
					System.out.println( "viewAdditionalReportDetailsButton.actionPerformed" );

					ActiveReport report = getSelectedReport();

					if ( report == null )
					{
						JOptionPane.showMessageDialog( mainFrame, "You have not selected a report." );

						return;
					}

					if ( report.additionalDetails.length() > 0 )
					{
						viewAdditionalReportDetailsWindows.add( new UIViewAdditionalReportDetailsWindow( report ) );
					}
					else
					{
						JOptionPane.showMessageDialog( mainFrame, "The selected report does not contain any additional details." );
					}
				}
			}
		);

		JLabel filterLabel = new JLabel( "<html><u>Filter</u></html>" );

		// Problem type filter

		JLabel filterProblemTypeLabel = new JLabel( "Problem Type: " );
		filterProblemTypeLabel.setPreferredSize( new Dimension( 90, 20 ) );	
		
		final JComboBox filterProblemTypeComboBox = new JComboBox();
		filterProblemTypeComboBox.setPreferredSize( new Dimension( 150, 20 ) );
		filterProblemTypeComboBox.addItem( "<any>" );

		for ( ReportProblemType t : ReportProblemType.values() )
			filterProblemTypeComboBox.addItem( t );

		JPanel filterProblemType = new JPanel();
		filterProblemType.add( filterProblemTypeLabel );
		filterProblemType.add( filterProblemTypeComboBox );

		// Report status filter

		JLabel filterStatusLabel = new JLabel( "Report Status: " );
		filterStatusLabel.setPreferredSize( new Dimension( 90, 20 ) );
		
		final JComboBox filterStatusComboBox = new JComboBox();
		filterStatusComboBox.setPreferredSize( new Dimension( 150, 20 ) );	
		filterStatusComboBox.addItem( "<any>" );

		for ( ReportStatus t : ReportStatus.values() )
			filterStatusComboBox.addItem( t );
		
		JPanel filterStatus = new JPanel();
		filterStatus.add( filterStatusLabel );
		filterStatus.add( filterStatusComboBox );

		// Room number filter
	
		JLabel filterRoomNumberLabel = new JLabel( "Room Number: " );
		filterRoomNumberLabel.setPreferredSize( new Dimension( 90, 20 ) );
		final NumberTextField filterRoomNumberField = new NumberTextField( 4 );
		filterRoomNumberField.setPreferredSize( new Dimension( 50, 20 ) );
		JPanel filterRoomNumber = new JPanel();
		filterRoomNumber.add( filterRoomNumberLabel );
		filterRoomNumber.add( filterRoomNumberField );

		// Apply filter button
		
		JButton applyFilterButton = new JButton( "Apply" );
		applyFilterButton.setPreferredSize( new Dimension( 70, 20 ) );
		applyFilterButton.addActionListener(
			new AbstractAction()
			{
				public void actionPerformed( ActionEvent e )
				{
					System.out.println( "applyFilterButton.actionPerformed" );
					
					// Enable report filtering
					reportFilterEnabled = true;

					// Problem type filter
					
					int reportProblemTypeIndex = filterProblemTypeComboBox.getSelectedIndex();
					
					if ( reportProblemTypeIndex != 0 )
					{
						reportProblemTypeFilter = (ReportProblemType)filterProblemTypeComboBox.getSelectedItem();
						
						System.out.println( "Problem type: " + reportProblemTypeFilter );
					}
					else
					{
						reportProblemTypeFilter = null;
					}
					
					// Report status filter

					int reportStatusIndex = filterStatusComboBox.getSelectedIndex();
	
					if ( reportStatusIndex != 0 )
					{
						reportStatusFilter = (ReportStatus)filterStatusComboBox.getSelectedItem();

						System.out.println( "Report status: " + reportStatusFilter );
					}
					else
					{
						reportStatusFilter = null;
					}
					
					// Room number filter
					
					roomNumberFilter = filterRoomNumberField.getIntegerValue();
					
					System.out.println( "Room number: " + roomNumberFilter );

					// Clear reports table
					while ( reportsTable.getRowCount() > 0 )
						((DefaultTableModel)reportsTable.getModel()).removeRow( 0 );

					// Re-add reports with filter enabled
					for ( ActiveReport report : ActiveReports.list )
						addFilteredReportRow( report );
				}
			}
		);
		
		// Reset filter button
		
		JButton resetFilterButton = new JButton( "Reset" );
		resetFilterButton.setPreferredSize( new Dimension( 70, 20 ) );
		resetFilterButton.addActionListener(
			new AbstractAction()
			{
				public void actionPerformed( ActionEvent e )
				{
					System.out.println( "resetFilterButton.actionPerformed" );

					// Disable report filtering
					reportFilterEnabled = false;

					// Clear reports table
					while ( reportsTable.getRowCount() > 0 )
						((DefaultTableModel)reportsTable.getModel()).removeRow( 0 );

					// Re-add all reports
					for ( ActiveReport report : ActiveReports.list )
						addFilteredReportRow( report );
				}
			}
		);

		// Filter buttons (apply, reset)

		JPanel filterButtons = new JPanel();
		filterButtons.setPreferredSize( new Dimension( 150, 25 ) );
		filterButtons.add( applyFilterButton );
		filterButtons.add( resetFilterButton );

		// Filter layout panel
		
		JPanel gridPanel = new JPanel( new GridBagLayout() );
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		
		gridBagConstraints.insets = new Insets( 0, 0, 0, 0 );
		gridBagConstraints.gridwidth = 3;
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridPanel.add( reportsPanel, gridBagConstraints );
		
		gridBagConstraints.insets = new Insets( 5, 0, 0, 0 );
		gridBagConstraints.gridwidth = 1;
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridPanel.add( optionsLabel, gridBagConstraints );

		gridBagConstraints.insets = new Insets( 5, 0, 0, 0 );
		gridBagConstraints.gridwidth = 1;
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		gridPanel.add( reportStatusButton, gridBagConstraints );

		gridBagConstraints.insets = new Insets( 5, 0, 0, 0 );
		gridBagConstraints.gridwidth = 1;
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 3;
		gridPanel.add( viewAdditionalReportDetailsButton, gridBagConstraints );

		gridBagConstraints.insets = new Insets( 0, 18, 0, 0 );
		gridBagConstraints.gridwidth = 1;
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 1;
		gridPanel.add( filterLabel, gridBagConstraints );

		gridBagConstraints.insets = new Insets( 5, 235, 0, 0 );
		gridBagConstraints.gridwidth = 1;
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 2;
		gridPanel.add( filterProblemType, gridBagConstraints );

		gridBagConstraints.insets = new Insets( 5, 235, 0, 0 );
		gridBagConstraints.gridwidth = 1;
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 3;
		gridPanel.add( filterStatus, gridBagConstraints );

		gridBagConstraints.insets = new Insets( 5, 10, 0, 0 );
		gridBagConstraints.gridwidth = 1;
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 2;
		gridPanel.add( filterRoomNumber, gridBagConstraints );

		gridBagConstraints.insets = new Insets( 5, 10, 0, 0 );
		gridBagConstraints.gridwidth = 1;
		gridBagConstraints.gridx = 2;
		gridBagConstraints.gridy = 3;
		gridPanel.add( filterButtons, gridBagConstraints );

		// Create window

		JPanel contentPanel = new JPanel();
		contentPanel.add( gridPanel );
		
		SetupMainFrame( "Report Manager", WINDOW_WIDTH, WINDOW_HEIGHT, contentPanel,
			new WindowAdapter()
			{
				public void windowClosing( WindowEvent e )
				{
					System.out.println( "Report Manager: windowClosing()" );

					// Destroy window
					destroy();

					// Update manager
					UIManager.reportManagerWindow = null;
				}
			}
		);
	}
}