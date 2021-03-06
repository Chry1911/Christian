package registerserver;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
//import java.util.Random;

import com.smartfoxserver.v2.db.IDBManager;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.entities.data.ISFSObject;
import com.smartfoxserver.v2.entities.data.SFSArray;
import com.smartfoxserver.v2.entities.data.SFSObject;

import com.smartfoxserver.v2.extensions.BaseClientRequestHandler;


public class UserRegistrationHandler extends BaseClientRequestHandler {
	
	Object obj = null;
	Date date;
	private Connection connection;
	public void handleClientRequest(User user, ISFSObject params){

		trace("Sto chiedendo di registrare uno user al server");
		
		
		String first_name = params.getUtfString("first_name");
		String last_name = params.getUtfString("last_name");
		String birth_day = params.getUtfString("date");
		String name = params.getUtfString("name");
        String password = params.getUtfString("password");
		String email = params.getUtfString("email");
		int trofei = params.getInt("trofei");
		int gemme = params.getInt("gems");
		int gold = params.getInt("gold");
		String nation = params.getUtfString("nation");
		
		
		RandomString captcha = new RandomString();
	    String code = captcha.toString();
		
		IDBManager dbmanager = getParentExtension().getParentZone().getDBManager();
		
		connection = null;
		SFSArray ar ;
			try {
				trace("sono entrato nel primo try");
				connection = dbmanager.getConnection();
				//obj = dbmanager.executeQuery(sql2, new Object[] {1});
				
				
				obj = dbmanager.executeQuery("Select * from users where email = ?", 
						new Object[]{email});
				
				ar = (SFSArray)obj;
				if(ar.size() >= 1){
					trace("Errore email gi� presente nel sistema");
					ISFSObject error = new SFSObject();
					error.putUtfString("error", "Email gi� presente nel database");
					send("register" , error, user);
					
				}}	catch(SQLException e) {
					
					ISFSObject error = new SFSObject();
					error.putUtfString("error", "MySQL update error");
					send("register" , error, user);
				

				
		}
			
			try{
				
				obj = dbmanager.executeQuery("Select * from users where username = ? ",
						new Object[]{name});
				
				ar = (SFSArray)obj;
				if(ar.size() >= 1){
					trace("Errore username gi� presente nel sistema");
					ISFSObject error = new SFSObject();
					error.putUtfString("error", "Username gi� presente nel database");
					send("register" , error, user);
					
				}}
				
				catch(SQLException e) {
					
					ISFSObject error = new SFSObject();
					error.putUtfString("error", "MySQL update error");
					send("register" , error, user);
				

				
		}
				
				
				try{
				
				obj = dbmanager.executeQuery("SELECT * FROM users WHERE  email=? or username =?", 
						new Object[] {email,name}); 
				
				trace(obj.toString());
				
				ar = (SFSArray) obj;
				
				//trace(condition.toString() + "condizione");
				
				if(ar.size() >= 1){
					trace("Errore account gi� presente nel sistema");
					ISFSObject error = new SFSObject();
					error.putUtfString("error", "account gi� esistente nel db");
					send("register" , error, user);
					//return;
					return;
				}
				
				if(ar.size() == 0) {
					trace("registriamo l'utente nel nostro database");
					
				
					try{
						trace("sono entrato nel secondo try");
						
					    DateFormat readFormat = new SimpleDateFormat( "yyyy/mm/dd");
						//DateFormat readFormat = new SimpleDateFormat ( "yyyy-MM-dd HH:mm:ss");

					    DateFormat writeFormat = new SimpleDateFormat( "yyyy-mm-dd HH:MM:SS");
					    
					  
					    trace(birth_day);
					    
					       date = readFormat.parse( birth_day );
					      
					   trace(date);

					    String formattedDate = "";
					    if( date != null ) {
					    formattedDate = writeFormat.format( date );
					    trace(formattedDate);
					    }
					    
					    if(trofei >= 0){
					    	trofei = 0;
					    }
					    
					    if(gemme >=0){
					    	gemme = 100;
					    }
					    
					    if(gold >= 0){
					    	gold = 0;
					    }
					    
					    trace("stampiamo la data " + formattedDate);

						String sql = "INSERT into users(first_name, last_name, date_of_birth, username, password, email, trofei,gems,gold, position, captcha) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
				          obj = dbmanager.executeInsert(sql,
				                     new Object[] {first_name,last_name, formattedDate, name, password, email, trofei, gemme, gold, nation, code});
				          
				          ISFSObject success = new SFSObject();
				      	success.putUtfString("success" ,"User successfully registrated");
				      	send("register", success, user);
				      	
				      	
				      	
				      	
				      	trace("il server ora crea il deck per lo user");
				      	
				      	String sql2 = "Select id_user from users where email = '" + email + "'";
				      	
				      	PreparedStatement stmt = connection.prepareStatement(sql2);

						  ResultSet res = stmt.executeQuery();
						    
						  while(res.next()){
							  
							  trace("sono entrato nel while");
							  int id_user = res.getInt("id_user");
							  
							  String deckname = "deck1";
							  int selecteddeck = 1;
							  String[]cards = generateCards(4);
							  
							  trace(id_user + "id dell utente");
							  trace(deckname + "nome deck utente");
							  
							  
							  String carte = "";
							  
							  int livello = 1;
	    					  int copie = 1;
	    					  int carta = 0;
	    					  int tipocarta = 1;
							  
	    					  for(int i= 0; i < cards.length; i++){
								  trace(cards[i]);
								  trace("indice di posizione " + i);
								  carte = carte + cards[i] + ",";
								  

	    						  carta = Integer.parseInt(cards[i]);
	    						 
	    						  if(i == 3) {
	    							  tipocarta = 2;
	    						  }
	    						
		    						  
		    						 
	    						  String query = "INSERT into collection"
		    						  		+ "(id_user, id_carta, tipocarta, n_copie_carta, livello) "
		  							  		+ "values "
		  							  		+ "(" + id_user + "," + carta + "," + tipocarta + ", " 
		  							  		+ copie + ", " + livello + ")";
		    					  PreparedStatement pstmt = connection.prepareStatement(query);
		    					  pstmt.executeUpdate(query);
		    					  
		    					  trace("stampiamo la query di insert collection" + query);
							  }
							  
							
							  carte = carte.substring(0, carte.length() - 1);
							  
							  trace(carte + "carte");
							  trace(selecteddeck + "deck selezionato dall'utente");
							  
							  int slotdeck = 1;
							  
							  String sql3 = "INSERT into decks(id_user, deck_name, cards, selected_deck, id_slot) "
							  		+ "values (" + id_user + ",'" + deckname + "','" + carte + "', " + selecteddeck + ", " + slotdeck + ")";
							  

							  trace("query " + sql3);
							  
							  
							  
							  
							  PreparedStatement stmt4 = connection.prepareStatement(sql3);
	    						
	    					  stmt4.executeUpdate();
	    					  
	    			
					          //ISFSObject result = new SFSObject();
					      	success.putUtfString("success" ,"Deck created");
					      	send("register", success, user);
						  }
					} 
					
		catch(SQLException e) {
				
				ISFSObject error = new SFSObject();
				error.putUtfString("error", "MySQL update error");
				send("register" , error, user);
			    e.printStackTrace();

			
	} catch (ParseException e) {
			trace("vediamo l'errore dato " + date);
			e.printStackTrace();
		}
					
				}
			} catch (SQLException e1) {
				ISFSObject error = new SFSObject();
				error.putUtfString("error", "MySQL error");
				send("register" , error, user);
				e1.printStackTrace();
			}
			
			finally {
	        	try{
	        		connection.close();
	        	}catch (SQLException e){
	        		trace("A SQL Error occurred: " + e.getMessage());
	        	}
	        }
			
			
			
				}
	
	public String[] generateCards(int limit){
		String[] arr = new String[limit];
		int[] val = new int[limit];
		
		for (int i= 0; i < arr.length; i++){
			
				val[i] = (int)(10 * Math.random() + 1);
					arr[i] = String.valueOf(val[i]);
		
			trace("valore array " + arr[i]);
			
		}
		return arr;
		
	}
	
	
	}

	
