package com.spazomatic.jobyjob.db.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

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
import com.spazomatic.jobyjob.db.model.User;
import com.spazomatic.jobyjob.db.model.UserConnection;
import com.spazomatic.jobyjob.db.model.UserProfile;
import com.spazomatic.jobyjob.util.Util;

@Repository
public class UsersDao {

    private static final Logger LOG = LoggerFactory.getLogger(Util.LOG_TAG);

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public UsersDao(DataSource dataSource)
    {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
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
			        		String id = rs.getString("username");
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
    public void addRole(String userId, String roleName) {
        if (LOG.isDebugEnabled()) {
            LOG.debug(String.format("SQL INSERT ON users, authorities: %s with role: %s",userId,roleName));
        }

        jdbcTemplate.update("INSERT into authorities(username,authority) values(?,?)",userId,roleName);
    }
    public void createUser(String userId, UserProfile profile) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("SQL INSERT ON users, authorities and UserProfile: " + userId + " with profile: " +
                profile.getEmail() + ", " +
                profile.getFirstName() + ", " +
                profile.getLastName() + ", " +
                profile.getName() + ", " +
                profile.getUsername());
        }

        jdbcTemplate.update("INSERT into users(username,password,enabled) values(?,?,true)",userId, RandomStringUtils.randomAlphanumeric(8));
        jdbcTemplate.update("INSERT into authorities(username,authority) values(?,?)",userId,"USER");
        jdbcTemplate.update("INSERT into UserProfile(userId, email, firstName, lastName, name, username) values(?,?,?,?,?,?)",
            userId,
            profile.getEmail(),
            profile.getFirstName(),
            profile.getLastName(),
            profile.getName(),
            profile.getUsername());
    }
}
