package com.google.code.maven_replacer_plugin;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.logging.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProxyFileProcessor {
    private File proxyFile;
    private File outputPolicyLoc;
    private File policyLoc;
    private File resourceLoc;
    private File outputResourceLoc;
    private Log log;
    private Set<String> copyCache = new HashSet<String>();

    private void initPaths(String proxyFile, String outputPolicyLoc, String policyLoc, String resourceLoc,
                           String outputResourceLoc, Log log) {
        this.proxyFile = new File(proxyFile);
        this.outputPolicyLoc = new File(outputPolicyLoc);
        this.policyLoc = new File(policyLoc);
        this.resourceLoc = new File(resourceLoc);
        this.outputResourceLoc = new File(outputResourceLoc);
        this.log = log;
    }

    public void process(String proxyFile, String policyLoc, String outputPolicyLoc, String resourceLoc,
                        String outputResourceLoc, Log log) throws IOException {
        int initalSize = copyCache.size();
        initPaths(proxyFile, outputPolicyLoc, policyLoc, resourceLoc, outputResourceLoc, log);
        if (!validatePaths()) {
            return;
        }
        final List<String> polices = getPolicies(FileUtils.readLines(this.proxyFile));
        copyFiles(polices);
        if (copyCache.size() - initalSize > 0) {
            log.info(String.format("Copied %s files referenced by proxy %s.", copyCache.size() - initalSize, proxyFile));
        }
    }

    private void copyFiles(List<String> policies) throws IOException {
        for (String policy : policies) {
            final File policyFile = new File(policyLoc, policy);
            copyPolicyFile(policyFile);
            final ArrayList<String> resourceFiles = processPolicyJSResources(policyFile);
            copyFiles(resourceFiles);
        }
    }

    private void copyFiles(ArrayList<String> resourceFiles) throws IOException {
        for (String resourceFile : resourceFiles) {
            copyResourceFile(resourceFile);
        }
    }

    private void copyResourceFile(String resourceFile) throws IOException {
        final File inFile = new File(resourceLoc, resourceFile);
        if (!inFile.exists()) {
            log.warn(String.format("File %s does not exists.", inFile.getAbsolutePath()));
            return;
        }
        final File outFile = new File(outputResourceLoc, resourceFile);
        if (copyCache.contains(outFile.getCanonicalPath())) {
            return;
        }
        log.debug(String.format("Copying file %s to %s", inFile.getAbsolutePath(), outFile.getAbsolutePath()));
        FileUtils.copyFile(inFile, outFile);
        copyCache.add(outFile.getCanonicalPath());
    }

    private void copyPolicyFile(File srcFile) throws IOException {
        if (!srcFile.exists()) {
            log.warn(String.format("File %s does not exists.", srcFile.getAbsolutePath()));
            return;
        }
        final File outFile = new File(outputPolicyLoc, srcFile.getName());
        if (copyCache.contains(outFile.getCanonicalPath())) {
            return;
        }
        log.debug(String.format("Copying file %s to directory %s", srcFile.getAbsolutePath(), outputPolicyLoc.getAbsolutePath()));
        FileUtils.copyFileToDirectory(srcFile, outputPolicyLoc);
        copyCache.add(outFile.getCanonicalPath());
    }


    private ArrayList<String> processPolicyJSResources(File srcFile) throws IOException {
        if (!srcFile.exists()) {
            return new ArrayList<String>();
        }
        final List<String> lines = FileUtils.readLines(srcFile);
        final ArrayList<String> resourceFiles = new ArrayList<String>();
        for (String line : lines) {
            processJSURLs(resourceFiles, line);
        }
        return resourceFiles;
    }

    private void processJSURLs(ArrayList<String> resourceFiles, String line) {
        String res = getUrl(line, "ResourceURL");
        addResToList(resourceFiles, res);
        res = getUrl(line, "IncludeURL");
        addResToList(resourceFiles, res);
    }

    private void addResToList(ArrayList<String> resourceFiles, String res) {
        if (res != null) {
            resourceFiles.add(res);
        }
    }

    private String getUrl(String line, String urlTag) {
        Pattern pattern = Pattern.compile(String.format("<%1$s>jsc://(.*)</%1$s>", urlTag));
        Matcher matcher = pattern.matcher(line.trim());
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private List<String> getPolicies(List<String> lines) {
        final ArrayList<String> policies = new ArrayList<String>();
        for (String line : lines) {
            Pattern pattern = Pattern.compile("<Name>(.*)</Name>");
            Matcher matcher = pattern.matcher(line.trim());
            if (matcher.find()) {
                policies.add(matcher.group(1) + ".xml");
            }
        }
        return policies;
    }

    private boolean validatePaths() {
        if (!proxyFile.exists()) {
            return false;
        }
        if (!outputPolicyLoc.exists()) {
            return false;
        }
        if (!policyLoc.exists()) {
            return false;
        }
        return true;
    }
}
