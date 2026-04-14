package io.poojithairosha.query_guard_test_app.service;

import io.poojithairosha.query_guard_test_app.model.Course;
import io.poojithairosha.query_guard_test_app.model.Lesson;
import io.poojithairosha.query_guard_test_app.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;

    public List<String> getCourseLessons() {
        List<Course> courses = courseRepository.findAll();

        List<String> lessons = new ArrayList<>();

        for (Course course : courses) {
            for (Lesson lesson : course.getLessons()) {
                lessons.add(lesson.getTitle());
            }
        }

        return lessons;
    }
}
