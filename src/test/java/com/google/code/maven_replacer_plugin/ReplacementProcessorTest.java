package com.google.code.maven_replacer_plugin;


import com.google.code.maven_replacer_plugin.file.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;

import static java.util.Arrays.asList;
import static org.mockito.Matchers.endsWith;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ReplacementProcessorTest {

    private static final String FILE = "file";
    private static final String OUTPUT_FILE = "outputFile";
    private static final String NEW_CONTENT = "new content";
    private static final int REGEX_FLAGS = 0;
    private static final boolean USE_REGEX = true;
    private static final boolean NO_REGEX = false;
    private static final String TOKEN = "token";
    private static final String CONTENT = "content";
    private static final String VALUE = "value";
    private static final String ENCODING = "encoding";

    @Mock
    private FileUtils fileUtils;
    @Mock
    private Replacer replacer;
    @Mock
    private Replacement replacement;
    @Mock
    private ReplacerFactory replacerFactory;

    @Mock
    private ProxyFileProcessor proxyFileProcessor;

    private ReplacementProcessor processor;

    @Before
    public void setUp() throws Exception {
        when(fileUtils.readFile(FILE, ENCODING)).thenReturn(CONTENT);
        when(replacement.getToken()).thenReturn(TOKEN);
        when(replacement.getValue()).thenReturn(VALUE);
        when(replacerFactory.create(replacement)).thenReturn(replacer);

        processor = new ReplacementProcessor(fileUtils, replacerFactory, proxyFileProcessor);
    }

    @Test
    public void shouldWriteReplacedRegexTextToFile() throws Exception {
        when(replacer.replace(CONTENT, replacement, true, REGEX_FLAGS)).thenReturn(NEW_CONTENT);

        processor.replace(asList(replacement), USE_REGEX, FILE, OUTPUT_FILE, REGEX_FLAGS, ENCODING, null, null, null, null);
        verify(fileUtils).writeToFile(OUTPUT_FILE, NEW_CONTENT, ENCODING);
    }

    @Test
    public void shouldWriteReplacedNonRegexTextToFile() throws Exception {
        when(replacer.replace(CONTENT, replacement, false, REGEX_FLAGS)).thenReturn(NEW_CONTENT);

        processor.replace(asList(replacement), NO_REGEX, FILE, OUTPUT_FILE, REGEX_FLAGS, ENCODING, null, null, null, null);
        verify(fileUtils).writeToFile(OUTPUT_FILE, NEW_CONTENT, ENCODING);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfNoToken() throws Exception {
        when(replacement.getToken()).thenReturn(null);

        processor.replace(asList(replacement), USE_REGEX, FILE, OUTPUT_FILE, REGEX_FLAGS, ENCODING, null, null, null, null);
        verifyZeroInteractions(fileUtils);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfEmptyToken() throws Exception {
        when(replacement.getToken()).thenReturn("");

        processor.replace(asList(replacement), USE_REGEX, FILE, OUTPUT_FILE, REGEX_FLAGS, ENCODING, null, null, null, null);
        verifyZeroInteractions(fileUtils);
    }

    @Test
    public void shouldProcessProxyFilesIfReplacementIsProxy() throws IOException {
        final String valueFile = "./apiproxy/proxies/spike_arrest_and_quota.xmlfrag";
        final String inFile = "./apiproxy/proxies/default.xml";
        final String outFile = "./target/apiproxy/proxies/default.xml";

        when(replacement.isProxy()).thenReturn(true);
        when(replacement.getValueFile()).thenReturn(valueFile);
        when(fileUtils.readFile(inFile, ENCODING)).thenReturn(CONTENT);

        when(replacer.replace(CONTENT, replacement, true, REGEX_FLAGS)).thenReturn(NEW_CONTENT);

        processor.replace(asList(replacement),USE_REGEX,inFile,outFile,REGEX_FLAGS,ENCODING,"../policies","../policies","../ressources/jsc","../ressources/jsc");


        verify(fileUtils).writeToFile(outFile, NEW_CONTENT, ENCODING);
        verify(replacement).isProxy();
        verify(replacement).getValueFile();
        verify(proxyFileProcessor).process(eq(valueFile),endsWith("/apiproxy/policies"), endsWith("/target/apiproxy/policies"),
                endsWith("/apiproxy/ressources/jsc"),endsWith("/target/apiproxy/ressources/jsc"));
    }
}
