package com.spazomatic.jobyjob.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.geo.Point;

import com.spazomatic.jobyjob.nosql.entities.GigaProvider;

public interface GigaProviderService {
	GigaProvider update(GigaProvider gigaProvider);
    GigaProvider save(GigaProvider gigaProvider);
    GigaProvider findOne(String id);
    Iterable<GigaProvider> findAll();

    Page<GigaProvider> findByTagsName(String tagName, PageRequest pageRequest);
    Page<GigaProvider> findBySpatialDistance(String distance, Point location, PageRequest pageRequest);
    Page<GigaProvider> findByLocationNear(Point location, String distance, PageRequest pageRequest);
    Page<GigaProvider> findByActiveIsTrue(PageRequest pageRequest);
    Page<GigaProvider> findByActiveIsFalse(PageRequest pageRequest);
    GigaProvider findByUserId(String userId);
}
