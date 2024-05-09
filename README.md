## ITA-challenges-backend

<hr>

### Table of contents

1. [Description](#description)
2. [Usage](#usage)
3. [Installation](#installation)
4. [Newcomers](#newcomers)
5. [Guidelines and Code Conventions](#guidelines-and-code-conventions)
6. [FAQ's](#faqs)
7. [Contributors](#contributors)

<hr>

### Description

The ITA-challenges-backend project is built using Java with the Spring Boot framework and reactive programming. It is an
initiative designed specifically for students enrolled in the IT Academy program at Barcelona Activa. 

The primary goal is to provide students with hands-on experience in a real-world development environment, 
simulating scenarios they may encounter in their future careers. 

Simultaneously, students collaborate to create a valuable learning tool for the [ITA challenges application](http://dev.ita-challenges.eurecatacademy.org/ita-challenge/challenges)
dedicated to code and programming coding-challenges. This project involves tackling new features, addressing legacy code, resolving bugs, 
and fostering teamwork. The emphasis is not only on technical proficiency but also on providing a holistic learning experience 
for our students.

This project exists thanks to all the people who contribute.

**For newcomers**, we strongly emphasize the importance of thoroughly reading the entire README. 
This document contains crucial information about installation, configuration, and daily processes necessary for 
effectively working on this project.

### Usage

Each microservice exposes REST APIs for interacting with it. To use any microservice, call its main method in the App class. For more information about each microservice, refer to the API documentation in each of their README.md file.

### Installation

#### Required programs

It is important to make sure you have all these tools installed and configured correctly before you start working on the project.

- **MongoDB**

- **Postman**

- **Consul** 

- **Docker**

- **Git** 

- **Java SE Development Kit 17.0.7**

See [GUIDELINES.md](GUIDELINES.md/#61-required-programs) to learn how to install and configure these tools.

The following programs are optional but recommended:

- **Mongo Tools**
- **Mongo Shell**
- **IntelliJ IDEA**
- **Plugin SonarLint para IntelliJ IDEA**

See [GUIDELINES.md](GUIDELINES.md/#71-recommended-programs) to learn how to install and configure these tools.

These are the libraries and frameworks used in the project, just in case you want to know more about them:

See [GUIDELINES.md](GUIDELINES.md/#5-libraries-used)

#### Project links

- [GitHub](https://github.com/IT-Academy-BCN/ita-challenges-backend). The source code.
- [Backend Sprint Backlog](https://github.com/orgs/IT-Academy-BCN/projects/15/views/1). The tasks to be done.
- [Product Backlog](https://github.com/orgs/IT-Academy-BCN/projects/13/views/1?visibleFields=%5B%22Title%22%2C%22Assignees%22%2C%22Status%22%2C%22Labels%22%5D). The requirements of the project.
- [Swagger](http://localhost:8762/itachallenge/api/v1/challenge/swagger-ui.html). The API documentation of each microservice.
- [Wiki](https://github.com/IT-Academy-BCN/ita-challenges-backend/wiki) The technical documentation of the project.
- [Figma](https://www.figma.com/file/ScWpDKxEB3wEGbztXMSJO3/Projectes-IT-Academy?type=design&node-id=559-2230&mode=design). The design of the project.

#### Procedure

1. Install the required software listed in the "Required programs" and "Project links" sections.

2. Make sure you have Docker Desktop turned on.

3. Import data into MongoDB. You can see how in the [guidelines.](./GUIDELINES.md#212-import-data-into-mongodb-example-for-micro-itachallenge-challenge-data)

4. Clone the repository:
```
git clone https://github.com/IT-Academy-BCN/ita-challenges-backend.git
```
5. Navigate into the project directory:
```
cd ita-challenges-backend
```
6. Install the dependencies:
```
./gradlew build
```
7. Run the microservice you want to start, for example 'itachallenge-challenge'.
```
./gradlew itachallenge-challenge:bootRun
```
8. You can test the application in the browser at: http://localhost:8762/itachallenge/api/v1/challenge/test

9. If you want to test the application in postman, you can import the environments and collections from the folder [`postman`](./postman) in the root of the project.

### Newcomers

#### Git configuration

#### Daily working flow

#### Kanban Boards

[Kanban board from project](https://github.com/orgs/IT-Academy-BCN/projects/15)

Guidelines 2.4

#### Pull Requests

### Guidelines and Code Conventions

See [GUIDELINES.md](GUIDELINES.md)

### FAQ's

* **Can I use any kind of AI as a help on this project? What is the best way to use AI?**
    - You are totally allowed to use AI in this project. 
   
    - The best way to use AI is first to understand the problem you want to solve, then to choose the best algorithm to solve it, and finally to implement it in the application.
  Please, remember to double-check the AI's results and to test it in different scenarios.
  
* **Is there any diagram of architecture, a technical description or similar?**
    - Yes, you can find it in the [wiki](https://github.com/IT-Academy-BCN/ita-challenges-backend/wiki)

* **To contribute to this project, do I need make a fork?**
    - No, you don't need to fork the project. We work with a single repository, adding branches for each feature and requesting pull requests to merge them.

* **Do I need start up the project fully on my pc to start coding?**
    - Of course not.

## Contributors

Por supuesto, aquí está la traducción al inglés:

---

Thank you for participating in this project! Contributions are essential to expand and enhance the experience for all users.

You can access the link [GUIDELINES.md](GUIDELINES.md/#24-work-with-cards-procedure) to get detailed information on how to contribute. We look forward to your contributions! 
Don't forget that the project exists thanks to everyone who contributes!


<a href="https://github.com/IT-Academy-BCN/ita-challenges-backend/graphs/contributors">
<img src="https://contrib.rocks/image?repo=IT-Academy-BCN/ita-challenges-backend" /></a>

<hr>


[![Contributor Covenant](https://img.shields.io/badge/Contributor%20Covenant-v2.0%20adopted-ff69b4.svg)](CODE_OF_CONDUCT_EN.md)
[![Contributor Covenant](https://img.shields.io/badge/Contributor%20Covenant-v2.0%20adopted-ff69b4.svg)](CODE_OF_CONDUCT_ES.md)
[![Contributor Covenant](https://img.shields.io/badge/Contributor%20Covenant-v2.0%20adopted-ff69b4.svg)](CODE_OF_CONDUCT_CA.md)

## License

- License information: [link](https://github.com/IT-Academy-BCN/ita-challenges-backend/tree/feature%23370b?tab=License-1-ov-file)



