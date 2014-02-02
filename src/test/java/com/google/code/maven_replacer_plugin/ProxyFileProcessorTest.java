package com.google.code.maven_replacer_plugin;

import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.logging.Log;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class ProxyFileProcessorTest {

    private String proxyFile;
    private String policyLoc;
    private String outputLoc;
    private File policy_test_out;

    @Mock
    private Log log;

    @Before
    public void setUp() {
        final URL proxyFileURl = getClass().getResource("/proxy/common_flow.xmlfrag");
        proxyFile = proxyFileURl.getFile();
        policyLoc = new File(proxyFile).getParent();
        policy_test_out = new File(policyLoc, "policy_test_out");
        outputLoc = policy_test_out.getPath();
        policy_test_out.mkdirs();
    }

    @After
    public void cleanup() throws IOException {
        FileUtils.deleteDirectory(policy_test_out);
    }

    @Test
    public void shouldCopyAllPoliciesReferencedByProxyFile() throws IOException {
        final ProxyFileProcessor proxyFileProcessor = new ProxyFileProcessor(log);
        proxyFileProcessor.process(proxyFile, policyLoc, outputLoc, policyLoc,outputLoc);
        assertThat(new File(policy_test_out, "assign_set_variables.xml").exists(), is(true));
        assertThat(new File(policy_test_out, "fault_invalid_secret.xml").exists(), is(true));
        assertThat(new File(policy_test_out, "js_add_trusted_headers.xml").exists(), is(true));
        assertThat(new File(policy_test_out, "verify_apikey_clientid.xml").exists(), is(false));

        assertThat(new File(policy_test_out, "js_add_trusted_headers.js").exists(), is(true));
        assertThat(new File(policy_test_out, "crypto_js/x64-core-min.js").exists(), is(true));
        assertThat(new File(policy_test_out, "crypto_js/core-min.js").exists(), is(true));
        assertThat(new File(policy_test_out, "crypto_js/sha512-min.js").exists(), is(true));
        assertThat(new File(policy_test_out, "crypto_js/enc-base64-min.js").exists(), is(true));


    }
}
