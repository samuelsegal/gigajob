package com.spazomatic.jobyjob.controllers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.spazomatic.jobyjob.db.model.UserProfile;
import com.spazomatic.jobyjob.nosql.entities.GigaProvider;
import com.spazomatic.jobyjob.nosql.entities.IpLoc;
import com.spazomatic.jobyjob.service.GigaProviderService;
import com.spazomatic.jobyjob.util.SocialControllerUtil;
import com.spazomatic.jobyjob.util.Util;

@Controller
public class ProviderController {
	
	private static final Logger LOG = LoggerFactory.getLogger(
			String.format("%s.%s",Util.LOG_TAG,
					ProviderController.class.getSimpleName()));
	
	@Autowired private GigaProviderService gigaProviderService;		
	@Autowired private HttpServletRequest request;
    @Autowired private SocialControllerUtil util;
	
	public ProviderController() {
	}
	
	@RequestMapping(value = { "/providerProfile" }, method = RequestMethod.GET)
	public String providerProfile(Principal currentUser, Model model) {
		
		util.setModel(request, currentUser, model);
		GigaProvider gigaProvider = getGigaProvider(currentUser);
		model.addAttribute("gigaProvider",gigaProvider);		
		HttpSession session = request.getSession();
		session.setAttribute(Util.GIGA_PROVIDER,gigaProvider);
		return "provider/providerProfile";	
	}
	
	@RequestMapping(value = { "/viewProviderProfile" }, method = RequestMethod.GET)
	public String viewProviderProfile(Principal currentUser, Model model,
			@RequestParam( value = "userid", required = true ) String userid) {
		
		util.setModel(request, currentUser, model);

		GigaProvider gigaProvider = gigaProviderService.findByUserId(userid); 
		IpLoc ipLoc = new IpLoc();
		model.addAttribute("gigaProvider",gigaProvider);
		model.addAttribute("loc", ipLoc);
		HttpSession session = request.getSession();
		session.setAttribute(Util.GIGA_PROVIDER,gigaProvider);
		return "data/viewProviderProfile";	
	}	
	@RequestMapping(value = { "/toggleProviderAvail" }, method = RequestMethod.POST)
	public String updateProviderProfile(Principal currentUser, Model model,
			@RequestParam( value = "available" ) Boolean available){
		util.setModel(request, currentUser, model);

		HttpSession session = request.getSession();
		GigaProvider gigaProvider = (GigaProvider) session.getAttribute(Util.GIGA_PROVIDER);
		gigaProvider.setActive(gigaProvider.getActive() != null ? !gigaProvider.getActive() : true);
		session.setAttribute(Util.GIGA_PROVIDER, gigaProvider);
		model.addAttribute("available", available);
		model.addAttribute("gigaProvider",gigaProvider);
		gigaProviderService.save(gigaProvider);
		LOG.debug(String.format("Provider %s is available : %b", gigaProvider, gigaProvider.getActive()));
		return "provider/providerProfile";
	}

	@RequestMapping(value = { "/editProviderProfile" }, method = RequestMethod.GET)
	public String editProviderProfile(Principal currentUser, Model model) {
		
		util.setModel(request, currentUser, model);
		GigaProvider gigaProvider = gigaProviderService.findByUserId(currentUser.getName()) != null 
				? gigaProviderService.findByUserId(currentUser.getName()) 
				: new GigaProvider();
		IpLoc ipLoc = gigaProvider.getIpLoc() != null ? gigaProvider.getIpLoc() : new IpLoc();
		HttpSession session = request.getSession();
		session.setAttribute(Util.GIGA_PROVIDER, gigaProvider);
		model.addAttribute("gigaProvider",gigaProvider);
		model.addAttribute("loc", ipLoc);
		return "provider/editProviderProfile";	
	}
	
	@RequestMapping(value = { "/updateProviderProfile" }, method = RequestMethod.POST)
	public String updateProviderProfile(Principal currentUser, Model model,
			@ModelAttribute GigaProvider gigaProvider, @ModelAttribute IpLoc loc,
			@ModelAttribute MultipartFile fileInput1, 
			@ModelAttribute MultipartFile fileInput2,
			@ModelAttribute MultipartFile fileInput3,
			@ModelAttribute MultipartFile fileInput4,
			@ModelAttribute MultipartFile fileInput5) {
		
		HttpSession session = request.getSession();
		GigaProvider gp = (GigaProvider) session.getAttribute(Util.GIGA_PROVIDER);
		gp.setFormattedAddress(gigaProvider.getFormattedAddress());
		gigaProvider = gp;
		setProviderImages(gigaProvider, fileInput1, fileInput2, 
				fileInput3, fileInput4, fileInput5);
		util.setModel(request, currentUser, model);
		
		UserProfile client = (UserProfile) session.getAttribute(
				SocialControllerUtil.USER_PROFILE);
		if(loc != null && loc.getLatitude() != null && loc.getLongitude() != null){
			gigaProvider.setLocation(new double[]{
					loc.getLatitude(), loc.getLongitude()});
		}
		gigaProvider.setUserId(currentUser.getName());
		gigaProvider.setProviderName(client.getName());
		LOG.debug(String.format("gigaProvider :: %s", gigaProvider.toString()));
		gigaProviderService.update(gigaProvider);
		session.setAttribute(Util.GIGA_PROVIDER, gigaProvider);
		//IpLoc ipLoc = new IpLoc();
		model.addAttribute("gigaProvider", gigaProvider);
		//model.addAttribute("loc", ipLoc);
		return "provider/providerProfile";
	}
	
	@RequestMapping(value = { "/createProviderProfile" }, method = RequestMethod.POST)
	public String createProviderProfile(Principal currentUser, Model model,
			@ModelAttribute GigaProvider gigaProvider, @ModelAttribute IpLoc loc) {
		
		util.setModel(request, currentUser, model);		
		HttpSession session = request.getSession();
		UserProfile client = (UserProfile) session.getAttribute(SocialControllerUtil.USER_PROFILE);
		gigaProvider.setProviderName(client.getName());
		gigaProvider.setUserId(currentUser.getName());
		if(loc != null && loc.getLatitude() != null && loc.getLongitude() != null){
			gigaProvider.setLocation(new double[]{loc.getLatitude(), loc.getLongitude()});
		}		
		gigaProviderService.save(gigaProvider);	
		IpLoc ipLoc = new IpLoc();
		model.addAttribute("gigaProvider",gigaProvider);
		model.addAttribute("loc", ipLoc);
		return "provider/providerProfile";
	}	
	
	@RequestMapping(value= {"/getProviderImgFile/{id}"}, method = RequestMethod.GET)
	public void getUserImage(HttpServletResponse response, 
			Principal currentUser, Model model, 
			@PathVariable("id") String postNailId) throws IOException{
		
		HttpSession session = request.getSession();
		GigaProvider gigaProvider = session.getAttribute(Util.GIGA_PROVIDER) != null 
				? (GigaProvider) session.getAttribute(Util.GIGA_PROVIDER) 
				: new GigaProvider();

		if(gigaProvider.getImgFiles() != null && 
				gigaProvider.getImgFiles().get(postNailId) != null){
			//TODO: setContentType dynamic		
			response.setContentType("image/png");
			byte[] buffer = gigaProvider.getImgFiles().get(postNailId);
	
			InputStream in1 = new ByteArrayInputStream(buffer);
			IOUtils.copy(in1, response.getOutputStream());   
		}
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

	
	private void setProviderImages(GigaProvider gigaProvider, 
			MultipartFile fileInput1, 
			MultipartFile fileInput2, MultipartFile fileInput3,
			MultipartFile fileInput4, MultipartFile fileInput5) {
		
    	Map<String, byte[]> imgFiles = gigaProvider.getImgFiles() != null ? 
    			gigaProvider.getImgFiles() : new HashMap<>();
        try {
	    	if (!fileInput1.isEmpty()) {          	
	        	imgFiles.put("fileInput1", fileInput1.getBytes());    	        	
	    	}
	    	if (!fileInput2.isEmpty()) {          	
	        	imgFiles.put("fileInput2", fileInput2.getBytes());    
	    	}
	    	if (!fileInput3.isEmpty()) {          	
	        	imgFiles.put("fileInput3", fileInput3.getBytes());    
	    	}
	    	if (!fileInput4.isEmpty()) {          	
	        	imgFiles.put("fileInput4", fileInput4.getBytes());    
	    	}
	    	if (!fileInput5.isEmpty()) {          	
	        	imgFiles.put("fileInput5", fileInput5.getBytes());    	        	
	    	}
	    	
	    	gigaProvider.setImgFiles(imgFiles);
	    	
        } catch (Exception e) {
        	LOG.error(String.format(
        			"ERROR setting Provider Image files :: %s", 
        			e.getMessage()));
        }     			
	}
	/*
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
	*/
}
