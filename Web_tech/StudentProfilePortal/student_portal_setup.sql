
CREATE DATABASE IF NOT EXISTS student_portal
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;
USE student_portal;
DROP TABLE IF EXISTS students;
DROP TABLE IF EXISTS users;
CREATE TABLE users (
    id            INT          NOT NULL AUTO_INCREMENT,
    username      VARCHAR(50)  NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,               
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;



INSERT INTO users (username, password_hash) VALUES
(
  'admin',
  '$2a$10$DgERO1EAuGuhw95lliOgl.cUhEGoWPv94Qge5I4mGDcj8SxBmJV2S'
),
(
  'teacher',
  '$2a$10$fd8p3yFqsuCSnTono2UPgOwWWieUn/eaczDemPXtamLCw08ZaHQUi'

);



CREATE TABLE students (
    id            INT          NOT NULL AUTO_INCREMENT,
    roll_no       VARCHAR(20)  NOT NULL UNIQUE,        
    name          VARCHAR(100) NOT NULL,
    department    VARCHAR(100) NOT NULL,
    email         VARCHAR(150) NOT NULL,
    photo_path    VARCHAR(255) DEFAULT NULL,            
    document_path VARCHAR(255) DEFAULT NULL,            
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


INSERT INTO students (roll_no, name, department, email, photo_path, document_path) VALUES
(
  'CS2024001',
  'Alice Johnson',
  'Computer Science',
  'alice.johnson@college.edu',
  'alice.jpg',
  'alice_resume.pdf'
),
(
  'CS2024002',
  'Bob Martinez',
  'Computer Science',
  'bob.martinez@college.edu',
  'bob.jpg',
  'bob_resume.pdf'
),
(
  'EC2024003',
  'Priya Sharma',
  'Electronics & Communication',
  'priya.sharma@college.edu',
  NULL,           
  'priya_resume.pdf'
),
(
  'ME2024004',
  'David Chen',
  'Mechanical Engineering',
  'david.chen@college.edu',
  'david.jpg',
  NULL            
);



SELECT 'Users table:' AS '';
SELECT id, username, LEFT(password_hash, 20) AS hash_preview FROM users;

SELECT 'Students table:' AS '';
SELECT id, roll_no, name, department, photo_path, document_path FROM students;
