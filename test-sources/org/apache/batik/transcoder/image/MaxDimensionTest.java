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
package org.apache.batik.transcoder.image;

import java.util.Map;
import java.util.HashMap;

import org.apache.batik.transcoder.TranscoderInput;

/**
 * Test the ImageTranscoder with the KEY_MAX_WIDTH and/or the KEY_MAX_HEIGHT 
 * transcoding hint.
 *
 * @author <a href="mailto:ruini@iki.fi">Henri Ruini</a>
 * @version $Id$ 
 */
public class MaxDimensionTest extends AbstractImageTranscoderTest {

    //-- Variables -----------------------------------------------------------
    /** The URI of the input image. */
    protected String inputURI;
    /** The URI of the reference image. */
    protected String refImageURI;
    /** The maximum width of the image. */
    protected Float maxWidth = new Float(Float.NaN);
    /** The maximum height of the image. */
    protected Float maxHeight = new Float(Float.NaN);
    /** The width of the image. */
    protected Float width = new Float(Float.NaN);
    /** The height of the image. */
    protected Float height = new Float(Float.NaN);


    //-- Constructors --------------------------------------------------------
    /**
     * Constructs a new <tt>MaxDimensionTest</tt>.
     *
     * @param inputURI URI of the input image.
     * @param refImageURI URI of the reference image.
     * @param maxWidth Maximum image width (KEY_MAX_WIDTH value).
     * @param maxHeight Maximum image height (KEY_MAX_HEIGHT value).
     */
    public MaxDimensionTest(String inputURI, String refImageURI, Float maxWidth, Float maxHeight) {
        this.inputURI = inputURI;
        this.refImageURI = refImageURI;
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
    }

    /**
     * Constructs a new <tt>MaxDimensionTest</tt>.
     *
     * @param inputURI URI of the input image.
     * @param refImageURI URI of the reference image.
     * @param maxWidth Maximum image width (KEY_MAX_WIDTH value).
     * @param maxHeight Maximum image height (KEY_MAX_HEIGHT value).
     * @param width Image width (KEY_WIDTH value).
     * @param height Image height (KEY_HEIGH value).
     */
    public MaxDimensionTest(String inputURI, String refImageURI, Float maxWidth, Float maxHeight, Float width, Float height) {
        this.inputURI = inputURI;
        this.refImageURI = refImageURI;
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
        this.width = width;
        this.height = height;
    }


    //-- Methods -------------------------------------------------------------
    /**
     * Creates the <tt>TranscoderInput</tt>.
     */
    protected TranscoderInput createTranscoderInput() {
        return new TranscoderInput(resolveURL(inputURI).toString());
    }
    
    /**
     * Creates a Map that contains additional transcoding hints.
     *
     * @return Transcoding hint values.
     */
    protected Map createTranscodingHints() {
        Map hints = new HashMap(7);
        if (!width.isNaN() && width.floatValue() > 0) {
            hints.put(ImageTranscoder.KEY_WIDTH, width);
        }
        if (!height.isNaN() && height.floatValue() > 0) {
            hints.put(ImageTranscoder.KEY_HEIGHT, height);
        }
        if (maxWidth.floatValue() > 0) {
            hints.put(ImageTranscoder.KEY_MAX_WIDTH, maxWidth);
        }
        if (maxHeight.floatValue() > 0) {
            hints.put(ImageTranscoder.KEY_MAX_HEIGHT, maxHeight);
        }
        return hints;
    }

    /**
     * Returns the reference image for this test.
     */
    protected byte [] getReferenceImageData() {
        return createBufferedImageData(resolveURL(refImageURI));
    }
}

