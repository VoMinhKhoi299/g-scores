package vn.gscores;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test API tren PostgreSQL Testcontainers. Seeder nap fixture nho
 * (src/test/resources/dataset/test_scores.csv) nen chay nhanh va ket qua xac dinh.
 */
@Import(TestcontainersConfiguration.class)
@SpringBootTest
@ActiveProfiles("test")
class ScoreApiIntegrationTest {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc() {
        return MockMvcBuilders.webAppContextSetup(context).build();
    }

    @Nested
    @DisplayName("GET /api/scores/{sbd}")
    class Scores {

        @Test
        @DisplayName("Tra diem va tong hop theo nhom cho SBD hop le")
        void returnsStudentDetailForValidSbd() throws Exception {
            mvc().perform(get("/api/scores/01000001"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.sbd").value("01000001"))
                    .andExpect(jsonPath("$.foreignLanguage").value("N1"))
                    .andExpect(jsonPath("$.scores.length()").value(6))
                    .andExpect(jsonPath("$.groups.length()").value(3))
                    // Nhom A gom toan, vat_li, hoa_hoc — hoc sinh nay thi du ca 3.
                    .andExpect(jsonPath("$.groups[0].groupCode").value("A"))
                    .andExpect(jsonPath("$.groups[0].completed").value(true))
                    .andExpect(jsonPath("$.groups[0].total").value(19.65))
                    .andExpect(jsonPath("$.groups[0].average").value(6.55))
                    // Nhom B can ca 4 mon, hoc sinh nay chi co 1 (ngu_van) -> khong tinh tong.
                    .andExpect(jsonPath("$.groups[1].groupCode").value("B"))
                    .andExpect(jsonPath("$.groups[1].completed").value(false))
                    .andExpect(jsonPath("$.groups[1].total").value(Matchers.nullValue()));
        }

        @Test
        @DisplayName("SBD khong phai 8 chu so tra ve 400")
        void rejectsMalformedSbd() throws Exception {
            mvc().perform(get("/api/scores/12345"))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.status").value(400));
        }

        @Test
        @DisplayName("SBD khong co trong du lieu tra ve 404")
        void rejectsUnknownSbd() throws Exception {
            mvc().perform(get("/api/scores/99999999"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.message").isNotEmpty());
        }
    }

    @Nested
    @DisplayName("GET /api/reports/distribution")
    class Distribution {

        @Test
        @DisplayName("Tra phan bo diem cua mot mon")
        void returnsDistributionForValidSubject() throws Exception {
            mvc().perform(get("/api/reports/distribution").param("subjectCode", "toan"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].subjectCode").value("toan"))
                    .andExpect(jsonPath("$[0].bands.length()").value(4))
                    .andExpect(jsonPath("$[0].totalStudents").value(3));
        }

        @Test
        @DisplayName("Tra phan bo tat ca mon khi khong chuan subjectCode")
        void returnsAllSubjectsWhenParamMissing() throws Exception {
            mvc().perform(get("/api/reports/distribution"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(9));
        }

        @Test
        @DisplayName("Mon khong ton tai tra ve 404")
        void rejectsUnknownSubject() throws Exception {
            mvc().perform(get("/api/reports/distribution").param("subjectCode", "mon_la"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404));
        }
    }

    @Nested
    @DisplayName("GET /api/reports/groups")
    class Groups {

        @Test
        @DisplayName("Tra 3 nhom A, B, C kem so mon")
        void returnsAllStandardGroups() throws Exception {
            mvc().perform(get("/api/reports/groups"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(3))
                    .andExpect(jsonPath("$[0].groupCode").value("A"))
                    .andExpect(jsonPath("$[0].subjectCount").value(3))
                    .andExpect(jsonPath("$[1].subjectCount").value(4))
                    .andExpect(jsonPath("$[2].subjectCount").value(2));
        }
    }

    @Nested
    @DisplayName("GET /api/reports/groups/{code}/top")
    class GroupTop {

        @Test
        @DisplayName("Tra bang xep hang cua nhom A")
        void returnsTopForValidGroup() throws Exception {
            mvc().perform(get("/api/reports/groups/A/top"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.groupCode").value("A"))
                    .andExpect(jsonPath("$.subjectCount").value(3))
                    .andExpect(jsonPath("$.students.length()").value(2))
                    .andExpect(jsonPath("$.students[0].rank").value(1))
                    .andExpect(jsonPath("$.students[0].sbd").value("01000002"));
        }

        @Test
        @DisplayName("Nhom khong ton tai tra ve 404")
        void rejectsUnknownGroup() throws Exception {
            mvc().perform(get("/api/reports/groups/X/top"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404));
        }
    }

    @Nested
    @DisplayName("GET /api/dashboard/summary")
    class Dashboard {

        @Test
        @DisplayName("Tra tong ke dataset")
        void returnsDatasetSummary() throws Exception {
            mvc().perform(get("/api/dashboard/summary"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalStudents").value(3))
                    .andExpect(jsonPath("$.subjectCount").value(9))
                    .andExpect(jsonPath("$.groupCount").value(3))
                    .andExpect(jsonPath("$.sourceFile").value("test_scores.csv"));
        }
    }
}
