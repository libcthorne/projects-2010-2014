import java.util.concurrent.atomic.AtomicReference;

public class ClientProperties
{
	private static final String PROPERTIES_FILE_NAME = "IssueSystemReporterProperties";

	public static AtomicReference<String> masterAddress = new AtomicReference<String>();
	public static AtomicReference<Integer> masterPort = new AtomicReference<Integer>();
	public static AtomicReference<Integer> roomNumber = new AtomicReference<Integer>();
	public static AtomicReference<Integer> machineNumber = new AtomicReference<Integer>();

	public static Boolean load()
	{
		return PropertyReader.load( PROPERTIES_FILE_NAME,
			new PropertyValue[]
			{
				PropertyReader.StringValue( "masterAddress", false, masterAddress ),
				PropertyReader.IntegerValue( "masterPort", false, masterPort ),
				PropertyReader.IntegerValue( "roomNumber", true, roomNumber ),
				PropertyReader.IntegerValue( "machineNumber", true, machineNumber ),
			}
		);
	}
}