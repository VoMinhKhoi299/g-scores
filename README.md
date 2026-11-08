# g-scores

Đây là project nhỏ để xem và tra cứu điểm thi THPT Quốc gia năm 2024. Project có giao diện Vue, backend Spring Boot và dùng PostgreSQL để lưu dữ liệu.

Mình để sẵn Docker Compose để chạy các phần cùng lúc. Nếu máy đã có Docker thì không cần cài riêng database hay tự chạy backend từng bước.

## Chạy project bằng Docker

Cần cài Docker Desktop (hoặc Docker Engine có Docker Compose). Mở Terminal ở thư mục project rồi chạy:

```bash
docker compose up --build
```

Lần đầu chạy, Docker sẽ tải một số image và build project nên có thể hơi lâu. Backend cũng sẽ nạp file CSV vào database, bước này thường mất vài phút.

Khi chạy xong, mở trang web ở:

<http://localhost:8088>

API cũng được đi qua cùng địa chỉ, ví dụ:

<http://localhost:8088/api/dashboard/summary>

Nếu muốn xem backend đang nạp dữ liệu tới đâu, mở thêm Terminal và chạy:

```bash
docker compose logs -f backend
```

Nhấn `Ctrl+C` để thoát phần xem log. Lệnh này không dừng các container đang chạy.

## Dừng và chạy lại

Dừng các service nhưng giữ lại dữ liệu database:

```bash
docker compose down
```

Chạy lại sau đó thì dùng:

```bash
docker compose up -d
```

Database được lưu trong Docker volume nên sẽ còn dữ liệu sau khi tắt hoặc khởi động lại container. Nếu muốn xóa database để nạp CSV lại từ đầu, chạy:

```bash
docker compose down -v
docker compose up --build
```

Lưu ý là `docker compose down -v` sẽ xóa hẳn dữ liệu database hiện tại.

## Một vài cấu hình

Có thể tạo file `.env` ở thư mục gốc để đổi port hoặc thông tin database. Nếu chưa tạo file này thì project dùng các giá trị mặc định bên dưới:

```dotenv
WEB_PORT=8088
DB_PORT=5434
POSTGRES_DB=gscores
POSTGRES_USER=postgres
POSTGRES_PASSWORD=local-dev-only
```

Các giá trị mặc định này chỉ để chạy ở máy cá nhân. Đừng dùng nguyên mật khẩu mẫu nếu đưa database ra môi trường public.

File dữ liệu CSV được đóng gói cùng backend tại `g-scores-backend/dataset/diem_thi_thpt_2024.csv`.

## Chạy frontend riêng để phát triển

Nếu muốn chạy frontend bằng Vite, có thể để PostgreSQL và backend chạy trong Docker trước:

```bash
docker compose up -d postgres backend
```

Compose hiện chưa mở port backend ra máy host. Vì vậy, cần thêm cấu hình port sau vào service `backend` trong `compose.yaml`:

```yaml
ports:
  - "8080:8080"
```

Sau đó chạy frontend:

```bash
cd g-scores-frontend
VITE_PROXY_TARGET=http://localhost:8080 npm run dev
```

Trang Vite thường chạy ở <http://localhost:5173>. Khi phát triển, frontend sẽ chuyển các request `/api` sang backend ở port `8080`.
