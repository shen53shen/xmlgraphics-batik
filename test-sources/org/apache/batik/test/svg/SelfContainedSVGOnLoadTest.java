/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Batik" and  "Apache Software Foundation" must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation. For more  information on the
 Apache Software Foundation, please see <http://www.apache.org/>.

*/

package org.apache.batik.test.svg;

import java.io.File;
import java.io.IOException;

import java.net.URL;
import java.net.MalformedURLException;

import org.apache.batik.bridge.BaseScriptingEnvironment;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.BridgeException;

import org.apache.batik.bridge.GVTBuilder;
import org.apache.batik.bridge.UserAgent;
import org.apache.batik.bridge.UserAgentAdapter;

import org.apache.batik.test.AbstractTest;
import org.apache.batik.test.DefaultTestReport;
import org.apache.batik.test.TestReport;

import org.apache.batik.util.XMLResourceDescriptor;

import org.apache.batik.dom.svg.SAXSVGDocumentFactory;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.NamedNodeMap;

/**
 * This test takes an SVG file as an input. It processes the input SVG
 * (meaning it turns it into a GVT tree) and then dispatches the 'onload'
 * event.
 * 
 * The SVG input file should contain script that will position the 
 * result of the test in the DOM using the following namespace: <br />
 * xmlns:test="http://xml.apache.org/batik/test" <br />
 * 
 * The result should be set in the <code>result</code> attribute of the
 * &lt;testResult&gt; element by the script embeded in the input SVG
 * test file. <br />
 * 
 * Sample input SVG file:<br /><code>
 * &lt;svg ... onload="runTest(evt)" xmlns:test="http://xml.apache.org/batik/test" &gt;
 *   &lt;script type="text/ecmascript"&gt;
 *   function runTest(evt) {
 *      ...; // do some test
 *      var rootSvg = document.getDocumentElement();
 *      var result = document.createElementNS("http://xml.apache.org/batik/test",
 *                                            "testResult");
 *      result.setAttributeNS(null, "result", "failed");
 *      result.setAttributeNS(null, "errorCode", "org.apache.batik.css.dom.wrong.computed.value");
 *      rootSvg.appendChild(result);
 *   }
 * &lt;/script&gt;
 * &lt;/svg&gt;</code>
 *
 * @author <a href="mailto:vhardy@apache.org">Vincent Hardy</a>
 * @version $Id$
 */
public class SelfContainedSVGOnLoadTest extends AbstractTest {
    /**
     * Error when the input file cannot be loaded into a
     * Document object
     * {0} = IOException message
     */
    public static final String ERROR_CANNOT_LOAD_SVG_DOCUMENT
        = "SelfContainedSVGOnLoadTest.error.cannot.load.svg.document";

    /**
     * Error while building the GVT tree or dispatching the 
     * 'onload' event.
     */
    public static final String ERROR_WHILE_PROCESSING_SVG_DOCUMENT
        = "SelfContainedSVGOnLoadTest.error.while.processing.svg.document";

    /**
     * There is either 0 or more than one <testResult> elements in
     * the document after dispatching the onload event.
     */
    public static final String ERROR_UNEXPECTED_NUMBER_OF_TEST_RESULT_ELEMENTS
        = "SelfContainedSVGOnLoadTest.error.unexpected.number.of.test.result.elements";

    /**
     * The 'result' attribute value is neither 'passed' nor 'failed'
     */
    public static final String ERROR_UNEXPECTED_RESULT_VALUE
        = "SelfContainedSVGOnLoadTest.error.unexpected.result.value";

    /**
     * The result was 'failed' but there was no 'errorCode' attribute or
     * it was the empty string
     */
    public static final String ERROR_MISSING_OR_EMPTY_ERROR_CODE_ON_FAILED_TEST
        = "SelfContainedSVGOnLoadTest.error.missing.or.empty.error.code.on.failed.test";

    /**
     * Entry describing the error
     */
    public static final String ENTRY_KEY_ERROR_DESCRIPTION 
        = "SelfContainedSVGOnLoadTest.entry.key.error.description";

    /**
     * Entry describing the number of testResult elements found in the 
     * document after dispatching onload.
     */
    public static final String ENTRY_KEY_NUMBER_OF_TEST_RESULT_ELEMENTS
        = "SelfContainedSVGOnLoadTest.entry.key.number.of.test.result.elements";

    /**
     * Entry describing the result value (different from 'passed' or 'failed'
     * found in the 'result' attribute.
     */
    public static final String ENTRY_KEY_RESULT_VALUE
        = "SelfContainedSVGOnLoadTest.entry.key.result.value";

    /**
     * Test Namespace
     */
    public static final String testNS = "http://xml.apache.org/batik/test";

    /**
     * Test Constants
     */
    public static final String TAG_TEST_RESULT = "testResult";
    public static final String TAG_ERROR_DESCRIPTION_ENTRY = "errorDescriptionEntry";
    public static final String ATTRIBUTE_RESULT = "result";
    public static final String ATTRIBUTE_KEY = "id";
    public static final String ATTRIBUTE_VALUE = "value";
    public static final String TEST_RESULT_PASSED = "passed";
    public static final String TEST_RESULT_FAILED = "failed";

    /**
     * The URL for the input SVG document to be tested
     */
    protected String svgURL;

    /**
     * @param svgURL the URL string for the SVG document being tested
     */
    public SelfContainedSVGOnLoadTest(String svgURL){
        this.svgURL = resolveURL(svgURL);
    }

    /**
     * Default constructor
     */
    protected SelfContainedSVGOnLoadTest(){
    }

    /**
     * Resolves the input string as follows.
     * + First, the string is interpreted as a file description.
     *   If the file exists, then the file name is turned into
     *   a URL.
     * + Otherwise, the string is supposed to be a URL. If it
     *   is an invalid URL, an IllegalArgumentException is thrown.
     */
    protected String resolveURL(String url){
        // Is url a file?
        File f = (new File(url)).getAbsoluteFile();
        if(f.getParentFile().exists()){
            try{
                return f.toURL().toString();
            }catch(MalformedURLException e){
                throw new IllegalArgumentException();
            }
        }
        
        // url is not a file. It must be a regular URL...
        try{
            return (new URL(url)).toString();
        }catch(MalformedURLException e){
            throw new IllegalArgumentException(url);
        }
    }


    /**
     * Run this test and produce a report.
     * The test goes through the following steps: <ul>
     * <li>load the input SVG into a Document</li>
     * <li>build the GVT tree corresponding to the 
     *     Document and dispatch the 'onload' event</li>
     * <li>looks for one and only one &lt;testResult&gt; element in
     *     the Document. This is used to build the returned 
     *     TestReport</li>
     * </ul>
     *
     */
    public TestReport runImpl() throws Exception{
        DefaultTestReport report 
            = new DefaultTestReport(this);

        //
        // First step: 
        //
        // Load the input SVG into a Document object
        //
        String parserClassName = XMLResourceDescriptor.getXMLParserClassName();
        SAXSVGDocumentFactory f = new SAXSVGDocumentFactory(parserClassName);
        Document doc = null;

        try {
            doc = f.createDocument(svgURL);
        } catch(IOException e){
            report.setErrorCode(ERROR_CANNOT_LOAD_SVG_DOCUMENT);
            report.addDescriptionEntry(ENTRY_KEY_ERROR_DESCRIPTION,
                                       e.getMessage());
            report.setPassed(false);
            return report;
        } catch(Exception e){
            report.setErrorCode(ERROR_CANNOT_LOAD_SVG_DOCUMENT);
            report.addDescriptionEntry(ENTRY_KEY_ERROR_DESCRIPTION,
                                       e.getMessage());
            report.setPassed(false);
            return report;
        }

        //
        // Second step:
        // 
        // Now that the SVG file has been loaded, build
        // a GVT Tree from it
        //
        UserAgent userAgent = buildUserAgent();
        GVTBuilder builder = new GVTBuilder();
        BridgeContext ctx = new BridgeContext(userAgent);
        ctx.setDynamic(true);

        try {
            builder.build(ctx, doc);
            BaseScriptingEnvironment scriptEnvironment 
                = new BaseScriptingEnvironment(ctx);
            scriptEnvironment.loadScripts();
            scriptEnvironment.dispatchSVGLoadEvent();
        } catch (BridgeException e){
            e.printStackTrace();
            report.setErrorCode(ERROR_WHILE_PROCESSING_SVG_DOCUMENT);
            report.addDescriptionEntry(ENTRY_KEY_ERROR_DESCRIPTION,
                                       e.getMessage());
            report.setPassed(false);
            return report;
        } catch(Exception e){
            e.printStackTrace();
            report.setErrorCode(ERROR_WHILE_PROCESSING_SVG_DOCUMENT);
            report.addDescriptionEntry(ENTRY_KEY_ERROR_DESCRIPTION,
                                       e.getMessage());
            report.setPassed(false);
            return report;
        }

        //
        // Final step:
        //
        // Look for one and only one <testResult> element
        //
        NodeList testResultList = doc.getElementsByTagNameNS(testNS,
                                                             TAG_TEST_RESULT);

        // Check that there is one and only one testResult element
        if(testResultList.getLength() != 1){
            report.setErrorCode(ERROR_UNEXPECTED_NUMBER_OF_TEST_RESULT_ELEMENTS);
            report.addDescriptionEntry(ENTRY_KEY_NUMBER_OF_TEST_RESULT_ELEMENTS,
                                  "" + testResultList.getLength());
            report.setPassed(false);
            return report;
        }

        Element testResult = (Element)testResultList.item(0);
        
        // Now, get the result attribute. Whould be either "passed" or "failed"
        String result = testResult.getAttributeNS(null, ATTRIBUTE_RESULT);
        boolean passed = true;
        if(TEST_RESULT_PASSED.equals(result)){
            // OK
        } else if (TEST_RESULT_FAILED.equals(result)){
            passed = false;
        } else {
            report.setErrorCode(ERROR_UNEXPECTED_RESULT_VALUE);
            report.addDescriptionEntry(ENTRY_KEY_RESULT_VALUE, result);
            report.setPassed(false);
            return report;
        }

        // If the test failed, then there should be an error code
        if( !passed ){
            String errorCode = testResult.getAttributeNS(null, "errorCode");
            if("".equals(errorCode)){
                report.setErrorCode(ERROR_MISSING_OR_EMPTY_ERROR_CODE_ON_FAILED_TEST);
                report.setPassed(false);
                return report;
            }

            // We got an error code, set it on the report object
            report.setErrorCode(errorCode);

            // Now, add descriptions from children <errorDescriptionEntry> elements
            NodeList desc = testResult.getElementsByTagNameNS(testNS,
                                                              TAG_ERROR_DESCRIPTION_ENTRY);
            int nDesc = desc.getLength();
            for (int i=0; i<nDesc; i++){
                Element entry = (Element)desc.item(i);
                String key = entry.getAttributeNS(null, ATTRIBUTE_KEY);
                String value = entry.getAttributeNS(null, ATTRIBUTE_VALUE);
                report.addDescriptionEntry(key, value);
            }
            report.setPassed(false);
            return report;
        }

        return report;
    }

    /**
     * Give subclasses a chance to build their own UserAgent
     */
    protected UserAgent buildUserAgent(){
        return new UserAgentAdapter();
    }

}

