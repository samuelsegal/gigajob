package com.spazomatic.jobyjob.controllers;


import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.spazomatic.jobyjob.location.ServerLocation;
import com.spazomatic.jobyjob.location.ServerLocationBo;
import com.spazomatic.jobyjob.util.Util;


@Controller
public class MapController {

	private static final Logger log = LoggerFactory.getLogger(Util.LOG_TAG);
	
	@Autowired
	ServerLocationBo serverLocationBo;

	@RequestMapping(value = "/getLocationByIpAddress", method = RequestMethod.GET)
	public @ResponseBody
	String getDomainInJsonFormat(@RequestParam String ipAddress) {

		ObjectMapper mapper = new ObjectMapper();
		
		ServerLocation location;
		try {
			location = serverLocationBo.getLocation(ipAddress);
		} catch (IOException | GeoIp2Exception e1) {
			log.error(e1.getMessage());
			return "error";
		}

		String result = "";

		try {
			result = mapper.writeValueAsString(location);
		} catch (Exception e) {
			log.debug(e.getMessage());
		}

		return result;

	}

}
