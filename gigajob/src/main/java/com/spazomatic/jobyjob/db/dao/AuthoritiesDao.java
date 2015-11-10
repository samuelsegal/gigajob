package com.spazomatic.jobyjob.db.dao;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
@Repository
public class AuthoritiesDao {

	private JdbcTemplate jdbcTemplate;

	@Autowired
	public AuthoritiesDao(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	
}
