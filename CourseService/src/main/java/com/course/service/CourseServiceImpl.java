package com.course.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.course.entity.CourseEntity;
import com.course.model.Course;
import com.course.model.CourseStatus;
import com.course.repository.CourseRepo;
import com.course.util.RabbitConstant;
import com.course.util.SequenceGeneratorService;

@Service
public class CourseServiceImpl implements CourseService {

	@Autowired
	private CourseRepo courseRepo;

	@Autowired
	private RabbitTemplate template;

	@Override
	public CourseEntity addCourse(Course course) {
		// TODO Auto-generated method stub
		CourseEntity courseEntity = new CourseEntity();
		courseEntity.setCourseId(SequenceGeneratorService.generateSequence(Course.SEQUENCE_NAME));
		courseEntity.setCoursename(course.getCoursename());
		courseEntity.setDescription(course.getDescription());
		courseEntity.setTechnology(course.getTechnology());
		courseEntity.setURL(course.getUrl());
		courseEntity.setDuration(course.getDuration());

		CourseStatus courseStatus = new CourseStatus(courseEntity, "Adding course details",
				courseEntity.getCoursename() + "course added");
		template.convertAndSend(RabbitConstant.EXCHANGE, RabbitConstant.ROUTING_KEY, courseStatus);
		return courseRepo.insert(courseEntity);
	}

	@Override
	public List<CourseEntity> getAllCourses() {
		// TODO Auto-generated method stub

		List<CourseEntity> coursesList = courseRepo.findAll();

		return coursesList;
	}

	@Override
	public List<CourseEntity> searchByCoursename(String coursename) {
		// TODO Auto-generated method stub
		List<CourseEntity> courseEntities = new ArrayList<CourseEntity>();
		courseRepo.findByCoursenameContaining(coursename).forEach(courseEntities::add);
		return courseEntities;
	}

	@Override
	public Optional<CourseEntity> getByCourseId(Long id) {
		// TODO Auto-generated method stub
		Optional<CourseEntity> entity = courseRepo.findById(id);
		return entity;
	}

	@Override
	public void deleteById(Long id) {
		// TODO Auto-generated method stub
		courseRepo.deleteById(id);
	}

	@Override
	public List<CourseEntity> searchByTechnology(String technology) {
		// TODO Auto-generated method stub
		List<CourseEntity> courseEntities = courseRepo.findByTechnologyContaining(technology);
		return courseEntities;
	}

	@Override
	public List<CourseEntity> searchByDuration(int fromduration, int toduration, String technology)

	{
		// TODO Auto-generated method stub
		List<CourseEntity> courseEntities = courseRepo.findByDurationBetweenAndTechnologyContaining(fromduration,
				toduration, technology);
		;
		return courseEntities;
	}

}
