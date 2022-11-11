package com.course.model;

import com.course.entity.CourseEntity;

public class CourseStatus {

	private CourseEntity courseEntity;
	
	private String status; //added the course details
	
	private String message;

	
	public CourseStatus() {
	}

	public CourseStatus(CourseEntity courseEntity, String status, String message) {
		super();
		this.courseEntity = courseEntity;
		this.status = status;
		this.message = message;
	}

	public CourseEntity getCourseEntity() {
		return courseEntity;
	}

	public void setCourseEntity(CourseEntity courseEntity) {
		this.courseEntity = courseEntity;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "CourseStatus [courseEntity=" + courseEntity + ", status=" + status + ", message=" + message + "]";
	}
	
	
}
