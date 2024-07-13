package zju.cst.aces.api.impl;

import org.apache.maven.project.DefaultProjectBuildingRequest;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuildingRequest;
import org.apache.maven.shared.dependency.graph.DependencyGraphBuilder;
import org.apache.maven.shared.dependency.graph.DependencyNode;
import zju.cst.aces.api.Project;
import zju.cst.aces.parser.ProjectParser;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * ProjectImpl class for Project.
 * It contains methods to get project information.
 * It uses MavenProject and DependencyGraphBuilder to get project information.
 * The project information includes parent project, basedir, packaging, group id, artifact id, version,
 * compile source roots, artifact path, build path, and class paths.
 * @see  org.apache.maven.project.MavenProject
 * @see org.apache.maven.shared.dependency.graph.DependencyGraphBuilder
 */
public class ProjectImpl implements Project {

    MavenProject project;
    List<String> classPaths;

    public ProjectImpl(MavenProject project) {
        this.project = project;
    }

    public ProjectImpl(MavenProject project, List<String> classPaths) {
        this.project = project;
        this.classPaths = classPaths;
    }

    /**
     * Get the parent project
     * @return Project
     */
    @Override
    public Project getParent() {
        if (project.getParent() == null) {
            return null;
        }
        return new ProjectImpl(project.getParent());
    }

    /**
     * Get the basedir
     * @return String
     */
    @Override
    public File getBasedir() {
        return project.getBasedir();
    }

    /**
     * Get the project packages
     * @return String
     */
    @Override
    public String getPackaging() {
        return project.getPackaging();
    }

    /**
     * Get the project group id
     * @return String
     */
    @Override
    public String getGroupId() {
        return project.getGroupId();
    }

    /**
     * Get the project artifact id
     * @return String
     */
    @Override
    public String getArtifactId() {
        return project.getArtifactId();
    }

    /**
     * Get the project version
     * @return String
     */
    @Override
    public List<String> getCompileSourceRoots() {
        return project.getCompileSourceRoots();
    }

    /**
     * Get the artifact path
     * @return String
     */
    @Override
    public Path getArtifactPath() {
        return Paths.get(project.getBuild().getDirectory()).resolve(project.getBuild().getFinalName() + ".jar");
    }

    /**
     * Get the build path
     * @return String
     */
    @Override
    public Path getBuildPath() {
        return Paths.get(project.getBuild().getOutputDirectory());
    }

    /**
     * Get the class paths
     * @return String
     */
    @Override
    public List<String> getClassPaths() {
        return this.classPaths;
    }
}
