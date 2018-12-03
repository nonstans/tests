/* ----------------------------------------------------------------------------
 * file: W7XDbObject.java
 * project: ConfigDB-BusinessSchema
 * by:  Josef Maier, Marc Lewerentz, Anett Spring
 *
 * Copyright (c) 2004 - 2014, All rights reserved.
 * Max-Planck-Institut für Plasmaphysik. W7-X CoDaC group.
 * ----------------------------------------------------------------------------
 */
package org.datanucleus.tests.users;

/**
 * W7X Database Object
 * extends ooObj
 * all persistent objetcs inherit from this object
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Organisation: IPP</p>
 * @author Josef Maier
 * @version 1.0
 */

import java.io.Serializable;
import java.util.logging.Logger;

public abstract class W7XDbObject
    implements Serializable, Cloneable {

    static final long serialVersionUID = 0;

    protected static final Logger logger = Logger.getLogger("BusinessSchemaLogger");

    /**
     * Clear object content.
     * 
     * Do override this in inherited classes!
     * 
     */
    public void clear() {}

    /**
     * Checks if this object is empty - to compare with null objects in "checkForEqualObjects". <br>
     * TODO this cannot be done generically because of fields to ignore in our business logic!? <br>
     * <br>
     * Do override this in inheriting classes! This method should check whether values of declared
     * attributes of this class are null or empty (Strings, arrays) or 0 (primitive numbers).
     * 
     * @return true, if is empty
     */
    public boolean isEmpty() {
        return false;
    }




}
