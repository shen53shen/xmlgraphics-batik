/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package org.apache.batik.svggen;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.geom.Point2D;
import java.awt.geom.AffineTransform;
import java.util.Stack;

import org.apache.batik.ext.awt.g2d.GraphicContext;
import org.apache.batik.ext.awt.g2d.TransformType;
import org.apache.batik.ext.awt.g2d.TransformStackElement;

/**
 * Utility class that converts a GraphicContext transform stack
 * into an SVG transform attribute.
 *
 * @author <a href="mailto:vincent.hardy@eng.sun.com">Vincent Hardy</a>
 * @author <a href="mailto:paul_evenblij@compuware.com">Paul Evenblij</a>
 * @version $Id$
 */
public class SVGTransform extends AbstractSVGConverter{
    /**
     * Ratio used to convert radians to degrees
     */
    private static double radiansToDegrees = 180.0 / Math.PI;
    /**
     * Converts part or all of the input GraphicContext into
     * a set of attribute/value pairs and related definitions
     *
     * @param gc GraphicContext to be converted
     * @return descriptor of the attributes required to represent
     *         some or all of the GraphicContext state, along
     *         with the related definitions
     * @see org.apache.batik.svggen.SVGDescriptor
     */
    public SVGDescriptor toSVG(GraphicContext gc){
        return new SVGTransformDescriptor(toSVGTransform(gc));
    }

    /**
     * @param gc GraphicContext whose transform stack should be converted
     *           to SVG.
     * @return the value of an SVG attribute equivalent to the input
     *         GraphicContext's transform stack.
     */
    public static String toSVGTransform(GraphicContext gc){
        return toSVGTransform(gc.getTransformStack());
    }

    /**
     * This method tries to collapse the transform stack into an SVG
     * string as compact as possible while still conveying the semantic
     * of the stack. Successive stack elements of the same kind (e.g., two
     * successive transforms or scales) are collapsed into a single element.
     *
     * @param transformStack sequence of transform that should
     *        be converted to an SVG transform attribute equivalent
     */
    public static String toSVGTransform(TransformStackElement transformStack[]){
        StringBuffer transformStackBuffer = new StringBuffer();
        int nTransforms = transformStack.length;
        //
        // Append transforms in the presentation stack
        //
        Stack presentation = new Stack() {
            /**
             * Adapted push implementation
             */
            public Object push(Object o) {
                Object element;
                if(((TransformStackElement)o).isIdentity()) {
                    // identity transform: don't push,
                    // and try to return top of stack
                    element = pop();
                } else {
                    // non-identity: push,
                    // and return null
                    super.push(o);
                    element = null;
                }
                return element;
            }
            
            /**
             * Adapted pop implementation
             */
            public Object pop() {
                Object element = null;
                if(!super.empty()) {
                    element = super.pop();
                }
                return element;
            }
        };
        boolean canConcatenate = false;
        int i = 0, j = 0, next = 0;
        TransformStackElement element = null;

        // We keep a separate 'presentation' stack, which contains
        // all concatenated elements. The top of this stack is the
        // element we try to concatenate onto. If this element
        // becomes an identity transform, we discard it and look at
        // the element underneath it instead.
        // The presentation stack is guaranteed not to contain
        // identity transforms.

        while(i < nTransforms) {

            // If we do not have an element to concatenate onto,
            // we grab one here.
            next = i;
            if(element == null) {
                element = (TransformStackElement) transformStack[i].clone();
                next++;
            }

            // try to concatenate as much as possible
            canConcatenate = true;
            for(j = next; j < nTransforms; j++) {
                canConcatenate = element.concatenate(transformStack[j]);
                if(!canConcatenate)
                    break;
            }
            // loop variable assertion: 
            // If "i" does not increment during this iteration, it is guaranteed
            // to do so in the next, since "i" can only keep the same value as a
            // result of "element" having a non-null value on starting this
            // iteration, which can only be the case if it was popped from the
            // stack during the previous one. The stack does not contain
            // identities, and since "i" has not grown, "element" has remained
            // unchanged and will be pushed onto the stack again. "element" will
            // then become null, so "i" will eventually increment.
            i = j;
 
            // Get rid of identity transforms within the stack.
            // If an identity is pushed, it is immediately removed, and
            // the current top of stack will be returned to concatenate onto.
            // Otherwise, null will be returned.
            element = (TransformStackElement) presentation.push(element);
        }
 
        //
        // Transform presentation stack to SVG
        //
        int nPresentations = presentation.size();
        
        for(i = 0; i < nPresentations; i++) {
            transformStackBuffer.append(convertTransform((TransformStackElement) presentation.elementAt(i)));
            transformStackBuffer.append(SPACE);
        }

        String transformValue = transformStackBuffer.toString().trim();
        return transformValue;
    }

    /**
     * Converts an AffineTransform to an SVG transform string
     */
    static String convertTransform(TransformStackElement transformElement){
        StringBuffer transformString = new StringBuffer();
        double transformParameters[] = transformElement.getTransformParameters();
        switch(transformElement.getType().toInt()){
        case TransformType.TRANSFORM_TRANSLATE:
            if(!transformElement.isIdentity()) {
                transformString.append(TRANSFORM_TRANSLATE);
                transformString.append(OPEN_PARENTHESIS);
                transformString.append(doubleString(transformParameters[0]));
                transformString.append(COMMA);
                transformString.append(doubleString(transformParameters[1]));
                transformString.append(CLOSE_PARENTHESIS);
            }
            break;
        case TransformType.TRANSFORM_ROTATE:
            if(!transformElement.isIdentity()) {
                transformString.append(TRANSFORM_ROTATE);
                transformString.append(OPEN_PARENTHESIS);
                transformString.append(doubleString(radiansToDegrees*transformParameters[0]));
                transformString.append(CLOSE_PARENTHESIS);
            }
            break;
        case TransformType.TRANSFORM_SCALE:
            if(!transformElement.isIdentity()) {
                transformString.append(TRANSFORM_SCALE);
                transformString.append(OPEN_PARENTHESIS);
                transformString.append(doubleString(transformParameters[0]));
                transformString.append(COMMA);
                transformString.append(doubleString(transformParameters[1]));
                transformString.append(CLOSE_PARENTHESIS);
            }
            break;
        case TransformType.TRANSFORM_SHEAR:
            if(!transformElement.isIdentity()) {
                transformString.append(TRANSFORM_MATRIX);
                transformString.append(OPEN_PARENTHESIS);
                transformString.append(1);
                transformString.append(COMMA);
                transformString.append(doubleString(transformParameters[1]));
                transformString.append(COMMA);
                transformString.append(doubleString(transformParameters[0]));
                transformString.append(COMMA);
                transformString.append(1);
                transformString.append(COMMA);
                transformString.append(0);
                transformString.append(COMMA);
                transformString.append(0);
                transformString.append(CLOSE_PARENTHESIS);
            }
            break;
        case TransformType.TRANSFORM_GENERAL:
            if(!transformElement.isIdentity()) {
                transformString.append(TRANSFORM_MATRIX);
                transformString.append(OPEN_PARENTHESIS);
                transformString.append(doubleString(transformParameters[0]));
                transformString.append(COMMA);
                transformString.append(doubleString(transformParameters[1]));
                transformString.append(COMMA);
                transformString.append(doubleString(transformParameters[2]));
                transformString.append(COMMA);
                transformString.append(doubleString(transformParameters[3]));
                transformString.append(COMMA);
                transformString.append(doubleString(transformParameters[4]));
                transformString.append(COMMA);
                transformString.append(doubleString(transformParameters[5]));
                transformString.append(CLOSE_PARENTHESIS);
            }
            break;
        default:
            // This should never happen. If it does, there is a
            // serious error.
            throw new Error();
        }

        return transformString.toString();
    }
}
