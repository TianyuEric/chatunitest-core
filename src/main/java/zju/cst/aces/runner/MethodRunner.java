package zju.cst.aces.runner;

import zju.cst.aces.api.Phase;
import zju.cst.aces.api.config.Config;
import zju.cst.aces.api.impl.PromptConstructorImpl;
import zju.cst.aces.dto.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * MethodRunner is a class to generate code from methods.
 */
public class MethodRunner extends ClassRunner {

    public MethodInfo methodInfo;

    public MethodRunner(Config config, String fullClassName, MethodInfo methodInfo) throws IOException {
        super(config, fullClassName);
        this.methodInfo = methodInfo;
    }

    /**
     * Start the test program.
     * According to the configuration, decide whether to use single-threaded or multi-threaded execution of test rounds.
     * If the configuration is multi-threaded and not set to stop after success, use ExecutorService to manage a fixed number of thread pools.
     * If the configuration is single-threaded or set to stop after success, execute the test rounds one by one in single-threaded mode.
     * @throws IOException if an I/O error occurs during startup.
     */
    @Override
    public void start() throws IOException {
        if (!config.isStopWhenSuccess() && config.isEnableMultithreading()) {
            ExecutorService executor = Executors.newFixedThreadPool(config.getTestNumber());
            List<Future<String>> futures = new ArrayList<>();
            for (int num = 0; num < config.getTestNumber(); num++) {
                int finalNum = num;
                Callable<String> callable = () -> {
                    startRounds(finalNum);
                    return "";
                };
                Future<String> future = executor.submit(callable);
                futures.add(future);
            }
            Runtime.getRuntime().addShutdownHook(new Thread(executor::shutdownNow));

            for (Future<String> future : futures) {
                try {
                    String result = future.get();
                    System.out.println(result);
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }
            }

            executor.shutdown();
        } else {
            for (int num = 0; num < config.getTestNumber(); num++) {
                boolean result = startRounds(num);
                if (result && config.isStopWhenSuccess()) {
                    break;
                }
            }
        }
    }

    /**
     * Starts the test rounds.
     * Executes a series of phases including prompt generation, test generation, validation, and repair (if necessary) to complete the test rounds.
     * If the validation passes after the repair, the test round is considered successful; otherwise, it is considered failed after attempting the maximum number of rounds.
     * @param num The number of test rounds to execute.
     * @return true if the test rounds are successful, otherwise false.
     */
    public boolean startRounds(final int num) {

        Phase phase = new Phase(config);

        // Prompt Construction Phase
        PromptConstructorImpl pc = phase.new PromptGeneration(classInfo, methodInfo).execute(num);
        PromptInfo promptInfo = pc.getPromptInfo();
        promptInfo.setRound(0);

        // Test Generation Phase
        phase.new TestGeneration().execute(pc);

        // Validation
        if (phase.new Validation().execute(pc)) {
            exportRecord(pc.getPromptInfo(), classInfo, num);
            return true;
        }

        // Validation and Repair Phase
        for (int rounds = 1; rounds < config.getMaxRounds(); rounds++) {

            promptInfo.setRound(rounds);

            // Repair
            phase.new Repair().execute(pc);

            // Validation
            if (phase.new Validation().execute(pc)) {
                exportRecord(pc.getPromptInfo(), classInfo, num);
                return true;
            }

        }

        exportRecord(pc.getPromptInfo(), classInfo, num);
        return false;
    }
}