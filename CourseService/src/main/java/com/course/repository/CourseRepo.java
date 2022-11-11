package com.course.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.course.entity.CourseEntity;


@Repository
public interface CourseRepo extends MongoRepository<CourseEntity, Long> {

	Optional<CourseEntity> findById(Long id);

//	@Query("{ coursename :{$regex : ?0 , $options:'i' }}")
	List<CourseEntity> findByCoursenameContaining(String coursename);

	List<CourseEntity> findByTechnologyContaining(String technology);

	List<CourseEntity> findByDurationBetweenAndTechnologyContaining(int fromduration,int toduration,String technology);



}
