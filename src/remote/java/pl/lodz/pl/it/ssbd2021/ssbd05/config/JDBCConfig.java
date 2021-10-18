package pl.lodz.pl.it.ssbd2021.ssbd05.config;

import javax.annotation.sql.DataSourceDefinition;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.sql.Connection;

//@DataSourceDefinition(
//        name = "java:app/jdbc/ssbd05adminDS",
//        className = "org.postgresql.ds.PGSimpleDataSource",
//        user = "ssbd05admin",
//        password = "L0dbr0k",
//        serverName = "studdev.it.p.lodz.pl",
//        portNumber = 5432,
//        databaseName = "ssbd05",
//        initialPoolSize = 1,
//        minPoolSize = 0,
//        maxPoolSize = 1,
//        maxIdleTime = 10,
//        transactional = true,
//        isolationLevel = Connection.TRANSACTION_READ_COMMITTED)
//
//@DataSourceDefinition(
//        name = "java:app/jdbc/ssbd05authDS",
//        className = "org.postgresql.ds.PGSimpleDataSource",
//        user = "ssbd05auth",
//        password = "woj@uth",
//        serverName = "studdev.it.p.lodz.pl",
//        portNumber = 5432,
//        databaseName = "ssbd05",
//        transactional = true,
//        isolationLevel = Connection.TRANSACTION_READ_COMMITTED)
//
//@DataSourceDefinition(
//        name = "java:app/jdbc/ssbd05mokDS",
//        className = "org.postgresql.ds.PGSimpleDataSource",
//        user = "ssbd05mok",
//        password = "w1k1ngow1e",
//        serverName = "studdev.it.p.lodz.pl",
//        portNumber = 5432,
//        databaseName = "ssbd05",
//        transactional = true,
//        isolationLevel = Connection.TRANSACTION_READ_COMMITTED)
//
//@DataSourceDefinition(
//        name = "java:app/jdbc/ssbd05mooDS",
//        className = "org.postgresql.ds.PGSimpleDataSource",
//        user = "ssbd05moo",
//        password = "w0j0wnicy",
//        serverName = "studdev.it.p.lodz.pl",
//        portNumber = 5432,
//        databaseName = "ssbd05",
//        transactional = true,
//        isolationLevel = Connection.TRANSACTION_READ_COMMITTED)
//
//@Stateless
//public class JDBCConfig {
//    @PersistenceContext(unitName = "ssbd05adminPU")
//    private EntityManager em;
//}
