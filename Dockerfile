FROM jboss/wildfly:24.0.0.Final
RUN /opt/jboss/wildfly/bin/add-user.sh admin Admin123! --silent
CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0"]