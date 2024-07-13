package zju.cst.aces.api.impl;

import lombok.Data;
import zju.cst.aces.api.Repair;
import zju.cst.aces.api.config.Config;
import zju.cst.aces.dto.ChatResponse;
import zju.cst.aces.dto.PromptInfo;

import static zju.cst.aces.runner.AbstractRunner.*;
import static zju.cst.aces.api.impl.ChatGenerator.*;

/**
 * RepairImpl implements Repair interface
 * The main function of this class is to repair code.
 * It has two types of repair methods: rule based repair and LLM based repair.
 * The rule based repair method is used to repair code based on rule.
 * The LLM based repair method is used to repair code based on LLM.
 * @see zju.cst.aces.api.Repair
 */
@Data
public class RepairImpl implements Repair {

    Config config;

    PromptConstructorImpl promptConstructorImpl;

    boolean success = false;

    /**
     * Constructor
     * @param config config
     * @param promptConstructorImpl prompt constructor
     */
    public RepairImpl(Config config, PromptConstructorImpl promptConstructorImpl) {
        this.config = config;
        this.promptConstructorImpl = promptConstructorImpl;
    }

    /**
     * Repair code based on rule and return repaired code
     * @param code code
     * @return repaired code
     */
    @Override
    public String ruleBasedRepair(String code) {
        code = changeTestName(code, promptConstructorImpl.getTestName());
        code = repairPackage(code, promptConstructorImpl.getPromptInfo().getClassInfo().getPackageName());
        code = repairImports(code, promptConstructorImpl.getPromptInfo().getClassInfo().getImports());
        return code;
    }

    /**
     * Repair code based on LLM and return repaired code
     * @param code code
     * @param rounds rounds
     * @return repaired code
     */
    @Override
    public String LLMBasedRepair(String code, int rounds) {
        PromptInfo promptInfo = promptConstructorImpl.getPromptInfo();
        promptInfo.setUnitTest(code);
        String fullClassName = promptInfo.getClassInfo().getPackageName() + "." + promptInfo.getClassInfo().getClassName();
        if (runTest(config, promptConstructorImpl.getFullTestName(), promptInfo, rounds)) {
            this.success = true;
            return code;
        }

        promptConstructorImpl.generate();
        if (promptConstructorImpl.isExceedMaxTokens()) {
            config.getLogger().error("Exceed max prompt tokens: " + promptInfo.methodInfo.methodName + " Skipped.");
            return code;
        }
        ChatResponse response = chat(config, promptConstructorImpl.getChatMessages());
        String newcode = extractCodeByResponse(response);
        if (newcode.isEmpty()) {
            config.getLogger().warn("Test for method < " + promptInfo.methodInfo.methodName + " > extract code failed");
            return code;
        } else {
            return newcode;
        }
    }

    /**
     * Repair code based on LLM and return repaired code
     * @param code code
     * @return repaired code
     */
    @Override
    public String LLMBasedRepair(String code) {
        PromptInfo promptInfo = promptConstructorImpl.getPromptInfo();
        promptInfo.setUnitTest(code);
        String fullClassName = promptInfo.getClassInfo().getPackageName() + "." + promptInfo.getClassInfo().getClassName();
        if (runTest(config, promptConstructorImpl.getFullTestName(), promptInfo, 0)) {
            config.getLogger().info("Test for method < " + promptInfo.methodInfo.methodName + " > doesn't need repair");
            return code;
        }

        promptConstructorImpl.generate();

        if (promptConstructorImpl.isExceedMaxTokens()) {
            config.getLogger().error("Exceed max prompt tokens: " + promptInfo.methodInfo.methodName + " Skipped.");
            return code;
        }
        ChatResponse response = chat(config, promptConstructorImpl.getChatMessages());
        String newcode = extractCodeByResponse(response);
        if (newcode.isEmpty()) {
            config.getLogger().warn("Test for method < " + promptInfo.methodInfo.methodName + " > extract code failed");
            return code;
        } else {
            return newcode;
        }
    }
}
