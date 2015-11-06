package com.spazomatic.jobyjob.location;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.record.City;
import com.maxmind.geoip2.record.Country;
import com.maxmind.geoip2.record.Location;
import com.maxmind.geoip2.record.Postal;
import com.maxmind.geoip2.record.Subdivision;
import com.spazomatic.jobyjob.utility.Util;


@Component
public class ServerLocationBoImpl implements ServerLocationBo {

	private static final Logger log = LoggerFactory.getLogger(Util.LOG_TAG);
	@Override
	public ServerLocation getLocation(String ipAddress) throws IOException, GeoIp2Exception {
		//TODO: use Environment var from app.props
		String dataFile = "/usr/local/share/GeoIP/GeoLite2-City.mmdb";
		return getLocation(ipAddress, dataFile);
	}

	private ServerLocation getLocation(String ipAddress, String locationDataFile) throws IOException, GeoIp2Exception {
		ClassLoader classLoader = getClass().getClassLoader();
		FileSystemResource fsr = new FileSystemResource(locationDataFile);
		File database = fsr.getFile();
		// A File object pointing to your GeoIP2 or GeoLite2 database
		//File database = new File("/path/to/GeoIP2-City.mmdb");

		// This creates the DatabaseReader object, which should be reused across
		// lookups.
		DatabaseReader reader = new DatabaseReader.Builder(database).build();


		// Replace "city" with the appropriate method for your database, e.g.,
		// "country".
		InetAddress ipa = InetAddress.getByName(ipAddress.trim());
		CityResponse response = reader.city(ipa);

		Country country = response.getCountry();
		System.out.println(country.getIsoCode());            // 'US'
		System.out.println(country.getName());               // 'United States'
		System.out.println(country.getNames().get("zh-CN")); // '美国'

		Subdivision subdivision = response.getMostSpecificSubdivision();
		System.out.println(subdivision.getName());    // 'Minnesota'
		System.out.println(subdivision.getIsoCode()); // 'MN'

		City city = response.getCity();
		System.out.println(city.getName()); // 'Minneapolis'

		Postal postal = response.getPostal();
		System.out.println(postal.getCode()); // '55455'

		Location location = response.getLocation();
		System.out.println(location.getLatitude());  // 44.9733
		System.out.println(location.getLongitude()); // -93.2323
		ServerLocation serverLocation = new ServerLocation();
		
		serverLocation.setCity(city.getName());
		serverLocation.setCountryCode(country.getIsoCode());
		serverLocation.setCountryName(country.getName());
		serverLocation.setPostalCode(postal.getCode());
		serverLocation.setLatitude(Double.toString(location.getLatitude()));
		serverLocation.setLongitude(Double.toString(location.getLongitude()));
		
		return serverLocation;

	}
}