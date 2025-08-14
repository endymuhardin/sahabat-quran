# Skema Database Yayasan Sahabat Quran

Berikut adalah rancangan skema database untuk aplikasi manajemen operasional Yayasan Sahabat Quran. Skema ini dibuat berdasarkan daftar fitur yang ada di file `README.md`.

## Entity Relationship Diagram (ERD)

```mermaid
erDiagram
    users {
        uuid id PK
        varchar fullname
        varchar username
        varchar password
        varchar role "ENUM('ADMIN', 'FINANCE', 'TEACHER', 'STUDENT')"
        varchar email
        varchar phone_number
        boolean is_active
    }

    teachers {
        uuid id PK
        uuid id_user FK
        text address
        text bio
    }

    students {
        uuid id PK
        uuid id_user FK
        text address
    }

    curriculums {
        uuid id PK
        varchar name
        text description
        varchar level
    }

    rooms {
        uuid id PK
        varchar name
        varchar location
        int capacity
    }

    classes {
        uuid id PK
        varchar name
        uuid id_curriculum FK
        uuid id_room FK
    }

    class_schedules {
        uuid id PK
        uuid id_class FK
        uuid id_teacher FK
        varchar day_of_week
        time start_time
        time end_time
    }

    class_sessions {
        uuid id PK
        uuid id_class_schedule FK
        date session_date
        text notes "Berita Acara"
        varchar status
    }

    attendances {
        uuid id PK
        uuid id_class_session FK
        uuid id_student FK
        boolean is_present
    }

    mutabaah {
        uuid id PK
        uuid id_student FK
        uuid id_curriculum FK
        text notes
        date record_date
    }

    exams {
        uuid id PK
        uuid id_class FK
        varchar name
        datetime exam_date
    }

    exam_grades {
        uuid id PK
        uuid id_exam FK
        uuid id_student FK
        float grade
    }

    invoices {
        uuid id PK
        uuid id_student FK
        decimal amount
        date due_date
        varchar status "ENUM('PENDING', 'PAID')"
        varchar type "ENUM('TUITION', 'EVENT', 'OTHER')"
    }

    payments {
        uuid id PK
        uuid id_invoice FK
        decimal amount
        datetime payment_date
        varchar method "ENUM('MANUAL', 'VA')"
    }

    events {
        uuid id PK
        varchar name
        text description
        datetime start_time
        datetime end_time
    }

    event_attendances {
        uuid id PK
        uuid id_event FK
        uuid id_user FK
        boolean is_present
    }

    users ||--o{ teachers : "is a"
    users ||--o{ students : "is a"
    users ||--o{ event_attendances : "attends"

    teachers ||--|{ class_schedules : "teaches"
    students ||--o{ invoices : "has"
    students ||--o{ attendances : "records"
    students ||--o{ exam_grades : "receives"
    students ||--o{ mutabaah : "has"

    curriculums ||--|{ classes : "has"
    curriculums ||--|{ mutabaah : "tracks"
    rooms ||--|{ classes : "is in"
    classes ||--|{ class_schedules : "has"
    classes ||--|{ exams : "has"

    class_schedules ||--|{ class_sessions : "has"
    class_sessions ||--|{ attendances : "records"

    exams ||--|{ exam_grades : "has"
    invoices ||--|{ payments : "is paid with"
    events ||--|{ event_attendances : "has"
```
