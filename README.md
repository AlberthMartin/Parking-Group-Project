# Parking App - Group Project

 This repository contains the backend and frontend code for the Parking App, developed as part of a group project course at ÅA University.
 
---
## Project File Structure
- **Backend**:`src/main/java/com/example/demo/controller`, `src/main/java/com/example/demo/service` `src/main/java/com/example/demo/model`, `src/main/java/com/example/demo/repository`,... 
+ Maven wrapper files (`mvnw`, `mvnw.cmd`) and `pom.xml`
- **Frontend**: `frontend/` folder (React Native)
---
## System Architecture Overview
<img width="662" height="519" alt="image" src="https://github.com/user-attachments/assets/89bc5f70-ac48-494b-8a20-5a60722aa3c6" />

---
## Tech Stack
- **Frontend**: React Native
- **Backend**: Java Spring Boot 
- **Database**: MySQL
- **Security**: JWT, Spring Security

---
## Current Database
<img width="759" height="703" alt="image" src="https://github.com/user-attachments/assets/558ef35b-36e5-4041-aa5e-629c03096d69" />

---
## Database Setup

The backend uses MySQL. Make sure you have a MySQL server running locally (e.g., via MySQL Workbench).

 1. Log in to MySQL as root
 2. Create the database if it doesn’t exist
 ```sql
   CREATE DATABASE parkingappdb;
```
 3. Create the user (replace 'yourpassword' with your desired password):
 ```sql
   CREATE USER 'parkingAppUser'@'localhost' IDENTIFIED BY 'yourpassword';
```
  (Replace 'yourpassword' with the one you used in application.properties.)
 4. Grant privileges
  ```sql
   GRANT ALL PRIVILEGES ON parkingappdb.* TO 'parkingAppUser'@'localhost';
   FLUSH PRIVILEGES;
```
This ensures your Spring Boot app can connect, read, and write.

### Configure application properties
 The configuration file is located at src/main/resources/application.properties. (**.gitignored** for security)
 Before running the app, update the following with your own credentials:

In your src/main/resources/application.properties:
```markdown
spring.datasource.url=jdbc:mysql://localhost:3306/parkingappdb?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=parkingAppUser
spring.datasource.password=yourpassword

spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```
---
## Running the Backend (after setting up the database)

### Build the Backend
The project uses Maven Wrapper, so you don’t need Maven installed globally.

Mac / Linux: (in project root)
```bash
./mvnw clean install
```
Windows:
```bash
./mvnw clean install
```

This will download the correct Maven version automatically and build the project.

### Run the Backend
Mac / Linux:
```bash
./mvnw spring-boot:run
```
Windows:
```bash
mvnw.cmd spring-boot:run
```

The backend will start on port 8080. The frontend can now make API requests to `http://localhost:8080`.


---
## Notes
- `.idea/` files are excluded from the repository. Each collaborator can use their own IDE settings.
- `application.properties` (backend) is gitignored - use your own credentials.
- The backend API endpoints are defined in the controllers in `src/main/java/com/example/demo/controller`
---
