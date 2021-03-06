package clanserver;

import java.sql.Connection;
import java.sql.SQLException;

import com.smartfoxserver.v2.db.IDBManager;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSArray;
import com.smartfoxserver.v2.entities.data.ISFSObject;
//import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;
import com.smartfoxserver.v2.extensions.BaseClientRequestHandler;

public class TopClanHandler extends BaseClientRequestHandler{


	Object obj = null;
	private Connection connection;
	public void handleClientRequest(User user, ISFSObject params) {
	
		trace("Sto richiedendo al server i 100 migliori clan");
		
		IDBManager dbmanager = getParentExtension().getParentZone().getDBManager();
		String nation = params.getUtfString("nation");
		String search = params.getUtfString("search");
		connection = null;
		try{
			trace("Ho fatto l'accesso per richiedere al server la mia query");
			connection = dbmanager.getConnection();
			
			ISFSArray arr = dbmanager.executeQuery("SELECT clan.id_clan, clan.stemma, clan.clan_name, clan.maxusers, clan.trofei_total, "
					+ "Left(guesswho.clan.descrizione, 21) as preview, "
					+ "count(guesswho.clan_users.id_user) as numutenti "
					+ "FROM guesswho.clan "
					+ "LEFT JOIN clan_users ON clan_users.id_clan = clan.id_clan "
                    + "LEFT JOIN users ON users.id_user = clan_users.id_user "
					+ "where guesswho.clan.position Like ? and clan_name Like ? "
					+ "group by guesswho.clan.id_clan order by guesswho.clan.trofei_total desc, "
					+ "guesswho.clan.clan_name limit 100", 
					new Object[] {"%"+ nation + "%", "%" + search + "%"});
			if (arr.size() > 0)
			{
			  SFSObject result = new SFSObject();
			  result.putSFSArray("success", arr);
			  send("topclans", result, user);
			}else{
				 SFSObject result2 = new SFSObject();
				  result2.putUtfString("nosuccess", "Nessuna corrispondenza trovata");
				  send("topclans", result2, user);
			}
			
			
		}catch (SQLException ex) {
			ISFSObject error = new SFSObject();
			error.putUtfString("error", "MySQL error");
			send("topclans" , error, user);
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
