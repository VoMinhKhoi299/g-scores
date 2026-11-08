package vn.gscores.web;

import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.gscores.dto.StudentDetailResponse;
import vn.gscores.service.StudentService;

/**
 * Tra diem cua mot hoc sinh theo so bao danh.
 */
@RestController
@RequestMapping("/api/scores")
@RequiredArgsConstructor
@Validated
public class ScoreController {

    private final StudentService studentService;

    @GetMapping("/{sbd}")
    public ResponseEntity<StudentDetailResponse> getStudentScores(
            @PathVariable
            @Pattern(regexp = "^[0-9]{8}$", message = "sbd phai la 8 chu so")
            String sbd) {
        return ResponseEntity.ok(studentService.getStudentDetail(sbd));
    }
}
