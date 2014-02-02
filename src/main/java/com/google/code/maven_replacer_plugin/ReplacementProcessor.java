package com.google.code.maven_replacer_plugin;

import com.google.code.maven_replacer_plugin.file.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.apache.commons.lang.StringUtils.isEmpty;

public class ReplacementProcessor {
    private final FileUtils fileUtils;
    private final ReplacerFactory replacerFactory;
    private final ProxyFileProcessor proxyFileProcessor;

    public ReplacementProcessor(FileUtils fileUtils, ReplacerFactory replacerFactory,ProxyFileProcessor proxyFileProcessor) {
        this.fileUtils = fileUtils;
        this.replacerFactory = replacerFactory;
        this.proxyFileProcessor = proxyFileProcessor;
    }

    public void replace(List<Replacement> replacements, boolean regex, String file,
                        String outputFile, int regexFlags, String encoding, String srcPolicyDir,
                        String outputPolicyDir, String srcResourceDir, String outputResourceDir) throws IOException {
        String content = fileUtils.readFile(file, encoding);
        for (Replacement replacement : replacements) {
            content = replaceContent(regex, regexFlags, content, replacement);
            processProxyFiles(outputFile,srcPolicyDir,outputPolicyDir,srcResourceDir,outputResourceDir,replacement);
        }

        fileUtils.writeToFile(outputFile, content, encoding);
    }

    private void processProxyFiles(String outputFile, String srcPolicyDir, String outputPolicyDir,
                                   String srcResourceDir, String outputResourceDir, Replacement replacement) throws IOException {

        if(!replacement.isProxy()){
            return;
        }
        final String valueFile = replacement.getValueFile();
        if(valueFile==null){
            return;
        }
        final File proxyFile = new File(valueFile);
        final File outFile = new File(outputFile);
        proxyFileProcessor.process(valueFile,buildLocRelativeTo(proxyFile,srcPolicyDir),
                buildLocRelativeTo(outFile,outputPolicyDir),buildLocRelativeTo(proxyFile,srcResourceDir),
                buildLocRelativeTo(outFile,outputResourceDir));
    }

    private String buildLocRelativeTo(File proxyFile, String srcDir) throws IOException {
        final File src = new File(srcDir);
        return src.isAbsolute()?src.getCanonicalPath():new File(proxyFile.getParent(),srcDir).getCanonicalPath();
    }

    private String replaceContent(boolean regex, int regexFlags, String content, Replacement replacement) {
        if (isEmpty(replacement.getToken())) {
            throw new IllegalArgumentException("Token or token file required");
        }

        Replacer replacer = replacerFactory.create(replacement);
        return replacer.replace(content, replacement, regex, regexFlags);
    }
}
