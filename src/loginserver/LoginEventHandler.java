package loginserver;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.smartfoxserver.bitswarm.sessions.ISession;
import com.smartfoxserver.v2.core.ISFSEvent;
import com.smartfoxserver.v2.core.SFSEventParam;
import com.smartfoxserver.v2.db.IDBManager;
import com.smartfoxserver.v2.exceptions.SFSErrorCode;
import com.smartfoxserver.v2.exceptions.SFSErrorData;
import com.smartfoxserver.v2.exceptions.SFSException;
import com.smartfoxserver.v2.exceptions.SFSLoginException;
import com.smartfoxserver.v2.extensions.BaseServerEventHandler;


public class LoginEventHandler extends BaseServerEventHandler {
	@Override
	public void handleServerEvent(ISFSEvent event) throws SFSException {
		// TODO Auto-generated method stub
		trace("Sto chiedendo al server di loggare un utente");
		// Grab parameters from client request
		String userName = (String) event.getParameter(SFSEventParam.LOGIN_NAME);
	    String cryptedPass = (String) event.getParameter(SFSEventParam.LOGIN_PASSWORD);
		//String email = (String)event.getParameter(SFSEventParam.LOGIN_PASSWORD);
		ISession session = (ISession) event.getParameter(SFSEventParam.SESSION);
		trace("Username----------" + userName);
		trace("Password----------" + cryptedPass);
		trace("Session----------" + session);
		
		// Get password from DB
		IDBManager dbManager = getParentExtension().getParentZone().getDBManager();
		Connection connection = null;

		// Grab a connection from the DBManager connection pool
        try {
			connection = dbManager.getConnection();

			// Build a prepared statement
	        PreparedStatement stmt = connection.prepareStatement("SELECT username, password, clan_name FROM Users "
	        		+ "inner join guesswho.clan on guesswho.users.id_user = guesswho.clan.utente_fondatore    "
					+ "or guesswho.users.id_user = guesswho.clan.utente_2 "
					+ "or guesswho.users.id_user = guesswho.clan.utente_3 "
					+ "or guesswho.users.id_user = guesswho.clan.utente_4 "
					+ "or guesswho.users.id_user = guesswho.clan.utente_5 "
					+ "or guesswho.users.id_user = guesswho.clan.utente_6 "
					+ "or guesswho.users.id_user = guesswho.clan.utente_7 "
					+ "or guesswho.users.id_user = guesswho.clan.utente_8 "
					+ "or guesswho.users.id_user = guesswho.clan.utente_9 "
					+ "or guesswho.users.id_user = guesswho.clan.utente_10 "
					+ "or guesswho.users.id_user = guesswho.clan.utente_11 "
					+ "or guesswho.users.id_user = guesswho.clan.utente_12 "
					+ "or guesswho.users.id_user = guesswho.clan.utente_13 "
					+ "or guesswho.users.id_user = guesswho.clan.utente_14 "
					+ "or guesswho.users.id_user = guesswho.clan.utente_15 "
					+ "or guesswho.users.id_user = guesswho.clan.utente_16 "
					+ "or guesswho.users.id_user = guesswho.clan.utente_17 "
					+ "or guesswho.users.id_user = guesswho.clan.utente_18 "
					+ "or guesswho.users.id_user = guesswho.clan.utente_19 "
					+ "or guesswho.users.id_user = guesswho.clan.utente_20 "
	        		+ "where username='"+userName+"'");

		    // Execute query
		    ResultSet res = stmt.executeQuery();
		   // Verify that one record was found
 			if (!res.first())
 			{
 				// This is the part that goes to the client
 				SFSErrorData errData = new SFSErrorData(SFSErrorCode.LOGIN_BAD_USERNAME);
 				errData.addParameter(userName);

 				// Sends response if user gave incorrect user name
 				throw new SFSLoginException("Bad user name: " + userName, errData);
 				
 			}

 			String dbPword = res.getString("password");

			// Verify the secure password
			if (!getApi().checkSecurePassword(session, dbPword, cryptedPass))
			{
				SFSErrorData data = new SFSErrorData(SFSErrorCode.LOGIN_BAD_PASSWORD);
				data.addParameter(userName);
				// Sends response if user gave incorrect password
				throw new SFSLoginException("Login failed for user: "  + userName, data);
			}

        }

        // User name was not found
        catch (SQLException e)
        {
        	SFSErrorData errData = new SFSErrorData(SFSErrorCode.GENERIC_ERROR);
        	errData.addParameter("SQL Error: " + e.getMessage());
        	// Sends response about mysql errors
        	throw new SFSLoginException("A SQL Error occurred: " + e.getMessage(), errData);
        }
        finally {
        	try{
        		connection.close();
        	}catch (SQLException e){
        		throw new SFSLoginException("A SQL Error occurred: " + e.getMessage());
        	}
        }

    }

}