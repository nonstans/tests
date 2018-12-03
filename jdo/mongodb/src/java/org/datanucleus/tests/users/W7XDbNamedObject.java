package org.datanucleus.tests.users;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.StringTokenizer;
import java.util.TimeZone;
import javax.jdo.annotations.Discriminator;
import javax.jdo.annotations.DiscriminatorStrategy;
import javax.jdo.annotations.Extension;
import javax.jdo.annotations.FetchGroup;
import javax.jdo.annotations.FetchGroups;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.Inheritance;
import javax.jdo.annotations.InheritanceStrategy;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.datanucleus.tests.users.W7XDbNamedObject.Id;

@PersistenceCapable (detachable = "true", identityType = IdentityType.APPLICATION, objectIdClass = Id.class, table = "W7XDbNamedObject")
@Discriminator (strategy = DiscriminatorStrategy.CLASS_NAME)
@Inheritance (strategy = InheritanceStrategy.COMPLETE_TABLE)
@FetchGroups ({
                  @FetchGroup (name = "PKonly", members = {
                      @Persistent (name = "sysId"), @Persistent (name = "className") }),
                  @FetchGroup (name = "MetaInfo", members = {
                      @Persistent (name = "sysId"),
                      @Persistent (name = "className"),
                      @Persistent (name = "sysName"),
                      @Persistent (name = "sysText"),
                      @Persistent (name = "sysState"),
                      @Persistent (name = "sysOwner"),
                      @Persistent (name = "sysEditInfo"),
                      @Persistent (name = "sysFlags"),
                      @Persistent (name = "sysVersionInfo") }) })

public abstract class W7XDbNamedObject
    extends W7XDbObject
    implements Serializable, Comparable<W7XDbNamedObject> {

    /**
     * The Constant METAATTRIBUTES.
     */
    public static final String METAATTRIBUTES =
        "sysId, className, sysName, sysText, sysState, sysOwner, sysEditInfo, sysFlags, sysVersionInfo ";
    static final long serialVersionUID = 0;
    /**
     * constants for sysFlags
     */
    private static final int PROTECTED = 1;
    @SuppressWarnings ("unused")
    private static final int INPOOL = 2;
    @SuppressWarnings ("unused")
    private static final int DELETEFROMPOOL = 4;
    private static final int NOTFORREUSE = 16;
    /** name set by XDV */
    @Persistent
    //    @Index(name = "sysNameIdx") // does not help in concrete classes!?
    protected String sysName = "";
    /** text, contains comment set by XDV */
    @Persistent
    protected String sysText = "";
    /** state, contains status information set by XDV */
    @Persistent
    protected int sysState = 0;
    /**
     * owner set by XDV; this field contains the sysId of the UserGroup and determines the access
     * rights for this object
     */
    @Persistent
    protected int sysOwner = 0;
    /**
     * The sysEditInfo field contains information set by XDV-Editor. This field is amended by
     * objyaccess write methods This field is typically used to store user-name and timestamp of the
     * last change to this object or its related configuration-database object.
     */
    @Persistent
    protected String sysEditInfo = "";
    /**
     * Zusammenfassung der Bits in sysFlags:
     *
     * Bit 0 - PROTECT (Objektinstanz darf im Arbeitsspeicher/Datenbank nicht gelöscht werden)
     *
     * Bit 1 - INPOOL (Eine Instanz des Objekts soll im Arbeitsspeicher angelegt werden)
     *
     * Bit 2 - DELETEFROMPOOL (Eine Instanz des Objekts im Arbeitsspeicher kann gelöscht werden)
     *
     * Bit 3 - Reseviert für EXTRA_FEASIBILITY_CHECK( braucht die FCS, ist noch nicht ganz
     * durchgestaltet )
     *
     * Bit 4 - NOT_FOR_REUSE (keine Wiederverwendung erlaubt)
     *
     * Bit 5 .. Bit 30 - können noch mit einer Bedeutung belegt werden
     *
     * Bit 31 - IS_DUMMY (Wird von der Datenbankschnittstelle FCS <-> josDaemon verwendet)
     */
    @Persistent
    protected int sysFlags = 0;
    /**
     * The sysVersionInfo field contains information set by XDV-objyaccess. This field is set by
     * objyaccess write methods This field is compared on objyaccess modify methods to support
     * optimistic locking This field is typically used to store user-name, timestamp and version
     * number of the last change to this object or its related configuration-database object.
     */
    @Persistent
    protected String sysVersionInfo = "";
    /**
     * Numeric part of the identifier for the W7XDbNamedObject. (Actual classname is the other
     * part.) This identifier is set (by XDV software) when a new W7XDbNamedObject is created in the
     * configuration database; it is not the objectivity ObjectId. In the (measured-data-)
     * archive(s) it only acts as reference back to the object in the configuration database. For
     * archived signals without associated configuration object (for example, data not measured by
     * CoDaC systems), sysId can be zero.
     */
    @PrimaryKey (column = "sysId")
    @Persistent (valueStrategy = IdGeneratorStrategy.INCREMENT, extensions = {
        @Extension (vendorName = "datanucleus", key = "key-cache-size", value = "1"),
        @Extension (vendorName = "datanucleus", key = "key-initial-value", value = "10000"),
        @Extension (vendorName = "datanucleus", key = "strategy-when-notnull", value = "false") })
    //    @Index(name = "sysIdIdx") // does not help in concrete classes!?
    private Integer sysId = null;
    @PrimaryKey (column = "className")
    private String className = this.getClass().getSimpleName();

    /**
     * Field access method to set persistent field id.
     *
     * @param id
     *     the new sys id
     *
     *     usually sysId is set by DB - use {@link #resetSysId()} to clear
     *
     *     for some admin processes this is still needed
     */

    public void setSysId( int id ) {
        this.sysId = id;
    }

    /**
     * Field access method to get persistent field id.
     *
     * @return the sys id
     */
    public int getSysId() {
        return sysId == null ? 0 : this.sysId;
    }

    /**
     * Field access method to set persistent field id.
     *
     * @param id
     *     the new sys id
     *
     *     usually sysId is set by DB - use {@link #resetSysId()} to clear
     *
     *     for some admin processes this is still needed
     */
    public void setSysId( Integer id ) {
        this.sysId = id;
    }

    /**
     * Reset sysId - to force generating a new ID when storing to DB. Used to mark an object as new.
     */
    public void resetSysId() {
        sysId = null;
    }

    /**
     * Field access method to get persistent field name.
     *
     * @return the sys name
     */
    public String getSysName() {
        return this.sysName;
    }

    /**
     * Field access method to set persistent field name.
     *
     * @param name
     *     the new sys name
     */
    public void setSysName( String name ) {
        this.sysName = name;
    }

    /**
     * Field access method to get persistent field text.
     *
     * @return the sys text
     */
    public String getSysText() {
        return this.sysText;
    }

    /**
     * Field access method to set persistent field text.
     *
     * @param text
     *     the new sys text
     */
    public void setSysText( String text ) {
        this.sysText = text;
    }

    /**
     * Reset sysState to 0.
     */
    public void resetSysState() {
        this.sysState = 0;
    }

    /**
     * <br>
     * Field access method to get persistent field state.
     *
     * @return the sys state
     */
    public int getSysState() {
        return this.sysState;
    }

    /**
     * Field access method to set persistent field state.
     *
     * @param state
     *     the new sys state
     */
    public void setSysState( int state ) {
        this.sysState = state;
    }

    /**
     * <br>
     * Field access method to get persistent field owner.
     *
     * @return the sys owner
     */
    public int getSysOwner() {
        return this.sysOwner;
    }

    /**
     * <br>
     * Field access method to set persistent field owner.
     *
     * @param owner
     *     the new sys owner
     */
    public void setSysOwner( int owner ) {
        this.sysOwner = owner;
    }

    /**
     * Sets the editing information in persistent field sysEditInfo. The editing information
     * consists of user-name and current timestamp (timestamp of the last change to this object or
     * its related configuration-database object)
     *
     * @param userName
     *     the user
     */
    public void setSysEditInfoForUser( String userName ) {
        Objects.requireNonNull(userName, "userName must not be null");
        if (userName.isEmpty()) {
            logger.severe(
                "Wrong usage: set sysEditInfoForUser with empty username - use 'resetSysEditInfo' - will reset sysEditInfo");
            resetSysEditInfo(); // hier besser eine Exception werfen und auf resetSysEditInfo verweisen
            return;
        }
        if (userName.contains(";")) {
            // preliminaryly check whether userName is only the user (new) or the complete sysEditInfo string (old)
            this.sysEditInfo = userName;
            logger.severe("Wrong usage: set sysEditInfoForUser with '"
                          + userName
                          + "' instead of a username");
        } else {
            // get formatted UTC time
            String modificationDate = getUTCnow();
            // set user and modification date
            this.sysEditInfo = userName + ";" + modificationDate;
        }
    }

    /**
     * Gets the formatted UTC time now.
     *
     * @return the UT cnow
     */
    private String getUTCnow() {
        DateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy HH:mm 'UTC'");
        dateFormatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        return dateFormatter.format(new Date().getTime());
    }

    /**
     * Reset sysEditInfo to emtpy string.
     */
    public void resetSysEditInfo() {
        sysEditInfo = "";
    }

    /**
     * Gets the sysEditInfo (unformatted).
     *
     * @return the sysEditInfo
     */
    public String getSysEditInfo() {
        return sysEditInfo;
    }

    /**
     * Sets the sysEditInfo.
     *
     * @param sysEditInfo
     *     the new sysEditInfo
     */
    public void setSysEditInfo( String sysEditInfo ) {
        this.sysEditInfo = sysEditInfo;
    }

    /**
     * <br>
     * Field access method to get persistent field sysFlags.
     *
     * @return the sys flags
     */
    public int getSysFlags() {
        return this.sysFlags;
    }

    /**
     * <br>
     * Field access method to set persistent field sysFlags.
     *
     * @param flags
     *     the new sys flags
     */
    public void setSysFlags( int flags ) {
        this.sysFlags = flags;
    }

    /**
     * <br>
     * Field access method to test persistent field PROTECTED in sysFlags.
     *
     * @return true, if is protected
     */
    public boolean isProtected() {
        if ((this.sysFlags & PROTECTED) == 0)
            return false;
        else
            return true;
    }

    /**
     * <br>
     * Field access method to set/reset persistent field PROTECTED in sysFlags.
     *
     * @param b
     *     the new protected
     */
    public void setProtected( boolean b ) {
        if (b)
            this.sysFlags |= PROTECTED;
        else
            this.sysFlags &= ~PROTECTED;
    }

    /**
     * <br>
     * Field access method to test persistent field PROTECTED in sysFlags.
     *
     * @return true, if is notforreuse
     */
    public boolean isNotforreuse() {
        if ((this.sysFlags & NOTFORREUSE) == 0)
            return false;
        else
            return true;
    }

    /**
     * <br>
     * Field access method to set/reset persistent field NOTFORREUSE in sysFlags.
     *
     * @param b
     *     the new notforreuse
     */
    public void setNotforreuse( boolean b ) {
        if (b)
            this.sysFlags |= NOTFORREUSE;
        else
            this.sysFlags &= ~NOTFORREUSE;
    }

    /**
     * For interface Comparable, here used to facilitate sorting by sysName.
     *
     * @param o
     *     the object
     * @return BEFORE = -1; EQUAL = 0; AFTER = 1
     */
    @Override
    public int compareTo( W7XDbNamedObject o ) {
        return this.getSysName().compareTo(o.getSysName());
    }

    /**
     * Sets the default.
     */
    public void setDefault() {
        /*
         * !Do not reset any sysXXX parameters because they contain meta information
         * about the object not to be deleted or reset!
         */
    }

    /**
     * Gets the objects version info for optimistic locking.
     *
     * @return the object version info as String
     */
    public String getSysVersionInfo() {
        if (this.sysVersionInfo == null)
            return "no sysVersionInfo available";
        return this.sysVersionInfo;
    }

    /**
     * Sets the sys version info.
     *
     * @param sysVersionInfo
     *     the new sys version info
     */
    public final void setSysVersionInfo( String sysVersionInfo ) {
        this.sysVersionInfo = sysVersionInfo;
    }

    /**
     * Sets the objects version info for optimistic locking.
     */
    private void setSysVersionInfo() {
        int writeVersion = 0;
        String writeUser = "writeUser";
        String writeDate = "writeDate";

        // get formatted write time
        writeDate = getUTCnow();
        // get write user name
        try {
            writeUser = System.getProperty("user.name", "unknown write username");
        } catch (Exception e) {
        }
        // determine new version number;
        try {
            StringTokenizer st = new StringTokenizer(this.sysVersionInfo, ";");
            try {
                if ((st.countTokens() == 3)) {
                    writeVersion = Integer.parseInt(st.nextToken()) + 1;
                }
            } catch (NumberFormatException e) {
                // e.printStackTrace();
            }
        } catch (Exception e) {
            // e.printStackTrace();
        }
        this.sysVersionInfo = writeVersion + ";" + writeUser + ";" + writeDate;
    }

    /**
     * Gets the class name.
     *
     * @return the class name
     */
    public String getClassName() {
        return className;
    }

    /**
     * @author marcl
     */
    public static class Id
        implements Serializable {

        private static final long serialVersionUID = -3410881584863686048L;
        public Integer sysId;
        public String className;
        @NotPersistent
        public String targetClassName;

        /**
         * Instantiates a new id.
         */
        public Id() {
        }

        /**
         * Instantiates a new id.
         *
         * @param str
         *     the str
         */
        public Id( String str ) {
            StringTokenizer token = new StringTokenizer(str, ".");
            this.className = token.nextToken();
            this.sysId = Integer.valueOf(token.nextToken());
            this.targetClassName = "org.datanucleus.tests.users." + this.className;
        }

        @Override
        public String toString() {
            return this.className + "." + this.sysId;
        }

        @Override
        // TODO equality by sysId and className - does this make sense?
        // Have to have in mind when adding W7xDbNamedObjects to Sets etc.!
        public boolean equals( Object o ) {
            if (this == o) {
                return true;
            }
            if (o == null) {
                return false;
            }
            if (o.getClass() != getClass()) {
                return false;
            }
            Id objToCompare = (Id) o;
            return ((this.sysId.equals(objToCompare.sysId)) && (this.className == null ?
                objToCompare.className == null :
                this.className.equals(objToCompare.className)));
        }

        @Override
        public int hashCode() {
            return this.sysId ^ this.className.hashCode();
        }

    }

}
