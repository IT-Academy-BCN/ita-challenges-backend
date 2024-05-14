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
8. [Contact Information](#contact-information)
9. [License](#License)

<hr>

### Description

The ITA-challenges-backend project is an application designed for users to participate in coding challenges. 
The application offers a diverse range of categories and features such as the type of language, the challenge difficulty or the user score. 
Moreover, users have access to links to take part in Hackatons and to helpful resources. 
This project is a training space where users can reinforce programming skills across the most commonly used 
languages in the software landscape.

The ITA-challenges-backend project is built using Java with the Spring Boot framework and reactive programming. 
Students enrolled in the IT Academy program at Barcelona Activa are an integral part of this project, 
as they collaborate in its development and use the application to improve their programming skills. 
Students have the opportunity to work in a real development environment, tackling new features, solving problems in existing code, 
correcting errors, and fostering teamwork.

This project exists thanks to all the people who contribute.

**For new contributors**, we strongly emphasize the importance of thoroughly reading the entire README. 
This document contains crucial information about installation, configuration, and daily processes necessary for 
effectively working on this project.

### Usage

This project provides REST API services for the ITA challenges application and consists of five microservices.

##### Challenges

The challenge microservice is designed to manage and distribute information related to challenges within the project. For instance, it provides details such as title, level, creation date, solution, and other relevant information. This microservice communicates with various modules of the project through a set of endpoints.

##### Score

The score microservice is built to provide information related to the score analysis for a specific challenge solution provided in a determined language.

##### User

The user microservice is crafted to efficiently manage and deliver information stemming from user, challenge, and score interactions. For example, it facilitates access to user challenge solutions, bookmarks, and challenge statistics.

##### Auth

The auth microservice plays a pivotal role in ensuring the security and integrity of user interactions within the system. It is primarily responsible for validating user authentication tokens, thereby safeguarding access to sensitive resources and functionalities. By validating authentication tokens, this microservice verifies the identity of users and ensures that only authorized individuals can access the system's features and data.

##### Document

The document microservice serves as a unified gateway consolidating all documents from various endpoints within the system. Acting as a single entry point, it will be seamlessly redirected by the project's overarching API Gateway. Named itachallenge-document, this microservice is entrusted with aggregating all Swagger documentation from other microservices, ensuring comprehensive access and management of system documentation.

Each microservice exposes REST APIs to interact with it. To use any microservice, you can refer to the [procedure](#procedure) section or start it from the App class in your IDE.

### Installation

#### Required programs

It is important to make sure you have all these tools installed and configured correctly before you start working on the project.

- **MongoDB**
- **Docker**
- **Java SE Development Kit 17.0.7**

See [GUIDELINES.md](GUIDELINES.md#61-required-programs) to learn how to install and configure these tools.

The following programs are optional but recommended:

- **Mongo Tools**
- **Mongo Shell**
- **Consul**
- **IntelliJ IDEA**
- **Plugin SonarLint para IntelliJ IDEA**

See [GUIDELINES.md](GUIDELINES.md#71-recommended-programs) to learn how to install and configure these tools.

These are the libraries and frameworks used in the project, just in case you want to know more about them:

See [GUIDELINES.md](GUIDELINES.md#5-libraries-used)

#### Project links

- [GitHub](https://github.com/IT-Academy-BCN/ita-challenges-backend). The source code.
- [Backend Sprint Backlog](https://github.com/orgs/IT-Academy-BCN/projects/15/views/1). The tasks to be done.
- [Product Backlog](https://github.com/orgs/IT-Academy-BCN/projects/13/views/1?visibleFields=%5B%22Title%22%2C%22Assignees%22%2C%22Status%22%2C%22Labels%22%5D). The requirements of the project.
- [Swagger](http://dev.ita-challenges.eurecatacademy.org:9080/swagger-ui/index.html?urls.primaryName=All%20OpenAPI%20Definition). The API documentation of each microservice.
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
  - If you use Windows cmd:
```
gradlew build
```
  - If you use Windows PowerShell, Linux, macOS, etc:
```
./gradlew build
```
7. Run the microservice you want to start, for example 'itachallenge-challenge'.
   - If you use Windows cmd:
```
gradlew itachallenge-challenge:bootRun
```
  - If you use Windows PowerShell, Linux, macOS, etc:
```
./gradlew itachallenge-challenge:bootRun
```
8. You can test the application in the browser at: http://localhost:8762/itachallenge/api/v1/challenge/test

9. If you want to test the application in postman, you can import the environments and collections from the folder [`postman`](./postman) in the root of the project.

### Newcomers

#### Git configuration

You can see git configuration in the [guidelines](./GUIDELINES.md#22-git-configurations)

#### Daily working flow

You can see daily working flow in the [guidelines](./GUIDELINES.md#23-daily-procedure)

#### Work with cards Procedure

You can see how work with cards in the [guidelines](./GUIDELINES.md#24-work-with-cards-procedure)

[Backend Sprint Backlog](https://github.com/orgs/IT-Academy-BCN/projects/15)

#### Pull Requests

You can see the points to consider when doing a PR in the [guidelines](./GUIDELINES.md#25-Points-to-consider-when-doing-a-PR)

### Guidelines and Code Conventions

See [GUIDELINES.md](GUIDELINES.md)

### FAQ's

* **Can I use any kind of AI as a help on this project? What is the best way to use AI?**
    - You are totally allowed to use AI in this project. 
   
    - The best way to use AI is first to understand the problem you want to solve, then to choose the best algorithm 
  to solve it, and finally to implement it in the application.
  Please, remember to double-check the AI's results and to test it in different scenarios.
  
* **Is there any diagram of architecture, a technical description or similar?**
    - Yes, you can find it in the [Wiki](https://github.com/IT-Academy-BCN/ita-challenges-backend/wiki)

* **To contribute to this project, do I need make a fork?**
  - No, you don't need to fork of the project, you need to **clone** it.

  - Our workflow is based on a single repository model, where we create separate branches for each feature.
    Once a feature is completed, we initiate a pull request to merge the changes back into the main branch.

  - You can find how to do a clone in point 4 of the [Procedure](#procedure) section.
  
* **Do I need start up the project fully on my pc to start coding?**
    - No, you don't need to start up the project fully on your pc to start coding. 
  You can start coding in your IDE and run the **App** class in the microservice you are working on.
  
* **What should I do if I discover a bug?**
   - If you discover a new bug, please create a new issue in the GitHub repository ita-challenges-backend repository 
  describing the bug instead of trying to resolve it in another issue, and comment it to the product owner.
  
* **What are the coding standards for this project?**
  - The coding standards for this project are defined in the [GUIDELINES.md](GUIDELINES.md) file in the root of the repository. 
  Please read and follow them before contributing.

* **How can I get help if I have difficulty contributing or understanding the project?**
  - Feel free to ask in the daily meetings or to your team if you find something hard to understand or if you encounter any other technical issues.
  
* **What programs do I need and how to proceed to set up the project?**
  - Just go to the paragraphs: [Installation](#installation) and [Procedure](#procedure) and follow the steps.
  
## Contributors

Thank you for participating in this project! Contributions are essential to expand and enhance the experience for all users.

You can access the link [GUIDELINES.md](GUIDELINES.md#24-work-with-cards-procedure) to get detailed information on how to contribute. We look forward to your contributions! 
Don't forget that the project exists thanks to everyone who contributes!

## Contact Information

Project manager: Jonatan Vicente.

Discord: Jonatan Vicente#1211


<a href="https://github.com/IT-Academy-BCN/ita-challenges-backend/graphs/contributors">
<img src="https://contrib.rocks/image?repo=IT-Academy-BCN/ita-challenges-backend" /></a>

<hr>


[![Contributor Covenant](https://img.shields.io/badge/Contributor%20Covenant-v2.0%20adopted-ff69b4.svg)](CODE_OF_CONDUCT_EN.md)
[![Contributor Covenant](https://img.shields.io/badge/Contributor%20Covenant-v2.0%20adopted-ff69b4.svg)](CODE_OF_CONDUCT_ES.md)
[![Contributor Covenant](https://img.shields.io/badge/Contributor%20Covenant-v2.0%20adopted-ff69b4.svg)](CODE_OF_CONDUCT_CA.md)

## License

- License information: [link](https://github.com/IT-Academy-BCN/ita-challenges-backend/blob/main/LICENSE)



