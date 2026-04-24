# Smart Campus REST API

**Module:** Client-Server Architectures (5COSC022C)

This project is a RESTful API for managing rooms, sensors, and sensor readings in a smart campus environment. It is built using Java, JAX-RS (Jersey), and Apache Tomcat.

---

## Technologies Used

- Java 11
- JAX-RS (Jersey)
- Apache Tomcat 9
- Maven
- JSON (Jackson)
- In-memory storage (ConcurrentHashMap)

---

## How to Run the Project

### 1. Build the project

mvn clean package

---

### 2. Deploy to Tomcat

Copy the WAR file:

target/smart-campus.war

into Tomcat:

/webapps/

---

### 3. Start Tomcat

Run:

bin/startup.sh  (Mac/Linux)  
bin/startup.bat (Windows)

---

### 4. Open API

Use:

http://localhost:8080/api/v1

---

## API Endpoints

### Rooms

GET /api/v1/rooms  
POST /api/v1/rooms  
GET /api/v1/rooms/{id}  
DELETE /api/v1/rooms/{id}  

---

### Sensors

GET /api/v1/sensors  
GET /api/v1/sensors?type=TYPE  
POST /api/v1/sensors  

---

### Sensor Readings

GET /api/v1/sensors/{id}/readings  
POST /api/v1/sensors/{id}/readings  

---

## Example Requests

### Create Room

POST /api/v1/rooms

{
  "id": "LAB-001",
  "name": "Computer Lab",
  "capacity": 50
}

---

### Create Sensor

POST /api/v1/sensors

{
  "id": "TEMP-001",
  "type": "TEMPERATURE",
  "status": "ACTIVE",
  "roomId": "LAB-001"
}

---

### Add Reading

POST /api/v1/sensors/TEMP-001/readings

{
  "value": 28.5
}

---

## Error Handling

- 400 → Invalid input  
- 404 → Resource not found  
- 409 → Duplicate data  
- 403 → Sensor under maintenance  

---

## Features

- RESTful API design  
- In-memory data storage  
- Filtering using query parameters  
- Sub-resource for readings  
- Exception handling using ExceptionMapper  
- Logging using JAX-RS filters  

---

## Notes

- No database is used  
- Data is stored temporarily in memory  
- All endpoints tested using Postman  

---

## Author

N.M.V.A.Navarathna