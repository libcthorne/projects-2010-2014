// Test net message types
public enum NetMessageType
{
	C2S_TEST,
	S2C_TEST,
	C2S_UNHANDLED_TEST_MESSAGE;
	
	public static NetMessageType convert( short i )
	{
		for ( NetMessageType type : values() )
		{
			if ( type.ordinal() == i )
				return type;
		}
		
		return null;
	}
}