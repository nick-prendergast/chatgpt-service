package com.github.kolomolo.service.openaiclient;

import com.github.kolomolo.service.openaiclient.service.OpenAIClientService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ChatIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OpenAIClientService openAIClientService;

    @Test
    void fullChatFlow() throws Exception {
        when(openAIClientService.chat(any())).thenReturn("Test response from ChatGPT");

        // Initial GET request to start session
        MvcResult initialResult = mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("chat"))
                .andReturn();

        MockHttpSession session = (MockHttpSession) initialResult.getRequest().getSession();

        // POST request with message
        MvcResult postResult = mockMvc.perform(post("/chatgpt")
                        .session(session)
                        .param("prompt", "test message"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/chatgpt/results"))
                .andReturn();

        session = (MockHttpSession) postResult.getRequest().getSession();

        // GET results with same session
        mockMvc.perform(get("/chatgpt/results").session(session))
                .andExpect(status().isOk())
                .andExpect(view().name("chat"))
                .andExpect(model().attributeExists("conversationHistory"));
    }

    @Test
    void directAccessToResults_ShouldRedirectToHome() throws Exception {
        mockMvc.perform(get("/chatgpt/results"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }
}