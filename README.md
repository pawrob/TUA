# TUA
```
docker build -t tua .
docker run -dp 8443:8443 -p 9990:9990 tua
```
```
docker pull mariadb
docker run --name mariadb_tua -e MYSQL_ROOT_PASSWORD=password -p 3306:3306 -d docker.io/library/mariadb
docker exec -it mariadb_tua bash
mysql -u root -p  -h localhost


CREATE DATABASE ssbd05;
CREATE USER 'ssbd05'@'%' IDENTIFIED BY 'yinReneif1';
CREATE USER 'ssbd05admin'@'%' IDENTIFIED BY 'L0dbr0k';
CREATE USER 'ssbd05mok'@'%' IDENTIFIED BY 'w1k1ngow1e';
CREATE USER 'ssbd05moo'@'%' IDENTIFIED BY 'w0j0wnicy';
CREATE USER 'ssbd05auth'@'%' IDENTIFIED BY 'woj@uth';
GRANT ALL PRIVILEGES ON *.* TO 'ssbd05'@'%';
```

