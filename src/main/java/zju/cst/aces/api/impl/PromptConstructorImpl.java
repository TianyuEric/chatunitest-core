package zju.cst.aces.api.impl;

import lombok.Data;
import zju.cst.aces.api.PromptConstructor;
import zju.cst.aces.api.config.Config;
import zju.cst.aces.dto.ChatMessage;
import zju.cst.aces.dto.ClassInfo;
import zju.cst.aces.dto.MethodInfo;
import zju.cst.aces.dto.PromptInfo;
import zju.cst.aces.prompt.PromptGenerator;
import zju.cst.aces.runner.AbstractRunner;
import zju.cst.aces.util.TokenCounter;

import java.io.IOException;
import java.util.List;

/**
 * PromptConstructorImpl implements PromptConstructor.
 * The main function of this class is to generate prompt messages.
 * It contains a method to generate prompt messages.
 * After generating prompt messages, it will count token in chat messages.
 * If token count exceed max tokens, it has a method to check if token count exceed max tokens.
 * @see zju.cst.aces.api.PromptConstructor
 */
@Data
public class PromptConstructorImpl implements PromptConstructor {

    Config config;
    PromptInfo promptInfo;
    List<ChatMessage> chatMessages;
    int tokenCount = 0;
    String testName;
    String fullTestName;
    static final String separator = "_";

    public PromptConstructorImpl(Config config) {
        this.config = config;
    }

    /**
     * Generate prompt messages
     * @return prompt messages
     */
    @Override
    public List<ChatMessage> generate() {
        try {
            if (promptInfo == null) {
                throw new RuntimeException("PromptInfo is null, you need to initialize it first.");
            }
            this.chatMessages = new PromptGenerator(config).generateMessages(promptInfo);
            countToken();
            return this.chatMessages;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Set prompt info with dependency
     * @param classInfo class info
     * @param methodInfo method info
     * @throws IOException IOException
     */
    public void setPromptInfoWithDep(ClassInfo classInfo, MethodInfo methodInfo) throws IOException {
        this.promptInfo = AbstractRunner.generatePromptInfoWithDep(config, classInfo, methodInfo);
    }

    /**
     * Set prompt info without dependency
     * @param classInfo class info
     * @param methodInfo method info
     * @throws IOException IOException
     */
    public void setPromptInfoWithoutDep(ClassInfo classInfo, MethodInfo methodInfo) throws IOException {
        this.promptInfo = AbstractRunner.generatePromptInfoWithoutDep(config, classInfo, methodInfo);
    }

    /**
     * Set prompt info with dependency
     * @param fullTestName full test name
     */
    public void setFullTestName(String fullTestName) {
        this.fullTestName = fullTestName;
        this.testName = fullTestName.substring(fullTestName.lastIndexOf(".") + 1);
        this.promptInfo.setFullTestName(this.fullTestName);
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    /**
     * Count token in chat messages
     */
    public void countToken() {
        for (ChatMessage p : chatMessages) {
            this.tokenCount += TokenCounter.countToken(p.getContent());
        }
    }

    /**
     * Check if token count exceed max tokens
     * @return true if exceed max tokens, false otherwise
     */
    public boolean isExceedMaxTokens() {
        if (this.tokenCount > config.maxPromptTokens) {
            return true;
        } else {
            return false;
        }
    }
}
