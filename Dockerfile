FROM jboss/wildfly

COPY wildfly/modules/ /opt/jboss/wildfly/modules/
COPY wildfly/standalone.xml /opt/jboss/wildfly/standalone/configuration/
COPY target/ssbd05-Kv1.0.0.war /opt/jboss/wildfly/standalone/deployments/