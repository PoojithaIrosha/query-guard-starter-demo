package io.poojithairosha.query_guard_test_app.repository;

import io.poojithairosha.query_guard_test_app.model.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeacherRepository extends JpaRepository<Teacher, Long> {
}
