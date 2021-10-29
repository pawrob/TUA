#!/bin/sh
set -m

sh /opt/jboss/wildfly/bin/standalone.sh -b "0.0.0.0" -bmanagement "0.0.0.0" &
until [ \
  "$(curl -s -w '%{http_code}' -o /dev/null "http://localhost:9990/console/index.html")" \
  -eq 200 ]; do
  echo "$(curl -s -w '%{http_code}' -o /dev/null "http://localhost:9990/console/index.html")"
  /opt/jboss/wildfly/bin/jboss-cli.sh -c --commands="read-attribute server-state"
  sleep 5
done

echo 'Connected!'

sh /opt/jboss/wildfly/bin/jboss-cli.sh -c --file=/opt/jboss/wildfly/bin/cli_commands.cli

fg
