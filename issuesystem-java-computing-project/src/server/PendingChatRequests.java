import java.util.ArrayList;

class NetChatRequest
{
	String name;
	long id;
}

public class PendingChatRequests
{
	public static ArrayList<NetChatRequest> pendingChatRequests = new ArrayList<NetChatRequest>( LimitConstants.MAX_PENDING_CHAT_REQUESTS );

	public static Boolean add( String name, long id )
	{
		System.out.println( "New pending chat request (" + name + ", " + id + ")" );

		NetChatRequest netChatRequest = new NetChatRequest();
		netChatRequest.name = name;
		netChatRequest.id = id;
		
		Boolean result = pendingChatRequests.add( netChatRequest );
		
		if ( result && UIManager.chatManagerWindow != null )
			UIManager.chatManagerWindow.reloadChatRequests();
			
		return result;
	}
	
	public static Boolean remove( NetChatRequest netChatRequest )
	{
		Boolean result = pendingChatRequests.remove( netChatRequest );
		
		if ( result && UIManager.chatManagerWindow != null )
			UIManager.chatManagerWindow.reloadChatRequests();
			
		return result;
	}
	
	public static Boolean removeByID( long id )
	{
		for ( NetChatRequest netChatRequest : pendingChatRequests )
		{
			if ( netChatRequest.id == id )
			{
				return remove( netChatRequest );
			}
		}
		
		return false;
	}
}