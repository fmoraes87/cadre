# README

## Project Overview

This project provides a comprehensive solution for data consumption, process definition, user management, and integration. 

This project is based on the core of iDempiere, leveraging its robust framework for enterprise resource planning (ERP). It uses metadata to register tables, windows, and permissions, allowing for flexible and dynamic configuration. There's no need to generate Java code unless desired, as the system can be extensively customized through its metadata-driven approach.

The front-end of this project utilizes the PrimeNG Diamond template, providing a modern and responsive user interface. This template is used under the Basic License. It's important to review the usage policy of the PrimeNG Diamond template, especially if the project will be used for commercial purposes, to ensure compliance with their licensing terms. For more details, please visit PrimeFaces Layouts Licenses.

Below are the key features and functionalities:

- **Data Consumption**: Allows data to be consumed by other applications using OAuth.
- **Parameter Definition**: Define parameters to be used within the application.
- **Script Creation**: Create and manage scripts using Groovy.
- **Process Definition**: Define and manage business processes.
- **Job Definition**: Define and schedule jobs.
- **Rule System Definition**: Establish a system of rules for the application.
- **User Management**: Manage users and their access rights.
- **Identity Management Support**: OAuth, Facebook Login, Google support for identity management.
- **Service Access Policies**: Define which services or methods can be accessed remotely.
- **Multitenancy**: Organize users by categories such as department, location, or office.
- **Integration Framework**: A central presentation layer platform for integrating backend or legacy content and services, supporting REST integration.
- **Maven Project Import**: Import projects from Workspace into any IDE with native Maven support.
- **Forms**: Validate input data, apply conditional rules, and fill in predetermined fields.
- **CSV Data Import**: Import data via .csv files.
- **Data Engine APIs**: APIs to help create flexible business applications, allowing CRUD operations on data definitions and enabling rapid development.
- **Filtering/Sorting**: Users can filter and sort search results in the user interface.
- **Governance**: Access control and permissions management in each project environment.
- **Responsiveness**: Fully responsive design.

### Translations
- Allows running applications in multiple languages without duplicating application logic.

### Localization
- Number formatting, time zones, and territories.

## Cadre Front-End

### Overview
The Cadre Front-End is an Angular application designed to provide the user interface for our system. This project uses Angular 11 and Node.js v14.18.2.

### Prerequisites
- Node.js v14.18.2
- Angular CLI

### Installation
1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd cadre-front-end
   ```

2. Install dependencies:
   ```bash
   npm install
   ```

### Running the Application
To run the application, use the Angular CLI command:
```bash
ng serve
```

By default, the application will be served at `http://localhost:4200/`.

### Building the Application
To build the application for production, use the following command:
```bash
ng build --prod
```

## Cadre Backend

### Overview
The Cadre Backend is a Java application built using Maven. This project requires Java 8 and Tomcat 9.089 for deployment.

### Prerequisites
- Java 8
- Maven
- Docker

### Running the Application

#### Using Docker Compose
To run the backend using Docker Compose, execute the following command:
```bash
docker-compose -f docker-compose-dev.yml up -d
```

#### Deploying to Tomcat
1. Build the project and generate the WAR file:
   ```bash
   mvn clean install
   ```

2. Locate the generated WAR file in the `target` directory.

3. Deploy the WAR file to Tomcat:
   - Copy the WAR file to the Tomcat `webapps` directory.
   - Start Tomcat (if not already running).

### Accessing the Application
Once deployed, the backend application will be accessible at `http://localhost:8080/<your-app-context>`.

### Additional Information
For further details on configuring and deploying the application, refer to the official documentation of Angular, Node.js, Java, Maven, and Tomcat.

---

This README provides a basic overview and instructions for setting up and running the Cadre Front-End and Backend projects. For more detailed documentation, please refer to the respective project directories and documentation files.
