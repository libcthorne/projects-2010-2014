import java.io.*;
import java.net.*;

interface NetPacketWriter
{
	public void write( DataOutputStream stream ) throws IOException;
}

public class NetPacketSender
{
	public static Boolean send( Socket socket, NetMessageType messageType, NetPacketWriter writer )
	{
		try
		{
			DataOutputStream outStream = new DataOutputStream( socket.getOutputStream() );
		
			outStream.writeShort( messageType.ordinal() );

			if ( writer != null )
				writer.write( outStream );

			outStream.flush();
			
			return true;
		}
		catch ( IOException e )
		{
			System.out.println( "Exception writing " + messageType + ": " + e.toString() );

			return false;
		}		
	}
}