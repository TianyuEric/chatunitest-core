package zju.cst.aces.api.impl.obfuscator.frame;

import lombok.Data;

import java.util.List;

/**
 * Symbol is a class to store the symbol information in the code
 * It contains the symbol name, owner, type and line number
 * It also provides a method to check if the symbol is in the group
 * The group is defined by the group ids
 * The group ids are the owner and type of the symbol
 */
@Data
public class Symbol {
    private String name;
    private String owner;
    private String type;
    private Integer lineNum;

    /**
     * Constructor
     * @param name  symbol name
     * @param owner symbol owner
     * @param type symbol type
     * @param line symbol line number
     */
    public Symbol(String name, String owner, String type, Integer line) {
        this.name = name;
        this.owner = owner;
        this.type = type;
        this.lineNum = line;
    }

    /**
     * Check if the symbol is in the group
     * @param groupIds group ids
     * @return true if the symbol is in the group
     */
    public boolean isInGroup(List<String> groupIds) {
        for (String gid : groupIds) {
            if (owner.contains(gid) || type.contains(gid)) {
                return true;
            }
        }
        return false;
    }
}
