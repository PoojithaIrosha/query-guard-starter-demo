package io.poojithairosha.query_guard_test_app.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@AllArgsConstructor
@Data
public class UserDTO {
    private Long id;
    private String name;
    private List<String> courses;
}
