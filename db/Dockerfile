FROM mariadb:latest
MAINTAINER me
ENV MYSQL_ALLOW_EMPTY_PASSWORD=true
ADD ./1-create_roles.sql /docker-entrypoint-initdb.d
ADD ./2-create_database.sql /docker-entrypoint-initdb.d
ADD ./3-init_data.sql /docker-entrypoint-initdb.d

