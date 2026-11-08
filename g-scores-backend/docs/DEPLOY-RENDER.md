# Deploy g-scores len Render (mien phi)

Huong dan nay dua app len **Render** de chay online 24/7, chi phi 0 dong.
Bac 3 thanh phan chay tren Render, dung `render.yaml` (Blueprint) de deploy het mot lan.

```
                    +--------------------------+
  Nguoi dung  --->  |  Static Site  (frontend) |   khong bao gio ngu
                    |  g-scores.onrender.com    |
                    +------------+-------------+
                                 |  fetch /api/...
                                 |  (can CORS)
                                 v
                    +--------------------------+
                    |  Web Service (backend)   |   ngu sau ~15 phut idle
                    |  g-scores-api.onrender.com|
                    +------------+-------------+
                                 |  JDBC
                                 v
                    +--------------------------+
                    |  Postgres (Render free)   |   het han ~30 ngay
                    |  gscores                  |
                    +--------------------------+
```

---

## Buoc 0 — Code da chuan bi (co san trong repo nay)

| File | Vai tro |
|---|---|
| `g-scores-backend/Dockerfile` | Build backend thanh image Docker (2 stage: Maven build -> JRE chay) |
| `g-scores-backend/.dockerignore` | Loai bo `.git`, `target`, `.mvn` khoi context build |
| `g-scores-backend/src/main/java/vn/gscores/web/WebConfig.java` | Bat buoc CORS de frontend goi cross-origin |
| `g-scores-frontend/Dockerfile` | Build frontend thanh image nginx (dung khi deploy frontend bang Web Service) |
| `g-scores-frontend/nginx.conf` | nginx serve file tinh + proxy `/api` sang backend |

> **Da them CORS o `WebConfig.java`.** Khong co file nay, trinh duyet se chan
> request tu `g-scores.onrender.com` sang `g-scores-api.onrender.com` va moi
> man hinh bao loi "Failed to fetch".

Bien moi truong cua backend (dat tren Render, khong sua code):

```
SPRING_DATASOURCE_URL     jdbc:postgresql://<ten-db-host>:5432/gscores
SPRING_DATASOURCE_USERNAME <user>
SPRING_DATASOURCE_PASSWORD <password>
DATASET_PATH              /app/dataset/diem_thi_thpt_2024.csv
CORS_ALLOWED_ORIGINS      https://g-scores.onrender.com
```

Spring Boot tu dong phu bien moi truong len tren `application.yml`. Bien
`SPRING_DATASOURCE_*` co ten khop 1-1 voi `spring.datasource.*` nen khong can
doi mot dong code nao.

---

## Buoc 1 — Dua frontend len GitHub

Backend da co repo: `git@github.com:VoMinhKhoi299/g-scores.git` (chi chua backend).
Frontend **chua co repo rieng** — can tao:

```bash
cd g-scores-frontend
git init
git remote add origin git@github.com:VoMinhKhoi299/g-scores-frontend.git
git add .
git commit -m "feat: frontend vue + vite cho g-scores"
git push -u origin main
```

> `.gitignore` cua frontend lo `node_modules`, `dist` va `.env` (config local
> cua ban) nen chi dung cai chinh xac len GitHub. File `.env.production`
> (chua `VITE_API_BASE_URL` — doi cong khai) **nen commit** de Render build dung.

Neu GitHub chua co repo `g-scores-frontend`, tao repo empty tren web truoc
(khong chon "Add README"), roi chay 3 lenh tren.

---

## Buoc 2 — Commit Dockerfile + CORS cho backend

```bash
cd g-scores-backend
git add Dockerfile .dockerignore src/main/java/vn/gscores/web/WebConfig.java
git add src/main/java/vn/gscores/ src/main/resources/ docs/DEPLOY-RENDER.md
git commit -m "feat: them Dockerfile, CORS config va huong dan deploy"
git push origin main
```

> Repo backend hien tai con nhieu file **chua commit** (DTO, entity, service,
> web controller, migration `V3`/`V4`, test). Render build tu `main` nen thieu
> file la build fail. Hay `git add -A` de don het ve, tru `FETCH_HEAD`:
>
> ```bash
> git add -A
> git reset FETCH_HEAD
> git commit -m "feat: hoan tat backend"
> git push origin main
> ```

---

## Buoc 3 — (Phu kha nang) `render.yaml` Blueprint

Muon tao ca 3 service bang mot file duy nhat, tao `g-scores-backend/render.yaml`:

```yaml
services:
  - type: web
    name: g-scores-api
    runtime: docker
    dockerfilePath: ./Dockerfile
    dockerContext: .
    plan: free
    healthCheckPath: /actuator/health
    autoDeploy:
      enabled: true
    envVars:
      - key: SPRING_DATASOURCE_USERNAME
        fromDatabase:
          name: gscores-db
          property: username
      - key: SPRING_DATASOURCE_PASSWORD
        fromDatabase:
          name: gscores-db
          property: password
      - key: SPRING_DATASOURCE_URL
        value: "jdbc:postgresql://<DAY-URL-DB-SAU-KHI-TAO>"   # buoc sua tay
      - key: DATASET_PATH
        value: /app/dataset/diem_thi_thpt_2024.csv
      - key: CORS_ALLOWED_ORIGINS
        value: https://g-scores.onrender.com

databases:
  - name: gscores-db
    databaseName: gscores
    user: gscores
    plan: free
```

**Vay: sao `SPRING_DATASOURCE_URL` khong dung duoc `fromDatabase`?**
Blueprint tra ve URL loai `postgres://<host>/<db>` — deu Spring Boot can
`jdbc:postgresql://...` (HikariCP khong nhan ra scheme `postgres:`). Vi vay
cach thuc nghiem: de Blueprint tao DB + service, moi lan tao DB moi (het han
30 ngay) thi copy **Internal Database URL**, them doan `jdbc:` dau vao env var,
roi deploy lai. Cau hinh Dashboard (Buoc 4) dung chinh cach nay — de
**duoc don gon**, huong dan chi dau dan theo Dashboard.

---

## Buoc 4 — Deploy PostgreSQL + backend (Dashboard)

Day la cach chinh cuong nhat (khong can Blueprint). Tao truc tiep tren web UI Render:

**4.1. Tao database**
- Dashboard → **New +** → **Postgres**
- Name: `gscores-db`, Database: `gscores`, User: `gscores`
- Region: gần bạn (Singapore/India)
- Plan: **Free**
- Tao xong, copy **Internal Database URL**

**4.2. Tao backend web service**
- Dashboard → **New +** → **Web Service** → connect repo `g-scores`
- Runtime: **Docker**
- Dockerfile path: `./Dockerfile`, Context: `.`
- Region: **cùng region với Postgres** (bắt buộc — cross-region làm chậm nhiều)
- Instance type: **Free**
- Environment Variables (them 5 bien sau):

```
SPRING_DATASOURCE_URL     = jdbc:postgresql://<internal-db-url>   (them jdbc:postgresql://)
SPRING_DATASOURCE_USERNAME = <user>
SPRING_DATASOURCE_PASSWORD = <password>
DATASET_PATH              = /app/dataset/diem_thi_thpt_2024.csv
CORS_ALLOWED_ORIGINS      = https://g-scores.onrender.com
```

**4.3. Health check**
- Health Check Path: `/actuator/health`
- Backend da co dependency `spring-boot-starter-actuator` va expose `health` nen
  dung ngay. Render doi `healthCheckPath` giup service kiem tra DB truoc khi
  nhan traffic.

**4.4. Bien "Auto-Deploy"**
- Bat `Auto-Deploy` = **On**. Moi push code len `main` → Render tu build lai.

> **Thoi gian build dau tien lau (~8-15 phut).** Docker build phai tai Maven
> dependencies tu internet (`dependency:go-offline`) roi build JAR 62MB, sau do
> lan dau backend **nap 1.061.605 thi sinh** tu CSV vao Postgres (6 trieu dong
> `student_scores`). Seeder chay trong mot transaction nen may chay het
> RAM 512MB free tier co the bi OOM. Theo doi log Render de xem tien do.
>
> **Neu backend crash lap lai (OOM):** sua Dockerfile thanh multi-stage that
> hon, hoac nap du lieu theo tung phan. Khi do xem "Buoc 6 — Xu ly loi thuong gap".

---

## Buoc 5 — Deploy frontend (Static Site)

**5.1. Tao static site**
- Dashboard → **New +** → **Static Site**
- Repo: `g-scores-frontend`
- Branch: `main`
- Build Command: `npm ci && npm run build`
- Publish Directory: `dist`
- **Free** (khong ton tai gioi han, khong bao gio ngu)

**5.2. Rewrite cho SPA (bat buoc)**
Frontend dung `createWebHistory` (vue-router) → deep link nhu `/reports` se
tra 404 neu khong co rewrite. Vao **Redirects & Rewrites** cua static site:

```
Source:  /*
Destination: /index.html
Status: 200
```

Dung `200` (rewrite) chu khong phai `301/302` (redirect) — redirect lam doi
URL, khong dung cho SPA.

**5.3. Tro frontend ve backend**
Frontend build voi `VITE_API_BASE_URL`. File `.env` hien tai:
```
VITE_PROXY_TARGET=http://localhost:8080
```
Bien nay chi dung cho `npm run dev` (Vite dev server proxy `/api`). Khi build
de deploy, dung `VITE_API_BASE_URL` tro thang ve backend:

```
VITE_API_BASE_URL=https://g-scores-api.onrender.com
```

File `.env.production` da duoc tao san trong repo frontend (xem Buoc 1):
```
VITE_API_BASE_URL=https://g-scores-api.onrender.com
```
Vite tu dong doc `.env.production` khi `npm run build`, nen Build Command
giu nguyen: `npm ci && npm run build`. Neu doi domain sau do, sua file nay
va commit lai la du.

**Quan trong:** `import.meta.env` duoc Vite **inline luc build**, nen env var
phai co **trong luc build**, khong phai luc runtime. Neu them env var sau khi
build xong thi phai **re-deploy** moi co hieu luc.

---

## Buoc 6 — Kiem tra va xu ly loi

### 6.1. Kiem tra backend
```bash
curl https://g-scores-api.onrender.com/actuator/health
# mong doi: {"status":"UP",...}
```

### 6.2. Kiem tra API
```bash
curl https://g-scores-api.onrender.com/api/reports/groups
curl "https://g-scores-api.onrender.com/api/reports/distribution?subjectCode=toan"
```

### 6.3. Kiem tra frontend
Mo `https://g-scores.onrender.com` → cac man hinh Overview / Reports / Lookup
phai tai du lieu (khong phai loi CORS trong Console).

### Loi thuong gap

| Loi | Nguyen nhan | Cach sua |
|---|---|---|
| `Failed to fetch` / `CORS error` | `WebConfig.java` chua duoc deploy, hoac `CORS_ALLOWED_ORIGINS` sai | Commit + push `WebConfig.java`, kiem tra env var dung domain static site |
| Frontend build loi `npm ci` | `package-lock.json` khong khop `package.json` | `npm install` roi commit lai lock file |
| Backend crash vong lap (OOM) | Seed 6 trieu dong tren may 512MB | Xem Buoc 7 |
| `/reports` tra 404 | Chua them rewrite | Buoc 5.2 |
| API tra 500, log co "relation does not exist" | Flyway chua chay het | Kiem tra log; xem Buoc 7 |
| Slow dau tien moi lan mo | Service free bi "sleep" | Xem Buoc 7 |

---

## Buoc 7 — Xu ly "sleep" va vong lap do tu tai

### 7.1. Free web service ngu sau ~15 phut idle
Sau khi ngu, request dau tien phai **wake** may -> chay lai ~30-60 giay. Giao
dien co `TIMEOUT_MS = 90_000` (90 giay) nen van du.

Muon khuong "cú tai" 50 giay, dung **UptimeRobot** (mien phi):
- UptimeRobot.com → account free → Add Monitor
- Type: HTTP(s), URL `https://g-scores-api.onrender.com/actuator/health`
- Monitoring Interval: **5 minutes** (nho hon 15 phut cua Render)
- Monitor se goi health check moi 5 phut → Render khong bao gio ngu

> UptimeRobot chi chong "sleep" tren **may**. No khong chong Postgres het han.

### 7.2. Postgres free het han sau ~30 ngay
Render free Postgres co thoi han. Het han:
- Instance bi **xoa**, du lieu **khong khoi phuc**.
- Nhung app **tu phuc hoi**! Backend luu co `dataset_metadata` + CSV trong image.
  Neu DB bi tao lai trong, backend se:
  1. Flyway chay `V1`–`V4` tao schema,
  2. `CsvSeeder` nap lai 1.061.605 dong tu CSV trong image.

=> **Sau khi het han, chi can tao Postgres moi + tro backend vao lai** (5 phut),
khong can upload data thu cong.

### 7.3. Neu backend OOM khi seed (RAM 512MB)
Free tier chi co 512MB, con dataset 6 trieu dong. Fix dau tien: dua `pg_stat`
va `COPY` vao dung transaction (code hien tai da dung `CopyManager`, rat nhanh).

Neu van OOM, dung **Postgres free co dung luong lon hon**:
- Neon (free) — 0.5GB storage, du cho dataset nay
- Hoac `Neon` la provider Postgres duoc Render support

Hoac mot cach khac: nap seed **mot lan** vao Postgres local cua may, roi `pg_dump`
roi import. Phuc tap hon, chi lam khi that su can.

---

## Buoc 8 — Deploy lai / cap nhat code

```bash
# Backend
cd g-scores-backend
git add -A && git commit -m "fix: ..." && git push origin main
# Render tu build lai (Auto-Deploy = On)

# Frontend
cd g-scores-frontend
git add -A && git commit -m "fix: ..." && git push origin main
# Render tu build lai static site
```

Render hien log build. Backend can ~3-5 phut, frontend ~1-2 phut.

---

## Tong hop cac lenh can chay

```bash
# 1. Frontend -> GitHub
cd g-scores-frontend
git init && git remote add origin git@github.com:VoMinhKhoi299/g-scores-frontend.git
git add -A && git commit -m "feat: frontend" && git push -u origin main

# 2. Backend -> GitHub (keo het file chua commit)
cd ../g-scores-backend
git add -A && git reset FETCH_HEAD
git commit -m "feat: hoan tat backend + docker + cors" && git push origin main
```

## Link cuoi cung

- Frontend: `https://g-scores.onrender.com`
- Backend API: `https://g-scores-api.onrender.com`
- Health: `https://g-scores-api.onrender.com/actuator/health`
