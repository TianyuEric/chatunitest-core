package zju.cst.aces.api;

import zju.cst.aces.dto.ChatMessage;

import java.util.List;

/**
 * PromptConstructor is an interface to generate prompt.
 */
public interface PromptConstructor {

    List<ChatMessage> generate();

}
