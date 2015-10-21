package com.spazomatic.jobyjob.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;

import com.spazomatic.jobyjob.entities.Post;

public interface PostService {
    Post save(Post post);
    Post findOne(String id);
    Iterable<Post> findAll();

    Page<Post> findByTagsName(String tagName, PageRequest pageRequest);
    Page<Post> findBySpatialDistance(String distance, GeoPoint location, PageRequest pageRequest);
}
