package com.google.code.maven_replacer_plugin;

import com.google.code.maven_replacer_plugin.file.FileUtils;
import org.apache.commons.lang.StringEscapeUtils;

import java.io.IOException;


public class Replacement {
    private final FileUtils fileUtils;

    private DelimiterBuilder delimiter;
    private boolean unescape;
    private String token;
    private String value;
    private String encoding;
    private String xpath;
    private boolean isProxy;
    private String valueFile;


    public Replacement() {
        this.fileUtils = new FileUtils();
        this.unescape = false;
        this.isProxy = false;
    }

    public Replacement(FileUtils fileUtils, String token, String value, boolean unescape,
                       String xpath, String encoding, boolean isProxy) {
        this.fileUtils = fileUtils;
        setUnescape(unescape);
        setToken(token);
        setValue(value);
        setXpath(xpath);
        setEncoding(encoding);
        setIsProxy(isProxy);
    }

    public void setTokenFile(String tokenFile) throws IOException {
        if (tokenFile != null) {
            setToken(fileUtils.readFile(tokenFile, encoding));
        }
    }

    public void setValueFile(String valueFile) throws IOException {
        this.valueFile = valueFile;
        if (valueFile != null) {
            setValue(fileUtils.readFile(valueFile, encoding));
        }
    }

    public String getToken() {
        String newToken = unescape ? unescape(token) : token;
        if (delimiter != null) {
            return delimiter.apply(newToken);
        }
        return newToken;
    }

    public String getValue() {
        return unescape ? unescape(value) : value;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setValue(String value) {
        this.value = value;
    }

    private String unescape(String text) {
        return StringEscapeUtils.unescapeJava(text);
    }

    public void setUnescape(boolean unescape) {
        this.unescape = unescape;
    }

    public boolean isUnescape() {
        return unescape;
    }

    public static Replacement from(Replacement replacement) {
        return new Replacement(replacement.fileUtils, replacement.token, replacement.value,
                replacement.unescape, replacement.xpath, replacement.encoding, replacement.isProxy);
    }

    public Replacement withDelimiter(DelimiterBuilder delimiter) {
        this.delimiter = delimiter;
        return this;
    }

    public void setXpath(String xpath) {
        this.xpath = xpath;
    }

    public String getXpath() {
        return xpath;
    }

    public void setEncoding(String encoding) {
        this.encoding = encoding;
    }

    public String getEncoding() {
        return encoding;
    }

    public void setIsProxy(boolean isProxy) {
        this.isProxy = isProxy;
    }

    public boolean isProxy() {
        return isProxy;
    }

    public void setProxy(boolean isProxy) {
        this.isProxy = isProxy;
    }

    public String getValueFile() {
        return valueFile;
    }
}
