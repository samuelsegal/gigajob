package com.spazomatic.jobyjob.location;

import java.io.IOException;

import com.maxmind.geoip2.exception.GeoIp2Exception;

public interface ServerLocationBo {

	ServerLocation getLocation(String ipAddress) throws IOException, GeoIp2Exception;

}