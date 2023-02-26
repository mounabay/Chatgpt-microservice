// For test the API 

package com.example.chatgpt.controller;

import com.example.chatgpt.model.Question;
import com.example.chatgpt.repository.QuestionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class QuestionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private QuestionRepository questionRepository;

    private Question testQuestion;

    @BeforeEach
    void setUp() {
        testQuestion = new Question("Test question", "Test answer");
    }

    @Test
    void askQuestion() throws Exception {
        when(questionRepository.save(testQuestion)).thenReturn(testQuestion);

        MvcResult result = mockMvc.perform(post("/questions")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"question\": \"Test question\"}")
                .header("Authorization", "Bearer test_token"))
                .andExpect(status().isOk())
                .andReturn();

        assertThat(result.getResponse().getContentAsString()).isEqualTo("Test answer");
    }

    @Test
    void getAllQuestions() throws Exception {
        when(questionRepository.findAll()).thenReturn(Collections.singletonList(testQuestion));

        MvcResult result = mockMvc.perform(get("/questions")
                .header("Authorization", "Bearer test_token"))
                .andExpect(status().isOk())
                .andReturn();

        assertThat(result.getResponse().getContentAsString()).contains("Test question", "Test answer");
    }
}
