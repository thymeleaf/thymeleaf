#!/bin/sh
# Syntax: "./benchmark.sh [-t] {profile}
#
# This script requires bash 4.0+ and awk
#

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

OUTPUT_FILE_DATE_SUFFIX=`date +"%Y%m%d%H%M%S"`
OUTPUT_FILE_RANDOM_SUFFIX=`date | md5sum | cut -c1-6`
OUTPUT_FILE="benchmark-output-$PROFILE-$OUTPUT_FILE_DATE_SUFFIX-$OUTPUT_FILE_RANDOM_SUFFIX"

COMMAND="mvn -P $PROFILE clean compile test -Dtest=org.thymeleaf.benchmark.BenchmarkTest"
if [ "$SHOW_ONLY_TIME" == 'false' ];
then
  echo "
Executing benchmark using profile: $PROFILE
"
  $COMMAND;
else
  EXPR='.*\[THYMELEAF\]\[\(.*\)\]\[.*'
  $COMMAND > $OUTPUT_FILE;
  OUTPUT=`cat $OUTPUT_FILE`
  TIME=$((`expr match "$OUTPUT" "$EXPR"`))
  if [ "$TIME" == "0" ];
  then
    $(mv $OUTPUT_FILE "$OUTPUT_FILE-error.log")
    echo 0
    exit 1
  fi
  rm $OUTPUT_FILE
  echo $TIME
fi
