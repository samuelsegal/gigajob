package com.spazomatic.jobyjob.util;

import java.security.Principal;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import com.spazomatic.jobyjob.db.dao.DataDao;
import com.spazomatic.jobyjob.db.dao.UsersDao;
import com.spazomatic.jobyjob.db.model.Authority;
import com.spazomatic.jobyjob.db.model.User;
import com.spazomatic.jobyjob.db.model.UserConnection;
import com.spazomatic.jobyjob.db.model.UserProfile;

@Component
public class SocialControllerUtil {

    private static final Logger LOG = LoggerFactory.getLogger(
    		String.format("%s :: %s", Util.LOG_TAG, 
    				SocialControllerUtil.class));
    public static final String USER_CONNECTIONS = "GIGA_USER_CONNECTIONS";
    public static final String USER_CONNECTION = "GIGA_USER_CONNECTION";
    public static final String USER_PROFILE = "GIGA_USER_PROFILE";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DataDao dataDao;

    @Autowired
    private UsersDao usersDao;

    public void dumpDbInfo() {
        try {
            Connection c = jdbcTemplate.getDataSource().getConnection();
            DatabaseMetaData md = c.getMetaData();
            ResultSet rs = md.getTables(null, null, "%", null);
            
            LOG.debug(String.format("DB Connection INFO Catalog: %s", c.getCatalog()));
            while (rs.next()) {
                if (rs.getString(4).equalsIgnoreCase("TABLE")) {

                    LOG.debug("TABLE NAME = " + rs.getString(3) + ", Cat = " + rs.getString(1) + ", Schema = " + rs.getString(2) + ", Type = " + rs.getString(4));

                    String tableName = rs.getString(3);
                    List<String> sl = jdbcTemplate.query("select * from " + tableName,
                        new RowMapper<String>() {
                            public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                            StringBuffer sb = new StringBuffer();
                            for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                                sb.append(rs.getString(i)).append(' ');
                            }
                            return sb.toString();
                            }
                        });
                    LOG.debug("No of rows: {}", sl.size());
                    for (String s: sl) {
                        LOG.debug(s);
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setModel(HttpServletRequest request, Principal currentUser, Model model) {

        // SecurityContext ctx = (SecurityContext) session.getAttribute("SPRING_SECURITY_CONTEXT");

        String userId = currentUser == null ? null : currentUser.getName();
        String path = request.getRequestURI();
        HttpSession session = request.getSession();

        UserConnection connection = null;
        Map<String, UserConnection> userConnections = null;
        UserProfile profile = null;
        String displayName = null;
        String data = null;

        // Collect info if the user is logged in, i.e. userId is set
        if (userId != null) {

            // Get the current UserConnection from the http session
            connection = getUserConnection(session, userId);
            
            //SLoppy but USERCONNECTIONS is set to session in getUserConnection
            userConnections = (Map<String, UserConnection>) session.getAttribute(USER_CONNECTIONS);
            
            // Get the current UserProfile from the http session
            profile = getUserProfile(session, userId);

            // Compile the best display name from the connection and the profile
            displayName = getDisplayName(connection, profile);

            // Get user data from persistence storage
            data = dataDao.getData(userId);
            

        }

        Throwable exception = (Throwable)session.getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);

        // Update the model with the information we collected
        model.addAttribute("exception",               exception == null ? null : exception.getMessage());
        model.addAttribute("currentUserId",           userId);
        model.addAttribute(Util.GIGA_USER,            profile);
        model.addAttribute(Util.GIGA_USER_CONNECTION, connection != null ? connection : null);
        model.addAttribute(Util.GIGA_USER_CONNECTIONS, userConnections);
        model.addAttribute("currentUserDisplayName",  displayName);
        model.addAttribute("currentData",             data);

        if (LOG.isDebugEnabled()) {
            logInfo(request, model, userId, path, session);
        }
    }

    public void updateSession(HttpServletRequest request, String userId){
    	 HttpSession session = request.getSession();
    	 //TODO: optimize for when there bazillions...
    	 UserProfile userProfile  = usersDao.getUserProfile(userId);
         
         User user = usersDao.findUserByName(userId);
         List<String> profileRoles = new ArrayList<>();
         for(Authority auth : user.getRoles()){
         	profileRoles.add(auth.getAuthority());
         }
         userProfile.setRoles(profileRoles);
         
         session.setAttribute(USER_PROFILE, userProfile);
    }
    
    
    protected void logInfo(HttpServletRequest request, Model model, String userId, String path, HttpSession session) {
        // Log the content of the model
        LOG.debug("Path: " + path + ", currentUserId: " + userId);

        LOG.debug("Non-null request-attributes:");
        for (Enumeration<String> rane = request.getAttributeNames(); rane.hasMoreElements();) {
            String key = rane.nextElement();
            Object value = session.getAttribute(key);
            if (value != null) {
                LOG.debug(" - " + key + " = " + value);
            }
        }

        LOG.debug("Session-attributes:");
        for (Enumeration<String> sane = session.getAttributeNames(); sane.hasMoreElements();) {
            String key = sane.nextElement();
            LOG.debug(" - " + key + " = " + session.getAttribute(key));
        }

        Set<Map.Entry<String, Object>> me = model.asMap().entrySet();
        LOG.debug("ModelElements (" + me.size() + "):");
        for (Map.Entry<String, Object> e: me) {
            LOG.debug(" - " + e.getKey() + " = " + e.getValue());
        }
    }

    /**
     * Get the current UserProfile from the http session
     *
     * @param session
     * @param userId
     * @return
     */
    protected UserProfile getUserProfile(HttpSession session, String userId) {
        UserProfile profile = (UserProfile) session.getAttribute(USER_PROFILE);

        // Reload from persistence storage if not set or invalid (i.e. no valid userId)
        if (profile == null || !userId.equals(profile.getUserId())) {
            profile = usersDao.getUserProfile(userId);
            //get User for authorities
            User user = usersDao.findUserByName(userId);
            List<String> profileRoles = new ArrayList<>();
            for(Authority auth : user.getRoles()){
            	profileRoles.add(auth.getAuthority());
            }
            profile.setRoles(profileRoles);
            
            session.setAttribute(USER_PROFILE, profile);
        }
        return profile;
    }

    /**
     * Get the current UserConnection from the http session
     *
     * @param session
     * @param userId
     * @return
     */
    public UserConnection getUserConnection(HttpSession session, String userId) {
        UserConnection connection = null;
        connection = (UserConnection) session.getAttribute(USER_CONNECTION);

        // Reload from persistence storage if not set or invalid (i.e. no valid userId)
        if (connection == null || !userId.equals(connection.getUserId())) {

        	Map<String,UserConnection> userConnections = usersDao.getUserConnections(userId);
        	List<UserConnection> conns = new ArrayList<>();
        	userConnections.forEach( (k,v) -> {
        		LOG.debug(String.format("UserConnection Provider %s", 
        				v.getProviderId()));
        		conns.add(v); 
        	});
        	//TODO: Have a default preferred with ability to select preferred connection to define profile.

        	connection = !conns.isEmpty() ? conns.get(0) : new UserConnection();
        	session.setAttribute(USER_CONNECTION, connection);
        	session.setAttribute(USER_CONNECTIONS, userConnections);
        }
        return connection;
    }

    /**
     * Compile the best display name from the connection and the profile
     *
     * @param connection
     * @param profile
     * @return
     */
    protected String getDisplayName(UserConnection connection, UserProfile profile) {

        // The name is set differently in different providers so we better look in both places...
        if (connection.getDisplayName() != null) {
            return connection.getDisplayName();
        } else {
            return profile.getName();
        }
    }
}
