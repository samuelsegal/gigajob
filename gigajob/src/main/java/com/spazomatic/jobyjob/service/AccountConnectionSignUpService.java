package com.spazomatic.jobyjob.service;

import java.util.Locale;
import java.util.UUID;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.social.facebook.api.Facebook;

import com.spazomatic.jobyjob.db.dao.UsersDao;
import com.spazomatic.jobyjob.db.model.UserProfile;
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
    	
    	String userId = UUID.randomUUID().toString();
    	Object socialConn = connection.getApi();
    		
    	if (socialConn instanceof Facebook) {
 		
    		//TODO: fork spring-social-facebook and remove get address from user object in fetchProfile
    		//      probably in RestTemplate or FacebookTemplate 
			Facebook fbAPI = (Facebook) socialConn;
			String name  = fbAPI.fetchObject("me", String.class, "name");
			String firstName  = fbAPI.fetchObject("me", String.class, "first_name");
			String lastName  = fbAPI.fetchObject("me", String.class, "last_name");
			
			
			Locale locale = LocaleContextHolder.getLocale();
			ApplicationContext context = new ClassPathXmlApplicationContext("locale.xml");							
			String email  = context.getMessage("giga.email.notverified", null, locale);	
			((AbstractApplicationContext)context).close();
			
			try {
				JSONParser parser  = new JSONParser();
				JSONObject jo = (JSONObject)parser.parse(name);
				name = (String) jo.get("name");
				jo = (JSONObject)parser.parse(firstName);
				firstName = (String) jo.get("first_name");
				jo = (JSONObject)parser.parse(lastName);
				lastName = (String) jo.get("last_name");
			} catch (ParseException e) {
				LOG.error(String.format("ERROR PARSING JSON %s : ", e.getMessage()));
			}
			
			UserProfile u = new UserProfile(
					userId,name,firstName,lastName,name,email);			
			usersDao.createUser(userId, new UserProfile(userId, 
					u.getName(), u.getFirstName(), u.getLastName(), 
					u.getUsername(), u.getEmail()));

			LOG.debug(String.format("FACEBOOK USER: %s", 
					u.toString()));  		 
		}else{
    	
			org.springframework.social.connect.UserProfile profile = connection.fetchUserProfile();
	        usersDao.createUser(userId, new UserProfile(userId, profile));
	        
		}

        return userId;
    }
}