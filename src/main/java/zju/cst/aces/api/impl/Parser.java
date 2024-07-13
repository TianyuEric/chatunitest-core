package zju.cst.aces.api.impl;

import lombok.Data;
import zju.cst.aces.api.*;
import zju.cst.aces.api.config.Config;
import zju.cst.aces.parser.ProjectParser;

import zju.cst.aces.api.PreProcess;

import java.nio.file.Path;

/**
 * Parser is a class to parse class info.
 * It contains a method to parse class info.
 * It is used in the API to parse class info from the project.
 * It uses ProjectParser to parse class info.
 * @see zju.cst.aces.parser.ProjectParser
 */
@Data
public class Parser implements PreProcess {

    ProjectParser parser;

    Project project;
    Path parseOutput;
    Logger log;

    public Parser(ProjectParser parser, Project project, Path parseOutput, Logger log) {
        this.parser = parser;
        this.project = project;
        this.parseOutput = parseOutput;
        this.log = log;
    }

    @Override
    public void process() {
        this.parse();
    }

    /**
     * Parse class info
     */
    public void parse() {
        try {
            Task.checkTargetFolder(project);
        } catch (RuntimeException e) {
            getLog().error(e.toString());
            return;
        }
        if (project.getPackaging().equals("pom")) {
            log.info("\n==========================\n[ChatUniTest] Skip pom-packaging ...");
            return;
        }
        if (! parseOutput.toFile().exists()) {
            log.info("\n==========================\n[ChatUniTest] Parsing class info ...");
            parser.parse();
            log.info("\n==========================\n[ChatUniTest] Parse finished");
        } else {
            log.info("\n==========================\n[ChatUniTest] Parse output already exists, skip parsing!");
        }
    }
}
