public enum NetMessageType
{
	// Server -> Master
	S2M_REGISTER,
	S2M_CHATREQUEST_RESPONSE,
	S2M_UPDATEREPORT_STATUS,
	// Master -> Server
	M2S_CHATREQUEST_CREATE,
	M2S_CHATREQUEST_REMOVE,
	M2S_CREATEREPORT,
	M2S_UPDATEREPORT_STATUS,

	// Client -> Master
	C2M_CHATREQUEST,
	C2M_CHATREQUEST_CANCEL,
	C2M_SUBMITREPORT,
	C2M_REPORTSTATUS_REQUEST,
	// Master -> Client
	M2C_CHATREQUEST_ACK,
	M2C_CHATREQUEST_RESPONSE,
	M2C_SUBMITREPORT_ACK,
	M2C_REPORTSTATUS,
	
	// Server -> Client
	S2C_CHAT_INIT,
	S2C_CHAT_MESSAGE,
	// Client -> Server
	C2S_CHAT_INIT,
	C2S_CHAT_MESSAGE;
	
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