import java.util.concurrent.atomic.AtomicReference;

public class ServerProperties
{
	private static final String PROPERTIES_FILE_NAME = "IssueSystemManagerProperties";

	public static AtomicReference<String> chatHostAddress = new AtomicReference<String>();
	public static AtomicReference<Integer> chatHostPort = new AtomicReference<Integer>();
	public static AtomicReference<String> masterAddress = new AtomicReference<String>();
	public static AtomicReference<Integer> masterPort = new AtomicReference<Integer>();
	public static AtomicReference<String> serverPassword = new AtomicReference<String>();

	public static Boolean load()
	{
		return PropertyReader.load( PROPERTIES_FILE_NAME,
			new PropertyValue[]
			{
				PropertyReader.StringValue( "chatHostAddress", true, chatHostAddress ),
				PropertyReader.IntegerValue( "chatHostPort", false, chatHostPort ),
				PropertyReader.StringValue( "masterAddress", false, masterAddress ),
				PropertyReader.IntegerValue( "masterPort", false, masterPort ),
				PropertyReader.StringValue( "serverPassword", false, serverPassword ),
			}
		);
	}
}