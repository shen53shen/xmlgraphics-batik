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

package org.apache.batik.ext.awt.image.renderable;

/**
 * Implements a Morphology operation, where the kernel size is
 * defined by radius along the x and y axis.
 *
 * @author <a href="mailto:sheng.pei@eng.sun.com>Sheng Pei</a>
 * @version $Id$
 */
public interface MorphologyRable extends Filter {
    /**
     * Returns the source to be offset.
     */
    public Filter getSource();

    /**
     * Sets the source to be offset.
     * @param src image to offset.
     */
    public void setSource(Filter src);

    /**
     * The radius along the x axis, in user space.
     * @param radiusX should be greater than zero.
     */
    public void setRadiusX(double radiusX);

    /**
     * The radius along the y axis, in user space.
     * @param radiusY should be greater than zero.
     */
    public void setRadiusY(double radiusY);

    /**
     * The switch that determines if the operation
     * is to "dilate" or "erode".
     * @param doDilation do "dilation" when true and "erosion" when false
     */
    public void setDoDilation(boolean doDilation);

    /**
     * Returns whether the operation is "dilation" or not("erosion")
     */
    public boolean getDoDilation();

    /**
     * Returns the radius along the x-axis, in user space.
     */
    public double getRadiusX();

    /**
     * Returns the radius along the y-axis, in user space.
     */
    public double getRadiusY();
}
