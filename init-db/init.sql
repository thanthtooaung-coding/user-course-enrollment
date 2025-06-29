# Create the user that the microservices will use to connect to the database.
# The '%' wildcard allows connections from any host (i.e., from other containers).
CREATE USER 'user_enrollment_service_user'@'%' IDENTIFIED BY 'password';

# Create the databases for each service
CREATE DATABASE users_db;
CREATE DATABASE courses_db;
CREATE DATABASE enrollments_db;

# Grant all privileges on these new databases to the user
GRANT ALL PRIVILEGES ON users_db.* TO 'user_enrollment_service_user'@'%';
GRANT ALL PRIVILEGES ON courses_db.* TO 'user_enrollment_service_user'@'%';
GRANT ALL PRIVILEGES ON enrollments_db.* TO 'user_enrollment_service_user'@'%';

FLUSH PRIVILEGES;