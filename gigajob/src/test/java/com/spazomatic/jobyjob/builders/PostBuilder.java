package com.spazomatic.jobyjob.builders;


import com.spazomatic.jobyjob.entities.Post;



public class PostBuilder {

	private Post result;

	public PostBuilder(String id, double lat, double lon) {
		result = new Post(id,1,1);
	}

	public PostBuilder title(String title) {
		result.setTitle(title);
		return this;
	}

	public PostBuilder location(double latitude, double longitude) {
		//result.setLocation(new Point(latitude, longitude));
		return this;
	}

	public Post build() {
		return result;
	}


}