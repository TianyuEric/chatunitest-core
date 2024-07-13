package zju.cst.aces.api;

import zju.cst.aces.dto.ChatMessage;

import java.util.List;

/**
 * Generator Interface
 * It contains a method to generate code by chat messages
 */
public interface Generator {

    String generate(List<ChatMessage> chatMessages);

}
