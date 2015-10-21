package com.spazomatic.jobyjob.service.impl;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.domain.Page;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.spazomatic.jobyjob.JobyjobApplication;
import com.spazomatic.jobyjob.builders.PostBuilder;
import com.spazomatic.jobyjob.entities.Post;
import com.spazomatic.jobyjob.service.PostService;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = JobyjobApplication.class)
@WebAppConfiguration
public class PostServiceImplTest{
    @Autowired
    private PostService postService;
    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;
    
    @Before
    public void before() {
    	elasticsearchTemplate.createIndex(Post.class);
    	elasticsearchTemplate.refresh(Post.class, true);
    	elasticsearchTemplate.putMapping(Post.class);
    }

    @Test
    public void testSave() throws Exception {
    	List<IndexQuery> indexQueries = new ArrayList<IndexQuery>();
    	
    	indexQueries.add(new PostBuilder("1").title("Lawn Boy").location(45.7806d, 3.0875d).buildIndex());
    	indexQueries.add(new PostBuilder("2").title("Mover").location(51.5171d, 0.1062d).buildIndex());
    	indexQueries.add(new PostBuilder("3").title("Jigalo").location(-23.16257482d, -8.16478155).buildIndex());
    	elasticsearchTemplate.bulkIndex(indexQueries);

    }

    public void testFindOne() throws Exception {

    }

    public void testFindAll() throws Exception {

    }
    @Test
    public void testFindByTagsName() throws Exception {

    	CriteriaQuery geoLocationCriteriaQuery = new CriteriaQuery(
    			new Criteria("location").within(
    					new GeoPoint(45.7806d, 3.0875d), "20km"));
    	Page<Post> geoAuthorsForGeoCriteria = 
    			elasticsearchTemplate.queryForPage(geoLocationCriteriaQuery, 
    					Post.class);

        //Page<AuthorMarkerEntity> posts  = authorMarkerService.findByLocation("tech", new PageRequest(0,10));
       // Page<Post> posts2  = postService.findByTagsName("tech", new PageRequest(0,10));
       // Page<Post> posts3  = postService.findByTagsName("maz", new PageRequest(0,10));

    	//Assert.assertTrue(geoAuthorsForGeoCriteria.size() == 1);
    	assertThat(geoAuthorsForGeoCriteria.getTotalElements(), is(1L));
    }
}