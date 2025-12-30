# Development Guide

## 📋 Table of contents

- [Introduction](#-introduction)
- [Technologies](#-technologies)
- [Tools](#-tools)
- [Architecture](#-architecture)
- [Quality Control](#-quality-control)
- [Development Process](#-development-process)

## 📖 Introduction

The EasyMatch web application is developed as a **Single Page Application** (SPA) on the frontend. In a SPA, only a single HTML file is initially loaded, and the content is dynamically updated using TypeScript, rather than reloading complete pages from the server. This SPA, therefore frontend, was built using **Angular 20**.

On the backend, the application was developed using **Spring Boot**, offering a REST API to handle communication between the server and the client. For data management the application uses a **MySQL** database.

### 📌 Summary

| Component              | Description                                                                 |
|------------------------|-----------------------------------------------------------------------------|
| Application Type       | Web SPA with REST API                                                       |
| Frontend               | Angular                                                                     |
| Backend                | Spring Boot                                                                 |
| Database               | MySQL                                                                       |
| Languages              | Java, TypeScript and JavaScript                                             |
| IDE                    | Visual Studio Code                                                          |
| Auxiliary Tools        | Postman (Visual Studio Extension), Git and GitHub               |
| Tests                  | Unit, Integration and System (e2e) Tests                                    |
| Testing Libraries      | JUnit, AssertJ, Mockito, REST Assured, Selenium, Jasmine and Karma        |
| Deployment             | Docker                                                                      |
| Development Process    | Iterative and incremental, version control with Git and CI/CD with GitHub Actions |

## 💻 Technologies

The website relies on the following technologies for its execution:

### Frontend
- **Node.js**: a JavaScript runtime environment that allows executing code outside the browser, typically used to support server-side development. For additional details, visit the [official Node.js web](https://nodejs.org/en).
- **npm**: default package manager for Node.js, used to install and manage project dependencies. More information is available on the [official npm website](https://www.npmjs.com/).
- **Angular**: a frontend framework designed for building dynamic Single Page Applications (SPAs). Learn more on the the  [Angular official web](https://angular.dev/).

---

### Backend
- **Maven**: build and dependency management tool for Java projects. For more information, consult the [Maven official web](https://maven.apache.org/).
- **Spring Boot**: a backend framework used to build Java-based web applications and RESTful APIs. For more details, visit the [official Spring website](https://spring.io/). Main modules:
  - **Spring MVC**: used to develop web applications and manage controllers.
  - **Spring Data**: facilitates interaction with the database.
  - **Spring Security**: handles authentication and authorization.
- **MySQL**: database used to store and manage the application data. For more information, consult the [MySQL official website](https://www.mysql.com/).

### Deployment

**Docker** is used as the containerization technology to deploy the application, ensuring a consistent and portable execution environment. For more information, please refer to the [official Docker website](https://www.docker.com/).


## 🔧 Tools 

The following IDEs and auxiliary tools were used during the development of this website:

### IDEs
- **Visual Studio Code (VS Code)** is a lightweight, open-source code editor developed by Microsoft. It offers powerful features such as debugging, version control, and extension support. It was used to develop both the client (frontend) and server(backend) of the website.

### Auxiliary tools
- **Postman** is collaborative platform for testing and managing APIs. It allows sending HTTP requests, inspecting responses, and organizing collections to simplify API development and debugging.

- **Git** is a decentralized version control tool that helps manage and track changes in source code throughout the software development lifecycle.

- **GitHub** is a cloud-based service that hosts Git repositories and enhances them with collaborative features for developers. Within GitHub, you can find tools such as:
  - **GitHub Actions** that enables automation of continuous integration (CI) workflows.
  - **GitHub Projects** that assists in organizing tasks using a Kanban-style board layout.

- **Docker Desktop** which is a containerization platform that enables building, running, and managing applications within isolated containers. It will be used to package and deploy the web application consistently across different environments (CD).

## 🏛️ Architecture 


### Deployment architecture

The user engages with the **frontend** (Angular), which communicates with the **backend** (Spring) via the **REST API**. The backend, in turn, interacts with the **MySQL database** using JDBC to store and retrieve data.

```mermaid
flowchart LR
    A[User] -- "HTTP:4200" --> B[Angular Frontend]
    B -- "API Request" --> C[Backend Spring Boot]
    C -- "API Response" --> B
    C -- "JDBC/SQL:3306" --> D[(MySQL)]
    D -- "SQL Response:3306" --> C
```

As it is shown in the previous diagram, the deployment of the application is divided into 3 different proceses:

  1. **Frontend process** which runs in a development server on port 4200.
  2. **Backend process** which on port 8080 (http), but in a near future will switch to 8443 (https).
  3. **MySQL database** which runs on port 3306.

### Domain model 

It is composed mainly of 4 entities: **User**, **Match**, **Sport** and **Club**. Embedded classes such as **MatchResult** or **PlayerStats** are modeled as value objects and represented using composition relationships, as they do not have an independent lifecycle.

![Domain model image](/images/domainModelDiagram.jpg)

### REST API

This project includes a complete OpenAPI specification that describes all available endpoints, their parameters, and responses.
To make the documentation easier to explore, it has been converted to a static HTML file format using OpenAPI and it is used [RawGitHackservice](https://raw.githack.com) so it can be accessible without executing the application.

🔗 Access the API documentation here:
👉 [OpenAPI HTML Documentation](https://raw.githack.com/codeurjc-students/2025-EasyMatch/refs/heads/main/docs/api/api-docs.html)

### Server architecture

The next diagram shows the backend structure, highlighting the different layers of the hexagonal architecture and the way they interact with one another:

![Backend structure diagram](/images/backendStructureDiagram.jpg)

### Client architecture
The next diagram presents the frontend structure, outlining the different layers of the **MVC architecture** and the way they interact with one another.
<br>
![Frontend structure diagram](/images/frontendStructureDiagram.jpg)



## 💎 Quality Control

### Backend Tests

#### Types of tests

- **Unit tests** aim to isolate and validate the **business logic** implemented within the **Service layer**. To achieve this, **mocks** are used to simulate the behavior of external dependencies such as **repositories**. In other words, unit tests focus exclusively on the logic within the **Services**, while their dependencies are replaced with mocked objects.

- **Integration tests** verify that the database interacts correctly with the rest of the backend components. Since the goal is to validate this real integration, **no mocks** are used, allowing the tests to run against the actual database layer.

- **System/e2e tests** are designed to validate the **complete functionality** of the system, ensuring that all components (from the frontend to the backend including database) work together as it is expected. They focus on verifying that API requests and user interactions produce the correct outcomes. For this purpose, **REST Assured** was used to test the REST API endpoints, while **Selenium** was employed to automate and validate user interface interactions in the browser.

#### Tests metrics

In order to generate a report using JaCoCo, you only need to execute the following command in a terminal inside the folder backend: 

```bash
mvn test
```

The following screenshot shows the coverage report of the executed tests:
![Coverage report backend image](/images/backendTestsCoverage.png)

JaCoCo generates automatically its coverage reports in the ```backend/target/site/jacoco``` folder. Nevertheless, in this project, the plugin configuration within the ```pom.xml``` file was modified so that the coverage report is generated **outside the target folder**. This adjustment ensures that the coverage report remains easily accessible at all times directly from the repository. 

```
<plugins>
  <plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <executions>
      <execution>
        <goals>
          <goal>prepare-agent</goal>
        </goals>
      </execution>
      <execution>
        <id>report</id>
        <phase>test</phase>
        <goals>
          <goal>report</goal>
        </goals>
        <configuration>
          <outputDirectory>${project.basedir}/coverage</outputDirectory>
        </configuration>
      </execution>
    </executions>
  </plugin>
</plugins>
```
As it is shown in the previous code snippet, the coverage report is now generated by default in this project in ```/backend/coverage```. By opening the file ```index.html``` within that folder you can display the report in your browser.

### Frontend tests

#### Types of tests

- **Unit test**, in this context, are applied to **services** and **components** to ensure that each one functions correctly and independently. Once again **mocks** are used but in TypeScript.v

- **Integration tests** focus on verifying the interaction between services and components. Since the objective is to test their real integration, dependencies are not mocked, allowing the tests to run against the actual application context.

#### Metric tests

In order to generate a report using ```Karma```, you only need to execute the following command inside a terminal in the folder frontend: 

```bash
ng test --watch=false --code-coverage
```

The next picture illustrates the coverage report of the executed tests:
![Coverage report frontend image](/images/frontendTestsCoverage.png)


This process generates the report in the ```/frontend/coverage/frontend directory```. By opening the ```index.html``` file from that folder will display the coverage report in the browser.

## 🐋 Deployment with Docker

### Image build

The project provides **Docker images** with 2 different tags: one intended for development (**dev**) and another for production, which corresponds to the **current stable version** of the application (at this moment **0.1**). To build the image using the Dockerfile, navigate to docker directory in the project and execute the following command:
```bash
docker build -f ./Dockerfile -t dmunozm5/easymatch:tag ..
```
### Image publication
The Docker images were published to Docker Hub in this [Docker repository](https://hub.docker.com/repositories/dmunozm5) using the following command:

```bash
docker push dmunozm5/easymatch:tag
```

After it’s published, you can pull the image from the repository to use it locally.

```bash
docker pull dmunozm5/easymatch:tag
```

The [docker-compose.yml](https://github.com/codeurjc-students/2025-EasyMatch/blob/acd6919ff579c7d7f4a9460930405406af5dd6af/docker/docker-compose.yml) file was published as an **OCI artifact** in this [Docker repository](https://hub.docker.com/repositories/dmunozm5) using the following command:
```bash
docker compose -f docker-compose.yml publish dmunozm5/easymatch-compose:tag --with-env -y
```

### Deploying the app using OCI artefact
To deploy the application, execute the **docker-compose** file with the following command inside docker's folder: 
```bash
docker compose -f docker-compose.yml up
```

## 🔁 Development process
The application is developed using an **incremental** and **cyclical** workflow aligned with **Agile principles**, integrating selected practices from methodologies such as **Extreme Programming** (**XP**) and **Kanban**. This approach emphasizes brief development cycles, continuous integration, and the use of automated tests to ensure code quality and steady progress.



### Task coordination
Task management throughout the development lifecycle was handled using **``GitHub Issues``** and **``GitHub Projects``**.
- **GitHub Issues** were used to define and track individual tasks associated with each stage of development, which were organized into milestones representing the different phases.
- **GitHub Projects** provided a Kanban-style board that allowed these issues to be visualized, organized, and managed efficiently.

#### GitHub project board
![GitHub project board](/images/projectBoardGitHub.png)

### Git
**Git** was used as the version control system for this project. The branching strategy follows **GitHub Flow**, with the following branch types:

- **main/master** which corresponds to the stable branch, always ready for deployment.
- **feature** used to implement new features. Each branch represents a single feature along with its corresponding tests.
- **fix** where bug-fixing occurs. Each branch addresses a specific bug.
- **documentation** used to add or update project documentation.
All changes are merged into **main/master** through **Pull Requests** (PR) at the end of the process.
### Continuous Integration (CI)

For Continuous Integration (CI), this project uses **GitHub Actions**. Two workflows were implemented to automate testing and ensure code quality:

#### Basic quality control

This workflow ```workflow-1.yml``` is triggered on every commit made to non-main branches, specifically when changes occur in either the backend or frontend source code.

##### Jobs

- **build-backend**: set up the Java environment (JDK 21) and prepare the backend for testing.

- **backend-unit-tests**: execute the unit tests of the backend using Maven with ```mvn test -Dgroups='unit'```

- **build-frontend**: set up the Node.js environment (Node 20) and install dependencies.

- **frontend-unit-tests**: execute the unit tests of the frontend with ```npx ng test --include "src/**/*.unit.spec.ts" --watch=false --browsers=ChromeHeadless --no-progress```

#### Complete quality control

This workflow ```workflow-2.yml``` is triggered when a Pull Request is opened targeting the main branch. It performs a complete validation pipeline of both the frontend and backend, including unit, integration, and system tests.

##### Jobs

- **build-backend**: set up the Java environment (JDK 21) and prepare the backend for testing.

- **build-frontend**: Set up and compile the frontend.

- **backend-unit-tests**: Run the backend unit tests with Maven using command ```mvn test -Dgroups='unit'```

- **backend-integration-tests**: Spin up a MySQL service and execute integration tests to validate database interaction ```mvn test -Dgroups='integration'```


- **build-frontend**: Set up and compile the frontend.

- **backend-system-tests**: Start the frontend and execute full end-to-end tests using the real backend ```mvn test -Dgroups='e2e'```

- **build-frontend**: Set up and compile the frontend.

- **frontend-unit-tests**: Run frontend unit tests with ```npx ng test --include "src/**/*.unit.spec.ts" --watch=false --browsers=ChromeHeadless --no-progress```

- **build-backend**: set up the Java environment (JDK 21) and prepare the backend for testing.

- **frontend-integration-tests**: Execute frontend integration tests using the same test runner ```npx ng test --include "src/**/*.integration.spec.ts" --watch=false --browsers=ChromeHeadless --no-progress```

### Continuous Delivery (CD)
For Continuous Delivery, this project uses 3 workflows, each triggered by different scenarios:
  - **Pull Request to main**
  - **Release creation**
  - **Workflow dispatch from any branch**

#### Pull Request to main 
This workflow ```workflow-3.yml``` is triggered whenever a pull request is opened against the main branch.
##### Steps
- **Login to DockerHub** using my credentials via GitHub project secrets. 
- **Build docker image** under the **dev** tag with ``docker build -t dmunozm5/easymatch:tag -f Dockerfile ..``, and publish it on Docker Hub with ``docker push dmunozm5/easymatch:tag``.
- **Publish docker-compose-dev.yml** as an OCI artifact under the **dev** tag with ``docker compose -f docker-compose-dev.yml publish dmunozm5/easymatch-compose:dev --with-env -y``.

#### Release creation
This workflow ```workflow-4.yml``` is triggered whenever a release is created. It performs the same steps as the previous workflows but with one key difference: it publishes both **Docker images** and **Compose artifacts** using **release version** and **latest** as tags.

#### Workflow dispatch from any branch
This workflow ```workflow-5.yml``` can be triggered **manually** from any branch using the **workflow_dispatch** event. It runs the same steps as the previous workflows, but the tagging strategy is different: 
The Docker image and Compose artifact are tagged using the format:
```bash
<branch-name>-<date-hour>-<commit>
```


### ⚙️ Tools usage

#### VS Code
Visual Studio Code is the primary IDE used for developing the application. It is simple to use: the main requirements are having a **JDK** installed on your system (for this project, JDK 21 is recommended) and installing the **Java Extension Pack** and **Spring Boot Extension Pack** extensions for Visual Studio Code.

#### Postman 
**Postman** is a tool used to send and test API requests and is also available as a **Visual Studio Code extension**. In this project, it is used to validate the backend endpoints. The API requests are organized into **Postman collections**, which can be imported into Postman (or the VS Code extension) to run the tests. To use it, first install Postman, then import the provided collection files from the project repository here: [EasyMatch Postman Collection](/EasyMatch%20collection.postman_collection.json).

### 🧪 Test execution

#### Backend tests

First, navigate to the ```backend``` folder
```bash
cd backend
```
and execute the next command
```bash
mvn test
```

#### Frontend unitary and integration tests
First, navigate to the ```frontend``` folder
```bash
cd frontend
```
and in order to run both unit and integration tests execute
```bash
ng test
# or
npm run test
```

To only run the unit tests:
```bash
ng test --include "src/**/*.unit.spec.ts" --watch=false --browsers=ChromeHeadless --no-progress
```
To only run the integration tests:
```bash
ng test --include "src/**/*.integration.spec.ts" --watch=false --browsers=ChromeHeadless --no-progress
```

### 🆕 Release creation 
You can create a release directly on GitHub. Simply go to the repository’s **Releases** section, create a new release, and publish it.

 [↩️ Return to README](../README.md)