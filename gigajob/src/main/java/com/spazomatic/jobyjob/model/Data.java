package com.spazomatic.jobyjob.model;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the Data database table.
 * 
 */
@Entity
@NamedQuery(name="Data.findAll", query="SELECT d FROM Data d")
public class Data implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	private String userId;

	private String data;

	public Data() {
	}

	public String getUserId() {
		return this.userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getData() {
		return this.data;
	}

	public void setData(String data) {
		this.data = data;
	}

}