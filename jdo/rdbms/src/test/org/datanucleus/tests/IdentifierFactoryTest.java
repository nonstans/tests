/**********************************************************************
Copyright (c) 2006 Andy Jefferson and others. All rights reserved.
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Contributors :
    ...
***********************************************************************/
package org.datanucleus.tests;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;

import org.datanucleus.ClassLoaderResolver;
import org.datanucleus.ClassLoaderResolverImpl;
import org.datanucleus.ExecutionContext;
import org.datanucleus.PersistenceConfiguration;
import org.datanucleus.api.jdo.JDOPersistenceManager;
import org.datanucleus.api.jdo.JDOPersistenceManagerFactory;
import org.datanucleus.metadata.AbstractClassMetaData;
import org.datanucleus.metadata.AbstractMemberMetaData;
import org.datanucleus.metadata.FieldRole;
import org.datanucleus.store.rdbms.adapter.DatastoreAdapter;
import org.datanucleus.store.rdbms.RDBMSStoreManager;
import org.datanucleus.store.rdbms.identifier.DatastoreIdentifier;
import org.datanucleus.store.rdbms.identifier.IdentifierFactory;
import org.datanucleus.store.rdbms.identifier.IdentifierType;
import org.datanucleus.store.rdbms.identifier.JPOXIdentifierFactory;
import org.datanucleus.tests.JDOPersistenceTestCase;
import org.datanucleus.util.ClassUtils;

/**
 * Tests for IdentifierFactory implementations.
 **/
public class IdentifierFactoryTest extends JDOPersistenceTestCase
{
    protected PersistenceManager pm;

    public IdentifierFactoryTest(String name)
    {
        super(name);
    }

    /**
     * Tests for DNIdentifierFactory
     */
    public void testDataNucleus2()
    {
        RDBMSStoreManager srm = (RDBMSStoreManager)storeMgr;
        IdentifierFactory idFactory = srm.getIdentifierFactory();
        ClassLoaderResolver clr = new ClassLoaderResolverImpl();

        // Table identifiers
        // a). generated name shorter than max length
        DatastoreIdentifier id = idFactory.newIdentifier(IdentifierType.TABLE, "MyClass");
        assertTrue("newIdentifier(TABLE, String) has generated an incorrect name : " + id.getIdentifierName(), 
            "MYCLASS".equalsIgnoreCase(id.getIdentifierName()));

        // b). specified name shorter than max length
        id = idFactory.newTableIdentifier("MY_TABLE_NAME");
        assertTrue("newDatastoreContainerIdentifier(String) has returned an incorrect name when should have used the supplied name " + id.getIdentifierName(), 
            "MY_TABLE_NAME".equalsIgnoreCase(id.getIdentifierName()));

        // c). name specified via ClassMetaData
        AbstractClassMetaData managerCMD = storeMgr.getNucleusContext().getMetaDataManager().getMetaDataForClass("org.jpox.samples.models.company.Manager", clr);
        id = idFactory.newTableIdentifier(managerCMD);
        assertTrue("newDatastoreContainerIdentifier(clr, ClassMetaData) has returned an incorrect generated name " + id.getIdentifierName(), 
            "MANAGER".equalsIgnoreCase(id.getIdentifierName()));

        // d). name specified via ClassMetaData
        AbstractMemberMetaData fmd = managerCMD.getMetaDataForMember("subordinates");
        id = idFactory.newTableIdentifier(fmd);
        assertTrue("newDatastoreContainerIdentifier(clr, AbstractMemberMetaData) has returned an incorrect generated name " + id.getIdentifierName(), 
            "MANAGER_SUBORDINATES".equalsIgnoreCase(id.getIdentifierName()));

        // Column identifiers
        // a). generated name shorter than max length
        id = idFactory.newIdentifier(IdentifierType.COLUMN, "myField");
        assertTrue("newIdentifier(COLUMN, String) has generated an incorrect name : " + id.getIdentifierName(), 
            "MYFIELD".equalsIgnoreCase(id.getIdentifierName()));

        // b). specified name shorter than max length
        id = idFactory.newColumnIdentifier("MYCOLUMNNAME");
        assertTrue("newColumnIdentifier(String) has returned an incorrect name when should have used the supplied name " + id.getIdentifierName(), 
            "MYCOLUMNNAME".equalsIgnoreCase(id.getIdentifierName()));

        // c). Discriminator column identifier
        id = idFactory.newDiscriminatorFieldIdentifier();
        assertTrue("newDiscriminatorFieldIdentifier() has returned an incorrect name : " + id.getIdentifierName(),
            "DISCRIMINATOR".equalsIgnoreCase(id.getIdentifierName()));

        // d). Version column identifier
        id = idFactory.newVersionFieldIdentifier();
        assertTrue("newVersionFieldIdentifier() has returned an incorrect name : " + id.getIdentifierName(),
            "VERSION".equalsIgnoreCase(id.getIdentifierName()));

        // e). Index (ordering) column identifier
        id = idFactory.newIndexFieldIdentifier(fmd);
        assertTrue("newIndexFieldIdentifier() has returned an incorrect name : " + id.getIdentifierName(),
            "IDX".equalsIgnoreCase(id.getIdentifierName()));

        // f). Adapter Index column identifier
        id = idFactory.newAdapterIndexFieldIdentifier();
        assertTrue("newAdapterIndexFieldIdentifier() has returned an incorrect name : " + id.getIdentifierName(),
            "IDX".equalsIgnoreCase(id.getIdentifierName()));

        AbstractMemberMetaData[] relatedMmds = fmd.getRelatedMemberMetaData(clr);

        // g). join table owner column identifier (1-N bi JoinTable)
        DatastoreIdentifier destId = idFactory.newColumnIdentifier("MANAGER_ID");
        id = idFactory.newJoinTableFieldIdentifier(fmd, 
            relatedMmds != null ? relatedMmds[0] : null, 
            destId, false, FieldRole.ROLE_OWNER);
        assertTrue("newJoinTableFieldIdentifier(OWNER) has returned an incorrect generated name " + id.getIdentifierName(), 
            "MANAGER_ID_OID".equalsIgnoreCase(id.getIdentifierName()));

        // h). join table element column identifier (1-N bi JoinTable)
        destId = idFactory.newColumnIdentifier("EMPLOYEE_ID");
        id = idFactory.newJoinTableFieldIdentifier(fmd, 
            relatedMmds != null ? relatedMmds[0] : null, 
            destId, false, FieldRole.ROLE_COLLECTION_ELEMENT);
        assertTrue("newJoinTableFieldIdentifier(ELEMENT) has returned an incorrect generated name " + id.getIdentifierName(), 
            "EMPLOYEE_ID_EID".equalsIgnoreCase(id.getIdentifierName()));

        // i). FK owner column identifier (1-N bi FK)
        AbstractMemberMetaData deptsFMD = managerCMD.getMetaDataForMember("departments");
        AbstractMemberMetaData[] deptsRelatedMmds = deptsFMD.getRelatedMemberMetaData(clr);
        destId = idFactory.newColumnIdentifier("MANAGER_ID");
        id = idFactory.newForeignKeyFieldIdentifier(deptsFMD, 
            deptsRelatedMmds != null ? deptsRelatedMmds[0] : null, 
            destId, false, FieldRole.ROLE_OWNER);
        assertTrue("newForeignKeyFieldIdentifier(OWNER) has returned an incorrect generated name " + id.getIdentifierName(), 
            "MANAGER_MANAGER_ID_OID".equalsIgnoreCase(id.getIdentifierName()));

        // Primary key identifiers

        // Index identifiers

        // Foreign key identifiers

        // Candidate key identifiers

        // Sequence identifiers
    }

    /**
     * Tests for JPAIdentifierFactory.
     */
    public void testJPA()
    {
        RDBMSStoreManager srm = (RDBMSStoreManager)storeMgr;
        IdentifierFactory idFactory = null;
        ClassLoaderResolver clr = new ClassLoaderResolverImpl();
        try
        {
            JDOPersistenceManagerFactory thePMF = (JDOPersistenceManagerFactory)pmf;
            Map props = new HashMap();
            PersistenceConfiguration conf = getConfigurationForPMF(thePMF);
            if (conf.getStringProperty("datanucleus.mapping.Catalog") != null)
            {
                props.put("DefaultCatalog", conf.getStringProperty("datanucleus.mapping.Catalog"));
            }
            if (conf.getStringProperty("datanucleus.mapping.Schema") != null)
            {
                props.put("DefaultSchema", conf.getStringProperty("datanucleus.mapping.Schema"));
            }
            if (conf.getStringProperty("datanucleus.identifier.case") != null)
            {
                props.put("RequiredCase", conf.getStringProperty("datanucleus.identifier.case"));
            }
            else
            {
                props.put("RequiredCase", srm.getDefaultIdentifierCase());
            }
            if (conf.getStringProperty("datanucleus.identifier.wordSeparator") != null)
            {
                props.put("WordSeparator", conf.getStringProperty("datanucleus.identifier.wordSeparator"));
            }
            if (conf.getStringProperty("datanucleus.identifier.tablePrefix") != null)
            {
                props.put("TablePrefix", conf.getStringProperty("datanucleus.identifier.tablePrefix"));
            }
            if (conf.getStringProperty("datanucleus.identifier.tableSuffix") != null)
            {
                props.put("TableSuffix", conf.getStringProperty("datanucleus.identifier.tableSuffix"));
            }

            Class cls = Class.forName("org.datanucleus.store.rdbms.identifier.JPAIdentifierFactory");
            Class[] argTypes = new Class[]
               {DatastoreAdapter.class, ClassLoaderResolver.class, Map.class};
            Object[] args = new Object[]
               {
                   srm.getDatastoreAdapter(), srm.getNucleusContext().getClassLoaderResolver(null), props
               };
            idFactory = (IdentifierFactory)ClassUtils.newInstance(cls, argTypes, args);
        }
        catch (Exception e)
        {
            fail("Error creating JPAIdentifierFactory : " + e.getMessage());
        }

        // Table identifiers
        // a). generated name shorter than max length
        DatastoreIdentifier id = idFactory.newIdentifier(IdentifierType.TABLE, "MyClass");
        assertTrue("newIdentifier(TABLE, String) has generated an incorrect name : " + id.getIdentifierName(), 
            "MYCLASS".equalsIgnoreCase(id.getIdentifierName()));

        // b). specified name shorter than max length
        id = idFactory.newTableIdentifier("MY_TABLE_NAME");
        assertTrue("newDatastoreContainerIdentifier(String) has returned an incorrect name when should have used the supplied name " + id.getIdentifierName(), 
            "MY_TABLE_NAME".equalsIgnoreCase(id.getIdentifierName()));

        // c). name specified via ClassMetaData
        AbstractClassMetaData managerCMD = storeMgr.getNucleusContext().getMetaDataManager().getMetaDataForClass("org.jpox.samples.models.company.Manager", clr);
        id = idFactory.newTableIdentifier(managerCMD);
        assertTrue("newDatastoreContainerIdentifier(clr, ClassMetaData) has returned an incorrect generated name " + id.getIdentifierName(), 
            "MANAGER".equalsIgnoreCase(id.getIdentifierName()));

        // d). name specified via ClassMetaData
        AbstractMemberMetaData fmd = managerCMD.getMetaDataForMember("subordinates");
        id = idFactory.newTableIdentifier(fmd);
        assertTrue("newDatastoreContainerIdentifier(clr, AbstractMemberMetaData) has returned an incorrect generated name " + id.getIdentifierName(), 
            "MANAGER_EMPLOYEE".equalsIgnoreCase(id.getIdentifierName()));

        // Column identifiers
        // a). generated name shorter than max length
        id = idFactory.newIdentifier(IdentifierType.COLUMN, "myField");
        assertTrue("newIdentifier(COLUMN, String) has generated an incorrect name : " + id.getIdentifierName(), 
            "MYFIELD".equalsIgnoreCase(id.getIdentifierName()));

        // b). specified name shorter than max length
        id = idFactory.newColumnIdentifier("MY_COLUMN_NAME");
        assertTrue("newColumnIdentifier(String) has returned an incorrect name when should have used the supplied name " + id.getIdentifierName(), 
            "MY_COLUMN_NAME".equalsIgnoreCase(id.getIdentifierName()));

        // c). Discriminator column identifier
        id = idFactory.newDiscriminatorFieldIdentifier();
        assertTrue("newDiscriminatorFieldIdentifier() has returned an incorrect name : " + id.getIdentifierName(),
            "DTYPE".equalsIgnoreCase(id.getIdentifierName()));

        // d). Version column identifier
        id = idFactory.newVersionFieldIdentifier();
        assertTrue("newVersionFieldIdentifier() has returned an incorrect name : " + id.getIdentifierName(),
            "VERSION".equalsIgnoreCase(id.getIdentifierName()));

        // e). Index (ordering) column identifier
        id = idFactory.newIndexFieldIdentifier(fmd);
        assertTrue("newIndexFieldIdentifier() has returned an incorrect name : " + id.getIdentifierName(),
            "SUBORDINATES_ORDER".equalsIgnoreCase(id.getIdentifierName()));

        // f). Adapter Index column identifier
        id = idFactory.newAdapterIndexFieldIdentifier();
        assertTrue("newAdapterIndexFieldIdentifier() has returned an incorrect name : " + id.getIdentifierName(),
            "IDX".equalsIgnoreCase(id.getIdentifierName()));

        AbstractMemberMetaData[] relatedMmds = fmd.getRelatedMemberMetaData(clr);

        // g). join table owner column identifier (1-N bi JoinTable)
        DatastoreIdentifier destId = idFactory.newColumnIdentifier("MANAGER_ID");
        id = idFactory.newJoinTableFieldIdentifier(fmd, 
            relatedMmds != null ? relatedMmds[0] : null, 
            destId, false, FieldRole.ROLE_OWNER);
        assertTrue("newJoinTableFieldIdentifier(OWNER) has returned an incorrect generated name " + id.getIdentifierName(), 
            "MANAGER_MANAGER_ID".equalsIgnoreCase(id.getIdentifierName()));

        // h). join table element column identifier (1-N bi JoinTable)
        destId = idFactory.newColumnIdentifier("EMPLOYEE_ID");
        id = idFactory.newJoinTableFieldIdentifier(fmd, 
            relatedMmds != null ? relatedMmds[0] : null, 
            destId, false, FieldRole.ROLE_COLLECTION_ELEMENT);
        assertTrue("newJoinTableFieldIdentifier(ELEMENT) has returned an incorrect generated name " + id.getIdentifierName(), 
            "SUBORDINATES_EMPLOYEE_ID".equalsIgnoreCase(id.getIdentifierName()));

        // i). FK owner column identifier (1-N bi FK)
        AbstractMemberMetaData deptsFMD = managerCMD.getMetaDataForMember("departments");
        AbstractMemberMetaData[] deptsRelatedMmds = deptsFMD.getRelatedMemberMetaData(clr);
        destId = idFactory.newColumnIdentifier("MANAGER_ID");
        id = idFactory.newForeignKeyFieldIdentifier(deptsFMD,
            deptsRelatedMmds != null ? deptsRelatedMmds[0] : null, 
            destId, true, FieldRole.ROLE_OWNER);
        assertTrue("newForeignKeyFieldIdentifier(OWNER) has returned an incorrect generated name " + id.getIdentifierName(), 
            "MANAGER_MANAGER_ID".equalsIgnoreCase(id.getIdentifierName()));

        // Primary key identifiers

        // Index identifiers

        // Foreign key identifiers

        // Candidate key identifiers

        // Sequence identifiers
    }

    /**
     * Verify that JPOXIdentifierFactory does truncation with a 2-character hashcode, as was done in JPOX
     * 1.2.0 (this had been changed to 4-character hashcode in datanucleus)
     */
    public void testJPOXTruncate()
    {
        class SubclassForTesting extends JPOXIdentifierFactory
        {
            private SubclassForTesting(DatastoreAdapter dba, ClassLoaderResolver clr, Map props)
            {
                super(dba, clr, props);
            }

            public String publicTestTruncate(String string, int length)
            {
                return truncate(string, length);
            }
        }
        
        RDBMSStoreManager srm = (RDBMSStoreManager)storeMgr;
        SubclassForTesting jpoxCompatibilityIdentifierFactory = new SubclassForTesting(srm.getDatastoreAdapter(), new ClassLoaderResolverImpl(), new Properties());
        assertEquals("BIDIR_CONTAINED_LEVEL2_I2S", jpoxCompatibilityIdentifierFactory.publicTestTruncate("BIDIR_CONTAINED_LEVEL2_INTEGER",26).toUpperCase());
    }
    
    /**
     * Verify that putting "datanucleus.identifierFactory=jpox" results in JPOXIdentifierFactory being used
     */
    public void testJPOX()
    {
        Properties props = new Properties();
        props.put("datanucleus.identifierFactory", "jpox");
        PersistenceManagerFactory pmf2 = TestHelper.getPMF(1, props);
        PersistenceManager pm = pmf2.getPersistenceManager();
        ExecutionContext ec = ((JDOPersistenceManager)pm).getExecutionContext();
        IdentifierFactory identifierFactory = ((RDBMSStoreManager)ec.getStoreManager()).getIdentifierFactory();
        assertTrue(identifierFactory instanceof JPOXIdentifierFactory);
    }
}