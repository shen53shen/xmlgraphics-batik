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

package org.apache.batik.extension.svg;


public interface BatikExtConstants {

    /** Namespace for batik extentions. */
    public static final String BATIK_EXT_NAMESPACE_URI = 
        "http://xml.apache.org/batik/ext";

    /** Tag name for Batik's regular poly extension. */
    public static final String BATIK_EXT_REGULAR_POLYGON_TAG = 
        "regularPolygon";

    /** Tag name for Batik's star extension. */
    public static final String BATIK_EXT_STAR_TAG = 
        "star";

    /** Tag name for Batik's flowText extension (SVG 1.2). */
    public static final String BATIK_EXT_FLOW_TEXT_TAG = 
        "flowText";

    /** Tag name for Batik's flowText extension Region element (SVG 1.2). */
    public static final String BATIK_EXT_FLOW_REGION_TAG = 
        "flowRegion";

    /** Tag name for Batik's flowText extension Region element (SVG 1.2). */
    public static final String BATIK_EXT_FLOW_REGION_EXCLUDE_TAG = 
        "flowRegionExclude";

    /** Tag name for Batik's flowText extension div element SVG 1.2). */
    public static final String BATIK_EXT_FLOW_DIV_TAG = 
        "flowDiv";

    /** Tag name for Batik's flowText extension p element SVG 1.2). */
    public static final String BATIK_EXT_FLOW_PARA_TAG = 
        "flowPara";

    /** Tag name for Batik's flowText extension flow Region break 
     *  element SVG 1.2). */
    public static final String BATIK_EXT_FLOW_REGION_BREAK_TAG = 
        "flowRegionBreak";

    /** Tag name for Batik's flowText extension line element SVG 1.2). */
    public static final String BATIK_EXT_FLOW_LINE_TAG = 
        "flowLine";

    /** Tag name for Batik's flowText extension span element SVG 1.2). */
    public static final String BATIK_EXT_FLOW_SPAN_TAG = 
        "flowSpan";

    /** Tag name for Batik's solid color extension (SVG 1.2). */
    public static final String BATIK_EXT_SOLID_COLOR_TAG = 
        "solidColor";

    /** Tag name for Batik's color switch extension. */
    public static final String BATIK_EXT_COLOR_SWITCH_TAG = 
        "colorSwitch";

    /** Tag name for Batik's histogram normalization extension. */
    public static final String BATIK_EXT_HISTOGRAM_NORMALIZATION_TAG =
        "histogramNormalization";

    /** Tag name for Batik's multiImage extension. */
    public static final String BATIK_EXT_MULTI_IMAGE_TAG =
        "multiImage";

    /** Tag name for Batik's subImage multiImage extension. */
    public static final String BATIK_EXT_SUB_IMAGE_TAG =
        "subImage";
    /** Tag name for Batik's subImageRef multiImage extension. */
    public static final String BATIK_EXT_SUB_IMAGE_REF_TAG =
        "subImageRef";

    /** Attribute name for dx attribute */
    public static final String BATIK_EXT_DX_ATRIBUTE =
        "dx";
    
    /** Attribute name for dy attribute */
    public static final String BATIK_EXT_DY_ATRIBUTE =
        "dy";
    
    /** Attribute name for dw attribute */
    public static final String BATIK_EXT_DW_ATRIBUTE =
        "dw";
    
    /** Attribute name for dh attribute */
    public static final String BATIK_EXT_DH_ATRIBUTE =
        "dh";

    /** Attribute name for filterPrimitiveMarginsUnits */
    public static final String BATIK_EXT_FILTER_PRIMITIVE_MARGINS_UNITS_ATTRIBUTE
        = "filterPrimitiveMarginsUnits";

    /** Attribute name for filterMarginsUnits */
    public static final String BATIK_EXT_FILTER_MARGINS_UNITS_ATTRIBUTE
        = "filterMarginsUnits";

    /** Attribute name for x attribute */
    public static final String BATIK_EXT_X_ATTRIBUTE = 
        "x";
    /** Attribute name for y attribute */
    public static final String BATIK_EXT_Y_ATTRIBUTE = 
        "y";
    /** Attribute name for width attribute */
    public static final String BATIK_EXT_WIDTH_ATTRIBUTE = 
        "width";
    /** Attribute name for height attribute */
    public static final String BATIK_EXT_HEIGHT_ATTRIBUTE = 
        "height";

    /** Attribute name for margin psudo-attribute */
    public static final String BATIK_EXT_MARGIN_ATTRIBUTE = 
        "margin";
    /** Attribute name for top-margin attribute */
    public static final String BATIK_EXT_TOP_MARGIN_ATTRIBUTE = 
        "top-margin";
    /** Attribute name for right-margin attribute */
    public static final String BATIK_EXT_RIGHT_MARGIN_ATTRIBUTE = 
        "right-margin";
    /** Attribute name for bottom-margin attribute */
    public static final String BATIK_EXT_BOTTOM_MARGIN_ATTRIBUTE = 
        "bottom-margin";
    /** Attribute name for left-margin attribute */
    public static final String BATIK_EXT_LEFT_MARGIN_ATTRIBUTE = 
        "left-margin";
    /** Attribute name for indent attribute/property */
    public static final String BATIK_EXT_INDENT_ATTRIBUTE = 
        "indent";
    /** Attribute name for justification */
    public static final String BATIK_EXT_JUSTIFICATION_ATTRIBUTE = 
        "justification";
    /** Value for justification to start of region */
    public static final String BATIK_EXT_JUSTIFICATION_START_VALUE  = "start";
    /** Value for justification to middle of region */
    public static final String BATIK_EXT_JUSTIFICATION_MIDDLE_VALUE = "middle";
    /** Value for justification to end of region */
    public static final String BATIK_EXT_JUSTIFICATION_END_VALUE    = "end";
    /** Value for justification to both edges of region */
    public static final String BATIK_EXT_JUSTIFICATION_FULL_VALUE = "full";


    /** Attribute name for preformated data */
    public static final String BATIK_EXT_PREFORMATTED_ATTRIBUTE = 
        "preformatted";

   /** Attribute name for preformated data */
    public static final String BATIK_EXT_VERTICAL_ALIGN_ATTRIBUTE =
        "vertical-align";

    /** Value for vertical-align to top of region */
    public static final String BATIK_EXT_ALIGN_TOP_VALUE    = "top";
    /** Value for vertical-align to middle of region */
    public static final String BATIK_EXT_ALIGN_MIDDLE_VALUE = "middle";
    /** Value for vertical-align to bottom of region */
    public static final String BATIK_EXT_ALIGN_BOTTOM_VALUE = "bottom";

    /** Attribute name for sides attribute */
    public static final String BATIK_EXT_SIDES_ATTRIBUTE = 
        "sides";

    /** Attribute name for inner radius attribute */
    public static final String BATIK_EXT_IR_ATTRIBUTE = 
        "ir";

    /** Attribute name for trim percent attribute */
    public static final String BATIK_EXT_TRIM_ATTRIBUTE = 
        "trim";

    /** Attribute name for pixel-width attribute */
    public static final String BATIK_EXT_MIN_PIXEL_SIZE_ATTRIBUTE = 
        "min-pixel-size";

    /** Attribute name for pixel-height attribute */
    public static final String BATIK_EXT_MAX_PIXEL_SIZE_ATTRIBUTE = 
        "max-pixel-size";

    /** Attribute name for color attribute */
    public static final String BATIK_EXT_SOLID_COLOR_PROPERTY = 
        "solid-color";

    /** Attribute name for opacity attribute */
    public static final String BATIK_EXT_SOLID_OPACITY_PROPERTY = 
        "solid-opacity";

    /** Default value for filter dx */
    public static final String SVG_FILTER_DX_DEFAULT_VALUE = "0";

    /** Default value for filter dy */
    public static final String SVG_FILTER_DY_DEFAULT_VALUE = "0";

    /** Default value for filter dw */
    public static final String SVG_FILTER_DW_DEFAULT_VALUE = "0";

    /** Default value for filter dh */
    public static final String SVG_FILTER_DH_DEFAULT_VALUE = "0";
}
