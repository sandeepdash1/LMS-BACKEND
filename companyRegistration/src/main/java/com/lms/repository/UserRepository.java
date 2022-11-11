package com.lms.repository;

import java.util.Optional;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;

import com.lms.models.User;

public interface UserRepository extends MongoRepository<User, String> {
	Optional<User> findByUsername(String username);

	Boolean existsByUsername(String username);

	Boolean existsByEmail(String email);

//	@Query("update UserDetail user set user.otp =:otp where user.username =:username")
//	void Update(@Param("otp") int otp, @Param("username") String username);

//	public User updateOtpfindAndReplace(int otp, String username) {
//		
//	}

	Optional<User> findEmailByOtp(int otpnum);

	Optional<User> findByEmail(String username);
}