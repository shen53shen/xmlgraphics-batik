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

package org.apache.batik.apps.svgbrowser;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.batik.dom.svg.SAXSVGDocumentFactory;
import org.apache.batik.dom.svg.SVGDOMImplementation;
import org.apache.batik.dom.util.DOMUtilities;
import org.apache.batik.dom.util.HashTable;
import org.apache.batik.util.ParsedURL;
import org.apache.batik.util.SVGConstants;
import org.apache.batik.util.XMLResourceDescriptor;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.svg.SVGDocument;

/**
 * This implementation of the <tt>SquiggleInputHandler</tt> class
 * handles XML files by looking for the first
 * &lt;?xml-stylesheet ... ?&gt; processing instruction referencing
 * an xsl document. In case there is one, the transform is applied to the 
 * input XML file and the handler checks that the result is an 
 * SVG document with an SVG root.
 *
 * @author <a mailto="vincent.hardy@sun.com">Vincent Hardy</a>
 * @version $Id$
 */
public class XMLInputHandler implements SquiggleInputHandler {
    public static final String[] XVG_MIME_TYPES = 
    { "image/xml+xsl+svg" };

    public static final String[] XVG_FILE_EXTENSIONS =
    { ".xml", ".xsl" };

    public static final String ERROR_NO_XML_STYLESHEET_PROCESSING_INSTRUCTION
        = "XMLInputHandler.error.no.xml.stylesheet.processing.instruction";

    public static final String ERROR_TRANSFORM_OUTPUT_NOT_SVG
        = "XMLInputHandler.error.transform.output.not.svg";

    public static final String ERROR_TRANSFORM_PRODUCED_NO_CONTENT
        = "XMLInputHandler.error.transform.produced.no.content";

    public static final String ERROR_TRANSFORM_OUTPUT_WRONG_NS
        = "XMLInputHandler.error.transform.output.wrong.ns";

    public static final String ERROR_RESULT_GENERATED_EXCEPTION 
        = "XMLInputHandler.error.result.generated.exception";

    public static final String XSL_PROCESSING_INSTRUCTION_TYPE
        = "text/xsl";

    public static final String PSEUDO_ATTRIBUTE_TYPE
        = "type";

    public static final String PSEUDO_ATTRIBUTE_HREF
        = "href";

    /**
     * Returns the list of mime types handled by this handler.
     */
    public String[] getHandledMimeTypes() {
        return XVG_MIME_TYPES;
    }
    
    /**
     * Returns the list of file extensions handled by this handler
     */
    public String[] getHandledExtensions() {
        return XVG_FILE_EXTENSIONS;
    }

    /**
     * Returns a description for this handler
     */
    public String getDescription() {
        return "";
    }

    /**
     * Returns true if the input file can be handled by the handler
     */
    public boolean accept(File f) {
        return f.isFile() && accept(f.getPath());
    }

    /**
     * Returns true if the input URI can be handled by the handler
     */
    public boolean accept(ParsedURL purl) {
        if (purl == null) {
            return false;
        }

        // <!> Note: this should be improved to rely on Mime Type 
        //     when the http protocol is used. This will use the 
        //     ParsedURL.getContentType method.

        String path = purl.getPath();        
        return accept(path);
    }

    /**
     * Return true if the resource with the given path can 
     * be handled.
     */
    public boolean accept(String path) {
        if (path == null) {
            return false;
        }

        for (int i=0; i<XVG_FILE_EXTENSIONS.length; i++) {
            if (path.endsWith(XVG_FILE_EXTENSIONS[i])) {
                return true;
            }
        }

        return false;
    }

    /**
     * Handles the given input for the given JSVGViewerFrame
     */
    public void handle(ParsedURL purl, JSVGViewerFrame svgViewerFrame) throws Exception {
        String uri = purl.toString();

        TransformerFactory tFactory 
            = TransformerFactory.newInstance();
        
        // First, load the input XML document into a generic DOM tree
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setValidating(false);
        dbf.setNamespaceAware(true);

        DocumentBuilder db = dbf.newDocumentBuilder();

        Document inDoc = db.parse(uri);
       
        // Now, look for <?xml-stylesheet ...?> processing instructions
        String xslStyleSheetURI 
            = extractXSLProcessingInstruction(inDoc);
        
        if (xslStyleSheetURI == null) {
            // Assume that the input file is a literal result template
            xslStyleSheetURI = uri;
        }

        ParsedURL parsedXSLStyleSheetURI 
            = new ParsedURL(uri, xslStyleSheetURI);

        Transformer transformer
            = tFactory.newTransformer
            (new StreamSource(parsedXSLStyleSheetURI.toString()));

        // Set the URIResolver to properly handle document() and xsl:include
        transformer.setURIResolver
            (new DocumentURIResolver(parsedXSLStyleSheetURI.toString()));

        // Now, apply the transformation to the input document.
        DOMImplementation impl = SVGDOMImplementation.getDOMImplementation();
        String svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI;

        //
        // <!> Due to issues with namespaces, the transform creates the 
        //     result in a stream which is parsed. This is sub-optimal
        //     but this was the only solution found to be able to 
        //     generate content in the proper namespaces.
        //
        // SVGOMDocument outDoc = 
        //   (SVGOMDocument)impl.createDocument(svgNS, "svg", null);
        // outDoc.setURLObject(new URL(uri));
        // transformer.transform
        //     (new DOMSource(inDoc),
        //     new DOMResult(outDoc.getDocumentElement()));
        //
        StringWriter sw = new StringWriter();
        StreamResult result = new StreamResult(sw);
        transformer.transform(new DOMSource(inDoc),
                              result);
        sw.flush();
        sw.close();

        String parser = XMLResourceDescriptor.getXMLParserClassName();
        SAXSVGDocumentFactory f = new SAXSVGDocumentFactory(parser);
        SVGDocument outDoc = null;

        try {
            outDoc = f.createSVGDocument
                (uri, new StringReader(sw.toString()));
        } catch (Exception e) {
            System.err.println("======================================");
            System.err.println(sw.toString());
            System.err.println("======================================");
            
            throw new IllegalArgumentException
                (Resources.getString(ERROR_RESULT_GENERATED_EXCEPTION));
        }

        // Patch the result tree to go under the root node
        // checkAndPatch(outDoc);
        
        svgViewerFrame.getJSVGCanvas().setSVGDocument((SVGDocument)outDoc);
        svgViewerFrame.setSVGDocument(outDoc,
                                      uri,
                                      outDoc.getTitle());
    }

    /**
     * This method checks that the generated content is SVG.
     *
     * This method accounts for the fact that the root svg's first child
     * is the result of the transform. It moves all its children under the root
     * and sets the attributes
     */
    protected void checkAndPatch(Document doc) {
        Element root = doc.getDocumentElement();
        Node realRoot = root.getFirstChild();
        String svgNS = SVGDOMImplementation.SVG_NAMESPACE_URI;

        if (realRoot == null) {
            throw new IllegalArgumentException
                (Resources.getString(ERROR_TRANSFORM_PRODUCED_NO_CONTENT));
        }

        if (realRoot.getNodeType() != realRoot.ELEMENT_NODE
            || 
            !SVGConstants.SVG_SVG_TAG.equals(realRoot.getLocalName())) {
            throw new IllegalArgumentException
                (Resources.getString(ERROR_TRANSFORM_OUTPUT_NOT_SVG));
        }

        if (!svgNS.equals(realRoot.getNamespaceURI())) {
            throw new IllegalArgumentException
                (Resources.getString(ERROR_TRANSFORM_OUTPUT_WRONG_NS));
        }

        Node child = realRoot.getFirstChild();
        while ( child != null ) {
            root.appendChild(child);
            child = realRoot.getFirstChild();
        }

        NamedNodeMap attrs = realRoot.getAttributes();
        int n = attrs.getLength();
        for (int i=0; i<n; i++) {
            root.setAttributeNode((Attr)attrs.item(i));
        }

        root.removeChild(realRoot);
    }

    /**
     * Extracts the first XSL processing instruction from the input 
     * XML document. 
     */
    protected String extractXSLProcessingInstruction(Document doc) {
        Node child = doc.getFirstChild();
        while (child != null) {
            if (child.getNodeType() == child.PROCESSING_INSTRUCTION_NODE) {
                ProcessingInstruction pi 
                    = (ProcessingInstruction)child;
                
                HashTable table = new HashTable();
                DOMUtilities.parseStyleSheetPIData(pi.getData(),
                                                   table);

                Object type = table.get(PSEUDO_ATTRIBUTE_TYPE);
                if (XSL_PROCESSING_INSTRUCTION_TYPE.equals(type)) {
                    Object href = table.get(PSEUDO_ATTRIBUTE_HREF);
                    if (href != null) {
                        return href.toString();
                    } else {
                        return null;
                    }
                }
            }
            child = child.getNextSibling();
        }

        return null;
    }

    /**
     * Implements the URIResolver interface so that relative urls used in 
     * transformations are resolved properly.
     */
    public class DocumentURIResolver implements URIResolver {
        String documentURI;

        public DocumentURIResolver(String documentURI) {
            this.documentURI = documentURI;
        }

        public Source resolve(String href, String base) {
            if (base == null || "".equals(base)) {
                base = documentURI;
            }

            ParsedURL purl = new ParsedURL(base, href);

            return new StreamSource(purl.toString());
        }
    }
}
