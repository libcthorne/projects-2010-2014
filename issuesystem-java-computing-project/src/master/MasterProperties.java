import java.util.concurrent.atomic.AtomicReference;

public class MasterProperties
{
	private static final String PROPERTIES_FILE_NAME = "IssueSystemMasterProperties";

	public static AtomicReference<String> hostAddress = new AtomicReference<String>();
	public static AtomicReference<Integer> hostPort = new AtomicReference<Integer>();
	public static AtomicReference<String> serverPassword = new AtomicReference<String>();

	public static Boolean load()
	{
		return PropertyReader.load( PROPERTIES_FILE_NAME,
			new PropertyValue[]
			{
				PropertyReader.StringValue( "hostAddress", true, hostAddress ),
				PropertyReader.IntegerValue( "hostPort", false, hostPort ),
				PropertyReader.StringValue( "serverPassword", false, serverPassword ),
			}
		);
	}
}