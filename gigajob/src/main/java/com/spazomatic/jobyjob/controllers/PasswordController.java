package com.spazomatic.jobyjob.controllers;

import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.spazomatic.jobyjob.conf.CustomUserDetailsService;
import com.spazomatic.jobyjob.db.dao.UsersDao;
import com.spazomatic.jobyjob.db.model.PasswordResetToken;
import com.spazomatic.jobyjob.db.model.User;
import com.spazomatic.jobyjob.db.model.UserProfile;
import com.spazomatic.jobyjob.exceptions.UserNotFoundException;
import com.spazomatic.jobyjob.security.SimpleSocialUsersDetailService;
import com.spazomatic.jobyjob.util.GenericResponse;
import com.spazomatic.jobyjob.util.SocialControllerUtil;

@Controller
public class PasswordController {

    @Autowired private UsersDao usersDao;
    @Autowired private Environment env;
    @Autowired private MessageSource messages;
    @Autowired private JavaMailSender mailSender;  
    @Autowired private SimpleSocialUsersDetailService userDetailsService;
  
    public PasswordController() {
		super();
	}
    
	@RequestMapping(value = "/user/resetPassword", method = RequestMethod.POST)
    @ResponseBody
    public GenericResponse resetPassword(
      HttpServletRequest request, @RequestParam("email") String userEmail) {
         
        UserProfile userProfile = usersDao.findUserByEmail(userEmail);
        User user = usersDao.findUserByName(userProfile.getUserId());
        if (user == null) {
            throw new UserNotFoundException();
        }
     
        String token = UUID.randomUUID().toString();
        usersDao.createPasswordResetTokenForUser(user, token);
        String appUrl = 
          "http://" + request.getServerName() + 
          ":" + request.getServerPort() + 
          request.getContextPath();
        SimpleMailMessage email = 
          constructResetTokenEmail(appUrl, request.getLocale(), token, userProfile);
        mailSender.send(email);
     
        return new GenericResponse(
          messages.getMessage("message.resetPasswordEmail", null, request.getLocale()));
    }
    @RequestMapping(value = "/user/savePassword", method = RequestMethod.POST)
    //@PreAuthorize("hasRole('READ_PRIVILEGE')")
    @ResponseBody
    public GenericResponse savePassword(Locale locale, @RequestParam("password") String password) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        usersDao.changeUserPassword(user, password);
        return new GenericResponse(messages.getMessage("message.resetPasswordSuc", null, locale));
    }    
    
    @RequestMapping(value = "/user/changePassword", method = RequestMethod.GET)
    public String showChangePasswordPage(
      Locale locale, Model model, @RequestParam("id") String id, @RequestParam("token") String token) {
         
        PasswordResetToken passToken = usersDao.getPasswordResetToken(token);
        User user = passToken.getUser();
        if (passToken == null || !user.getUsername().equals(id)) {
            String message = messages.getMessage("auth.message.invalidToken", null, locale);
            model.addAttribute("message", message);
            return "redirect:/login?lang=" + locale.getLanguage();
        }
     
        Calendar cal = Calendar.getInstance();
        if ((passToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
            model.addAttribute("message", messages.getMessage("auth.message.expired", null, locale));
            return "redirect:/login?lang=" + locale.getLanguage();
        }
     
        Authentication auth = new UsernamePasswordAuthenticationToken(
          user, null, userDetailsService.loadUserByUserId(user.getUsername()).getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
     
        return "redirect:/updatePassword?lang=" + locale.getLanguage();
    }

    @RequestMapping("/updatePassword")
    public String forgotPassword(){
    	return "updatePassword";
    }
    
    @RequestMapping(value = "/user/updatePassword", method = RequestMethod.POST)
    //TODO: @PreAuthorize("hasRole('READ_PRIVILEGE')")
    @ResponseBody
    public GenericResponse changeUserPassword(final Locale locale, @RequestParam("password") final String password, @RequestParam("oldpassword") final String oldPassword) {
        final User user = usersDao.findUserByName(SecurityContextHolder.getContext().getAuthentication().getName());
        //if (!usersDao.checkIfValidOldPassword(user, oldPassword)) {
        //    throw new InvalidOldPasswordException();
        //}
        usersDao.changeUserPassword(user, password);
        return new GenericResponse(messages.getMessage("message.updatePasswordSuc", null, locale));
    }
    
    private SimpleMailMessage constructResetTokenEmail(String contextPath, 
    						Locale locale, String token, UserProfile user) {
    	
	    String url = contextPath + "/user/changePassword?id=" + user.getUserId() + "&token=" + token;
	    String message = messages.getMessage("message.resetPassword", null, locale);
	    SimpleMailMessage email = new SimpleMailMessage();
	    email.setTo(user.getEmail());
	    email.setSubject("Reset Password");
	    email.setText(message + " rn" + url);
	    email.setFrom(env.getProperty("support.email"));
	    return email;
	}
    
}
