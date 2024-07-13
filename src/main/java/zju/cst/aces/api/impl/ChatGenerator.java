package zju.cst.aces.api.impl;

import zju.cst.aces.api.Generator;
import zju.cst.aces.api.config.Config;
import zju.cst.aces.dto.ChatMessage;
import zju.cst.aces.dto.ChatResponse;
import zju.cst.aces.runner.AbstractRunner;
import zju.cst.aces.util.AskGPT;
import zju.cst.aces.util.CodeExtractor;

import java.util.List;

/**
 * ChatGenerator is a class to generate code by chat messages.
 * It contains methods to generate code by chat messages, ask GPT, extract code by response, get content by response, extract code by content.
 */
public class ChatGenerator implements Generator {

    Config config;


    /**
     * @param config config
     */
    public ChatGenerator(Config config) {
        this.config = config;
    }

    /**
     * Generate code by chat messages
     * @param chatMessages chat messages
     * @return generated code
     */
    @Override
    public String generate(List<ChatMessage> chatMessages) {
        return extractCodeByResponse(chat(config, chatMessages));
    }

    /**
     * Generate code using config and chat messages by asking GPT
     * @param config config
     * @param chatMessages chat messages
     * @return generated code
     */
    public static ChatResponse chat(Config config, List<ChatMessage> chatMessages) {
        ChatResponse response = new AskGPT(config).askChatGPT(chatMessages);
        if (response == null) {
            throw new RuntimeException("Response is null, failed to get response.");
        }
        return response;
    }

    /**
     * Extract code by response
     * @param response response
     * @return extracted code
     */
    public static String extractCodeByResponse(ChatResponse response) {
        return new CodeExtractor(getContentByResponse(response)).getExtractedCode();
    }

    /**
     * Get content by response and parse it
     * @param response response
     * @return content
     */
    public static String getContentByResponse(ChatResponse response) {
        return AbstractRunner.parseResponse(response);
    }

    /**
     * Extract code by content
     * @param content content
     * @return extracted code
     */
    public static String extractCodeByContent(String content) {
        return new CodeExtractor(content).getExtractedCode();
    }
}
