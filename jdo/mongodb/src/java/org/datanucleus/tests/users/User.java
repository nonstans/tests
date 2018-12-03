package org.datanucleus.tests.users;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.Unique;

@PersistenceCapable (table = "User")
public class User extends W7XDbNamedObject{

    static final long serialVersionUID = 0;

    @Persistent (column = "uid")
    @Unique
    private String uid;

    /**
     * Instantiates a new user.
     */
    public User() {

    }



    /**
     * Gets the uid.
     *
     * @return the uid
     */
    public String getUid() {
        return uid;
    }

    @Override
    public boolean equals( Object obj ) {
        if (this == obj)
            return true;
        return obj instanceof User && ((User) obj).uid.equals(uid);
    }

    @Override
    public int hashCode() {
        return uid.hashCode();
    }

    /**
     * Sets the user id.
     *
     * @param uid
     *     the new uid
     */
    public void setUid( String uid ) {
        this.uid = uid;
    }

}
