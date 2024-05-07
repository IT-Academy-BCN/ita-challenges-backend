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

For newcomers, we strongly advise reading through all the provided material before diving in.

### Usage

Each microservice exposes REST APIs for interacting with it. To use any microservice, call its main method in the App class. For more information about each microservice, refer to the API documentation in each of their README.md file.

### Installation

#### Required programs

- **MongoDB**: MongoDB is a NoSQL database widely used in application development. It is used to store and retrieve data efficiently. You can download [MongoCompass](https://www.mongodb.com/try/download/compass) from the official website.

- **Postman**: Postman is a tool that will allow you to easily test and document APIs. It is especially useful for sending HTTP requests and verifying the responses. You can download [Postman](https://www.postman.com/downloads/) from the official website.

- **Consul** is a service discovery and configuration tool. It is used to manage communication between different application components. You can download [Consul](https://developer.hashicorp.com/consul/downloads) from the official website.

- **Docker**: Docker is a platform that enables packaging and distributing applications in containers. It provides an isolated environment to run the application and its dependencies. You can download [Docker](https://www.docker.com/products/docker-desktop/) from the official website.

- **Git**: Git is a distributed version control system widely used in software development. It allows you to collaborate with other developers and keep a history of changes to source code. You can download [Git](https://git-scm.com/downloads) from the official website.

- **Java SE Development Kit 17.0.7**: Java SE Development Kit (JDK) is a set of tools needed to develop Java applications. Make sure you have installed version 17.0.7 of the JDK which is the one used in this project. You can download [JDK 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html) from the official website.

It is important to make sure you have all these tools installed and configured correctly before you start working on the project.

#### Project links

- GITHUB [link](https://github.com/IT-Academy-BCN/ita-challenges-backend)
- Backend Sprint Backlog [link](https://github.com/orgs/IT-Academy-BCN/projects/15/views/1)
- Product Backlog [link](https://github.com/orgs/IT-Academy-BCN/projects/13/views/1?visibleFields=%5B%22Title%22%2C%22Assignees%22%2C%22Status%22%2C%22Labels%22%5D)
- Figma [link](https://www.figma.com/file/ScWpDKxEB3wEGbztXMSJO3/Projectes-IT-Academy?type=design&node-id=559-2230&mode=design)

#### Procedure

Install the required software listed in the "Required programs" and "Project links" sections.

Clone the repository: 

    git clone https://github.com/IT-Academy-BCN/ita-challenges-backend.git

Navigate into the project directory:

    cd ita-challenges-backend

Install the dependencies:

    gradle build

Run the application: 

    gradle bootRun

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

* **May I use AI on this project? How can I use it the best way?**
    - You must do it.

* **Is there any diagram of architecture, a technical description or similar?**
    - Yes, you can find it in the [wiki](https://github.com/IT-Academy-BCN/ita-challenges-backend/wiki)

* **To contribute to this project, do I need make a fork?**
    - No, you don't need to fork the project. We work with a single repository, adding branches for each feature and requesting pull requests to merge them.

* **Do I need start up the project fully on my pc to start coding?**
    - Of course not.

## Contributors

Thank you for participating in this project! Contributions are essential to expand and enhance the experience for all users.

### Contribution Instructions

1. **To start working on your contribution, create a new branch in your local repository.**
   Use the format `feature#` followed by the assigned ticket number. For example:

   -git checkout -b feature#123

2. **After you've finished, you should push your branch and then submit a pull request.**

3. **For your first contribution, you should add your name and GitHub profile to "contributors.md" in a new branch. The name of the branch can be your name.**

We look forward to your contributions! Don't forget that the project exists thanks to everyone who contributes.



<a href="https://github.com/IT-Academy-BCN/ita-challenges-backend/graphs/contributors">
<img src="https://contrib.rocks/image?repo=IT-Academy-BCN/ita-challenges-backend" /></a>

<hr>


[![Contributor Covenant](https://img.shields.io/badge/Contributor%20Covenant-v2.0%20adopted-ff69b4.svg)](CODE_OF_CONDUCT_EN.md)
[![Contributor Covenant](https://img.shields.io/badge/Contributor%20Covenant-v2.0%20adopted-ff69b4.svg)](CODE_OF_CONDUCT_ES.md)
[![Contributor Covenant](https://img.shields.io/badge/Contributor%20Covenant-v2.0%20adopted-ff69b4.svg)](CODE_OF_CONDUCT_CA.md)




