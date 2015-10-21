package com.spazomatic.jobyjob.builders;


import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.data.elasticsearch.core.query.IndexQuery;

import com.spazomatic.jobyjob.entities.Post;



public class PostBuilder {

	private Post result;

	public PostBuilder(String id) {
		result = new Post(id);
	}

	public PostBuilder title(String title) {
		result.setTitle(title);
		return this;
	}

	public PostBuilder location(double latitude, double longitude) {
		result.setLocation(new GeoPoint(latitude, longitude));
		return this;
	}

	public Post build() {
		return result;
	}

	public IndexQuery buildIndex() {
		IndexQuery indexQuery = new IndexQuery();
		indexQuery.setId(result.getId());
		indexQuery.setObject(result);
		return indexQuery;
	}
}