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

package org.apache.batik.gvt;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.batik.gvt.event.GraphicsNodeChangeAdapter;
import org.apache.batik.gvt.event.GraphicsNodeChangeEvent;
import org.apache.batik.ext.awt.image.renderable.Filter;
/**
 * This class tracks the changes on a GVT tree
 *
 * @author <a href="mailto:Thomas.DeWeeese@Kodak.com">Thomas DeWeese</a>
 * @version $Id$
 */
public class UpdateTracker extends GraphicsNodeChangeAdapter {

    Map dirtyNodes = null;
    Map fromBounds = new HashMap();

    public UpdateTracker(){
    }
    
    /**
     * Tells whether the GVT tree has changed.
     */
    public boolean hasChanged() {
        return (dirtyNodes != null);
    }

    /**
     * Returns the list of dirty areas on GVT.
     */
    public List getDirtyAreas() {
        if (dirtyNodes == null) 
            return null;

        List ret = new LinkedList();
        Set keys = dirtyNodes.keySet();
        Iterator i = keys.iterator();
        while (i.hasNext()) {
            WeakReference gnWRef = (WeakReference)i.next();
            GraphicsNode  gn     = (GraphicsNode)gnWRef.get();
            // GraphicsNode  srcGN  = gn;

            // if the weak ref has been cleared then this node is no
            // longer part of the GVT tree (and the change should be
            // reflected in some ancestor that should also be in the
            // dirty list).
            if (gn == null) continue;

            AffineTransform oat;
            oat = (AffineTransform)dirtyNodes.get(gnWRef);
            if (oat != null){
                oat = new AffineTransform(oat);
            }
            
            Rectangle2D srcORgn = (Rectangle2D)fromBounds.remove(gnWRef);

            Rectangle2D srcNRgn = null;
            AffineTransform nat = null;
            if (!(srcORgn instanceof ChngSrcRect)) {
                // For change srcs don't use the new bounds of parent node.
                srcNRgn = gn.getBounds();
                nat = gn.getTransform();
                if (nat != null)
                    nat = new AffineTransform(nat);
            }


            // System.out.println("Rgns: " + srcORgn + " - " + srcNRgn);
            // System.out.println("ATs: " + oat + " - " + nat);
            do {
                // f.invalidateCache(oRng);
                // f.invalidateCache(nRng);

                // f = gn.getEnableBackgroundGraphicsNodeRable(false);
                // (need to push rgn through filter chain if any...)
                // f.invalidateCache(oRng);
                // f.invalidateCache(nRng);

                gn = gn.getParent();
                if (gn == null)
                    break; // We reached the top of the tree

                Filter f= gn.getFilter();
                if ( f != null) {
                    srcNRgn = f.getBounds2D();
                    nat = null;
                }

                // Get the parent's current Affine
                AffineTransform at = gn.getTransform();
                // Get the parent's Affine last time we rendered.
                gnWRef = gn.getWeakReference();
                AffineTransform poat = (AffineTransform)dirtyNodes.get(gnWRef);
                if (poat == null) poat = at;
                if (poat != null) {
                    if (oat != null)
                        oat.preConcatenate(poat);
                    else 
                        oat = new AffineTransform(poat);
                }

                if (at != null){
                    if (nat != null)
                        nat.preConcatenate(at);
                    else
                        nat = new AffineTransform(at);
                }
            } while (true);

            if (gn == null) {
                // We made it to the root graphics node so add them.
                // System.out.println
                //      ("Adding: " + oat + " - " + nat + "\n" +
                //       srcORgn + "\n" + srcNRgn + "\n");
                // <!>
                Shape oRgn = srcORgn;
                if (oRgn != null) {
                    if (oat != null)
                        oRgn = oat.createTransformedShape(srcORgn);
                    // System.err.println("GN: " + srcGN);
                    // System.err.println("Src: " + oRgn.getBounds2D());
                    ret.add(oRgn);
                }
                
                if (srcNRgn != null) {
                    Shape nRgn = srcNRgn;
                    if (nat != null)
                        nRgn = nat.createTransformedShape(srcNRgn);
                    if (nRgn != null)
                        ret.add(nRgn);
                }
            }
        }
        return ret;
    }

    /**
     * This returns the dirty region for gn in the coordinate system
     * given by <code>at</at>.
     * @param gn Node tree to return dirty region for.
     * @param at Affine transform to coordinate space to accumulate
     *           dirty regions in.
     */
    public Rectangle2D getNodeDirtyRegion(GraphicsNode gn, 
                                          AffineTransform at) {
        WeakReference gnWRef = gn.getWeakReference();
        AffineTransform nat = (AffineTransform)dirtyNodes.get(gnWRef);
        if (nat == null) nat = gn.getTransform();
        if (nat != null) {
            at = new AffineTransform(at);
            at.concatenate(nat);
        }

        Rectangle2D ret = null;
        if (gn instanceof CompositeGraphicsNode) {
            CompositeGraphicsNode cgn = (CompositeGraphicsNode)gn;
            Iterator iter = cgn.iterator();

            while (iter.hasNext()) {
                GraphicsNode childGN = (GraphicsNode)iter.next();
                Rectangle2D r2d = getNodeDirtyRegion(childGN, at);
                if (r2d != null) {
                    if (ret == null) ret = r2d;
                    else ret = ret.createUnion(r2d);
                }
            }
        } else {
            ret = (Rectangle2D)fromBounds.remove(gnWRef);
            if (ret == null) 
                ret = gn.getBounds();
            if (ret != null)
                ret = at.createTransformedShape(ret).getBounds2D();
        }
        return ret;
    }

    public Rectangle2D getNodeDirtyRegion(GraphicsNode gn) {
        return getNodeDirtyRegion(gn, new AffineTransform());
    }

    /**
     * Recieves notification of a change to a GraphicsNode.
     * @param gn The graphics node that is changing.
     */
    public void changeStarted(GraphicsNodeChangeEvent gnce) {
        // System.out.println("A node has changed for: " + this);
        GraphicsNode gn = gnce.getGraphicsNode();
        WeakReference gnWRef = gn.getWeakReference();

        boolean doPut = false;
        if (dirtyNodes == null) {
            dirtyNodes = new HashMap();
            doPut = true;
        } else if (!dirtyNodes.containsKey(gnWRef)) 
            doPut = true;

        if (doPut) {
            AffineTransform at = gn.getTransform();
            if (at != null) at = (AffineTransform)at.clone();
            else            at = new AffineTransform();
            dirtyNodes.put(gnWRef, at);
        }

        GraphicsNode chngSrc = gnce.getChangeSrc();
        Rectangle2D rgn = null;
        if (chngSrc != null) {
            // A child node is moving in the tree so assign it's dirty
            // regions to this node before it moves.
            Rectangle2D drgn = getNodeDirtyRegion(chngSrc);
            if (drgn != null)
                rgn = new ChngSrcRect(drgn);
        } else {
            // Otherwise just use gn's current region.
            rgn = gn.getBounds();
        }
        // Add this dirty region to any existing dirty region.
        Rectangle2D r2d = (Rectangle2D)fromBounds.remove(gnWRef);
        if (rgn != null) {
            if (r2d != null) {
                // System.err.println("GN: " + gn);
                // System.err.println("R2d: " + r2d);
                // System.err.println("Rgn: " + rgn);
                r2d = r2d.createUnion(rgn);
                // System.err.println("Union: " + r2d);
            }
            else             r2d = rgn;
        }

        // if ((gn instanceof CompositeGraphicsNode) && 
        //     (r2d.getWidth() > 200)) {
        //     new Exception("Adding Large: " + gn).printStackTrace();
        // }

        // Store the bounds for the future.
        fromBounds.put(gnWRef, r2d);
    }

    class ChngSrcRect extends Rectangle2D.Float {
        ChngSrcRect(Rectangle2D r2d) {
            super((float)r2d.getX(), (float)r2d.getY(), 
                  (float)r2d.getWidth(), (float)r2d.getHeight());
        }
    }

    /**
     * Clears the tracker.
     */
    public void clear() {
        dirtyNodes = null;
    }
}
