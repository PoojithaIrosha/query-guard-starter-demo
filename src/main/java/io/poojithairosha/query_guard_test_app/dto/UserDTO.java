package io.poojithairosha.query_guard_test_app.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record UserDTO(
        Long id,
        String name,
        List<String> courses
) {
}
