# ITA Challenges Portal Backend

The ITA Challenges Portal is a {placeholder to mention tech keyword involved} initiative designed specifically for students enrolled in the IT Academy program at Barcelona Activa. The primary goal is to provide students with hands-on experience in a real-world development environment, simulating scenarios they may encounter in their future careers. Simultaneously, students collaborate to create a valuable learning tool for the ITA itinerary—a portal dedicated to code and programming coding-challenges. This project involves tackling new features, addressing legacy code, resolving bugs, and fostering teamwork. The emphasis is not only on technical proficiency but also on providing a holistic learning experience for our students.

sFor newcomers, we strongly advise reading through all the provided material before diving in.


### Newcomers

We're glad to have you on board. This section will guide you through the process of getting started with our project, which is divided into frontend and backend development. To help you understand the importance of our onboarding process, we'll discuss the benefits of the Work Breakdown Structure (WBS), our Git workflow, and the tools and libraries you'll need to familiarize yourself with. Remember, be proactive and seek answers on Google and Stack Overflow when you encounter challenges.

#### Quicklinks

- [Github](https://github.com/IT-Academy-BCN/ita-challenges-backend)
- [Sprint Backlog](https://github.com/orgs/IT-Academy-BCN/projects/15/views/1)
- [Product Backlog](https://github.com/orgs/IT-Academy-BCN/projects/13/views/1?visibleFields=%5B%22Title%22%2C%22Assignees%22%2C%22Status%22%2C%22Labels%22%5D)
- [API Swagger Doc]({placeholder to path}/swagger-ui/index.html)
- [Figma](https://www.figma.com/file/DynJHHUlOiqx3F5h9dtvAW/Projectes-IT-Academy?type=design&node-id=0-1&mode=design&t=FTuFtsErP6lXcfgz-0)
- [Demo]({placeholder to demo dev link})

#### Project modules brief

##### Challenges


The challenge microservice is designed to manage and distribute information related to challenges within the project. For instance, it provides details such as title, level, creation date, solution, and other relevant information. This microservice communicates with various modules of the project through a set of endpoints. For detailed documentation on how to interact with this microservice, please visit the following link:

- [Challenge microservice](itachallenge-challenge/README.md).


##### Score

The score microservice is build to serve information relates to the score analysis given an specific challenge solution provided in a determinated language.

- [Score microservice](itachallenge-score/README.md).


##### User

The user microservice is crafted to efficiently manage and deliver information stemming from user, challenge, and score interactions. For example, it facilitates access to user challenge solutions, bookmarks, and challenge statistics. For comprehensive documentation on how to interact with this microservice, please visit the following link:

- [itachallenge-user](itachallenge-user/README.md).

##### Auth

The auth microservice plays a pivotal role in ensuring the security and integrity of user interactions within the system. It is primarily responsible for validating user authentication tokens, thereby safeguarding access to sensitive resources and functionalities. By validating authentication tokens, this microservice verifies the identity of users and ensures that only authorized individuals can access the system's features and data.

- [itachallenge-auth](itachallenge-auth/README.md).


##### Document

The document microservice serves as a unified gateway consolidating all documents from various endpoints within the system. Acting as a single entry point, it will be seamlessly redirected by the project's overarching API Gateway. Named itachallenge-document, this microservice is entrusted with aggregating all Swagger documentation from other microservices, ensuring comprehensive access and management of system documentation. For detailed documentation on how to interact with this microservice, please visit the following link:

- [itachallenge-document](itachallenge-document/README.md).


##### Consul

Centralized service registry system that discovers, tracks, and monitors the microservices modules. For detailed documentation about Consul:

- [Consul](docker/consul.md).

##### APISIX Gateway

The integration of APISIX gateway serves as the backbone for orchestrating communication between the diverse microservices discussed earlier. Acting as a robust intermediary layer, APISIX efficiently routes requests, manages traffic, and ensures seamless interactions across the system. By leveraging APISIX's powerful capabilities, the system achieves enhanced scalability, resilience, and security, enabling smooth operation and optimal performance of all integrated microservices.

- [APISIX](docker/README.md).


#### Required versions

The project is compatible with the following verisons. It's essential to ensure that your development environment aligns with these specifications to maintain compatibility and maximize the functionality and performance of the project.

- Gradle 8.1.1
- Java 17.0.1

#### Backend Libraries and Tools

{placeholder for a list of libraries and tools, with brief description and, link reference}

#### Development Guidelines

The link to the project guidelines provides a vital resource to ensure consistency, quality, and efficiency in project development and collaboration. These guidelines establish standards and best practices for designing, implementing, and maintaining microservices, as well as other crucial aspects of software development. By adhering to these guidelines, contributors can more effectively and cohesively contribute to the project, thereby promoting its long-term success.

See [GUIDELINES.md](GUIDELINES.md) for more details.


##### Important procedures and meetings

- We hold a daily meeting at 9h15 AM from Monday to Thursday, and it is mandatory to assist.
- We have sprint planning every two weeks.
- We have a sprint review and retrospective every two weeks.
- Note: The project requires a weekly dedication of 15~25 hours. If you cannot dedicate enough time, nor be on the meetings, please reach out to the project lead.

###### Daily meeting

We hold a scrum daily meeting on Teams at 9h15 AM from Monday to Thursday, and it is mandatory to assist. The main purpose of the Daily Scrum is to inspect progress toward the Sprint Goal and adapt the Sprint Backlog as necessary, adjusting the upcoming planned work

See [Scrum Daily Meeting](https://www.scrum.org/resources/what-is-a-daily-scrum)

_Note: We have a Coffee brake at the 10h15 AM._

###### Sprint Planning meeting

We hold sprint planning every two weeks (at the end of every two-weeks sprint). Sprint Planning initiates the Sprint by laying out the work to be performed for the Sprint. This resulting plan is created by the collaborative work of the entire Scrum Team.

See [Sprint Planning Meeting](https://www.scrum.org/resources/what-is-sprint-planning)


###### Sprint Review meeting

We hold sprint review every two weeks (at the end of every two-weeks sprint). The purpose of the Sprint Review is to inspect the outcome of the Sprint and determine future adaptations. The Scrum Team presents the results of their work to key stakeholders and progress toward the Product Goal is discussed.

See [Sprint Review Meeting](https://www.scrum.org/resources/what-is-a-sprint-review)

###### Sprint Retrospective meeting

We hold sprint retrospective every two weeks (at the end of every two-weeks sprint). The purpose of the Sprint Review is to inspect the outcome of the Sprint and determine future adaptations. The Scrum Team presents the results of their work to key stakeholders and progress toward the Product Goal is discussed.

See [Sprint Retrospective Meeting](https://www.scrum.org/resources/what-is-a-sprint-retrospective?)


##### Git procedures

{placeholder for a list of key git procedures, with brief description}

##### Procedure for uploading the version

The following instructions outline the process for uploading a new version of a microservice within the project. It's essential to follow these steps meticulously to ensure the smooth transition and proper version management.

- Make sure you have Docker installed on your machine
- Modify the build.gradle file of the microservice you want to upload the version for (update the 'version' attribute)
- Modify the Dockerfile of the microservice to rename the version
- Update the tag corresponding to the microservice version in the conf/.env.dev file
- Request credentials from the Administrator to upload the image to DockerHub
- From the project's root directory, execute the following command to upload the image to DockerHub (Linux Bash)

```
./itachallenge-[nombreDelMicroservicio]/build_Docker.sh conf/.env.dev
```

#### Contributors

Please find the link to our contributors listed below.

See [contributors.md](contributors.md) for more details.

[![Contributor Covenant](https://img.shields.io/badge/Contributor%20Covenant-v2.0%20adopted-ff69b4.svg)](CODE_OF_CONDUCT_EN.md)

#### Wki

Explore the Wiki to gain insights into project architecture, development practices, and more:

See [Wiki](https://github.com/IT-Academy-BCN/ita-challenges-backend/wiki).

#### Code of conduct

We highly value a respectful and inclusive environment within our project community. Please review our project's Code of Conduct to understand the standards of behavior expected from all contributors. Our Code of Conduct outlines guidelines for communication, collaboration, and respectful interactions.

See [Code of conduct](https://github.com/IT-Academy-BCN/ita-challenges-backend/tree/develop?tab=coc-ov-file).


### Deployment

{placeholder for introduction to deployment procedure}