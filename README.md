# g-scores

Ứng dụng tra cứu và xem thống kê điểm thi THPT Quốc gia năm 2024. Đây là project được xây dựng để thực hành phát triển ứng dụng web cho bài IT Intern Assignment

## Demo

- Website: <https://g-scores-1-4gj7.onrender.com>
- Source code: <https://github.com/VoMinhKhoi299/g-scores>

## Tính năng

- Xem tổng quan và thống kê điểm thi theo môn
- Tra cứu điểm bằng số báo danh
- Xem phân bố điểm và thống kê theo tổ hợp môn

## Công nghệ

- **Frontend:** Vue 3, TypeScript, Vite
- **Backend:** Java 21, Spring Boot
- **Database:** PostgreSQL
- **Chạy local:** Docker Compose

## Triển khai

- **Frontend và backend:** Render
- **Database:** Neon PostgreSQL

Backend đang dùng gói Free của Render nên có thể tự tạm dừng khi không có truy cập. Lần đầu mở các chức năng cần dữ liệu, có thể cần đợi backend khởi động lại một chút. Em xin lỗi vì sự bất tiện này ạ.

## Chạy trên máy

Cần cài Docker Desktop hoặc Docker Engine có Docker Compose. Tại thư mục gốc của project, chạy:

```bash
docker compose up --build
```

Sau khi các dịch vụ khởi động, mở <http://localhost:8088>. Lần chạy đầu tiên có thể mất vài phút để nạp dữ liệu điểm thi.

Để dừng ứng dụng:

```bash
docker compose down
```
