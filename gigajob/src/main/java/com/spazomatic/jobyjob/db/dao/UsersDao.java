package com.spazomatic.jobyjob.db.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.sql.DataSource;
import javax.transaction.Transactional;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.spazomatic.jobyjob.db.model.Authority;
import com.spazomatic.jobyjob.db.model.PasswordResetToken;
import com.spazomatic.jobyjob.db.model.User;
import com.spazomatic.jobyjob.db.model.UserConnection;
import com.spazomatic.jobyjob.db.model.UserProfile;
import com.spazomatic.jobyjob.db.model.VerificationToken;
import com.spazomatic.jobyjob.exceptions.EmailExistsException;
import com.spazomatic.jobyjob.exceptions.UsernameAlreadyInUseException;
import com.spazomatic.jobyjob.util.Util;

@Repository
@Transactional
public class UsersDao {

    private static final Logger LOG = LoggerFactory.getLogger(
    		String.format("%s :: %s",Util.LOG_TAG, UsersDao.class));
    @Autowired private PasswordResetTokenRepository passwordTokenRepository;
    @Autowired private VerificationTokenRepository tokenRepository;
      
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public UsersDao(DataSource dataSource)
    {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public VerificationToken getVerificationToken(final String VerificationToken) {
        return tokenRepository.findByToken(VerificationToken);
    }    
    
    public UserProfile registerNewUserAccount(final UserProfile userProfile) throws EmailExistsException {
        
    	if (emailExist(userProfile.getEmail())) {
            throw new EmailExistsException("There is an account with that email adress: " + userProfile.getEmail());
        }
    	
        createUser(userProfile.getUserId(), userProfile);
        return userProfile;
       
    }
    public UserProfile getUserProfile(final String userId) {
    	String query = "select * from UserProfile where userId = ?";
        LOG.debug(String.format("SQL SELECT ON UserProfile: %s : %s",userId,query));

        return jdbcTemplate.queryForObject(query,
            new RowMapper<UserProfile>() {
                public UserProfile mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new UserProfile(
                    userId,
                    rs.getString("name"),
                    rs.getString("firstName"),
                    rs.getString("lastName"),
                    rs.getString("email"),
                    rs.getString("username"));
                }
            }, userId);
    }
    public User findUserByName(String name){
    	String query = "select * from  users u JOIN authorities a on a.username = u.username where u.username = ?";
        LOG.debug(String.format("SQL SELECT ON UserProfile: %s : %s",name,query));
        return jdbcTemplate.query(query, 
        		new ResultSetExtractor<User>() {

					@Override
					public User extractData(ResultSet rs) throws SQLException, DataAccessException {
			        	List<Authority> userAuthorities = new ArrayList<>();
			        	User user  = null;
			        	while (rs.next()) {
			        		Authority auth = new Authority();			        	
			        	    auth.setAuthority(rs.getString("authority"));
			        	    if (user == null) {
			        	        user = new User();
			        	        user.setUsername(rs.getString("username"));
			        	        user.setEnabled(new Byte("1"));			        	       
			        	    }
			        	    userAuthorities.add(auth);
			        	}
			        	user.setRoles(userAuthorities);
			        	return user;
					}

        },name);

    }
    public PasswordResetToken getPasswordResetToken(final String token) {
        return passwordTokenRepository.findByToken(token);
    }
    public UserProfile findUserByEmail(String email){
    	String query = "select * from  UserProfile where email = ?";
        LOG.debug(String.format("SQL SELECT ON UserProfile: %s : %s",email,query));
        return jdbcTemplate.queryForObject(query,
                new RowMapper<UserProfile>() {
                    public UserProfile mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return new UserProfile(
                        rs.getString("userId"),
                        rs.getString("name"),
                        rs.getString("firstName"),
                        rs.getString("lastName"),
                        rs.getString("email"),
                        rs.getString("username"));
                    }
                }, email);

    }

    public void createPasswordResetTokenForUser(final User user, final String token) {
        final PasswordResetToken myToken = new PasswordResetToken(token, user);
        passwordTokenRepository.save(myToken);
    }
    public void changeUserPassword(final User user, final String password) {
        //user.setPassword(passwordEncoder.encode(password));
    	//TODO:encode pw, pass User insterad of userprofile

    	user.setPassword(password);
        updateUserPassword(user);
    }    
    public UserConnection getUserConnection(final String userId) {
        LOG.debug("SQL SELECT ON UserConnection: {}", userId);

        return jdbcTemplate.queryForObject("select * from UserConnection where userId = ?",
            new RowMapper<UserConnection>() {
                public UserConnection mapRow(ResultSet rs, int rowNum) throws SQLException {
                return new UserConnection(
                    userId,
                    rs.getString("providerId"),
                    rs.getString("providerUserId"),
                    rs.getInt("rank"),
                    rs.getString("displayName"),
                    rs.getString("profileUrl"),
                    rs.getString("imageUrl"),
                    rs.getString("accessToken"),
                    rs.getString("secret"),
                    rs.getString("refreshToken"),
                    rs.getLong("expireTime"));
                }
            }, userId);
    }
    public Map<String,UserConnection> getUserConnections(final String userId) {
        LOG.debug("SQL SELECT ON UserConnection: {}", userId);
        return jdbcTemplate.query("select * from UserConnection where userId = ?", 
        		new ResultSetExtractor<Map<String, UserConnection>>(){
		            @Override
		            public Map<String,UserConnection> extractData(ResultSet rs) throws SQLException,DataAccessException {
		                HashMap<String,UserConnection> mapRet= new HashMap<String,UserConnection>();
		                while(rs.next()){
		                	UserConnection userConnection = new UserConnection(
		                            userId,
		                            rs.getString("providerId"),
		                            rs.getString("providerUserId"),
		                            rs.getInt("rank"),
		                            rs.getString("displayName"),
		                            rs.getString("profileUrl"),
		                            rs.getString("imageUrl"),
		                            rs.getString("accessToken"),
		                            rs.getString("secret"),
		                            rs.getString("refreshToken"),
		                            rs.getLong("expireTime"));                	
		                	String providerId = rs.getString("providerId");
		                    mapRet.put(providerId,userConnection);
		                }
		                return mapRet;
		            }
		        },userId);
    }
    public void addRole(String userId, String roleName) {
        if (LOG.isDebugEnabled()) {
            LOG.debug(String.format("SQL INSERT ON users, authorities: %s with role: %s",userId,roleName));
        }

        jdbcTemplate.update("INSERT into authorities(username,authority) values(?,?)",userId,roleName);
    }
    public void createUser(String userId, UserProfile profile) throws EmailExistsException{
        if (LOG.isDebugEnabled()) {
            LOG.debug("SQL INSERT ON users, authorities and UserProfile: " + userId + " with profile: " +
                profile.getEmail() + ", " +
                profile.getFirstName() + ", " +
                profile.getLastName() + ", " +
                profile.getName() + ", " +
                profile.getUsername());
        }
        try{
	        jdbcTemplate.update("INSERT into users(username,password,enabled) values(?,?,true)",userId,RandomStringUtils.randomAlphanumeric(8));       
	        jdbcTemplate.update("INSERT into authorities(username,authority) values(?,?)",userId,"USER");
	        jdbcTemplate.update("INSERT into UserProfile(userId, email, firstName, lastName, name, username) values(?,?,?,?,?,?)",
	            userId,
	            profile.getEmail(),
	            profile.getFirstName(),
	            profile.getLastName(),
	            profile.getName(),
	            profile.getUsername());
        }catch(Exception ex){
        	LOG.error(String.format("ERROR creating user, email %s already exists. ERROR Message %s", userId, ex.getMessage()));
        	throw new EmailExistsException(String.format("ERROR creating user, email %s already exists", userId));
        }
    }
    public void createUser(String userId, UserProfile profile, User user) throws EmailExistsException, UsernameAlreadyInUseException{
        if (LOG.isDebugEnabled()) {
            LOG.debug("SQL INSERT ON users, authorities and UserProfile: " + userId + " with profile: " +
                profile.getEmail() + ", " +
                profile.getFirstName() + ", " +
                profile.getLastName() + ", " +
                profile.getName() + ", " +
                profile.getUsername());
        }
        String pw = user.getPassword();
        try{
	        jdbcTemplate.update("INSERT into users(username,password,enabled) values(?,?,true)",userId,pw);
	        jdbcTemplate.update("INSERT into authorities(username,authority) values(?,?)",userId,"USER");
	        jdbcTemplate.update("INSERT into UserProfile(userId, email, firstName, lastName, name, username) values(?,?,?,?,?,?)",
	            userId,
	            profile.getEmail(),
	            profile.getFirstName(),
	            profile.getLastName(),
	            profile.getName(),
	            profile.getUsername());
        }catch(Exception ex){
        	LOG.error(String.format("ERROR creating user, email %s already exists. ERROR Message %s", userId, ex.getMessage()));
        	throw new EmailExistsException(String.format("ERROR creating user, email %s already exists", userId));
        }
    }
    public void updateUser(UserProfile up, String userId) throws DataAccessException{

        jdbcTemplate.update("UPDATE UserProfile SET name = ?, firstName = ?, lastName = ?, username = ? WHERE userId = ?",
        		up.getName(), 
        		up.getFirstName(), 
        		up.getLastName(),
        		up.getUsername(), 
        		userId);
    	if (LOG.isDebugEnabled()) {
            LOG.debug(String.format("SQL :: %s FOR USerProfile %s",jdbcTemplate.toString(), up));
        }        
    }
    public void updateUserPassword(User user) throws DataAccessException{

        jdbcTemplate.update("UPDATE users SET password = ? WHERE username = ?",
        		user.getPassword(), 
        		user.getUsername());
    	if (LOG.isDebugEnabled()) {
            LOG.debug(String.format("SQL :: %s FOR user %s",jdbcTemplate.toString(), user));
        }        
    }
    
    private boolean emailExist(final String email) {
        final UserProfile user = findUserByEmail(email);
        if (user != null) {
            return true;
        }
        return false;
    }

	public User saveRegisteredUser(User user) throws EmailExistsException{
		user.setEnabled(new Byte(""));
        jdbcTemplate.update("UPDATE users SET enabled = true WHERE username = ?",
        		user.getUsername());
    	if (LOG.isDebugEnabled()) {
            LOG.debug(String.format("SQL :: %s FOR user %s",jdbcTemplate.toString(), user));
        }        	
    	return user;
	}

    public void createVerificationTokenForUser(final User user, final String token) {
        final VerificationToken myToken = new VerificationToken(token, user);
        tokenRepository.save(myToken);
    }


    public VerificationToken generateNewVerificationToken(final String existingVerificationToken) {
        VerificationToken vToken = tokenRepository.findByToken(existingVerificationToken);
        vToken.updateToken(UUID.randomUUID().toString());
        vToken = tokenRepository.save(vToken);
        return vToken;
    }
    public User getUser(final String verificationToken) {
        final User user = tokenRepository.findByToken(verificationToken).getUser();
        return user;
    }
}
