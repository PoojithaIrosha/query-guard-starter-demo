package io.poojithairosha.query_guard_test_app.service;

import io.poojithairosha.query_guard_test_app.model.Enrollment;
import io.poojithairosha.query_guard_test_app.repository.EnrollmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;

    public List<Enrollment> getEnrollmentsByIds(List<Long> ids) {
        List<Enrollment> result = new ArrayList<>();

        for (Long id : ids) {
            result.add(enrollmentRepository.findById(id).orElse(null));
        }

        return result;
    }
}