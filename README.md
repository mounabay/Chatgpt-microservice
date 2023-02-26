# ChatGPT Microservice

This microservice communicates with the ChatGPT API endpoint using the API key obtained from the OpenAI website. It updates a database in our system with questions and answers received from the API endpoint and appends the question and answer to a CSV file.

## Requirements

- Java 17
- Maven 3.8.3

## Configuration

To configure the microservice, create a `application.yml` file in the `src/main/resources` folder with the following content:

```yaml
openai:
  api:
    key: <YOUR_OPENAI_API_KEY>

csv:
  file:
    name: chat.csv
    
```    
    
Replace the "YOUR_OPENAI_API_KEY" placeholder in the code with your actual OpenAI API key.

Run your application using your IDE's run/debug configuration or command-line tools.
  


## Dependencies

- Spring Boot 3.0.0
- Java 17
- Swagger/OpenAPI 3.0.3
- Maven 3.8.3
- Actuator 3.0.0
- Openai API 1.0.0

## Runtime

1. Clone this git repository
2. Open the project in VSCode
3. Open a terminal and run the command `mvn spring-boot:run`
4. Access the application at `http://localhost:8080`
