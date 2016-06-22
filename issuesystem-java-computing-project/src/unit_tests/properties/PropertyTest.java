import java.util.concurrent.atomic.AtomicReference;

public class PropertyTest
{
	public static void main( String[] args )
	{
		Boolean loadResult = PropertyReader.load( "TestProperties",
			new PropertyValue[]
			{
				PropertyReader.StringValue( "testString1", true, testString1 ), // Optional string
				PropertyReader.StringValue( "testString2", false, testString2 ), // Required string
				PropertyReader.IntegerValue( "testInt1", true, testInt1 ), // Optional integer
				PropertyReader.IntegerValue( "testInt2", false, testInt2 ), // Required integer
			}
		);

		System.out.println( "Load result: " + loadResult );
		
		if ( loadResult )
		{
			System.out.println( "testString1 = " + testString1 );
			System.out.println( "testString2 = " + testString2 );
			System.out.println( "testInt1 = " + testInt1 );
			System.out.println( "testInt2 = " + testInt2 );
		}
	}

	public static AtomicReference<String> testString1 = new AtomicReference<String>();
	public static AtomicReference<String> testString2 = new AtomicReference<String>();
	public static AtomicReference<Integer> testInt1 = new AtomicReference<Integer>();
	public static AtomicReference<Integer> testInt2 = new AtomicReference<Integer>();
}