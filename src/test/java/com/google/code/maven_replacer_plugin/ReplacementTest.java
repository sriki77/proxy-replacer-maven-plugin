package com.google.code.maven_replacer_plugin;

import com.google.code.maven_replacer_plugin.file.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class ReplacementTest {
	private static final String UNESCAPED = "test\\n123\\t456";
	private static final String ESCAPED = "test\n123\t456";
	private static final String FILE = "some file";
	private static final String TOKEN = "token";
	private static final String VALUE = "value";
	private static final String XPATH = "xpath";
	private static final String ENCODING = "encoding";
	
	@Mock
	private FileUtils fileUtils;
	@Mock
	private DelimiterBuilder delimiter;

	@Test
	public void shouldReturnConstructorParameters() throws Exception {
		Replacement replacement = new Replacement(fileUtils, TOKEN, VALUE, false, null, ENCODING,false);
		
		assertThat(replacement.getToken(), equalTo(TOKEN));
		assertThat(replacement.getValue(), equalTo(VALUE));
		verifyZeroInteractions(fileUtils);
	}
	
	@Test
	public void shouldApplyToTokenDelimeterIfExists() throws Exception {
		when(delimiter.apply(TOKEN)).thenReturn("new token");
		Replacement replacement = new Replacement(fileUtils, TOKEN, VALUE, false, null, ENCODING,false).withDelimiter(delimiter);
		
		assertThat(replacement.getToken(), equalTo("new token"));
		assertThat(replacement.getValue(), equalTo(VALUE));
		verifyZeroInteractions(fileUtils);
	}
	
	@Test
	public void shouldUseEscapedTokensAndValues() {
		Replacement replacement = new Replacement(fileUtils, UNESCAPED, UNESCAPED, true, null, ENCODING,false);
		
		assertThat(replacement.getToken(), equalTo(ESCAPED));
		assertThat(replacement.getValue(), equalTo(ESCAPED));
		verifyZeroInteractions(fileUtils);
	}
	
	@Test
	public void shouldUseEscapedTokensAndValuesFromFiles() throws Exception {
		when(fileUtils.readFile(FILE, ENCODING)).thenReturn(UNESCAPED);

		Replacement replacement = new Replacement(fileUtils, null, null, true, null, ENCODING,false);
		replacement.setTokenFile(FILE);
		replacement.setValueFile(FILE);
		
		assertThat(replacement.getToken(), equalTo(ESCAPED));
		assertThat(replacement.getValue(), equalTo(ESCAPED));
	}

	@Test
	public void shouldUseTokenFromFileUtilsIfGiven() throws Exception {
		when(fileUtils.readFile(FILE, ENCODING)).thenReturn(TOKEN);

		Replacement replacement = new Replacement(fileUtils, null, VALUE, false, null, ENCODING,false);
		replacement.setTokenFile(FILE);
		assertThat(replacement.getToken(), equalTo(TOKEN));
		assertThat(replacement.getValue(), equalTo(VALUE));
	}

	@Test
	public void shouldUseValueFromFileUtilsIfGiven() throws Exception {
		when(fileUtils.readFile(FILE, ENCODING)).thenReturn(VALUE);

		Replacement replacement = new Replacement(fileUtils, TOKEN, null, false, null, ENCODING,false);
		replacement.setValueFile(FILE);
		assertThat(replacement.getToken(), equalTo(TOKEN));
		assertThat(replacement.getValue(), equalTo(VALUE));
	}

    @Test
    public void shouldUseProcessProxyFileFromFileUtilsIfGiven() throws Exception {
        when(fileUtils.readFile(FILE, ENCODING)).thenReturn(VALUE);

        Replacement replacement = new Replacement(fileUtils, TOKEN, null, false, null, ENCODING,true);
        replacement.setValueFile(FILE);
        assertThat(replacement.getToken(), equalTo(TOKEN));
        assertThat(replacement.getValue(), equalTo(VALUE));
    }
	
	@Test
	public void shouldSetAndGetSameValues() {
		Replacement replacement = new Replacement();
		
		replacement.setToken(TOKEN);
		replacement.setValue(VALUE);
		replacement.setXpath(XPATH);
		assertThat(replacement.getToken(), equalTo(TOKEN));
		assertThat(replacement.getValue(), equalTo(VALUE));
		assertThat(replacement.getXpath(), equalTo(XPATH));
	}
	
	@Test
	public void shouldReturnCopyOfReplacementInFrom() {
		Replacement replacement = new Replacement(fileUtils, TOKEN, VALUE, true, XPATH, ENCODING,false);
		Replacement copy = Replacement.from(replacement);
		
		assertThat(copy.getToken(), equalTo(TOKEN));
		assertThat(copy.getValue(), equalTo(VALUE));
		assertThat(copy.isUnescape(), equalTo(true));
		assertThat(copy.getXpath(), equalTo(XPATH));
	}
}
