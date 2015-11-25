package com.spazomatic.jobyjob.security;

import java.util.UUID;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.social.facebook.api.Facebook;

import com.spazomatic.jobyjob.db.dao.UsersDao;
import com.spazomatic.jobyjob.db.model.UserProfile;
import com.spazomatic.jobyjob.exceptions.EmailExistsException;
import com.spazomatic.jobyjob.util.Util;

public class AccountConnectionSignUpService implements ConnectionSignUp {

    private static final Logger LOG = LoggerFactory.getLogger(
    		String.format("%s :: %s",Util.LOG_TAG,
    				AccountConnectionSignUpService.class));

    private final UsersDao usersDao;
     
    public AccountConnectionSignUpService(UsersDao usersDao) {
        this.usersDao = usersDao;
    }

    public String execute(Connection<?> connection) {
    	
    	//String userId = UUID.randomUUID().toString();
    	String userId;
    	Object socialConn = connection.getApi();
    		
    	if (socialConn instanceof Facebook) {
 		
    		//TODO: fork spring-social-facebook and remove get address from user object in fetchProfile
    		//      probably in RestTemplate or FacebookTemplate 
			Facebook fbAPI = (Facebook) socialConn;
			String name  = fbAPI.fetchObject("me", String.class, "name");
			String firstName  = fbAPI.fetchObject("me", String.class, "first_name");
			String lastName  = fbAPI.fetchObject("me", String.class, "last_name");
			String email  = fbAPI.fetchObject("me", String.class, "email");
			
			//Locale locale = LocaleContextHolder.getLocale();
			//ApplicationContext context = new ClassPathXmlApplicationContext("locale.xml");							
			//String email  = context.getMessage("giga.email.notverified", null, locale);	
			//((AbstractApplicationContext)context).close();
			
			try {
				JSONParser parser  = new JSONParser();
				JSONObject jo = (JSONObject)parser.parse(name);
				name = (String) jo.get("name");
				jo = (JSONObject)parser.parse(firstName);
				firstName = (String) jo.get("first_name");
				jo = (JSONObject)parser.parse(lastName);
				lastName = (String) jo.get("last_name");
				jo = (JSONObject)parser.parse(email);
				email = (String) jo.get("email");				
			} catch (ParseException e) {
				LOG.error(String.format("ERROR PARSING JSON %s : ", e.getMessage()));
			}
			
			userId = email;
			LOG.debug(String.format("Creating FACEBOOK PROFile :: %s", userId));
			UserProfile u = new UserProfile(
					userId,name,firstName,lastName,name,email);			
			try {
				usersDao.createUser(userId, new UserProfile(userId, 
						u.getName(), u.getFirstName(), u.getLastName(), 
						u.getUsername(), u.getEmail()));
			} catch (EmailExistsException e) {
				LOG.error(String.format("Duplicate Email Error :: %s",e.getMessage()));
			}

			LOG.debug(String.format("FACEBOOK USER: %s", 
					u.toString()));  		 
		}else{
    	
			org.springframework.social.connect.UserProfile profile = connection.fetchUserProfile();
			//TODO: All Social Connections should have email available here 
			//but currently do not, wtf? fixit...
			//Also when duplicate email happens, should user be provided option to add as a connection?
			userId = profile.getEmail() != null ? profile.getEmail() : 
				profile.getUsername() != null ? profile.getUsername() : 
					UUID.randomUUID().toString();
	        try {
				usersDao.createUser(userId, new UserProfile(userId, profile));
			} catch (EmailExistsException e) {
				LOG.error(String.format("Duplicate Email Error :: %s",e.getMessage()));
			}
	        
		}

        return userId;
    }
}