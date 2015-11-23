package com.spazomatic.jobyjob.controllers;

import java.util.Calendar;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.spazomatic.jobyjob.db.dao.UsersDao;
import com.spazomatic.jobyjob.db.model.User;
import com.spazomatic.jobyjob.db.model.UserProfile;
import com.spazomatic.jobyjob.db.model.VerificationToken;
import com.spazomatic.jobyjob.exceptions.EmailExistsException;
import com.spazomatic.jobyjob.exceptions.UserAlreadyExistException;
import com.spazomatic.jobyjob.util.GenericResponse;
import com.spazomatic.jobyjob.util.OnRegistrationCompleteEvent;
import com.spazomatic.jobyjob.util.Util;

@Controller
public class RegistrationController {

    private final Logger LOG = LoggerFactory.getLogger(
    		String.format("%s :: %s",Util.LOG_TAG,
    				RegistrationController.class));

    @Autowired private MessageSource messages;
    @Autowired private JavaMailSender mailSender;
    @Autowired private ApplicationEventPublisher eventPublisher;
    @Autowired private UsersDao usersDao;
    @Autowired private Environment env;
    
	public RegistrationController() {
		super();
	}

    @RequestMapping(value = "/user/registration", method = RequestMethod.POST)
    @ResponseBody
    public GenericResponse registerUserAccount(@Valid final User accountDto, final HttpServletRequest request) {
        LOG.debug("Registering user account with information: {}", accountDto);

        final User registered = createUserAccount(accountDto);
        if (registered == null) {
            throw new UserAlreadyExistException();
        }
        final String appUrl = "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
        eventPublisher.publishEvent(new OnRegistrationCompleteEvent(registered, request.getLocale(), appUrl));

        return new GenericResponse("success");
    }

    @RequestMapping(value = "/regitrationConfirm", method = RequestMethod.GET)
    public String confirmRegistration(final Locale locale, final Model model, @RequestParam("token") final String token) {
        final VerificationToken verificationToken = usersDao.getVerificationToken(token);
        if (verificationToken == null) {
            final String message = messages.getMessage("auth.message.invalidToken", null, locale);
            model.addAttribute("message", message);
            return "redirect:/badUser.html?lang=" + locale.getLanguage();
        }

        final User user = verificationToken.getUser();
        final Calendar cal = Calendar.getInstance();
        if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0) {
            model.addAttribute("message", messages.getMessage("auth.message.expired", null, locale));
            model.addAttribute("expired", true);
            model.addAttribute("token", token);
            return "redirect:/badUser.html?lang=" + locale.getLanguage();
        }

        user.setEnabled(new Byte("1"));
        try {
			usersDao.saveRegisteredUser(user);
		} catch (EmailExistsException e) {
			LOG.error(String.format(
					"ERROR Enabling User with message %s", 
					e.getMessage()));
		}
        model.addAttribute("message", messages.getMessage(
        		"message.accountVerified", null, locale));
        return "redirect:/login.html?lang=" + locale.getLanguage();
    }

    // user activation - verification

    @RequestMapping(value = "/user/resendRegistrationToken", method = RequestMethod.GET)
    @ResponseBody
    public GenericResponse resendRegistrationToken(final HttpServletRequest request, @RequestParam("token") final String existingToken) {
        final VerificationToken newToken = usersDao.generateNewVerificationToken(existingToken);
        //TODO: refactor to not call db to get user than to get profile, wtf, cmon doit..
        final User user = usersDao.getUser(newToken.getToken());
        final UserProfile userProfile = usersDao.getUserProfile(user.getUsername());
        final String appUrl = "http://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
        final SimpleMailMessage email = constructResendVerificationTokenEmail(appUrl, request.getLocale(), newToken, userProfile);
        mailSender.send(email);

        return new GenericResponse(messages.getMessage("message.resendToken", null, request.getLocale()));
    }	
    private final SimpleMailMessage constructResendVerificationTokenEmail(final String contextPath, final Locale locale, final VerificationToken newToken, final UserProfile user) {
        final String confirmationUrl = contextPath + "/regitrationConfirm.html?token=" + newToken.getToken();
        final String message = messages.getMessage("message.resendToken", null, locale);
        final SimpleMailMessage email = new SimpleMailMessage();
        email.setSubject("Resend Registration Token");
        email.setText(message + " \r\n" + confirmationUrl);
        email.setTo(user.getEmail());
        email.setFrom(env.getProperty("support.email"));
        return email;
    }
    private User createUserAccount(final User user) {
        User registered = null;
        try {
            registered = usersDao.saveRegisteredUser(user);
        } catch (final EmailExistsException e) {
            return null;
        }
        return registered;
    }
}
