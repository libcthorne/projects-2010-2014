import java.io.*;

public class ReportFiles
{
	public static final String REPORTS_DIRECTORY = "reports";

	public static void loadAll()
	{
		System.out.println( "ReportFiles.loadAll" );

		File reportsDirectory = new File( REPORTS_DIRECTORY );
		
		if ( !reportsDirectory.isDirectory() )
			return;

		File[] reportFiles = reportsDirectory.listFiles();

		for ( int i = 0; i < reportFiles.length; i++ )
		{
			String reportFileName = reportFiles[i].getName();

			ReportFile.load( reportFileName );
		}
	}
}

class ReportFile
{
	public static Boolean load( String fileName )
	{
		try
		{
			// Open file
			DataInputStream fileReaderStream = new DataInputStream(
														new BufferedInputStream(
															new FileInputStream(
																new File( ReportFiles.REPORTS_DIRECTORY + "/" + fileName )
															)
														)
													);

			// Read values
			int reportID = fileReaderStream.readInt();
			String reportTime = fileReaderStream.readUTF();
			String userName = fileReaderStream.readUTF();
			short roomNumber = fileReaderStream.readShort();
			short machineNumber = fileReaderStream.readShort();
			ReportProblemType problemType = ReportProblemType.convert( fileReaderStream.readByte() );
			String additionalDetails = fileReaderStream.readUTF();
			ReportStatus reportStatus = ReportStatus.convert( fileReaderStream.readByte() );
			String reportStatusNotes = fileReaderStream.readUTF();

			// Close file
			fileReaderStream.close();

			// Add file to active list
			return ActiveReports.add(
								reportID,
								reportTime,
								userName,
								roomNumber,
								machineNumber,
								problemType,
								additionalDetails,
								reportStatus,
								reportStatusNotes
							) != null;
		}
		catch ( IOException e )
		{
			System.out.println( "Exception reading report file " + fileName + ": " + e.toString() );
			
			return false;
		}
	}
	
	public static Boolean save( ActiveReport activeReport )
	{
		if ( activeReport == null )
			return false;

		try
		{
			// Open reports directory
			File reportsDirectory = new File( ReportFiles.REPORTS_DIRECTORY );

			// If there's a file named "reports" with no extension,
			// delete it to prevent it clashing with the folder.
			if ( reportsDirectory.isFile() )
				reportsDirectory.delete();

			// Create directory if it doesn't exist
			reportsDirectory.mkdirs();

			// Open report file
			DataOutputStream fileWriterStream = new DataOutputStream(
													new BufferedOutputStream(
														new FileOutputStream(
															new File( ReportFiles.REPORTS_DIRECTORY + "\\report-" + activeReport.reportID + ".bin" ), false // false = don't append
														)
													)
												);

			// Write values
			fileWriterStream.writeInt( activeReport.reportID );
			fileWriterStream.writeUTF( activeReport.reportTime );
			fileWriterStream.writeUTF( activeReport.userName );
			fileWriterStream.writeShort( activeReport.roomNumber );
			fileWriterStream.writeShort( activeReport.machineNumber );
			fileWriterStream.writeByte( activeReport.problemType.ordinal() );
			fileWriterStream.writeUTF( activeReport.additionalDetails );
			fileWriterStream.writeByte( activeReport.reportStatus.ordinal() );
			fileWriterStream.writeUTF( activeReport.reportStatusNotes );
			fileWriterStream.close();

			return true;
		}
		catch ( IOException e )
		{
			System.out.println( "Error writing report file: " + e.toString() );

			return false;
		}
	}
}