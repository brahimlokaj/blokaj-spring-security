# Spring Security - JWT
###### By Brahim Lokaj

![](https://img.shields.io/badge/build-success-brightgreen.svg)

# Stack
![](https://img.shields.io/badge/java_17-✓-blue.svg)
![](https://img.shields.io/badge/spring_boot_2.7.3-✓-blue.svg)
![](https://img.shields.io/badge/mysql-✓-blue.svg)
![](https://img.shields.io/badge/jwt-✓-blue.svg)
![](https://img.shields.io/badge/swagger_3-✓-blue.svg)

# File structure
```
blokaj-spring-security/
│
├── src/main/java/org/blokaj
│   ├── configurations
│   │   └── AuthenticationBeanConfiguration.java
│   │   └── AuthenticationSecurityConfiguration.java
│   │   └── FlywayConfigurations.java
│   │
│   ├── controllers
│   │   └── AuthenticationController.java
│   │   └── ExceptionHandlerController.java
│   │
│   ├── exceptions
│   │   └── AuthenticationUserNotFoundException.java
│   │   └── BadRequestException.java
│   │   └── RefreshTokenBadRequest.java
│   │
│   ├── models
│   │   ├── enities
│   │   │   └── BaseEntity.java
│   │   │   └── Role.java
│   │   │   └── User.java
│   │   │
│   │   ├── resuests
│   │   │   └── LoginUser.java
│   │   │   └── RefreshToken.java
│   │   │
│   │   └── responses
│   │       └── FieldError.java
│   │       └── MyProfile.java
│   │       └── Response.java
│   │       └── ResponseToken.java
│   │
│   ├── repository
│   │   └── UserRepository.java
│   │
│   ├── security
│   │   ├── AuthenticationEntryPoint.java
│   │   ├── AuthenticationTokenComponent.java
│   │   ├── AuthenticationTokenFilter.java
│   │   ├── AuthenticationUser.java
│   │   └── AuthenticationUserDetailsService.java
│   │
│   ├── service
│   │   ├── impl
│   │   │   └── AuthenticationServiceImpl.java
│   │   └── AuthenticationService.java
│   │
│   ├── utiles
│   │   └── DateUtil.java
│   │
│   └── BlokajSpringSecurityApplication.java
│    
├── src/main/resources/
│   ├── db/migration
│   │   └── V1_0__init.sql
│   │   └── V1_1__insert-roles.sql
│   │   └── V1_2__insert_users.sql
│   │
│   └── application.yml
│
├── src/test/java/
│   └── org/blokaj
│       └── AuthenticationControllerTests.java
│   
├── .gitignore
├── blokaj-spring-security.iml
├── docker-compose.yaml
├── Dockerfile
├── LICENSE
├── mvnw
├── mvnw.cmd
├── README.md
└── pom.xml
```

# Introduction in JSON Web Token
#### Read: (https://jwt.io/introduction)

# Configuration Details
```yml
app:
  uri:
      base-uri: ${baseUri} #Base uri configuration
      login: /login #Login uri configuration
      refresh-token: /refresh-token #Refresh token uri configuration
      myProfile: /my-profile #My profile uri configuration
  jwt:
    header: ${authorizationHeader} #Authorization header for Bearer token
    secret: ${secretKey} #Secret key 
    access-token:
      expiration: 1 #Access token expiration in hour
    refresh-token:
      expiration: 1 #Refresh token expiration in days

### DATABASE CONFIGURATION ###
spring:
  datasource:
    url:  jdbc:mysql://localhost:3306/${databaseName}?serverTimezone=UTC&useLegacyDatetimeCode=false #Database URL
    username: ${username} #Database username
    password: ${password} #Database password
  jpa:
    generate-ddl: true
    hibernate:
      ddl-auto: update
    show-sql: true
    open-in-view: true
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQL5InnoDBDialect

### PORT CONFIGURATION ###
server:
  port: ${port} #Port 

### SWAGGER CONFIGURATION ###
springdoc:
  swagger-ui:
    enabled: true
    path: /docs/api/v1/swagger-ui.html
  api-docs:
    enabled: true
    path: /docs/api/v1/v3/api-docs
```

# Dockerfile Configuration
```dockerfile
FROM maven:3.8.6 AS build
COPY src /home/apps/security/src
COPY pom.xml /home/apps/security

# Run build without tests
RUN mvn -f /home/apps/security/pom.xml clean compile package -DskipTests

#
# Package stage
#
FROM openjdk:17-jdk-slim-buster
COPY --from=build /home/apps/security/target/blokaj-spring-security-1.0.0-SNAPSHOT.jar /usr/local/lib/security-app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/usr/local/lib/security-app.jar"]
```

# Docker Compose Configuration
```yml
version: '3'

services:

  blokaj-db:
    container_name: 'blokaj_db'
    image: 'mysql:latest'
    restart: always
    ports:
      - '3306:3306'
    expose:
      - '3306'
    environment:
      MYSQL_ROOT_PASSWORD: 'root'
      MYSQL_USER: '${youDatabaseUsername}'
      MYSQL_PASSWORD: '${youDatabasePassword}'
      MYSQL_DATABASE: ${youDatabaseName}
    volumes:
      - blokaj-db:/var/lib/mysql
    networks:
      - blokaj

  ### BLokaj Spring Security
  blokaj-spring-security:
    container_name: blokaj_spring_security
    build:
#      context:  #the path must be where the app has been located in your local machine.
      dockerfile: Dockerfile #How is the dockerfile name
    ports:
      - '8080:8080' #in which port the app will be
    environment:
      SPRING_DATASOURCE_USERNAME: '${youDatabaseUsername}'
      SPRING_DATASOURCE_PASSWORD: '${youDatabasePassword}'
      SPRING_DATASOURCE_URL: 'jdbc:mysql://blokaj-db:3306/${youDatabaseName}'

    depends_on:
      - blokaj-db
    networks:
      - blokaj

volumes:
  blokaj-db:
    driver: local

networks:
  blokaj:
```
# How to use this code?
1. Make sure you have [Java 17](https://oracle.com/java/technologies/javase/jdk17-archive-downloads.html) and [Maven](https://maven.apache.org) installed
2. Fork this repository and clone it
```
$ git clone git@github.com:brahimlokaj/blokaj-spring-security.git
```
3. Navigate into the folder
```
$ cd blokaj-spring-security
```
4. Install dependencies
```
$ mvn install
```
5. Run the project

```
$ mvn spring-boot:run
```
6. Navigate to `http://localhost:8080/swagger-ui.html` in your browser to check everything is working correctly. You can change the default port in the `application.yml` file

```yml
server:
  port: 8080
```
7. Post login http://localhost:8080/api/v1/login
```javascript
{
  "usernameOrEmail": "blokaj",
  "password": "123"
}
```
8. This is the response that you can receive
```javascript
{
    "code": 200,
    "status": "OK",
    "data": {
        "accessToken": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJibG9rYWpAZXhhbXBsZS5jb20iLCJyb2xlIjoiQURNSU4iLCJpZCI6MSwiZXhwIjoxNjcwMzQ0MDczLCJpYXQiOjE2NzAzNDA0NzMsImVtYWlsIjoiYmxva2FqQGV4YW1wbGUuY29tIiwidXNlcm5hbWUiOiJibG9rYWoifQ.zlB-LYTyz6xyUEhTf93yIck7SZdhQ_DalpGtigZ2-trAiFMLo40kFzMg5Yk-Pi1zNLQebgHioSpA33swJMeTZA",
        "refreshToken": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJibG9rYWpAZXhhbXBsZS5jb20iLCJleHAiOjE2NzA0MjY4NzQsImlhdCI6MTY3MDM0MDQ3NCwiZW1haWwiOiJibG9rYWpAZXhhbXBsZS5jb20ifQ.pRuM9tV5deNowvS9IIdXJ1b4E_7B_qx0bzNTQsopRuDoxRlbU84Z7t6c7OsVe7dz9WHxfpwf6TLD46APO9Xfow"
    }
}
```
# Run Application With Docker
1. Run

```$ docker compse build ```

2. Run

```$ docker compse up ```