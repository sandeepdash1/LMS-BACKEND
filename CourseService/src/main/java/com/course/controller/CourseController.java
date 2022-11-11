package com.course.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.course.entity.CourseEntity;
import com.course.model.Course;
import com.course.model.CourseStatus;
import com.course.service.CourseService;


@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/lms/course")
public class CourseController {
	private static final Logger LOG = Logger.getLogger(CourseController.class.getName());

	@Autowired
	private CourseService courseService;

	@PostMapping("/addcourse")
	public CourseEntity addCourse(@RequestBody Course course) {
		LOG.log(Level.INFO,"Add Course :"+ course);
		return courseService.addCourse(course);

	}

	@GetMapping("/allcourses")
	public ResponseEntity<List<CourseEntity>> getAllCourses() {
		try {
			List<CourseEntity> list = courseService.getAllCourses();

			if (list.isEmpty()) {
				LOG.log(Level.INFO,"List of Courses is empty:"+ list.isEmpty());
				return new ResponseEntity<>(HttpStatus.NO_CONTENT);
			}
			return new ResponseEntity<>(list, HttpStatus.OK);
		} catch (Exception e) {
			LOG.error("Exception - " + e);
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/getcourses/{id}")
	public ResponseEntity<CourseEntity> getCoursesById(@PathVariable Long id) {
		LOG.log(Level.INFO,"Get Course by ID");
		Optional<CourseEntity> entity = courseService.getByCourseId(id);

		if (entity.isPresent()) {
			LOG.log(Level.INFO,"Course ID found ::" + entity.map(i -> i.getCourseId()).get());
			return new ResponseEntity<>(entity.get(), HttpStatus.OK);
		} else {
			LOG.log(Level.INFO,"Course ID not found"+ entity.map(i -> i.getCourseId()).get());
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

	}

	@DeleteMapping("/deletecourses/{id}")
	public ResponseEntity<?> deleteTutorial(@PathVariable Long id) {
		try {
			if(id!=null) {
			courseService.deleteById(id);
			CourseStatus courseStatus= new CourseStatus();
			courseStatus.setMessage("Deleted successfully");
			LOG.log(Level.INFO,"ID has been "+ courseStatus.getMessage());
			return ResponseEntity.ok(courseStatus);
			}
		} catch (Exception e) {
			LOG.error("Exception - " + e);
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}

	@GetMapping("/getcourses")
	public ResponseEntity<?> searchByCourseName(
			@RequestParam(value = "coursename", required = false) String coursename) {
		LOG.info("searchByCourseName-started");
		List<CourseEntity> list = new ArrayList<CourseEntity>();
		list = courseService.searchByCoursename(coursename);
		try {
			if (!list.isEmpty()) {
				return new ResponseEntity<List<CourseEntity>>(list, HttpStatus.OK);
			} else {
				CourseStatus courseStatus = new CourseStatus();
				courseStatus.setMessage("No Course Found!!");
				LOG.info("searchByCourseName ::" + courseStatus);
				List<CourseEntity> list1 = null;
				list = list1;
				return new ResponseEntity<List<CourseEntity>>(list, HttpStatus.OK);
			}
		} catch (Exception e) {
			LOG.error("Exception - " + e);
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@GetMapping("/gettechnology")
	public ResponseEntity<List<CourseEntity>> searchByTechnology(
			@RequestParam(value = "technology", required = false) String technology) {
		List<CourseEntity> list = courseService.searchByTechnology(technology);
		try {
			if (!list.isEmpty()) {
				return new ResponseEntity<List<CourseEntity>>(list, HttpStatus.OK);
			} else {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		} catch (Exception e) {
			LOG.error("Exception - " + e);
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@GetMapping("/gettechnology/{fromduration}/{toduration}")
	public ResponseEntity<List<CourseEntity>> searchByDuration(
			@RequestParam(value = "technology", required = true) String technology, @PathVariable int fromduration,@PathVariable int toduration) {
		List<CourseEntity> list = courseService.searchByDuration(fromduration, toduration, technology);
		try {
			if (!list.isEmpty()) {
				return new ResponseEntity<List<CourseEntity>>(list, HttpStatus.OK);
			} else {
				CourseStatus courseStatus = new CourseStatus();
				courseStatus.setMessage("No Course Found!!");
				LOG.info("searchByCourseName ::" + courseStatus);
				List<CourseEntity> list1 = null;
				list = list1;
				return new ResponseEntity<List<CourseEntity>>(list, HttpStatus.OK);
			}
		} catch (Exception e) {
			LOG.error("Exception - " + e);
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
