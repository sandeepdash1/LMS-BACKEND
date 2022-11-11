package com.lms.security.services;

import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.lms.models.User;
import com.lms.repository.UserRepository;

@Service
public class OtpService {

	@Autowired
	private UserRepository repo;

	@Autowired
	private JavaMailSender javaMailSender;

	private static final Integer EXPIRE_MINS = 10;
	private LoadingCache<String, Integer> otpCache;

	public OtpService() {
		super();
		otpCache = CacheBuilder.newBuilder().expireAfterWrite(EXPIRE_MINS, TimeUnit.MINUTES)
				.build(new CacheLoader<String, Integer>() {
					public Integer load(String key) {
						return 0;
					}
				});
		
		System.out.println("Cache value::"+otpCache);
	}

//	@Transactional
//	public String finduserbyNameAndEmail(User userDetails) throws MessagingException {
//		Optional<User> user = repo.findByUsername(userDetails.getUsername());
//		if (user.isPresent()) {
//			String email = updateOTPAndSendMail(user.get().getId(),user.get().getUsername(), user.get().getEmail());
//			System.out.println("email-->> " + email);
//			return email;
//
//		}
//		return "Not registered";
//	}
	
	@Transactional
	public String finduserbyName(String username) throws MessagingException {
		System.out.println("username-->> " + username);
		Optional<User> user = repo.findByUsername(username);
		if (user.isPresent()) {
			String email = updateOTPAndSendMail(user.get().getId(),user.get().getUsername(), user.get().getEmail());
			System.out.println("email-->> " + email);
			return email;

		}
		return "Not registered";
	}

	private String updateOTPAndSendMail(String id,String username, String email) throws MessagingException {
		int otp = generateOTP(email);
		System.out.println("=====otp===" + otp);
		//repo.sa(otp, userName);
		Optional<User> user1 = repo.findByUsername(username);
		User userdetails = new User();
		if(user1.isPresent()) {
			userdetails=(user1.get());
			userdetails.setOtp(otp);
			repo.save(userdetails);
		}
		
		sendOtpMessage(email, "OTP", String.valueOf(otp));
		System.out.println("Done");
		return email;

	}

	private void sendOtpMessage(String to, String subject, String message) throws  MessagingException {
		System.out.println("Mail server" + to);
		/*
		 * MimeMessage simpleMailMessage = javaMailSender.createMimeMessage();
		 * MimeMessageHelper helper = new MimeMessageHelper(simpleMailMessage);
		 * helper.setTo(to); System.out.println("simpleMailMessage" + helper);
		 * helper.setSubject(subject);
		 * helper.setText("This is one time password"+message);
		 * System.out.println("====" + message.toString());
		 * 
		 * javaMailSender.send(simpleMailMessage);
		 * System.out.println("Mail sent successfully");
		 */

		MimeMessage msg = javaMailSender.createMimeMessage();

		MimeMessageHelper helper = new MimeMessageHelper(msg, true);

		helper.setTo(to);
		helper.setSubject(subject);
		helper.setText("<!DOCTYPE html>\r\n" + "<html>\r\n" + "<head>\r\n" + " \r\n" + "</head>\r\n" + "<body>\r\n"
				+ "	<h3> Hi " + to + "</h3>\r\n" + "	<br/>\r\n" + "	<h2> Your Otp is " + message + "</h2> \r\n"
				+ "	<br/>\r\n" + "	Thanks,\r\n" + "</body>\r\n" + "</html>", true);
		System.out.println("====" + message.toString());
		javaMailSender.send(msg);
		System.out.println("Mail sent successfully");
	}

	private int generateOTP(String email) {
		Random random = new Random();
		int otp = 100000 + random.nextInt(900000);
		otpCache.put(email, otp);

		return otp;
	}

	public void clearOTP(String key) {
		otpCache.invalidate(key);
	}

//	public UserDetail save(UserDetail user) {
//		return repo.save(user);
//
//	}

	public int getOtp(String key) {
		try {
			return otpCache.get(key);
		} catch (Exception e) {
			return 0;
		}
	}

	public Optional<User> findEmailByOtp(int otpnum) {
		return repo.findEmailByOtp(otpnum);
	}

}
