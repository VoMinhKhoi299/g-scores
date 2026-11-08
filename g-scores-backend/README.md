# g-scores

The Web Intern Assignment — API tra du lieu diem thi THPT Quoc gia 2024.

## Cong nghe

Java 21 · Spring Boot 4 · Spring Data JPA · PostgreSQL · Flyway · Maven

## Chay du lieu

Dataset: `dataset/diem_thi_thpt_2024.csv` — 1.061.605 thi sinh, 9 mon.

```bash
# 1. Tao database
docker run -d --name gscores-db -p 5433:5432 \
  -e POSTGRES_DB=gscores -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres postgres:16

# 2. Chay app — Flyway tao schema, CsvSeeder nap du lieu lan dau
./mvnw spring-boot:run
```

Seeder tu bo qua neu `dataset_metadata` da co du lieu, nen restart app khong nap lai.

Bien moi truong dung duoc: `PORT`, `DATASET_PATH`, va cac thuoc tinh `spring.datasource.*`
trong `src/main/resources/application.yml`.

## REST API

| Method | Path | Mo ta |
|---|---|---|
| GET | `/api/scores/{sbd}` | Chi tiet diem cua mot hoc sinh + tong hop theo nhom A/B/C |
| GET | `/api/reports/distribution` | Phan bo diem theo mon. Bo qua `subjectCode` de lay tat ca mon |
| GET | `/api/reports/groups` | Danh sach nhom mon va cac mon thuoc nhom |
| GET | `/api/reports/groups/{groupCode}/top` | Bang top 10 hoc sinh cua nhom (doc tu bang pre-compute) |
| GET | `/api/dashboard/summary` | Tong ke dataset: so hoc sinh, so diem, so mon, thoi gian nap |

Vi du:

```bash
curl http://localhost:8080/api/scores/01000001
curl "http://localhost:8080/api/reports/distribution?subjectCode=toan"
curl http://localhost:8080/api/reports/groups/A/top
curl http://localhost:8080/api/dashboard/summary
```

Loi tra ve dung mot format chung:

```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Hoc sinh khong ton tai: 99999999",
  "timestamp": "2026-01-01T00:00:00Z",
  "fieldErrors": {}
}
```

## Kien truc

```
domain/     enum: Subject, SubjectGroup, ScoreBand, ForeignLanguage
entity/     JPA entities map bang trong DB
repository/ Spring Data JPA, moi truy van dung ORM
service/    logic tinh toan (tong diem theo nhom, phan bo)
dto/        record tra ra ngoai API
web/        @RestController + @RestControllerAdvice
seeder/     CsvSeeder: COPY CSV -> staging -> cac bang da tinh san
```

Diem tong hop theo nhom chi tinh khi hoc sinh thi **day du** tat ca mon cua nhom,
dung nghia voi bang `group_top_students` da tinh san.

So do: `docs/erd.mmd`, `docs/class-diagram.mmd`, `docs/data-flow.mmd`, `docs/subject-groups.mmd`.

## Test

```bash
./mvnw test
```

Test dung Testcontainers (PostgreSQL), nen can Docker dang chay.
