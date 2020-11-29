# Bank Accounts
A Java application providing RESTful API, backing service and data model to manage bank accounts (creation, deposit, withdrawal and money transfer)

## Architecture
Information and details about the software architecture and the taken decision are contained into the document "Implementation_Architecture.pdf" (available under the root of the project).

## Api Documentation

<details>
<summary><b>ACCOUNT CACHE</b></summary>
 
**Endpoint**: <code>`GET api/v1/account/all` </code>

Return: all accounts

**Endpoint**: <code>`GET api/v1/account/<string:id>` </code>

Return: the requested account
```json
{
    "name": "string",
    "surname": "string",
    "pin": "string",
    "amount": "double"
}
```

**Endpoint**: <code>`DELETE api/v1/account/<string:id>` </code>

Return: the deleted id

**Endpoint**: <code>`POST api/v1/account` </code>

Return: the new-added id

Request Parameter:
```json
{
    "name": "string",
    "surname": "string",
    "pin": "string",
    "amount": "double"
}
```

**Endpoint**: <code>`PUT api/v1/account` </code>

Return: the updated id

Request Parameter: account id and JSON
```json
{
    "name": "string",
    "surname": "string",
    "pin": "string"
}
```

</details>

<details>
<summary><b>TRANSACTION CACHE</b></summary>
 
**Endpoint**: <code>`GET /api/v1/transaction/all` </code>

Return: all transactions (deposit, withdrawal and transfer)

**Endpoint**: <code>`GET /api/v1/transaction/<string:id>` </code>

Return: the requested transaction (whose JSON is different base on the transaction type)

FOR DEPOSIT AND WITHDRAWAL TRANSACTION:
```json
{
    "id": "string",
    "amount": "double",
    "accountId": "string",
    "type": "string"
}
```
FOR TRANSFER TRANSACTION:
```json
{
    "id": "string",
    "amount": "double",
    "fromAccountId": "string",
    "toAccountId": "string",
    "type": "string"
}
```

**Endpoint**: <code>`GET /api/v1/transaction/allByAccount/<string:id>` </code>

Return: the list of transaction for the requested account id

**Endpoint**: <code>`POST /api/v1/transaction/deposit` </code>

Return: the new-added id

Request Parameter:
```json
{
    "accountId": "string",
    "amount": "double",
    "pin": "string"
}
```

**Endpoint**: <code>`POST /api/v1/transaction/withdraw` </code>

Return: the new-added id

Request Parameter:
```json
{
    "accountId": "string",
    "amount": "double",
    "pin": "string"
}
```

**Endpoint**: <code>`POST /api/v1/transaction/transfer` </code>

Return: the new-added id

Request Parameter:
```json
{
    "fromAccountId": "1",
    "toAccountId": "2",
    "amount": "double",
    "pin": "string"
}
```

</details>

## Project Management
This Application is developed in JAVA 8 and Spring Boot.

Project and dependencies are managed by [MAVEN](https://maven.apache.org/install.html).

### Persistency
Persistency is obtained using [REDIS](https://redis.io/), a fast in-memory key–value database.
- REDIS Server is provided in a docker container (from image "[redis](https://hub.docker.com/_/redis)")
- REDIS Client: Redis-Template from Spring Boot + Lettuce (as connector) 

### Version Control System
GIT is the version-control system used in this the project.

"Develop" branch is used for developing new features.
"Main" branch is used for releasing tags.

### Other Tools
- Spring Boot:
    - Web: to build Web Service;
    - Data-Redis: to easily manage Redis on client side;
- Log4J: to manage logs;
- Jackson: to serialize and deserialize data;
- Swagger: to provide a GUI for RESTful API;

## Deploy and Run 
The application can be deployed and run within Docker container using the Docker-Compose file (which in turn is based on the DockerFile). 

In particular, there are two containers running in localhost (of course) and under the same network (with the parameter: <code>network_mode: "host" </code>):
- Redis Server Container, on port 6379;
- Bank-Accounts Container, on port 8080 which connects to Redis Server Container;

### Steps
1. Install [Docker](https://docs.docker.com/get-docker/);
2. Install [Docker-Compose](https://docs.docker.com/compose/install/);
3. On the project root:
    - Use maven goal to create the jar into /target: <code>mvn clean package</code>
    - Build the container: <code>docker-compose -f docker-compose-bankaccounts.yml build </code>
    - Run the container: <code>docker-compose -f docker-compose-bankaccounts.yml.yml up</code>
4. The APIs are now exposed on port 8080. It is convenient to use the swagger at this URL: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
5. Turn off the container: <code>docker-compose -f docker-compose-bankaccounts.yml down</code>

## Testing
This project contains both Unit and Integration Tests.
 
### Unit Tests
Unit Tests aims to test the correctness of business logic

#### Running
Unit-Tests can be run in these different ways:
- Run all Unit-Test: <code> mvn test </code>
- Run a single Unit-Test called "MyTestClass": <code> mvn -Dtest=MyTestClass test </code>
- Run a desired test using your IDE

### Integration Tests
Integration Tests are mainly related to the persistency layer test.

In order to be the most similar as possible to a real scenario, a Docker container running REDIS is mandatory to succeed. 

#### Preconditions for IT
To run Integration Tests the following tools must be installed:
- Install [Docker](https://docs.docker.com/get-docker/);
- Install [Docker-Compose](https://docs.docker.com/compose/install/);

#### Running IT
Steps to run integration-test:
1. Turn on docker containers for test:
- Move to folder ./docker: <code>cd docker/ </code>
- Run containers with docker-compose (only one image, thus no needs to build the container): <code>docker-compose -f docker-compose-it.yml up</code>

2. Choose the test and run/debug it

3. Stop containers with docker-compose: <code>docker-compose -f docker-compose-it.yml down</code>