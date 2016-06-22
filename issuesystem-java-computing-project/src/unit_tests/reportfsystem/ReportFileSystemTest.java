public class ReportFileSystemTest
{
	public static void saveReportTest()
	{
		ActiveReport report = ActiveReports.add( ActiveReports.getFreeReportID(), "000", "test", (short)1, (short)2, ReportProblemType.SOFTWARE, "AAA", ReportStatus.PENDING, "BBB" );
		
		System.out.println( "Saved report: " + ReportFile.save( report ) );
	}
	
	public static void saveReportsTest()
	{
		for ( int i = 0; i < 1000; i++ )
		{
			int reportID = ActiveReports.getFreeReportID();

			ActiveReport report = ActiveReports.add( reportID, "000", "test", (short)1, (short)2, ReportProblemType.SOFTWARE, "AAA", ReportStatus.PENDING, "BBB" );
			
			System.out.println( "Saved report " + reportID + ": " + ReportFile.save( report ) );
		}
	}

	public static void loadReportsTest()
	{
		ReportFiles.loadAll();
		
		System.out.println( "Active reports: " + ActiveReports.list.size() );	
	}

	public static void main( String[] args )
	{
		loadReportsTest();
	}
}