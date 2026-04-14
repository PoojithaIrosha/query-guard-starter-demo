package io.poojithairosha.query_guard_test_app.service;

import io.poojithairosha.query_guard_test_app.dto.UserDTO;
import io.poojithairosha.query_guard_test_app.model.Enrollment;
import io.poojithairosha.query_guard_test_app.model.User;
import io.poojithairosha.query_guard_test_app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<String> getUserCourseNames() {
        List<User> users = userRepository.findAll();

        List<String> result = new ArrayList<>();

        for (User user: users) {
            for (Enrollment enrollment : user.getEnrollments()) {
                result.add(enrollment.getCourse().getTitle());
            }
        }

        return result;
    }

    public List<UserDTO> getUsersWithCourses() {
        List<User> users = userRepository.findAll();

        return users.stream().map(user -> {
            List<String> courses = user.getEnrollments().stream()
                    .map(e -> e.getCourse().getTitle())
                    .toList();

            return new UserDTO(user.getId(), user.getName(), courses);
        }).toList();
    }

}
