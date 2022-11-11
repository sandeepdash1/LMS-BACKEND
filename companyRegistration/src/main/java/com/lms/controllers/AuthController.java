package com.lms.controllers;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.mail.MessagingException;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lms.models.ERole;
import com.lms.models.Role;
import com.lms.models.User;
import com.lms.payload.request.LoginRequest;
import com.lms.payload.request.SignupRequest;
import com.lms.payload.response.MessageResponse;
import com.lms.payload.response.UserInfoResponse;
import com.lms.repository.RoleRepository;
import com.lms.repository.UserRepository;
import com.lms.security.jwt.JwtUtils;
import com.lms.security.services.UserDetailsImpl;
import com.lms.models.ApiResponse;
import com.lms.security.services.OtpService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/lms/register")
public class AuthController {
	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	UserRepository userRepository;

	@Autowired
	RoleRepository roleRepository;

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	JwtUtils jwtUtils;

	@Autowired
	OtpService otpService;

	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);

		// int otp = otpService.finduserbyNameAndEmail(loginRequest.getUsername());
		String jwt = jwtUtils.generateJwtToken(authentication);
		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

		// ResponseCookie jwtCookie = jwtUtils.generateJwtCookie(userDetails);

		List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
				.collect(Collectors.toList());

//		return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtCookie.toString()).body(
//				new UserInfoResponse(userDetails.getId(), userDetails.getUsername(), userDetails.getEmail(), roles));
		return ResponseEntity.ok(new UserInfoResponse(userDetails.getId(), userDetails.getUsername(),
				userDetails.getEmail(), roles, jwt));
	}

	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
		if (userRepository.existsByUsername(signUpRequest.getUsername())) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
		}

		if (userRepository.existsByEmail(signUpRequest.getEmail())) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
		}

		// Create new user's account
		User user = new User(signUpRequest.getUsername(), signUpRequest.getEmail(),
				encoder.encode(signUpRequest.getPassword()));

		Set<String> strRoles = signUpRequest.getRoles();
		Set<Role> roles = new HashSet<>();

		if (strRoles == null) {
			Role userRole = roleRepository.findByName(ERole.ROLE_USER)
					.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
			roles.add(userRole);
		} else {
			System.out.println("--->>>>"+strRoles);
			strRoles.forEach(role -> {
				switch (role) {
				case "admin":
					Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
					roles.add(adminRole);
					System.out.println("++++++"+roles);
					break;
				default:
					Role userRole = roleRepository.findByName(ERole.ROLE_USER)
							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
					roles.add(userRole);
				}
			});
		}

		user.setRoles(roles);
		userRepository.save(user);

		return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
	}

	/**
	 * @PostMapping("/signout") public ResponseEntity<?> logoutUser() {
	 * ResponseCookie cookie = jwtUtils.getCleanJwtCookie(); return
	 * ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
	 * .body(new MessageResponse("You've been signed out!")); }
	 * 
	 * @throws MessagingException
	 **/

	@PostMapping("/otp")
	public ResponseEntity<?> generateOTP(@RequestBody User user) throws MessagingException {

		otpService.finduserbyName(user.getUsername());

		return ResponseEntity.ok(new MessageResponse("OTP generated successfully!"));

	}

	@PostMapping("/validateotp")
	public ResponseEntity<?> validateOtp(@RequestBody User user) throws Exception {
		final String SUCCESS = "Entered Otp is valid";
		final String FAIL = "Entered Otp is NOT valid. Please Retry!";
		Optional<User> user1 = otpService.findEmailByOtp(user.getOtp());
		int rawPassword = user.getOtp();
		String email = "";
		if (user1.isPresent()) {
			email = user1.get().getEmail();
		}

		// Validate the Otp
		if (rawPassword >= 0) {
			System.out.println("rawPassword"+rawPassword);
			int serverOtp = otpService.getOtp(email);
			if (serverOtp > 0) {
				System.out.println("serverOtp"+serverOtp);
				if (rawPassword == serverOtp) {
					otpService.clearOTP(email);

					return ResponseEntity.ok().body(new ApiResponse(true, SUCCESS));

				} else {
					return ResponseEntity.ok().body(new ApiResponse(false, FAIL));
				}
			} else {
				return ResponseEntity.ok().body(new ApiResponse(false, FAIL));
			}
		} else {
			return ResponseEntity.ok().body(new ApiResponse(false, FAIL));
		}
	}
}