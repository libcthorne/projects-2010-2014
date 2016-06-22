import java.util.ArrayList;
import java.util.Random;

public class ChatInitiationValues
{
	private static ArrayList<Integer> initiationValues = new ArrayList<Integer>();
	private static Random randomGenerator = new Random();

	// Generate and store a unique initiation value
	// Duplicate values may be generated, but this is not a problem
	public static int generate()
	{
		int value = randomGenerator.nextInt();

		initiationValues.add( value );

		return value;
	}
	
	// Check if an initiation value is present
	public static Boolean exists( int value )
	{
		return initiationValues.contains( value );
	}
	
	// Invalidate an initiation value
	public static Boolean invalidate( int value )
	{
		return initiationValues.remove( (Integer)value );
	}
}