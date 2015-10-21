package com.spazomatic.jobyjob.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import com.spazomatic.jobyjob.entities.Post;

public interface PostRepository extends ElasticsearchRepository<Post, String>{

    Page<Post> findByTagsName(String name, Pageable pageable);
    Page<Post> findByLocation(GeoPoint location, Pageable pageable);
}
