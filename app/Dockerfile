FROM jboss/wildfly:24.0.0.Final
#COPY ./mariadb-java-client-2.3.0.jar /opt/jboss/wildfly/standalone/deployments/
COPY ./ssbd05-Kv1.0.0.war /opt/jboss/wildfly/todeploy/
COPY ./mariadb-java-client-2.3.0.jar /opt/jboss/wildfly/todeploy/mariadb-java-client-2.3.0.jar
COPY ./init_script.sh /opt/jboss/wildfly/
COPY ./cli_commands.cli /opt/jboss/wildfly/bin
RUN /opt/jboss/wildfly/bin/add-user.sh admin Admin123! --silent
#CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0"]
CMD ["/opt/jboss/wildfly/init_script.sh"]
#CMD ["/opt/jboss/wildfly/bin/jboss-cli.sh", "-c", "deploy /opt/jboss/wildfly/driver/mariadb-java-client-2.3.0.jar"]
#CMD ["/opt/jboss/wildfly/bin/jboss-cli.sh", "-c", "data-source add --name=ssbd05adminDS --enabled=true --jndi-name=java:jboss/jdbc/ssbd05adminDS --transaction-isolation=TRANSACTION_READ_COMMITTED --driver-name=mariadb-java-client-2.3.0.jar --connection-url=jdbc:mariadb://db:3306/ssbd05 --user-name=ssbd05admin --password=L0dbr0k --use-ccm=false"]
#CMD ["/opt/jboss/wildfly/bin/jboss-cli.sh", "-c", "data-source add --name=ssbd05authDS --enabled=true --jndi-name=java:jboss/jdbc/ssbd05authDS --transaction-isolation=TRANSACTION_READ_COMMITTED --driver-name=mariadb-java-client-2.3.0.jar --connection-url=jdbc:mariadb://db:3306/ssbd05 --user-name=ssbd05auth --password=woj@uth --use-ccm=false"]
#CMD ["/opt/jboss/wildfly/bin/jboss-cli.sh", "-c", "data-source add --name=ssbd05mokDS --enabled=true --jndi-name=java:jboss/jdbc/ssbd05mokDS --transaction-isolation=TRANSACTION_READ_COMMITTED --driver-name=mariadb-java-client-2.3.0.jar --connection-url=jdbc:mariadb://db:3306/ssbd05 --user-name=ssbd05mok --password=w1k1ngow1e --use-ccm=false"]
#CMD ["/opt/jboss/wildfly/bin/jboss-cli.sh", "-c", "data-source add --name=ssbd05mooDS --enabled=true --jndi-name=java:jboss/jdbc/ssbd05mooDS --transaction-isolation=TRANSACTION_READ_COMMITTED --driver-name=mariadb-java-client-2.3.0.jar --connection-url=jdbc:mariadb://db:3306/ssbd05 --user-name=ssbd05moo --password=w0j0wnicy --use-ccm=false"]