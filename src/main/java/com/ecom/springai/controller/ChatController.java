package com.ecom.springai.controller;

import com.ecom.springai.helper.PromptBuilder;
import com.ecom.springai.helper.PromptConstants;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api")
public class ChatController {

    private final ChatClient openAiChatClient;
    private final ChatClient ollamaChatClient;

    @Value("classpath:/promptTemplate/userPrompt.st")
    Resource userPrompt;

    public ChatController(@Qualifier("openAiChatClient") ChatClient openAiChatClient,@Qualifier("ollamaChatClient") ChatClient ollamaChatClient) {
        this.openAiChatClient = openAiChatClient;
        this.ollamaChatClient = ollamaChatClient;
    }

    @PostMapping("/ollama/transcript")
    public String generateNotes(@RequestParam("sectionNumber") int sectionNumber,
                                @RequestParam("videoNumber") int videoNumber,
                                @RequestPart("file")MultipartFile file,
                                @RequestParam(required = false) String title,
                                @RequestParam(required = false,defaultValue = "false") boolean codeHeavy) throws Exception {

        if (file.isEmpty()){
            throw  new IllegalArgumentException("Empty File");
        }

        String transcript = new String(file.getBytes(), StandardCharsets.UTF_8);
        String finalTitle = (title == null || title.isBlank()) ? file.getOriginalFilename() : title;
        var prompt = ollamaChatClient.prompt();

        ChatOptions options = null;

        if(codeHeavy){
            prompt.system(PromptConstants.CODE_HEAVY_PROMPT);
            options = ChatOptions.builder()
                    .temperature(0.15)
                    .topP(0.9)
                    .frequencyPenalty(0.2)
                    .presencePenalty(0.0)
                    .build();
            prompt.options(options);
        }


        String userPrompt = PromptBuilder.buildUserPrompt(sectionNumber,videoNumber,"",finalTitle,transcript);
        assert options != null;
        return prompt
                .user(userPrompt)
                .call().content();
    }

    @GetMapping("/ollama/chat")
    public String chat(@RequestParam String prompt){
        return ollamaChatClient.prompt(prompt)
                .call().content();
    }
}
