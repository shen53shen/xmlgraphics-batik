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

import java.awt.Dimension;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.batik.ext.awt.image.renderable.ClipRable8Bit;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.bridge.Bridge;
import org.apache.batik.bridge.BridgeContext;
import org.apache.batik.bridge.BridgeException;
import org.apache.batik.bridge.CSSUtilities;
import org.apache.batik.bridge.SVGImageElementBridge;
import org.apache.batik.bridge.SVGUtilities;
import org.apache.batik.bridge.Viewport;
import org.apache.batik.dom.svg.SVGOMElement;
import org.apache.batik.dom.svg.XMLBaseSupport;
import org.apache.batik.dom.util.XLinkSupport;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.ImageNode;
import org.apache.batik.util.ParsedURL;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * Bridge class for the &lt;multiImage> element.
 *
 * The 'multiImage' element is similar to the 'image' element (supports
 * all the same attributes and properties) except.
 * <ol>
 *    <li>It can only be used to reference raster content (this is an
 *        implementation thing really)</li>
 *    <li>It has two addtional attributes: 'pixel-width' and
 *        'pixel-height' which are the maximum width and height of the
 *        image referenced by the xlink:href attribute.</li>
 *    <li>It can contain a child element 'subImage' which has only
 *        three attributes, pixel-width, pixel-height and xlink:href.
 *        The image displayed is the smallest image such that
 *        pixel-width and pixel-height are greater than or equal to the
 *        required image size for display.</li>
 * </ol>
 *
 *
 * @author <a href="mailto:deweese@apache.org">Thomas DeWeese</a>
 * @version $Id$
 */
public class BatikMultiImageElementBridge extends SVGImageElementBridge
    implements BatikExtConstants {

    BatikMultiImageElementBridge() { }

    /**
     * Returns the Batik Extension namespace URI.
     */
    public String getNamespaceURI() {
        return BATIK_EXT_NAMESPACE_URI;
    }

    /**
     * Returns 'multiImage'.
     */
    public String getLocalName() {
        return BATIK_EXT_MULTI_IMAGE_TAG;
    }

    /**
     * Returns a new instance of this bridge.
     */
    public Bridge getInstance() {
        return new BatikMultiImageElementBridge();
    }

     /**
      * Creates a graphics node using the specified BridgeContext and for the
      * specified element.
      *  
      * @param  ctx the bridge context to use
      * @param  e   the element that describes the graphics node to build
      * @return a graphics node that represents the specified element
      */
    public GraphicsNode createGraphicsNode(BridgeContext ctx, Element e) {
        // 'requiredFeatures', 'requiredExtensions' and 'systemLanguage'
        if (!SVGUtilities.matchUserAgent(e, ctx.getUserAgent())) {
            return null;
        }

        ImageNode imgNode = (ImageNode)instantiateGraphicsNode();
        if (imgNode == null) {
            return null;
        }

        Rectangle2D b = getImageBounds(ctx, e);

        // 'transform'
        AffineTransform at = null;
        String s = e.getAttribute(SVG_TRANSFORM_ATTRIBUTE);
        if (s.length() != 0)
            at = SVGUtilities.convertTransform(e, SVG_TRANSFORM_ATTRIBUTE, s);
        else
            at = new AffineTransform();

        at.translate(b.getX(), b.getY());
        imgNode.setTransform(at);
        
        // 'visibility'
        imgNode.setVisible(CSSUtilities.convertVisibility(e));

        Rectangle2D clip;
        clip = new Rectangle2D.Double(0,0,b.getWidth(), b.getHeight());
        Filter filter = imgNode.getGraphicsNodeRable(true);
        imgNode.setClip(new ClipRable8Bit(filter, clip));

        // 'enable-background'
        Rectangle2D r = CSSUtilities.convertEnableBackground(e);
        if (r != null) {
            imgNode.setBackgroundEnable(r);
        }
        ctx.openViewport(e, new MultiImageElementViewport
                         ((float)b.getWidth(), (float)b.getHeight()));


        List elems  = new LinkedList();
        List minDim = new LinkedList();
        List maxDim = new LinkedList();

        for (Node n = e.getFirstChild(); n != null; n = n.getNextSibling()) {
            if (n.getNodeType() != Node.ELEMENT_NODE)
                continue;
            
            Element se = (Element)n;
            if (!se.getNamespaceURI().equals(BATIK_EXT_NAMESPACE_URI))
                continue;
            if (se.getLocalName().equals(BATIK_EXT_SUB_IMAGE_TAG)) {
                addInfo(se, elems, minDim, maxDim, b);
            }
            if (se.getLocalName().equals(BATIK_EXT_SUB_IMAGE_REF_TAG)) {
                addRefInfo(se, elems, minDim, maxDim, b);
            }
        }

        Dimension [] mindary = new Dimension[elems.size()];
        Dimension [] maxdary = new Dimension[elems.size()];
        Element   [] elemary = new Element  [elems.size()];
        Iterator mindi = minDim.iterator();
        Iterator maxdi = maxDim.iterator();
        Iterator ei = elems.iterator();
        int n=0;
        while (mindi.hasNext()) {
            Dimension minD = (Dimension)mindi.next();
            Dimension maxD = (Dimension)maxdi.next();
            int i =0;
            if (minD != null) {
                for (; i<n; i++) {
                    if ((mindary[i] != null) &&
                        (minD.width < mindary[i].width)) {
                        break;
                    }
                }
            }
            for (int j=n; j>i; j--) {
                elemary[j] = elemary[j-1];
                mindary[j] = mindary[j-1];
                maxdary[j] = maxdary[j-1];
            }
            
            elemary[i] = (Element)ei.next();
            mindary[i] = minD;
            maxdary[i] = maxD;
            n++;
        }

        GraphicsNode node = new MultiResGraphicsNode(e, clip, elemary, 
                                                     mindary, maxdary,
                                                     ctx);
        imgNode.setImage(node);

        return imgNode;
    }

    /**
     * Returns false as shapes are not a container.
     */
    public boolean isComposite() {
        return false;
    }

    public void buildGraphicsNode(BridgeContext ctx,
                                  Element e,
                                  GraphicsNode node) {
        initializeDynamicSupport(ctx, e, node);

        // Handle children elements such as <title>
        //SVGUtilities.bridgeChildren(ctx, e);
        //super.buildGraphicsNode(ctx, e, node);
        ctx.closeViewport(e);
    }

    /**
     * This method is invoked during the build phase if the document
     * is dynamic. The responsability of this method is to ensure that
     * any dynamic modifications of the element this bridge is
     * dedicated to, happen on its associated GVT product.
     */
    protected void initializeDynamicSupport(BridgeContext ctx,
                                            Element e,
                                            GraphicsNode node) {
        if (!ctx.isInteractive())
            return;

        // HACK due to the way images are represented in GVT
        ImageNode imgNode = (ImageNode)node;
        ctx.bind(e, imgNode.getImage());

        if (ctx.isDynamic()) {
            this.e = e;
            this.node = node;
            this.ctx = ctx;
            ((SVGOMElement)e).setSVGContext(this);
        }
    }

    /**
     * Disposes this BridgeUpdateHandler and releases all resources.
     */
    public void dispose() {
        ctx.removeViewport(e);
        super.dispose();
    }

    protected void addInfo(Element e, Collection elems, 
                           Collection minDim, Collection maxDim,
                           Rectangle2D bounds) {
        Document doc   = e.getOwnerDocument();
        Element  gElem = doc.createElementNS(SVG_NAMESPACE_URI, 
                                              SVG_G_TAG);
        NamedNodeMap attrs = e.getAttributes();
        int len = attrs.getLength();
        for (int i = 0; i < len; i++) {
            Attr attr = (Attr)attrs.item(i);
            gElem.setAttributeNS(attr.getNamespaceURI(),
                                 attr.getName(),
                                 attr.getValue());
        }
        // move the children from <subImage> to the <g> element
        for (Node n = e.getFirstChild();
             n != null;
             n = e.getFirstChild()) {
            gElem.appendChild(n);
        }
        e.appendChild(gElem);
        elems.add(gElem);
        minDim.add(getElementMinPixel(e, bounds));
        maxDim.add(getElementMaxPixel(e, bounds));
    }

    protected void addRefInfo(Element e, Collection elems, 
                              Collection minDim, Collection maxDim,
                              Rectangle2D bounds) {
        String uriStr = XLinkSupport.getXLinkHref(e);
        if (uriStr.length() == 0) {
            throw new BridgeException(e, ERR_ATTRIBUTE_MISSING,
                                      new Object[] {"xlink:href"});
        }
        String baseURI = XMLBaseSupport.getCascadedXMLBase(e);
        ParsedURL purl;
        if (baseURI == null) purl = new ParsedURL(uriStr);
        else                 purl = new ParsedURL(baseURI, uriStr);
        Document doc = e.getOwnerDocument();
        Element imgElem = doc.createElementNS(SVG_NAMESPACE_URI, 
                                              SVG_IMAGE_TAG);
        imgElem.setAttributeNS(XLinkSupport.XLINK_NAMESPACE_URI, 
                               "href", purl.toString());
        // move the attributes from <subImageRef> to the <image> element
        NamedNodeMap attrs = e.getAttributes();
        int len = attrs.getLength();
        for (int i = 0; i < len; i++) {
            Attr attr = (Attr)attrs.item(i);
            imgElem.setAttributeNS(attr.getNamespaceURI(),
                                   attr.getName(),
                                   attr.getValue());
        }
        String s;
        s = e.getAttribute("x");
        if (s.length() == 0) imgElem.setAttribute("x", "0");
        s = e.getAttribute("y");
        if (s.length() == 0) imgElem.setAttribute("y", "0");
        s = e.getAttribute("width");
        if (s.length() == 0) imgElem.setAttribute("width", "100%");
        s = e.getAttribute("height");
        if (s.length() == 0) imgElem.setAttribute("height", "100%");
        e.appendChild(imgElem);
        elems.add(imgElem);

        minDim.add(getElementMinPixel(e, bounds));
        maxDim.add(getElementMaxPixel(e, bounds));
    }

    protected Dimension getElementMinPixel(Element e, Rectangle2D bounds) {
        return getElementPixelSize
            (e, BATIK_EXT_MAX_PIXEL_SIZE_ATTRIBUTE, bounds);
    }
    protected Dimension getElementMaxPixel(Element e, Rectangle2D bounds) {
        return getElementPixelSize
            (e, BATIK_EXT_MIN_PIXEL_SIZE_ATTRIBUTE, bounds);
    }

    protected Dimension getElementPixelSize(Element e, 
                                            String attr,
                                            Rectangle2D bounds) {
        String s;
        s = e.getAttribute(attr);
        if (s.length() == 0) return null;

        Float [] vals = SVGUtilities.convertSVGNumberOptionalNumber
            (e, attr, s);

        if (vals[0] == null) return null;

        float xPixSz = vals[0].floatValue();
        float yPixSz = xPixSz;
        if (vals[1] != null)
            yPixSz = vals[1].floatValue();
        
        return new Dimension((int)(bounds.getWidth()/xPixSz+0.5), 
                             (int)(bounds.getHeight()/yPixSz+0.5)); 
    }

    /**
     * A viewport defined an &lt;svg> element.
     */
    public static class MultiImageElementViewport implements Viewport {
        private float width;
        private float height;

        /**
         * Constructs a new viewport with the specified <tt>SVGSVGElement</tt>.
         * @param e the SVGSVGElement that defines this viewport
         * @param w the width of the viewport
         * @param h the height of the viewport
         */
        public MultiImageElementViewport(float w, float h) {
            this.width = w;
            this.height = h;
        }

        /**
         * Returns the width of this viewport.
         */
        public float getWidth(){
            return width;
        }

        /**
         * Returns the height of this viewport.
         */
        public float getHeight(){
            return height;
        }
    }
}
