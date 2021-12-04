FROM mariadb:latest

RUN chgrp -R 0 /docker-entrypoint-initdb.d && \
    chmod -R g=u /docker-entrypoint-initdb.d

ENV MYSQL_ROOT_PASSWORD Admin123!
ENV MYSQL_DATABASE ssbd05
ENV MYSQL_USER ssbd05
ENV MYSQL_PASSWORD yinReneif1

ADD ./1-create_roles.sql /docker-entrypoint-initdb.d
ADD ./2-create_database.sql /docker-entrypoint-initdb.d
ADD ./3-init_data.sql /docker-entrypoint-initdb.d
