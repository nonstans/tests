package org.datanucleus.tests;

import java.util.List;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.Transaction;
import org.junit.Assert;
import org.junit.Test;

import org.datanucleus.tests.users.User;
import org.datanucleus.tests.users.UserGroup;

public class ResolveReferencesTest
    extends JDOPersistenceTestCase {

    public ResolveReferencesTest( String name ) {
        super(name);
    }


    @Test
    public void modifyNonCacheableObject() {
        List<?> result = null;

        PersistenceManager pm = pmf.getPersistenceManager();
        pm.setDetachAllOnCommit(true);
        Transaction tx = pm.currentTransaction();

        UserGroup userGroup = new UserGroup(); //has annotation @Cacheable("false")
        User user = new User();
        user.setUid(String.valueOf(Math.random() * 1000000));

        //prepare test object
        try {
            tx.begin();
            userGroup = pm.makePersistent(userGroup);
            user = pm.makePersistent(user);
            tx.commit();
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
        }

        Assert.assertNotNull(userGroup);
        Assert.assertTrue(userGroup.getSysId() > 0);
        System.out.println(userGroup.getSysId());

        //later: read object from database
        tx = pm.currentTransaction();
        try {
            tx.begin();
            Query q = pm.newQuery(UserGroup.class, "sysId == " + userGroup.getSysId());
            result = (List<?>) q.execute();
            tx.commit();
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
        }

        assertNotNull(result.get(0));
        userGroup = (UserGroup) result.get(0);

        //change something
        userGroup.setSysName("Test123");
        User[] users = new User[1];
        users[0] = user;
        userGroup.setUsers(users);

        //write it back to database
        tx = pm.currentTransaction();
        try {
            tx.begin();
            pm.makePersistent(userGroup); //NPE, because attributes from abstract superclass W7XDbNamedObject cannot be determined
            tx.commit();
        } finally {
            if (tx.isActive()) {
                tx.rollback();
            }
            pm.close();
        }

    }

}
