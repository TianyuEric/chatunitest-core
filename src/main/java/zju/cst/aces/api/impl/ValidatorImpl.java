package zju.cst.aces.api.impl;

import com.github.javaparser.ParseProblemException;
import com.github.javaparser.StaticJavaParser;
import lombok.Data;
import org.junit.platform.launcher.listeners.TestExecutionSummary;
import zju.cst.aces.api.Validator;
import zju.cst.aces.api.config.Config;
import zju.cst.aces.dto.PromptInfo;
import zju.cst.aces.util.TestCompiler;

import java.nio.file.Path;
import java.util.List;

import zju.cst.aces.api.Validator;

/**
 * ValidatorImpl implements Validator.
 * The main function of this class is to validate code.
 * It contains three types of validation methods: syntactic validate, semantic validate and runtime validate.
 */
@Data
public class ValidatorImpl implements Validator {

    TestCompiler compiler;

    /**
     * Constructor
     * @param testOutputPath test output path
     * @param compileOutputPath compile output path
     * @param targetPath target path
     * @param classpathElements classpath elements
     */
    public ValidatorImpl(Path testOutputPath, Path compileOutputPath, Path targetPath, List<String> classpathElements) {
        this.compiler = new TestCompiler(testOutputPath, compileOutputPath, targetPath, classpathElements);
    }

    /**
     * Syntactic validate
     * @param code code
     * @return true if code is syntactically valid
     */
    @Override
    public boolean syntacticValidate(String code) {
        try {
            StaticJavaParser.parse(code);
            return true;
        } catch (ParseProblemException e) {
            return false;
        }
    }

    /**
     * Semantic validate
     * @param code code
     * @param className class name
     * @param outputPath output path
     * @param promptInfo prompt info
     * @return true if code is semantically valid
     */
    @Override
    public boolean semanticValidate(String code, String className, Path outputPath, PromptInfo promptInfo) {
        compiler.setCode(code);
        return compiler.compileTest(className, outputPath, promptInfo);
    }

    /**
     * Runtime validate
     * @param fullTestName full test name
     * @return true if test passed
     */
    @Override
    public boolean runtimeValidate(String fullTestName) {
        return compiler.executeTest(fullTestName).getTestsFailedCount() == 0;
    }

    /**
     * Compile code
     * @param className class name
     * @param outputPath output path
     * @param promptInfo prompt info
     * @return true if code is compiled successfully
     */
    @Override
    public boolean compile(String className, Path outputPath, PromptInfo promptInfo) {
        return compiler.compileTest(className, outputPath, promptInfo);
    }

    /**
     * Execute test
     * @param fullTestName full test name
     * @return test execution summary
     */
    @Override
    public TestExecutionSummary execute(String fullTestName) {
        return compiler.executeTest(fullTestName);
    }
}
