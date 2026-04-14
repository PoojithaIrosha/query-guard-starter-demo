package io.poojithairosha.query_guard_test_app.controller;

import io.poojithairosha.query_guard_test_app.model.Enrollment;
import io.poojithairosha.query_guard_test_app.service.CourseService;
import io.poojithairosha.query_guard_test_app.service.EnrollmentService;
import io.poojithairosha.query_guard_test_app.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class MainController {

    private final UserService userService;
    private final CourseService courseService;
    private final EnrollmentService enrollmentService;

    @GetMapping("/users/courses")
    public List<String> userCourses() {
        return userService.getUserCourseNames();
    }

    @GetMapping("/courses/lessons")
    public List<String> courseLessons() {
        return courseService.getCourseLessons();
    }

    @PostMapping("/enrollments/by-ids")
    public List<Enrollment> enrollments(@RequestBody List<Long> ids) {
        return enrollmentService.getEnrollmentsByIds(ids);
    }

}
