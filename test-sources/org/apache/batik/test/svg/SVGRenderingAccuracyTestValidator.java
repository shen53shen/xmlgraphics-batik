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
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.StringReader;

import java.net.URL;
import java.net.MalformedURLException;

import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.ImageTranscoder;
import org.apache.batik.transcoder.image.PNGTranscoder;

import org.apache.batik.test.Test;
import org.apache.batik.test.AbstractTest;
import org.apache.batik.test.DefaultTestReport;
import org.apache.batik.test.DefaultTestSuite;
import org.apache.batik.test.TestReport;
import org.apache.batik.test.TestReportValidator;

/**
 * Validates the operation of <tt>SVGRenderingAccuracyTest</tt>
 * by forcing specific test case situations and checking that
 * they are handled properly by the class.
 * 
 * @author <a href="mailto:vhardy@apache.org">Vincent Hardy</a>
 * @version $Id$
 */
public class SVGRenderingAccuracyTestValidator extends DefaultTestSuite {
    /**
     * Simple valid SVG content used for this test
     */
    private static final String validSVG 
        = "<svg width=\"450\" height=\"500\" viewBox=\"0 0 450 500\"> \n" +
        "    <rect x=\"25\" y=\"25\" width=\"400\" height=\"450\" fill=\"blue\" /> \n" +
        "</svg>\n";

    /**
     * Simple valid SVG content used for this test
     */
    private static final String validSVGVariation
        = "<svg width=\"450\" height=\"500\" viewBox=\"0 0 450 500\"> \n" +
        "    <rect x=\"25\" y=\"25\" width=\"400\" height=\"450\" fill=\"#0000cc\" /> \n" +
        "</svg>\n";

    /**
     * Simple valid SVG content used for this test, small size
     */
    private static final String validSmallSVG 
        = "<svg width=\"45\" height=\"50\" viewBox=\"0 0 45 50\"> \n" +
        "    <rect x=\"2.5\" y=\"2.5\" width=\"40\" height=\"45\" fill=\"blue\" /> \n" +
        "</svg>\n";

    /**
     * Simple valid SVG content used for this test, red rectangle
     */
    private static final String validRedSVG 
        = "<svg width=\"450\" height=\"500\" viewBox=\"0 0 450 500\"> \n" +
        "    <rect x=\"25\" y=\"25\" width=\"400\" height=\"450\" fill=\"red\" /> \n" +
        "</svg>\n";

    /**
     * Simple invalid SVG content used for this test
     * (the error is that a double quote is missing at the 
     * end of the width attribute value.
     */
    private static final String invalidSVG 
        = "<svg width=\"450 height=\"500\" viewBox=\"0 0 450 500\"> \n" +
        "    <rect x=\"25\" y=\"25\" width=\"400\" height=\"450\" fill=\"blue\" /> \n" +
        "</svg>\n";

    /**
     * This test creates a sample SVG file dynamically and
     * valides that errors are generated for the
     * following cases:
     * 
     * + invalid SVG URL
     * + corrupted SVG image (i.e., cannot be transcoded to
     *   an image).
     * + invalid reference image URL
     * + valid reference image URL, but inexisting resource.
     * + reference image of different size than generated
     *   image.
     * + reference image different than the generated image
     * 
     * This test finally validates that the test
     * passes if the reference image corresponds to the
     * newly generated image.
     */
    public SVGRenderingAccuracyTestValidator(){
        addTest(new InvalidSVGURL());
        addTest(new InvalidSVGContent());
        addTest(new InvalidReferenceImageURL());
        addTest(new InexistingReferenceImage());
        addTest(new DifferentSizes());
        addTest(new SameSizeDifferentContent());
        addTest(new AccurateRendering());
        addTest(new AccurateRenderingWithVariation());
        addTest(new DefaultConfigTest());
    }
    
    /**
     * Creates a SVG source URL for the given svg content
     */
    public static URL createSVGSourceURL(String svgContent) throws Exception{
        File tmpFile = File.createTempFile(SVGRenderingAccuracyTest.TEMP_FILE_PREFIX,
                                           null);
        FileWriter writer = new FileWriter(tmpFile);
        writer.write(svgContent);
        writer.close();
        return tmpFile.toURL();
    }

    /**
     * Helper method: creates a valid reference image
     */
    public static URL createValidReferenceImage(String svgContent) throws Exception{
        TranscoderInput validSrc = new TranscoderInput(new StringReader(svgContent));
        
        File tmpFile = File.createTempFile(SVGRenderingAccuracyTest.TEMP_FILE_PREFIX,
                                           SVGRenderingAccuracyTest.TEMP_FILE_SUFFIX);
        
        TranscoderOutput validDst 
            = new TranscoderOutput(new FileOutputStream(tmpFile));
        
        ImageTranscoder transcoder 
            = new PNGTranscoder();
        
        transcoder.transcode(validSrc, validDst);
        
        tmpFile.deleteOnExit();
        
        return tmpFile.toURL();
    }


    /**
     * Validates that the default parameters computation is
     * working as expected.
     */
    static class DefaultConfigTest extends AbstractTest {
        String svgURL = "samples/anne.svg";
        String expectedRefImgURL = "test-references/samples/anne.png";
        String expectedVariationURL = "test-references/samples/accepted-variation/anne.png";
        String expectedCandidateURL = "test-references/samples/candidate-variation/anne.png";

        String ERROR_EXCEPTION_WHILE_BUILDING_TEST
            = "error.exception.while.building.test";

        String ERROR_UNEXPECTED_REFERENCE_IMAGE_URL
            = "error.unexpected.reference.image.url";

        String ERROR_UNEXPECTED_VARIATION_URL
            = "error.unexpected.variation.url";

        String ERROR_UNEXPECTED_CANDIDATE_URL
            = "error.unexpected.candidate.url";

        String ENTRY_KEY_EXPECTED_VALUE 
            = "entry.key.expected.value";

        String ENTRY_KEY_FOUND_VALUE
            = "entry.key.found.value";

        public DefaultConfigTest(){
            super();
            setId("defaultTest");
        }

        public TestReport runImpl() throws Exception {
            SVGRenderingAccuracyTest t 
                = new SamplesRenderingTest();

            t.setId(svgURL);

            if(!t.refImgURL.toString().endsWith(expectedRefImgURL)){
                TestReport r = reportError(ERROR_UNEXPECTED_REFERENCE_IMAGE_URL);
                r.addDescriptionEntry(ENTRY_KEY_EXPECTED_VALUE, expectedRefImgURL);
                r.addDescriptionEntry(ENTRY_KEY_FOUND_VALUE, "" + t.refImgURL);
                return r;
            }

            if(!t.variationURL.toString().endsWith(expectedVariationURL)){
                TestReport r = reportError(ERROR_UNEXPECTED_VARIATION_URL);
                r.addDescriptionEntry(ENTRY_KEY_EXPECTED_VALUE, expectedVariationURL);
                r.addDescriptionEntry(ENTRY_KEY_FOUND_VALUE, "" + t.variationURL);
                return r;
            }

            if(!t.saveVariation.toURL().toString().endsWith(expectedCandidateURL)){
                TestReport r = reportError(ERROR_UNEXPECTED_CANDIDATE_URL);
                r.addDescriptionEntry(ENTRY_KEY_EXPECTED_VALUE, expectedCandidateURL);
                r.addDescriptionEntry(ENTRY_KEY_FOUND_VALUE, "" + t.saveVariation.toURL().toString());
                return r;
            }

            return reportSuccess();
        }
    }



    /**
     * Creates an <tt>SVGRenderingAccuracyTest</tt> with an
     * invalid URL for the source SVG. Checks that this 
     * error is reported as a failure.
     */
    static class InvalidSVGURL extends TestReportValidator {
        public TestReport runImpl() throws Exception {
            // Create an invalid URL for the SVG file
            URL invalidSVGURL = new URL("http",
                                        "dummyHost",
                                        "dummyFile.svg");

            // Create a valid reference image
            URL refImgURL = createValidReferenceImage(validSVG);

            Test t = new SVGRenderingAccuracyTest(invalidSVGURL.toString(),
                                                  refImgURL.toString());

            setConfig(t,
                      false,
                      SVGRenderingAccuracyTest.ERROR_CANNOT_TRANSCODE_SVG);

            return super.runImpl();
        }
    }

    /**
     * Creates an <tt>SVGRenderingAccuracyTest</tt> with a
     * valid URL pointing to an invalid SVG document. Checks that this 
     * error is reported as a failure.
     */
    static class InvalidSVGContent extends TestReportValidator {
        public TestReport runImpl() throws Exception {
            // Create an SVG URL from invalid SVG content.
            URL validSVGURL = createSVGSourceURL(invalidSVG);
            
            // Create a valid reference image
            URL refImgURL = createValidReferenceImage(validSVG);

            Test t = new SVGRenderingAccuracyTest(validSVGURL.toString(),
                                                  refImgURL.toString());
            setConfig(t,
                      false,
                      SVGRenderingAccuracyTest.ERROR_CANNOT_TRANSCODE_SVG);

            return super.runImpl();
        }
    }

    /**
     * Creates an <tt>SVGRenderingAccuracyTest</tt> with an
     * valid URL for the source SVG but with an invalid 
     * URL for the reference image.
     */
    static class InvalidReferenceImageURL extends TestReportValidator {
        public TestReport runImpl() throws Exception {
            // Create a valid SVG URL from valid SVG content.
            URL validSVGURL = createSVGSourceURL(validSVG);

            // Create an invalid URL for the reference image.
            URL invalidReferenceImageURL = null;
            
            invalidReferenceImageURL = new URL("http",
                                               "dummyHost",
                                               "dummyFile.png");
            Test t = new SVGRenderingAccuracyTest(validSVGURL.toString(),
                                                  invalidReferenceImageURL.toString());

            setConfig(t,
                      false,
                      SVGRenderingAccuracyTest.ERROR_CANNOT_OPEN_REFERENCE_IMAGE);

            return super.runImpl();
        }
    }

    /**
     * Creates an <tt>SVGRenderingAccuracyTest</tt> with an
     * valid URL for the source SVG valid 
     * URL for the reference image, but the reference image,
     * but the reference image does not exist
     */
    static class InexistingReferenceImage extends TestReportValidator {
        public TestReport runImpl() throws Exception {
            // Create a valid SVG URL from valid SVG content.
            URL validSVGURL = createSVGSourceURL(validSVG);

            // Create an valid URL for the reference image.
            // We use the createSVGSourceURL method to create
            // a File that the ImageLoader is not able to load.
            File tmpFile = File.createTempFile(SVGRenderingAccuracyTest.TEMP_FILE_PREFIX,
                                               null);
            URL refImgURL = tmpFile.toURL();
            tmpFile.delete();
            
            Test t = new SVGRenderingAccuracyTest(validSVGURL.toString(),
                                                  refImgURL.toString());

            setConfig(t,
                      false,
                      SVGRenderingAccuracyTest.ERROR_CANNOT_OPEN_REFERENCE_IMAGE);

            return super.runImpl();
        }
    }

    static class DifferentSizes extends TestReportValidator {
        public TestReport runImpl() throws Exception {
            //
            // Create a valid SVG URL from valid SVG content.
            //
            URL validSVGURL = createSVGSourceURL(validSVG);

            //
            // Create an valid URL for the reference image.
            //
            URL validRefImageURL = createValidReferenceImage(validSmallSVG);
            
            //
            // Run test and check report
            //
            Test t = new SVGRenderingAccuracyTest(validSVGURL.toString(),
                                                  validRefImageURL.toString());

            setConfig(t,
                      false,
                      SVGRenderingAccuracyTest.ERROR_SVG_RENDERING_NOT_ACCURATE);                      

            return super.runImpl();
        }
    }

    static class SameSizeDifferentContent extends TestReportValidator {
        public TestReport runImpl() throws Exception {
            // Create a valid SVG URL from valid SVG content.
            URL validSVGURL = createSVGSourceURL(validSVG);

            // Create an valid URL for the reference image.
            URL validRefImageURL = createValidReferenceImage(validRedSVG);
            

            Test t = new SVGRenderingAccuracyTest(validSVGURL.toString(),
                                                  validRefImageURL.toString());

            setConfig(t,
                      false, 
                      SVGRenderingAccuracyTest.ERROR_SVG_RENDERING_NOT_ACCURATE);

            return super.runImpl();
        }
    }

    static class AccurateRendering extends TestReportValidator {
        public TestReport runImpl() throws Exception {
            // Create a valid SVG URL from valid SVG content.
            URL validSVGURL = createSVGSourceURL(validSVG);

            // Create an valid URL for the reference image.
            URL validRefImageURL = createValidReferenceImage(validSVG);

            setConfig(new SVGRenderingAccuracyTest(validSVGURL.toString(),
                                                   validRefImageURL.toString()),
                      true,
                      null);

            return super.runImpl();
        }
    }

    /**
     * Validates that test passes if proper variation is given
     */
    static class AccurateRenderingWithVariation extends TestReportValidator {
        public TestReport runImpl() throws Exception {
            // Create a valid SVG URL from valid SVG content.
            URL validSVGURL = createSVGSourceURL(validSVG);

            // Create an valid URL for the reference image.
            URL validRefImageURL = createValidReferenceImage(validSVGVariation);

            SVGRenderingAccuracyTest t 
                = new SVGRenderingAccuracyTest(validSVGURL.toString(),
                                               validRefImageURL.toString());

            File tmpVariationFile = File.createTempFile(SVGRenderingAccuracyTest.TEMP_FILE_PREFIX, null);

            // Run the test with the tmpVariationFile
            t.setSaveVariation(tmpVariationFile);

            setConfig(t,
                      false,
                      SVGRenderingAccuracyTest.ERROR_SVG_RENDERING_NOT_ACCURATE);

            super.runImpl();            

            t.setVariationURL(tmpVariationFile.toURL().toString());
            t.setSaveVariation(null);

            setConfig(t,
                      true,
                      null);

            return super.runImpl();
        }
    }

}
