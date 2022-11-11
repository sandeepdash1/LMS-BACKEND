package com.course.service;

import java.util.List;
import java.util.Optional;

import com.course.entity.CourseEntity;
import com.course.model.Course;

public interface CourseService {

	CourseEntity addCourse(Course course);

	List<CourseEntity> getAllCourses();

	Optional<CourseEntity> getByCourseId(Long id);

	void deleteById(Long id);

	List<CourseEntity> searchByCoursename(String coursename);

	List<CourseEntity> searchByTechnology(String technology);

	List<CourseEntity> searchByDuration(int fromduration,int toduration,String technology);;

}
