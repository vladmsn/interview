# Auto Repair Shop DVI Coding Exercise

## Steps to get started:

#### Prerequisites
- Maven 
- Java 21

### Goals
1. Design a CRUD API with data store using Spring Boot and in memory H2 database (pre-configured, see below)
2. API should include one object with create, read, update, and delete operations. Read should include fetching a single item and list of items.
3. Provide SQL create scripts for your object(s) in resources/data.sql
4. Demo API functionality using API client tool

### Considerations
This is an open-ended exercise for you to showcase what you know! We encourage you to think about best practices for structuring your code and handling different scenarios. Feel free to include additional improvements that you believe are important.

#### H2 Configuration
- Console: http://localhost:8080/h2-console 
- JDBC URL: jdbc:h2:mem:testdb
- Username: sa
- Password: password

<br/>

#### Problem Framing
We are building a minimal Digital Vehicle Inspection (DVI) system for an auto repair shop.

**Core Entities:**
- Vehicle: car in the shop for inspection (VIN, plate number, make, model, year)
- Inspection: technician's report on a vehicle which operates on a simple state machine (Draft -> Submitted -> Reviewed)
- InspectionItem: individual finding with supported types, severity levels, and optional notes

**API Requirements:**
1. Inspection Management:
   - Create, Read, Update, Delete inspections.
   - State transitions: Draft -> Submitted -> Reviewed.
2. Inspection Item Management:
   - Add, Read, Update, Delete inspection items within an inspection.
