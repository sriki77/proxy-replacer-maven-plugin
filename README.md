~~Proxy Replacer Maven Plugin~~ 
=======================
No longer supported.
--------------------
[maven-replacer-plugin](https://code.google.com/p/maven-replacer-plugin/) is an excellent plugin to deal with token replacements. We have been using it to handle proxy replacements in Apigee. 

We have a set of common proxies which is stored in a separate location of its own - as xml fragments that can be used with other proxies. When we need them, we create replacement tokens in our proxy *default.xml* and use the *maven-replacer-plugin* to replace the tokens with the content of these proxy fragments at build time.

The replacer plugin, true to its name, replaces the tokens with xml fragments. However, in our case we need to copy the policies referenced by the fragments and resources used by them when the replacement is made. This copy action is specific only to proxy replacements.

This proxy replacer plugin is a code fork of the original maven replacer plugin which adds this addtional functionality.

Usage
---------

Complete usage guide for this plugin is same as the original plugin. Please refer to the [original plugin documentation](https://code.google.com/p/maven-replacer-plugin/wiki/UsageGuide).

Following is an example usage of the enhancement.

<pre>
&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot;?&gt;
&lt;plugin&gt;
   &lt;groupId&gt;com.apigee.cs&lt;/groupId&gt;
   &lt;artifactId&gt;proxy-replacer-maven-plugin&lt;/artifactId&gt;
   &lt;version&gt;1.0&lt;/version&gt;
   &lt;executions&gt;
      &lt;execution&gt;
         &lt;phase&gt;prepare-package&lt;/phase&gt;
         &lt;goals&gt;
            &lt;goal&gt;replace&lt;/goal&gt;
         &lt;/goals&gt;
      &lt;/execution&gt;
   &lt;/executions&gt;
   &lt;configuration&gt;
      &lt;filesToInclude&gt;**&lt;/filesToInclude&gt;
      &lt;basedir&gt;target&lt;/basedir&gt;
      &lt;replacements&gt;
         &lt;replacement&gt;
            &lt;token&gt;#common_oauth_request_flow#&lt;/token&gt;
            &lt;valueFile&gt;./apiproxy/proxies/common_oauth_request_steps.xmlfrag&lt;/valueFile&gt;
            &lt;isProxy&gt;true&lt;/isProxy&gt;
         &lt;/replacement&gt;
         &lt;replacement&gt;
            &lt;token&gt;#common_logging_post_flow#&lt;/token&gt;
            &lt;valueFile&gt;./apiproxy/proxies/common_logging_post_flow_steps.xmlfrag&lt;/valueFile&gt;
            &lt;isProxy&gt;true&lt;/isProxy&gt;
         &lt;/replacement&gt;
      &lt;/replacements&gt;
   &lt;/configuration&gt;
&lt;/plugin&gt;
</pre>

The new *isProxy* tag indicates if this processing needs invoked. If it is *true*, the replacement is treated as proxy and the processing is done. The default value is *false*. In above example all the policies and Javascripts referenced by *common_oauth_request_steps.xmlfrag* and *common_logging_post_flow_steps.xmlfrag* will be copied to the basedir - *target* specified in the *pom.xml*

Following are the additional parameters added to the plugin

 * srcPolicyDir (Default: ../policies)
      Optional source policy directory to find referenced policies. Default value
      is '../policies' relative to location of the proxy file location.
      
 * srcResourceDir (Default: ../resources/jsc)
      Optional resources directory used by policies. Default value is
      '../resources/jsc' relative to location of the proxy file location.
      
 * outputPolicyDir (Default: ../policies)
      Output to directory for the policies. This is an optional parameter, by
      default it is apiproxy/policies under the directory containing the
      replaced files.

 * outputResourceDir (Default: ../resources/jsc)
      Output to directory for the resources dir. This is an optional parameter, by
      default it is resources/jsc under the directory containing the replaced
      files. 


Current State
------------------
The proxy replacer plugin copies all the policies referenced by the proxy xml fragments. It also copies Javasrcipt resources referenced by the Javascript policies too.

We need to implement support to copy other extension proxies like Java and Python.

Please feel free to contribute the same. If you need assistance do get in touch with me [Srikanth Seshadri](sseshadri@apigee.com) 

Acknowledgments
------------------------
The original [maven-replacer-plugin](https://code.google.com/p/maven-replacer-plugin/) developed by [Steven Baker](baker.steven.83@gmail.com) cornorstone for this work. The functionality, code and tests in the maven replacer plugin have been awesome and helped greatly in adding this additional functionality.



