package pl.lodz.pl.it.ssbd2021.ssbd05.config;

import javax.annotation.sql.DataSourceDefinition;
import javax.ejb.Stateless;
import java.sql.Connection;

@DataSourceDefinition(
        name = "java:app/jdbc/ssbd05adminDS",
        className = "org.mariadb.jdbc.MariaDbDataSource",
        user = "ssbd05admin",
        password = "L0dbr0k",
        serverName = "host.docker.internal",
        portNumber = 3306,
        databaseName = "ssbd05",
        initialPoolSize = 1,
        minPoolSize = 0,
        maxPoolSize = 1,
        maxIdleTime = 10,
        transactional = true,
        isolationLevel = Connection.TRANSACTION_READ_COMMITTED)

@DataSourceDefinition(
        name = "java:app/jdbc/ssbd05authDS",
        className = "org.mariadb.jdbc.MariaDbDataSource",
        user = "ssbd05auth",
        password = "woj@uth",
        serverName = "host.docker.internal",
        portNumber = 3306,
        databaseName = "ssbd05",
        transactional = true,
        isolationLevel = Connection.TRANSACTION_READ_COMMITTED)

@DataSourceDefinition(
        name = "java:app/jdbc/ssbd05mokDS",
        className = "org.mariadb.jdbc.MariaDbDataSource",
        user = "ssbd05mok",
        password = "w1k1ngow1e",
        serverName = "host.docker.internal",
        portNumber = 3306,
        databaseName = "ssbd05",
        transactional = true,
        isolationLevel = Connection.TRANSACTION_READ_COMMITTED)

@DataSourceDefinition(
        name = "java:app/jdbc/ssbd05mooDS",
        className = "org.mariadb.jdbc.MariaDbDataSource",
        user = "ssbd05moo",
        password = "w0j0wnicy",
        serverName = "host.docker.internal",
        portNumber = 3306,
        databaseName = "ssbd05",
        transactional = true,
        isolationLevel = Connection.TRANSACTION_READ_COMMITTED)

@Stateless
public class JDBCConfig {
}
