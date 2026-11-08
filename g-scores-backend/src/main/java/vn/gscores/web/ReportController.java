package vn.gscores.web;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vn.gscores.dto.DistributionResponse;
import vn.gscores.dto.GroupSummaryResponse;
import vn.gscores.dto.TopGroupResponse;
import vn.gscores.service.ReportService;

import java.util.List;

/**
 * Bao cao ve phan bo diem va xep hang theo nhom mon.
 */
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Validated
public class ReportController {

    private final ReportService reportService;

    /**
     * @param subjectCode ma mon trong enum Subject (toan, ngu_van, ...).
     *                   Rong thi tra ve phan bo cua tat ca cac mon.
     */
    @GetMapping("/distribution")
    public ResponseEntity<List<DistributionResponse>> getDistribution(
            @RequestParam(required = false) String subjectCode) {
        if (subjectCode == null || subjectCode.isBlank()) {
            return ResponseEntity.ok(reportService.getAllDistributions());
        }
        return ResponseEntity.ok(List.of(reportService.getDistribution(subjectCode)));
    }

    @GetMapping("/groups")
    public ResponseEntity<List<GroupSummaryResponse>> getGroups() {
        return ResponseEntity.ok(reportService.listGroups());
    }

    @GetMapping("/groups/{groupCode}/top")
    public ResponseEntity<TopGroupResponse> getGroupTop(
            @PathVariable
            @NotBlank(message = "groupCode khong duoc de trong")
            String groupCode) {
        return ResponseEntity.ok(reportService.getGroupTop(groupCode));
    }
}
