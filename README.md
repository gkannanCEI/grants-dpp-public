# Spring Boot + Angular 21 - Grants Appliaction

Full-stack Angular 21 + Spring Boot Tutorial CRUD Application in that:
- Each Tutorial has id, title, description, published status.
- We can create, retrieve, update, delete Tutorials.
- We can also find Tutorials by title.



## Run Spring Boot application

### Database Setup

You have two options for the database:

#### Option 1: H2 (Zero Setup - Recommended for quick start)
The project is configured to use H2 in-memory database by default. No installation is required.
1. Just run the Spring Boot application.
2. The database and tables will be created automatically.

#### Option 2: PostgreSQL with Docker (Automated Setup)
If you have Docker installed, you can spin up a PostgreSQL instance:
1. Open a terminal in the project root.
2. Run: `docker-compose up -d`
3. This will create a database named `grants-dpp` on port `5435`.
4. Run the Spring Boot application with the `pg` profile:
   ```bash
   ./gradlew bootRun --args='--spring.profiles.active=pg'
   ```

### Default Login Credentials
- **Username:** `admin`
- **Password:** `password123`

### Run Command
```
mvn spring-boot:run
```
The Spring Boot Server will export API at port `8081`.

## Run Angular Client
```
npm install
ng serve --port 8081
```
