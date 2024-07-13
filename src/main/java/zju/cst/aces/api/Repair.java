package zju.cst.aces.api;

import java.io.IOException;

/**
 * Repair is an interface to repair code.
 */
public interface Repair {

    String ruleBasedRepair(String code);
    String LLMBasedRepair(String code);
    String LLMBasedRepair(String code, int rounds);

}
