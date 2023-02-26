package com.example.chatgptmicroservice.controller;

import ai.openai.api.models.CompletionResponse;
import ai.openai.api.models.EngineType;
import ai.openai.api.models.Model;
import ai.openai.api.models.OpenaiApiException;
import ai.openai.api.models.TextRequest;
import com.example.chatgptmicroservice.model.Question;
import com.example.chatgptmicroservice.repository.QuestionRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/chatgpt")
@Api(value = "ChatGPT microservice", tags = "ChatGPT microservice")
public class QuestionController {
    private static final String CSV_SEPARATOR = ";";
    private static final String CSV_HEADER = "Question;Answer";

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private Model openaiModel;

    @ApiOperation(value = "Obtenir une réponse du modèle OpenAI GPT-3 pour une question donnée")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Réponse du modèle OpenAI GPT-3"),
            @ApiResponse(code = 400, message = "Mauvaise demande"),
            @ApiResponse(code = 401, message = "Non autorisé"),
            @ApiResponse(code = 403, message = "Interdit"),
            @ApiResponse(code = 404, message = "Non trouvé")
    })
    @PostMapping("/ask")
    public ResponseEntity<String> askQuestion(
            @ApiParam(value = "Question à poser au modèle OpenAI GPT-3", required = true)
            @RequestBody String question) {
        TextRequest textRequest = new TextRequest();
        textRequest.setModel(openaiModel.getId());
        textRequest.setPrompt(question);
        textRequest.setTemperature(0.5);
            // set up the engine type
        EngineType engineType = new EngineType();
        engineType.setCompletion(new Boolean(true));
        engineType.setDavinci(new Boolean(true));

        textRequest.setEngine(engineType);

        try {
            CompletionResponse completionResponse = openaiModel.completions(textRequest);

            // retrieve the answer from the completion response
            String answer = completionResponse.getChoices().get(0).getText().trim();

            // create a new question object
            Question newQuestion = new Question(question, answer);

            // save the new question to the database
            questionRepository.save(newQuestion);

            // append the question and answer to a csv file
            File csvFile = new File("questions.csv");
            FileWriter writer = new FileWriter(csvFile, true);

            // write the header if the file is empty
            if (csvFile.length() == 0) {
                writer.write(CSV_HEADER);
            }

            writer.write("\n" + question + CSV_SEPARATOR + answer);
            writer.close();

            // return the answer
            return new ResponseEntity<>(answer, HttpStatus.OK);
        } catch (OpenaiApiException | IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Erreur lors de la récupération de la réponse", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

@ApiOperation(value = "Obtenir toutes les questions et réponses stockées dans la base de données")
@ApiResponses(value = {
        @ApiResponse(code = 200, message = "Liste de toutes les questions et réponses stockées dans la base de données"),
        @ApiResponse(code = 400, message = "Mauvaise demande"),
        @ApiResponse(code = 401, message = "Non autorisé"),
        @ApiResponse(code = 403, message = "Interdit"),
        @ApiResponse(code = 404, message = "Non trouvé")
})
@GetMapping("/questions")
public ResponseEntity<List<Question>> getAllQuestions() {
    List<Question> questionList = questionRepository.findAll();
    return new ResponseEntity<>(questionList, HttpStatus.OK);
}
}

