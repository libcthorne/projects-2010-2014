import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;

interface PropertyValueParser
{
	public Boolean parse( String value );
}

class PropertyValue
{
	String identifier;
	PropertyValueParser parser;

	public PropertyValue( String identifier, PropertyValueParser parser )
	{
		this.identifier = identifier;
		this.parser = parser;
	}
}

public class PropertyReader
{
	// Load a property file

	public static Boolean load( String fileName, PropertyValue[] propertyValues )
	{
		Properties properties = new Properties();

		try
		{
			properties.load( new FileInputStream( fileName ) );
		}
		catch ( IOException e )
		{
			System.out.println( "PropertyReader: Failed to read properties file (" + fileName + ")" );

			return false;
		}
		
		for ( PropertyValue propertyValue : propertyValues )
		{
			String propertyString = properties.getProperty( propertyValue.identifier );
			
			System.out.println( "Parsing property " + propertyValue.identifier + "=" + propertyString );

			if ( !propertyValue.parser.parse( propertyString ) )
			{
				System.out.println( "Error parsing property in " + fileName + ": identifier=" + propertyValue.identifier );
				
				return false;
			}
		}

		return true;
	}

	// Helper functions for reading property values

	public static PropertyValue IntegerValue( String identifier, final Boolean optional, final AtomicReference<Integer> outValue )
	{
		return new PropertyValue( identifier,
			new PropertyValueParser()
			{
				public Boolean parse( String value )
				{
					if ( value == null )
						return optional;

					try
					{
						outValue.set( Integer.parseInt( value ) );
					}
					catch ( NumberFormatException e )
					{
						if ( !optional )
							return false;
					}

					return true;
				}
			}
		);
	}
	
	public static PropertyValue StringValue( String identifier, final Boolean optional, final AtomicReference<String> outValue )
	{
		return new PropertyValue( identifier,
			new PropertyValueParser()
			{
				public Boolean parse( String value )
				{
					if ( value == null )
						return optional;

					outValue.set( value );

					return true;
				}
			}
		);
	}
}