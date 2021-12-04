FROM jboss/wildfly:24.0.0.Final

RUN chgrp -R 0 /opt/jboss/wildfly && \
    chmod -R g=u /opt/jboss/wildfly

COPY ./ssbd05-Kv1.0.0.war /opt/jboss/wildfly/todeploy/
COPY ./mariadb-java-client-2.3.0.jar /opt/jboss/wildfly/todeploy/mariadb-java-client-2.3.0.jar
COPY ./init_script.sh /opt/jboss/wildfly/
COPY ./cli_commands.cli /opt/jboss/wildfly/bin
RUN /opt/jboss/wildfly/bin/add-user.sh admin Admin123! --silent
CMD ["/opt/jboss/wildfly/init_script.sh"]