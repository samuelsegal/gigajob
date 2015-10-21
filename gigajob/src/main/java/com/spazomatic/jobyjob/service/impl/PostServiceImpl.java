package com.spazomatic.jobyjob.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Service;

import com.spazomatic.jobyjob.entities.Post;
import com.spazomatic.jobyjob.repository.PostRepository;
import com.spazomatic.jobyjob.service.PostService;

@Service
public class PostServiceImpl implements PostService{
    @Autowired
    private PostRepository postRepository;
	
    @Autowired
	private ElasticsearchTemplate elasticsearchTemplate;
	
    @Override
    public Post save(Post post) {
        postRepository.save(post);
        return post;
    }

    @Override
    public Post findOne(String id) {
        return postRepository.findOne(id);
    }

    @Override
    public Iterable<Post> findAll() {
        return postRepository.findAll();
    }

    @Override
    public Page<Post> findByTagsName(String tagName, PageRequest pageRequest) {
        return postRepository.findByTagsName(tagName, pageRequest);
    }
    
	@Override
	public Page<Post> findBySpatialDistance( String distance, GeoPoint geoPoint, PageRequest pageRequest) {
    	CriteriaQuery geoLocationCriteriaQuery = new CriteriaQuery(
    			new Criteria("location").within(
    					geoPoint, distance));
    	Page<Post> geoAuthorsForGeoCriteria = 
    			elasticsearchTemplate.queryForPage(geoLocationCriteriaQuery, 
    					Post.class);
    	return geoAuthorsForGeoCriteria;
	}
}
