package org.datanucleus.tests.users;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.jdo.annotations.Cacheable;
import javax.jdo.annotations.Element;
import javax.jdo.annotations.FetchGroup;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;

@PersistenceCapable(table = "UserGroup")
@FetchGroup (name = "deep", members = {
    @Persistent (name = "users"), @Persistent (name = "subGroups") })
@Cacheable("false")
public class UserGroup
    extends W7XDbNamedObject {

    static final long serialVersionUID = 0;

    @Persistent (column = "users", dependentElement = "false", defaultFetchGroup = "true")
    @Element (types = User.class)
    private List<User> users;
    @Persistent (column = "subGroups", dependentElement = "false", defaultFetchGroup = "true")
    @Element (types = UserGroup.class)
    private List<UserGroup> subGroups;

    /**
     * Instantiates a new user group.
     */
    public UserGroup() {
        setDefault();
    }

    public void setDefault() {
        super.setDefault();
        users = new ArrayList<>();
        subGroups = new ArrayList<>();
    }

    @Override
    public boolean equals( Object obj ) {

        if (this == obj)
            return true;
        if (!(obj instanceof UserGroup))
            return false;

        UserGroup g = (UserGroup) obj;

        /* marcl:140212
         * Even if users and subgroups are equal, we have cases where we want to have separate groups. 
         * In my opinion we have to take the sysName for this special case into account, 
         * because it has the same function as the uid of the User class!
         * TODO: add String groupID to differ from sys-Attribute sysName!
         */
        if (!sysName.equals(g.getSysName()))
            return false;

        if (users == null ^ g.users == null)
            return false;
        if (subGroups == null ^ g.subGroups == null)
            return false;

        if (users != null && g.users != null) {
            if (users.size() != g.users.size())
                return false;
            if (!users.containsAll(g.users))
                return false;
            // because duplicate entries are not forbidden - check the other way, too:
            if (!g.users.containsAll(users))
                return false;
        }

        if (subGroups != null && g.subGroups != null) {
            if (subGroups.size() != g.subGroups.size())
                return false;
            if (!subGroups.containsAll(g.subGroups))
                return false;
            // because duplicate entries are not forbidden - check the other way, too:
            if (!g.subGroups.containsAll(subGroups))
                return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int hash = 23;
        if (users != null) {
            for( User user : users) {
                hash = 31 * hash + (user == null ? 13 : user.hashCode());
            }
        }
        if (subGroups != null) {
            for (UserGroup subGroup : subGroups) {
                hash = 13 * hash + (subGroup == null ? 37 : subGroup.hashCode());
            }
        }
        return hash;
    }

    /**
     * Gets the users.
     *
     * @return the users
     */
    public User[] getUsers() {
        if (users == null)
            users = new ArrayList<>();
        return users.toArray(new User[users.size()]);
    }

    /**
     * Sets the users.
     *
     * @param users
     *     the new users
     */
    public void setUsers( User[] users ) {
        this.users = new ArrayList<>();
        this.users.addAll(Arrays.asList(users));
    }

    /**
     * Gets the sub groups.
     *
     * @return the sub groups
     */
    public UserGroup[] getSubGroups() {
        if (subGroups == null)
            subGroups = new ArrayList<>();
        return subGroups.toArray(new UserGroup[subGroups.size()]);
    }

    /**
     * Sets the sub groups.
     *
     * @param subGroups
     *     the new sub groups
     */
    public void setSubGroups( UserGroup[] subGroups ) {
        this.subGroups = new ArrayList<>();
        this.subGroups.addAll(Arrays.asList(subGroups));
    }

    /**
     * Gets the description of this user group.
     *
     * @return the description
     */
    public String getDescription() {
        StringBuilder sb = new StringBuilder(super.toString());
        sb.append(" { users ").append(users);
        sb.append(", subgroups ").append(subGroups).append(" }");
        return sb.toString();
    }
}
