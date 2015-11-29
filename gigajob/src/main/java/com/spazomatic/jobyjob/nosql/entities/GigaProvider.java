package com.spazomatic.jobyjob.nosql.entities;

import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;

import com.google.api.client.util.DateTime;

public class GigaProvider {
    
	@Id private String id;
      
    private String userId;
    private String title;
    private String description;
    private Boolean active;
    private String providerName;
    
	@GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    private double[] location;	
	private String formattedAddress;
	private IpLoc ipLoc;
    private List<Tag> tags;
    
    @Transient Map<String, byte[]> imgFiles;   
    @CreatedDate private DateTime createdDate;
    
	public GigaProvider() {
	}

	public GigaProvider(String id, String userId, String title, String description, Boolean active, double[] location,
			List<Tag> tags) {
		super();
		this.id = id;
		this.userId = userId;
		this.title = title;
		this.description = description;
		this.active = active;
		this.location = location;
		this.tags = tags;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public String getProviderName() {
		return providerName;
	}

	public void setProviderName(String providerName) {
		this.providerName = providerName;
	}

	public double[] getLocation() {
		return location;
	}

	public void setLocation(double[] location) {
		this.location = location;
	}

	public String getFormattedAddress() {
		return formattedAddress;
	}

	public void setFormattedAddress(String formattedAddress) {
		this.formattedAddress = formattedAddress;
	}
	
	public IpLoc getIpLoc() {
		return ipLoc;
	}

	public void setIpLoc(IpLoc ipLoc) {
		this.ipLoc = ipLoc;
	}

	public List<Tag> getTags() {
		return tags;
	}

	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}

	public Map<String, byte[]> getImgFiles() {
		return imgFiles;
	}

	public void setImgFiles(Map<String, byte[]> imgFiles) {
		this.imgFiles = imgFiles;
	}

	public DateTime getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(DateTime createdDate) {
		this.createdDate = createdDate;
	}
    
}
