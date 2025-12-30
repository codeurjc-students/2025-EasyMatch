## 👨‍💻 Code editing and execution

### 📁 Repository cloning
In order to **clone this GitHub repository**, run the following command in your terminal:
```bash
git clone https://github.com/codeurjc-students/2025-EasyMatch.git
```
Next, navigate to the project directory:

```bash
cd 2025-EasyMatch
```
#### Requirements

<table>
  <thead>
    <tr>
      <th>Software / Tool</th>
      <th>Version</th>
    </tr>
  </thead>
  <tbody>
    <tr>
      <td>Java</td>
      <td>21</td>
    </tr>
    <tr>
      <td>Maven</td>
      <td>3.9</td>
    </tr>
    <tr>
      <td>Spring Boot</td>
      <td>3.5</td>
    </tr>
    <tr>
      <td>Node.js</td>
      <td>20</td>
    </tr>
    <tr>
      <td>npm</td>
      <td>10.9</td>
    </tr>
    <tr>
      <td>MySQL</td>
      <td>8.0</td>
    </tr>
    <tr>
      <td>XAMPP</td>
      <td>8.0</td>
    </tr>
    <tr>
      <td>Visual Studio Code</td>
      <td>1.88</td>
    </tr>
    <tr>
      <td>Docker Desktop</td>
      <td>4.40</td>
    </tr>
    <tr>
      <td>Docker Compose</td>
      <td>2.34-desktop.1</td>
    </tr>
    <tr>
      <td>Docker</td>
      <td>28</td>
    </tr>
  </tbody>
</table>



### 🚀 Execution
This section describes the steps required to run the EasyMatch website locally, including the database setup and the execution of all components.

#### Database and required services

In order to the backend server, you must first start the database server which in this case is MySQL:
1. Open **XAMPP** (or you favourite local server manager).
2. Start both **Apache** and **MySQL** servers as shown in the next screenshot
![Xampp Control Panel](/images/xamppControlPanel.png)
3. Access phpMyAdmin (or the MySQL CLI) and create a new database named ```dbeasymatch```
```
CREATE DATABASE dbeasymatch;
```

#### Backend server
First, you must navigate to the folder ```backend``` where ```pom.xml``` is located: 
```bash
cd backend
```
Then, the backend server can be launched either directly from **VS Code** or via terminal using **Maven**. :
```bash
mvn spring-boot:run
```

This command starts the **Spring Boot application** and **connects** it to the previously created **MySQL database**.

#### Frontend client
In order to start the **Angular** frontend application, navigate to the ```frontend``` directory 
```bash
cd frontend
```
and execute the following commands:
```bash
npm install
npm start
```
This will launch the Angular development server, which by default runs on port 4200 and communicates with the backend through the REST API.

#### Website access
Once both the backend and frontend are running, open your browser and access ```http://localhost:4200```. From this url, you can explore and interact with the **EasyMatch web application** running locally.

### 🐳 Docker deployment

Docker was used to containerize the application by generating images that enable simple and consistent deployment. The following sections describe the required prerequisites and the steps needed to run the containerized application.

#### Requirements for Mac OS and Windows
In case you a Mac or Windows user, you must download **Docker Desktop** on your device. If it isn't already installed already, you can refer to the [official website](https://www.docker.com/products/docker-desktop/) for detailed installation instructions.


#### Requirements for Linux OS
In case you are a Linux user, you must install **Docker** and **Docker Compose** on your device. If it isn't downloaded already, you can refer to these official websites for detailed installation instructions:

- [Docker download](https://docs.docker.com/engine/install/ubuntu/)
- [Docker Compose download](https://docs.docker.com/compose/install/linux/)

> [!IMPORTANT]
> If you are a Mac or Windows user, **Docker Desktop** must be running in order to execute any **Docker** command, as it is required for the **Docker Daemon** to be operational.

To run and deploy the application, you must pull the Docker Compose configuration published on Docker Hub. In addition, a .env file is required in the [docker directory](https://github.com/codeurjc-students/2025-EasyMatch/tree/acd6919ff579c7d7f4a9460930405406af5dd6af/docker) of the repository. This file must be created following the template shown below:

```bash
SPRING_DATASOURCE_PASSWORD=password
MYSQL_ROOT_PASSWORD=password
SPRING_DATASOURCE_USERNAME=root
MYSQL_DATABASE=dbeasymatch
SPRING_DATASOURCE_URL=jdbc:mysql://db/dbeasymatch
```

Once the file has been created, follow the 2 steps below to start the application containers:

1. Pull the **Docker Compose** configuration from **Docker Hub**:

```bash
docker compose pull
```
2. Launch the containers using **Docker Compose**:
```bash
docker compose up
```

#### 💾 Sample data

The website is initialized with a set of representative **sample data** to facilitate testing and **demonstration** of its main **functionalities**. This data includes multiple **sports** (tennis, padel, football, and volleyball), each with different **game modes** and **scoring systems**, as well as several clubs located in different cities of Spain, with defined schedules, facilities, price ranges, and associated sports. A group of **users** is also created, including an **administrator** and various **regular users** with diverse profiles, skill levels, and personal descriptions. Additionally, multiple **matches** are **preloaded** with different dates, sports, clubs, organizers, prices, and player assignments. Some matches include recorded results, which are used to initialize basic player statistics. 