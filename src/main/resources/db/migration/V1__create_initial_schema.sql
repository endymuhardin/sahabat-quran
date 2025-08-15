-- Create users table
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT random_uuid(),
    fullname VARCHAR(255),
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE,
    phone_number VARCHAR(50),
    is_active BOOLEAN DEFAULT true
);

-- Create teachers table
CREATE TABLE teachers (
    id UUID PRIMARY KEY DEFAULT random_uuid(),
    id_user UUID NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    address TEXT,
    bio TEXT
);

-- Create students table
CREATE TABLE students (
    id UUID PRIMARY KEY DEFAULT random_uuid(),
    id_user UUID NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    address TEXT
);

-- Create curriculums table
CREATE TABLE curriculums (
    id UUID PRIMARY KEY DEFAULT random_uuid(),
    name VARCHAR(255) NOT NULL,
    description TEXT,
    level VARCHAR(100)
);

-- Create rooms table
CREATE TABLE rooms (
    id UUID PRIMARY KEY DEFAULT random_uuid(),
    name VARCHAR(255) NOT NULL,
    location VARCHAR(255),
    capacity INTEGER
);

-- Create classes table
CREATE TABLE classes (
    id UUID PRIMARY KEY DEFAULT random_uuid(),
    name VARCHAR(255) NOT NULL,
    id_curriculum UUID REFERENCES curriculums(id),
    id_room UUID REFERENCES rooms(id)
);

-- Create class_schedules table
CREATE TABLE class_schedules (
    id UUID PRIMARY KEY DEFAULT random_uuid(),
    id_class UUID NOT NULL REFERENCES classes(id),
    id_teacher UUID NOT NULL REFERENCES teachers(id),
    day_of_week VARCHAR(20),
    start_time TIME,
    end_time TIME
);

-- Create class_sessions table
CREATE TABLE class_sessions (
    id UUID PRIMARY KEY DEFAULT random_uuid(),
    id_class_schedule UUID NOT NULL REFERENCES class_schedules(id),
    session_date DATE NOT NULL,
    notes TEXT, -- Berita Acara
    status VARCHAR(50)
);

-- Create attendances table
CREATE TABLE attendances (
    id UUID PRIMARY KEY DEFAULT random_uuid(),
    id_class_session UUID NOT NULL REFERENCES class_sessions(id),
    id_student UUID NOT NULL REFERENCES students(id),
    is_present BOOLEAN DEFAULT false,
    UNIQUE(id_class_session, id_student)
);

-- Create mutabaah table
CREATE TABLE mutabaah (
    id UUID PRIMARY KEY DEFAULT random_uuid(),
    id_student UUID NOT NULL REFERENCES students(id),
    id_curriculum UUID NOT NULL REFERENCES curriculums(id),
    notes TEXT,
    record_date DATE NOT NULL
);

-- Create exams table
CREATE TABLE exams (
    id UUID PRIMARY KEY DEFAULT random_uuid(),
    id_class UUID NOT NULL REFERENCES classes(id),
    name VARCHAR(255) NOT NULL,
    exam_date TIMESTAMP
);

-- Create exam_grades table
CREATE TABLE exam_grades (
    id UUID PRIMARY KEY DEFAULT random_uuid(),
    id_exam UUID NOT NULL REFERENCES exams(id),
    id_student UUID NOT NULL REFERENCES students(id),
    grade REAL
);

-- Create invoices table
CREATE TABLE invoices (
    id UUID PRIMARY KEY DEFAULT random_uuid(),
    id_student UUID NOT NULL REFERENCES students(id),
    amount DECIMAL(10, 2) NOT NULL,
    due_date DATE,
    status VARCHAR(255) NOT NULL,
    type VARCHAR(255) NOT NULL
);

-- Create payments table
CREATE TABLE payments (
    id UUID PRIMARY KEY DEFAULT random_uuid(),
    id_invoice UUID NOT NULL REFERENCES invoices(id),
    amount DECIMAL(10, 2) NOT NULL,
    payment_date TIMESTAMP,
    method VARCHAR(255) NOT NULL
);

-- Create events table
CREATE TABLE events (
    id UUID PRIMARY KEY DEFAULT random_uuid(),
    name VARCHAR(255) NOT NULL,
    description TEXT,
    start_time TIMESTAMP,
    end_time TIMESTAMP
);

-- Create event_attendances table
CREATE TABLE event_attendances (
    id UUID PRIMARY KEY DEFAULT random_uuid(),
    id_event UUID NOT NULL REFERENCES events(id),
    id_user UUID NOT NULL REFERENCES users(id),
    is_present BOOLEAN DEFAULT false,
    UNIQUE(id_event, id_user)
);
