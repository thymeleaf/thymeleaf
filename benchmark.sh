#!/bin/sh
# Syntax: "./benchmark.sh [-t] {profile}

if [ "$1" == '-t' ];
then
  SHOW_ONLY_TIME=true;
else
  SHOW_ONLY_TIME=false;
fi

if [ "$SHOW_ONLY_TIME" == 'true' ];
then
  PROFILE="$2";
else
  PROFILE="$1";
fi

if [ "$PROFILE" == '' ];
then
  echo 'Bad syntax: "./benchmark.sh [-t] {profile}"'
  exit 1;
fi

COMMAND="mvn -P $PROFILE clean compile test -Dtest=org.thymeleaf.benchmark.BenchmarkTest"
if [ "$SHOW_ONLY_TIME" == 'false' ];
then
  echo "
Executing benchmark using profile: $PROFILE
"
  $COMMAND;
else
  EXPR='.*\[THYMELEAF\]\[\(.*\)\]\[.*'
  OUTPUT=`$COMMAND`
  TIME=$((`expr match "$OUTPUT" "$EXPR"`))
  echo $TIME
fi

