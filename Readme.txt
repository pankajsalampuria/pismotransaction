Steps to run the service.

1. Checkout code locally from GitHub.
https://github.com/pankajsalampuria/pismotransaction.git

2. Build the code using maven
mvn clean install

3. Run the application
- using maven
    mvn spring-boot:run
- As executable jar file
    java -jar <location of local .m2 repository>/com/pismo/transaction/PismoTransaction/1.0.0-SNAPSHOT/PismoTransaction-1.0.0-SNAPSHOT.jar

4. Run Swagger to test the endpoints
http://localhost:8080/pismo/swagger-ui/index.html

5. Endpoints can also be invoked using curl
GET http://localhost:8080/pismo/accounts/
GET http://localhost:8080/pismo/accounts/{id}
POST http://localhost:8080/pismo/accounts/

GET http://localhost:8080/pismo/transactions/
GET http://localhost:8080/pismo/transactions/{id}
POST http://localhost:8080/pismo/transactions/