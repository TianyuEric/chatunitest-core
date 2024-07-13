package zju.cst.aces.api.impl.obfuscator.util;

import org.objectweb.asm.Handle;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import zju.cst.aces.api.impl.obfuscator.frame.Symbol;
import zju.cst.aces.api.impl.obfuscator.frame.SymbolFrame;

import java.io.IOException;
import java.util.*;
import java.util.jar.JarFile;

/**
 * SymbolAnalyzer is a class to analyze the symbol information in the code
 * It contains the method to analyze class node and method node
 * It is used in obfuscator to analyze class node and method node
 * @see zju.cst.aces.api.impl.obfuscator.Obfuscator
 */
public class SymbolAnalyzer {
    private static final String jarFile = "";

    public static void main(String[] args) throws IOException {
        Set<ClassNode> candidateClasses = new HashSet<>();
        ASMParser asmParser = new ASMParser(null);
        candidateClasses.addAll(asmParser.loadClasses(new JarFile(jarFile)));
        SymbolAnalyzer analyzer = new SymbolAnalyzer();
        for (ClassNode classNode : candidateClasses) {
            analyzer.analyze(classNode);
        }
    }

    /**
     * Analyze class node and return symbol frame
     * @param classNode class node
     * @return symbol frame
     */
    public SymbolFrame analyze(ClassNode classNode) {
        SymbolFrame frame = new SymbolFrame();
        String className = classNode.name;
        frame.setClassName(className);
        frame.setSuperName(classNode.superName);
        frame.setInterfaces(classNode.interfaces);
        for (FieldNode fieldNode : classNode.fields) {
            String type = fieldNode.signature != null ? fieldNode.signature : fieldNode.desc;
            frame.addFieldDef(new Symbol(fieldNode.name, className, type, null));
        }
        for (MethodNode methodNode : classNode.methods) {
            frame.merge(analyzeMethod(methodNode, className));
        }
        return frame;
    }

    /**
     * Analyze method node and return symbol frame
     * @param methodNode method node
     * @param className class name
     * @return symbol frame
     */
    public SymbolFrame analyzeMethod(MethodNode methodNode, String className) {
        List<LocalVariableNode> localVariables = new ArrayList<>();
        if (methodNode.localVariables != null) {
            localVariables.addAll(methodNode.localVariables);
        }
        SymbolFrame frame = new SymbolFrame();
        frame.addMethodDef(new Symbol(methodNode.name, className, methodNode.signature, null));
        localVariables.forEach(var -> {
            if (methodNode.parameters != null) {
                methodNode.parameters.forEach(param -> {
                    if (param.name.equals(var.name)) {
                        frame.addVarDef(new Symbol(var.name, var.desc, var.desc, null));
                    }
                });
            }
        });
        for (AbstractInsnNode insn : methodNode.instructions) {
            if (insn instanceof FieldInsnNode) {
                FieldInsnNode fi = (FieldInsnNode) insn;
                frame.addFieldUse(new Symbol(fi.name, fi.owner, fi.desc, getLine(insn)));
            }
            if (insn instanceof MethodInsnNode) {
                MethodInsnNode mi = (MethodInsnNode) insn;
                frame.addMethodUse(new Symbol(mi.name, mi.owner, mi.desc, getLine(insn)));
            }
            if (insn instanceof VarInsnNode) {
                VarInsnNode vi = (VarInsnNode) insn;
                if (vi.var >= localVariables.size()) {
                    continue;
                }
                if (vi.getOpcode() == Opcodes.ASTORE) {
                    LocalVariableNode var = localVariables.get(vi.var);
                    frame.addVarDef(new Symbol(var.name, var.desc, var.desc, getLine(insn))); //TODO: var def may be incomplete
                } else if (vi.getOpcode() == Opcodes.ALOAD) {
                    LocalVariableNode var = localVariables.get(vi.var);
                    if (var.name.equals("this")) {
                        continue;
                    }
                    frame.addVarUse(new Symbol(var.name, var.desc, var.desc, getLine(insn)));
                }
            }
            if (insn instanceof InvokeDynamicInsnNode) {
                InvokeDynamicInsnNode di = (InvokeDynamicInsnNode) insn;
                Arrays.stream(di.bsmArgs).filter(arg -> arg instanceof Handle).map(arg -> (Handle) arg).forEach(handle -> {
                    frame.addMethodUse(new Symbol(handle.getName(), handle.getOwner(), handle.getDesc(), getLine(insn)));
                });
            }
            // TODO: Handle other types of instructions
        }
        return frame;
    }

    /**
     * Get line number of instruction
     * @param insn instruction
     * @return line number
     */
    public int getLine(AbstractInsnNode insn) {
        while (insn != null && !(insn instanceof LineNumberNode)) {
            insn = insn.getPrevious();
        }
        return insn == null ? -1 : ((LineNumberNode) insn).line;
    }

}
