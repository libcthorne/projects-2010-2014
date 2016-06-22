import java.util.ArrayList;

class ActiveReport
{
	public int reportID;
	public String reportTime;
	public String userName;
	public short roomNumber;
	public short machineNumber;
	public ReportProblemType problemType;
	public String additionalDetails;
	public ReportStatus reportStatus;
	public String reportStatusNotes;
}

enum ReportProblemType
{
	OTHER,
	SOFTWARE,
	HARDWARE;

	public static ReportProblemType convert( short i )
	{
		for ( ReportProblemType t : values() )
		{
			if ( t.ordinal() == i )
				return t;
		}
		
		return OTHER;
	}
	
	@Override
	public String toString()
	{
		switch ( this )
		{
			case OTHER: return "Other";
			case SOFTWARE: return "Software";
			case HARDWARE: return "Hardware";
			default: return "Unknown";
		}
	}
}

enum ReportStatus
{
	PENDING,
	INVESTIGATION,
	RESOLVED,
	INVALID,
	DUPLICATE;
	
	public static ReportStatus convert( short i )
	{
		for ( ReportStatus s : values() )
		{
			if ( s.ordinal() == i )
				return s;
		}
		
		return null;
	}
	
	@Override
	public String toString()
	{
		switch ( this )
		{
		case PENDING:
			return "Pending";
		case INVESTIGATION:
			return "Under Investigation";
		case RESOLVED:
			return "Resolved";
		case INVALID:
			return "Invalid Report";
		case DUPLICATE:
			return "Duplicate Report";
		default:
			return "Unknown";
		}
	}
}

public class ActiveReports
{
	public static ArrayList<ActiveReport> list = new ArrayList<ActiveReport>();
	
	public static ActiveReport add( int reportID, String reportTime, String userName, short roomNumber, short machineNumber, ReportProblemType problemType, String additionalDetails, ReportStatus reportStatus, String reportStatusNotes )
	{
		// Debug output

		System.out.println( "ActiveReports.add:" );
		System.out.println( "\treportID=" + reportID );
		System.out.println( "\treportTime=" + reportTime );
		System.out.println( "\tuserName=" + userName );
		System.out.println( "\troomNumber=" + roomNumber );
		System.out.println( "\tmachineNumber=" + machineNumber );
		System.out.println( "\tproblemType=" + problemType );
		System.out.println( "\tadditionalDetails=" + additionalDetails );
		System.out.println( "\treportStatusNotes=[" + reportStatusNotes + "]\n" );

		// Verify enum parameters are valid

		if ( problemType == null )
			return null;
		
		if ( reportStatus == null )
			return null;

		// Create report container

		ActiveReport report = new ActiveReport();
		report.reportID = reportID;
		report.reportTime = reportTime;
		report.userName = userName;
		report.roomNumber = roomNumber;
		report.machineNumber = machineNumber;
		report.problemType = problemType;
		report.additionalDetails = additionalDetails;
		report.reportStatus = reportStatus;
		report.reportStatusNotes = reportStatusNotes;

		// Add to active list
		Boolean result = list.add( report );
		
		if ( result )
		{
			// Return ActiveReport object if report was successfully added to the list
			return report;
		}
		else
		{
			// Return null on failure
			return null;
		}
	}

	public static Boolean remove( ActiveReport report )
	{
		return list.remove( report );
	}

	public static Boolean removeByID( int reportID )
	{
		ActiveReport report = getReportFromID( reportID );
		
		if ( report != null )
			return list.remove( report );
		else
			return false;
	}

	public static ActiveReport getReportFromID( int reportID )
	{
		// Search for report by ID
		for ( ActiveReport report : ActiveReports.list )
		{
			if ( report.reportID == reportID )
			{
				// If report ID matches, return
				return report;
			}
		}
		
		// Report ID not found
		return null;
	}

	public static int getFreeReportID()
	{
		// Report IDs start at 1
		return list.size() + 1;
	}
}