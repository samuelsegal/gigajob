package com.spazomatic.jobyjob.controllers;

import java.io.IOException;
import java.security.Principal;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.spazomatic.jobyjob.db.model.UserProfile;
import com.spazomatic.jobyjob.location.ServerLocation;
import com.spazomatic.jobyjob.location.ServerLocationBo;
import com.spazomatic.jobyjob.nosql.entities.GigaProvider;
import com.spazomatic.jobyjob.nosql.entities.IpLoc;
import com.spazomatic.jobyjob.service.GigaProviderService;
import com.spazomatic.jobyjob.util.SocialControllerUtil;
import com.spazomatic.jobyjob.util.Util;

@Controller
public class ProviderController {
	
	private static final Logger LOG = LoggerFactory.getLogger(Util.LOG_TAG);
	
	@Autowired
	private GigaProviderService gigaProviderService;
	
	@Autowired
	private ServerLocationBo serverLocationBo;	
	
	@Autowired
	private HttpServletRequest request;

    @Autowired
    private SocialControllerUtil util;
	
	public ProviderController() {
	}
	
	@RequestMapping(value = { "/providerProfile" }, method = RequestMethod.GET)
	public String providerProfile(Principal currentUser, Model model) {
		
		util.setModel(request, currentUser, model);

		GigaProvider gigaProvider = getGigaProvider(currentUser);
		IpLoc ipLoc = new IpLoc();
		model.addAttribute("gigaProvider",gigaProvider);
		model.addAttribute("loc", ipLoc);
		return "provider/providerProfile";	
	}
	
	@RequestMapping(value = { "/viewProviderProfile" }, method = RequestMethod.GET)
	public String viewProviderProfile(Principal currentUser, Model model,
			@RequestParam( value = "userid", required = true) String userid) {
		
		util.setModel(request, currentUser, model);

		GigaProvider gigaProvider = gigaProviderService.findByUserId(userid); 
		IpLoc ipLoc = new IpLoc();
		model.addAttribute("gigaProvider",gigaProvider);
		model.addAttribute("loc", ipLoc);
		return "data/viewProviderProfile";	
	}	


	@RequestMapping(value = { "/editProviderProfile" }, method = RequestMethod.GET)
	public String editProviderProfile(Principal currentUser, Model model) {
		
		util.setModel(request, currentUser, model);
		GigaProvider gigaProvider = gigaProviderService.findByUserId(currentUser.getName()) != null 
				? gigaProviderService.findByUserId(currentUser.getName()) 
				: new GigaProvider();
		IpLoc ipLoc = new IpLoc();
		model.addAttribute("gigaProvider",gigaProvider);
		model.addAttribute("loc", ipLoc);
		return "provider/editProviderProfile";	
	}
	
	@RequestMapping(value = { "/updateProviderProfile" }, method = RequestMethod.POST)
	public String updateProviderProfile(Principal currentUser, Model model,
			@ModelAttribute GigaProvider gigaProvider, @ModelAttribute IpLoc loc) {
		
		util.setModel(request, currentUser, model);
		gigaProvider.setLocation(new double[]{loc.getLatitude(), loc.getLongitude()});
		gigaProvider.setUserId(currentUser.getName());
		
		HttpSession session = request.getSession();
		UserProfile client = (UserProfile) session.getAttribute(SocialControllerUtil.USER_PROFILE);
		gigaProvider.setProviderName(client.getName());
		
		gigaProviderService.update(gigaProvider);	
		IpLoc ipLoc = new IpLoc();
		model.addAttribute("gigaProvider",gigaProvider);
		model.addAttribute("loc", ipLoc);
		return "provider/providerProfile";
	}
	
	@RequestMapping(value = { "/createProviderProfile" }, method = RequestMethod.POST)
	public String createProviderProfile(Principal currentUser, Model model,
			@ModelAttribute GigaProvider gigaProvider, @ModelAttribute IpLoc loc) {
		
		util.setModel(request, currentUser, model);
		gigaProvider.setLocation(new double[]{loc.getLatitude(), loc.getLongitude()});
		gigaProvider.setUserId(currentUser.getName());
		
		HttpSession session = request.getSession();
		UserProfile client = (UserProfile) session.getAttribute(SocialControllerUtil.USER_PROFILE);
		gigaProvider.setProviderName(client.getName());
		
		gigaProviderService.save(gigaProvider);	
		IpLoc ipLoc = new IpLoc();
		model.addAttribute("gigaProvider",gigaProvider);
		model.addAttribute("loc", ipLoc);
		return "provider/providerProfile";
	}	
	
	private GigaProvider getGigaProvider(Principal currentUser) {
		GigaProvider gigaProvider = gigaProviderService.findByUserId(currentUser.getName()); 
		if(gigaProvider == null){
			HttpSession session = request.getSession();
			UserProfile userProfile = (UserProfile) session.getAttribute(
					SocialControllerUtil.USER_PROFILE);
			gigaProvider = new GigaProvider();
			gigaProvider.setActive(false);
			gigaProvider.setDescription("");
			gigaProvider.setTitle(userProfile.getName());
			gigaProvider.setProviderName(userProfile.getUsername());
		}
		return gigaProvider;
	}

	private ServerLocation getLocation(String ipAddress) throws Exception{
		ServerLocation location = null;
		try {
			location = serverLocationBo.getLocation(ipAddress);
		} catch (IOException | GeoIp2Exception e1) {
			if(e1 instanceof GeoIp2Exception){
				RestTemplate restTemplate = new RestTemplate();
				ipAddress = restTemplate.getForObject(
						"http://checkip.amazonaws.com",
						String.class);
				try {
					location = serverLocationBo.getLocation(ipAddress);
				} catch (IOException | GeoIp2Exception e) {
					LOG.error(e.getMessage());
					throw new Exception(e.getMessage());
				}
			}
		}
		return location;
	}

}
