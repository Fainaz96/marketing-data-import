package com.flaregames.imports.importlog;

import com.google.inject.Inject;

import com.flaregames.imports.integration.AbstractIntegrationTest;
import com.flaregames.imports.integration.TestDao;
import com.flaregames.imports.schema.DBSchemaName;

import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.Define;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.jdbi.v3.sqlobject.transaction.Transactional;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class TransactionDaoIntegrationTest extends AbstractIntegrationTest {

    @Inject
    Jdbi jdbi;

    @Inject
    TestDao testDao;

    @Inject
    DBSchemaName schemaName;

    @Before
    public void before() {
        testDao.dropTable(schemaName.toString(), "tx_test");
        testDao.createTable(schemaName.toString(), "tx_test", "(id INT8, name VARCHAR(32))");
    }

    @Test
    public void runInTransaction_rollbackExplicit() {

        TransactioDao transactioDao = jdbi.onDemand(TransactioDao.class);
        TestDao1 dao1 = jdbi.onDemand(TestDao1.class);
        TestDao2 dao2 = jdbi.onDemand(TestDao2.class);

        transactioDao.runInTransaction(() -> {

            dao1.insert(schemaName.toString(), 1, "test1");
            dao2.insert(schemaName.toString(), 2, "test2");
            dao1.commit();

            dao1.begin();
            dao1.insert(schemaName.toString(), 3, "test3");
            dao2.insert(schemaName.toString(), 4, "test4");
            dao1.rollback();

        });

        assertThat(dao1.count(schemaName.toString())).isEqualTo(2);

    }

    @Test
    public void runInTransaction_rollbackOnException() {

        TransactioDao transactioDao = jdbi.onDemand(TransactioDao.class);
        TestDao1 dao1 = jdbi.onDemand(TestDao1.class);
        TestDao2 dao2 = jdbi.onDemand(TestDao2.class);

        assertThatThrownBy(() -> {
            transactioDao.runInTransaction(() -> {

                dao1.insert(schemaName.toString(), 1, "test1");
                dao2.insert(schemaName.toString(), 2, "test2");

            });
            transactioDao.runInTransaction(() -> {

                dao1.insert(schemaName.toString(), 3, "test3");
                dao2.insert(schemaName.toString(), 4, "test4");

                throw new RuntimeException();

            });
        }).isExactlyInstanceOf(RuntimeException.class);

        assertThat(dao1.count(schemaName.toString())).isEqualTo(2);

    }

    @Test
    public void runInTransaction_commitedAsExpected() {

        TransactioDao transactioDao = jdbi.onDemand(TransactioDao.class);
        TestDao1 dao1 = jdbi.onDemand(TestDao1.class);
        TestDao2 dao2 = jdbi.onDemand(TestDao2.class);

        transactioDao.runInTransaction(() -> {

            dao1.insert(schemaName.toString(), 1, "test1");
            dao2.insert(schemaName.toString(), 2, "test2");

        });
        transactioDao.runInTransaction(() -> {

            dao1.insert(schemaName.toString(), 3, "test3");
            dao2.insert(schemaName.toString(), 4, "test4");

        });

        assertThat(dao1.count(schemaName.toString())).isEqualTo(4);

    }

    public interface TestDao1 extends Transactional {

        @SqlUpdate("INSERT INTO <schemaName>.tx_test VALUES(:id,:name)")
        void insert(@Define("schemaName") String schemaName, @Bind("id") long id, @Bind("name") String name);

        @SqlQuery("SELECT count(*) FROM <schemaName>.tx_test")
        int count(@Define("schemaName") String schemaName);

    }

    public interface TestDao2 {

        @SqlUpdate("INSERT INTO <schemaName>.tx_test VALUES(:id,:name)")
        void insert(@Define("schemaName") String schemaName, @Bind("id") long id, @Bind("name") String name);

    }

}
