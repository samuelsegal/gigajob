package com.spazomatic.jobyjob.service;

import java.util.Locale;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.i18n.LocaleContextHolder;
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
		    Locale locale = LocaleContextHolder.getLocale();
			ApplicationContext context 
			= new ClassPathXmlApplicationContext("locale.xml");		    
			String email  = context.getMessage("giga.email.notverified", null, locale);
			String name  = fbAPI.fetchObject("me", String.class, "name");
			String firstName  = fbAPI.fetchObject("me", String.class, "first_name");
			String lastName  = fbAPI.fetchObject("me", String.class, "last_name");
			String username  = fbAPI.fetchObject("me", String.class, "name");
			
			UserProfile u = new UserProfile(userId,name,firstName,lastName,username,email);
			
			usersDao.createUser(userId, new UserProfile(userId, u.getName(), u.getFirstName(), u.getLastName(), u.getUsername(), u.getEmail()));
			//profile = new UserProfileBuilder()
			//profile.getEmail();
			LOG.debug(String.format("FACEBOOK API: %s : User %s", 
					"WTFFF", 
					u.toString()));  		
		}else{
    	
			org.springframework.social.connect.UserProfile profile = connection.fetchUserProfile();
	        LOG.debug("Created user-id: " + userId);
	        usersDao.createUser(userId, new UserProfile(userId, profile));
	        
		}
       
        
        
        // TODO: Or simply use: r = new Random(); r.nextInt(); ???

        return userId;
    }
}