#!/bin/bash
set -e

declare logPath="/tmp/access.log"
declare -i counter=0

declare MAX_LOGS_PER_FILE=1000
declare USER_IDS=("luke" "yoda" "chewbacca" "han" "leia" "anakin" "obi" "c3po" "r2d2" "palpatine")
declare VERBS=("GET" "POST" "HEAD" "PUT" "DELETE")
declare RESOURCES=("/api/user" "/api/ships" "/report" "/login" "/logout")
declare STATUS_CODES=("200" "201" "300" "400" "404" "500")

if [ "$#" -gt 0 ]; then
  logPath="$1"
  exit 1;
fi;

echo "Press [CTRL+C] to stop.."
while true
do
  if [[ ${counter} -eq ${MAX_LOGS_PER_FILE} ]]; then
    counter=0;
    echo "File restarted";
    rm -f $1;
  fi;
  echo "10.$(( $RANDOM % 255 + 1 )).$(( $RANDOM % 255 + 1 )).$(( $RANDOM % 255 +1 )) - ${USER_IDS[$(( $RANDOM % 9 ))]} [$(date +%d/%b/%Y:%H:%M:%S\ %z)] \"${VERBS[$(( $RANDOM % 5 ))]} ${RESOURCES[$(( $RANDOM % 5 ))]} HTTP/1.0\" ${STATUS_CODES[$(( $RANDOM % 6 ))]} $(( $RANDOM % 120 + 1 ))" >> "${logPath}";
	sleep 0.1;
	counter+=1;
	if [[ $((counter % 100)) -eq 0 ]]; then
    echo "Written ${counter} logs, file will be recreated in $((MAX_LOGS_PER_FILE - counter)) iterations... Press [CTRL+C] to stop";
  fi;
done