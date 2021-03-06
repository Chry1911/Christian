package clanserver;

import java.sql.Connection;
import java.sql.SQLException;

import com.smartfoxserver.v2.db.IDBManager;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.extensions.BaseClientRequestHandler;

public class ClanDetailHandler extends BaseClientRequestHandler{


	Object obj = null;
	private Connection connection;
	
	public void handleClientRequest(User user, ISFSObject params) {
		int id_clan = params.getInt("clan_id");
		
		trace("Sto richiedendo al server i dettagli di un clan");
		
		IDBManager dbmanager = getParentExtension().getParentZone().getDBManager();
		connection = null;
		try{
			trace("Ho fatto l'accesso per richiedere al server la mia query");
			connection = dbmanager.getConnection();
			
			ISFSArray arr = dbmanager.executeQuery("select users.id_user,users.username, users.trofei, "
					+ "users.position, guesswho.clan.*, guesswho.clan_users.ruolo, guesswho.role.description from users "
					+ "INNER JOIN clan_users ON clan_users.id_user = users.id_user "
                    + "INNER JOIN clan ON clan.id_clan = clan_users.id_clan "
	        		+ "INNER JOIN role ON role.id_role = clan_users.ruolo "
					+ "where guesswho.clan.id_clan = ? order by users.trofei desc "
					,new Object[] {id_clan});
			if (arr.size() > 0)
			{
			  SFSObject result = new SFSObject();
			  result.putSFSArray("success", arr);
			  result.putInt("membriclan", arr.size());
			  send("clandetail", result, user);
			}
			
			
		}catch (SQLException ex) {
			ISFSObject error = new SFSObject();
			error.putUtfString("error", "MySQL error");
			send("clandetail" , error, user);
			ex.printStackTrace();
	}
		finally{
			try{
				connection.close();
			}catch (SQLException e){
        		trace("A SQL Error occurred: " + e.getMessage());
        	}
        }
		}
		
}
