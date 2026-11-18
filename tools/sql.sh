#!/usr/bin/env bash
# Run SQL against the running ParaBank HSQLDB instance.
#   ./tools/sql.sh "SELECT * FROM CUSTOMER"
set -euo pipefail
HERE="$(cd "$(dirname "$0")" && pwd)"
JAR=/usr/local/tomcat/webapps/parabank/WEB-INF/lib/hsqldb-2.7.4.jar

if ! docker ps --filter name=parabank --format '{{.Names}}' | grep -q parabank; then
  echo "ParaBank container is not running. Start it with:" >&2
  echo "  docker run -d --name parabank -p 8080:8080 parasoft/parabank" >&2
  exit 1
fi

# Compile the tiny JDBC runner once, push it into the container once.
if ! docker exec parabank test -f /tmp/Q.class 2>/dev/null; then
  mkdir -p "$HERE/../target/sqltool"
  javac -d "$HERE/../target/sqltool" "$HERE/Q.java"
  docker cp "$HERE/../target/sqltool/Q.class" parabank:/tmp/Q.class >/dev/null
fi

docker exec parabank sh -c "cd /tmp && java -cp $JAR:. Q \"${1//\"/\\\"}\""
