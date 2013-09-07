#!/bin/sh
#
# This script requires bash 4.0+ and awk
#

iter=5

itermaxlen=`expr length $iter`
timemaxlen=15
profilenamemaxlen=0

commandbase="./benchmark.sh -t "

declare -A times

for profile in "$@";
do
  times["$profile"]=0
  len=`expr length $profile`
  if [ "$profilenamemaxlen" -lt "$len" ];
  then
    profilenamemaxlen=$len
  fi
done

for i in `seq 1 $iter`;
do

  for profile in "$@";
  do
    profiletime=${times["$profile"]}
    time_nanos=`$commandbase $profile`
	((profiletime+=time_nanos))
    times["$profile"]=$profiletime
	fi=`printf "%0"$itermaxlen"s" "$i"`
	fprofile=`printf "%-"$profilenamemaxlen"s" "$profile"`
	ftime=`printf "%"$timemaxlen"sns" "$time_nanos"`
    echo "[$fi][$fprofile] $ftime"
  done
  
done

echo "
----TOTAL TIMES----
"

smallestvalue=0
smallestprofile=

for profile in "$@";
do
  profiletime=${times["$profile"]}
  if [ "$smallestvalue" == 0 ] || [ "$profiletime" -lt "$smallestvalue" ];
  then
    smallestvalue=$profiletime
	smallestprofile=$profile
  fi
done

for profile in "$@";
do
  profiletime=${times["$profile"]}
  fprofile=`printf "%-"$profilenamemaxlen"s" "$profile"`
  ftime=`printf "%"$timemaxlen"sns" "$profiletime"`
  if [ "$profile" == "$smallestprofile" ];
  then
    echo "[$fprofile] $ftime *"
  else
	difference=`awk 'BEGIN{printf("%0.2f", '$profiletime' / '$smallestvalue' * 100)}'`
    echo "[$fprofile] $ftime ($difference%)"
  fi
done

