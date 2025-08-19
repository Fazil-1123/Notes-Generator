package com.ecom.springai.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.stringtemplate.v4.ST;

@RestController
@RequestMapping("/api")
public class ChatController {

    private final ChatClient client;

    public ChatController(ChatClient.Builder builder) {
        this.client = builder.build();
    }

    @GetMapping("/chat")
    public String chat(@RequestParam String prompt){
        return client.prompt(prompt)
                .call().content();
    }
}
