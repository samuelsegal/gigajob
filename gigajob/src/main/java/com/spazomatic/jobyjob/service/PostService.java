package com.spazomatic.jobyjob.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.geo.Point;

import com.spazomatic.jobyjob.nosql.entities.Post;

public interface PostService {
    Post save(Post post);
    Post findOne(String id);
    Iterable<Post> findAll();

    Page<Post> findByTagsName(String tagName, PageRequest pageRequest);
    Page<Post> findBySpatialDistance(String distance, Point location, PageRequest pageRequest);
    Page<Post> findByLocationNear( Point location, String distance, PageRequest pageRequest);
    Page<Post> findByUserId(String userId, Pageable pageable);
    Page<Post> findByClientName(String clientName, Pageable pageable);
    Page<Post> findByTitle(String title, Pageable pageable);
    Page<Post> findByTitleLike(String title, Pageable pageable);
}
