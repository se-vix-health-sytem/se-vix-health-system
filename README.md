VIX Health System
Running the Project
  Clone the repository:
  git clone <repository-url>
  
  Open the project in IntelliJ IDEA.
  Reload Maven dependencies.
  Add the Firebase service account file at:
  src/main/resources/firebase/firebase-service-account.json
  
  Inside put the private key sent by email.
  
  Run the main Spring Boot class:
  VixHealthSystemApplication
  
  The application will be available at:
  http://localhost:8080
  
  The H2 database console is available at:
  http://localhost:8080/h2-console
  
Overview
  VIX Health System simulates the management of a healthcare facility and provides different functionalities depending on the user role. 
  It allows hospital employees and patients to interact with the system through dedicated interfaces.
  Main Features
    The application supports multiple user roles:
    Staff Manager: manages employees and can trigger password reset operations.
    Medical Specialist: accesses medical-related functionalities.
    Secretary: manages appointments, admissions, rooms, and patient-related operations.
    Technician: manages machines and updates their operational status.
    Buyer: manages hospital resources, inventory, and restocking.
    Patient: accesses patient-facing functionalities.
    Main functionalities include:
    Employee management
    Patient management
    Department browsing
    Appointment management
    Room and bed management
    Resource and inventory management
    Machine status monitoring
    Audit logging and traceability
    Firebase-based authentication for employees
  Technologies Used
    Java 21
    Spring Boot
    Spring MVC
    Spring Data JPA
    Thymeleaf
    H2 Database
    Firebase Authentication
    Jackson JSON
    Bootstrap
    
Architecture
  The project follows an MVC architecture combined with a layered architecture.
  The main layers are:
    Presentation Layer: Thymeleaf templates displayed to users.
    Controller Layer: handles HTTP requests and returns the appropriate views.
    Service Layer: contains the business logic of the application.
    Repository Layer: manages data access and persistence.
    Model Layer: represents the domain entities of the system.
    Data Layer: includes the H2 database, SQL scripts, and JSON-based storage files.
  This separation of responsibilities improves maintainability, modularity, and scalability.

Team
  Viviana Fraccaroli
  Alexandrina Harti
  Navjot Kaur
  Lorena Valentina Buitròn Zambrano
  
