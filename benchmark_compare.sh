#!/bin/sh
# Syntax: "./benchmark_compare.sh {times} {profile} ({profile})*
#
# This script requires bash 4.0+ and awk
#

iter=$1
shift   # eat $1

itermaxlen=`expr length $iter`
timemaxlen=15
profilenamemaxlen=0

commandbase="./benchmark.sh -t "


#
# INITIALIZE VARIABLES
#

declare -A times
declare -A iters

for profile in "$@";
do
  iters["$profile"]=0
  len=`expr length $profile`
  if [ "$profilenamemaxlen" -lt "$len" ];
  then
    profilenamemaxlen=$len
  fi
done



#
# EXECUTE BENCHMARK $iter TIMES FOR EACH $profile
#

for i in `seq 1 $iter`;
do
  for profile in "$@";
  do
    profileiters=${iters["$profile"]}
    time_nanos="$($commandbase $profile)"
    if [ "$time_nanos" != "0" ];
    then
      ((profileiters+=1))
      times["$profile,$profileiters"]=$time_nanos
      iters["$profile"]=$profileiters
      fi=`printf "%0"$itermaxlen"s" "$i"`
      fprofile=`printf "%-"$profilenamemaxlen"s" "$profile"`
      ftime=`printf "%"$timemaxlen"sns" "$time_nanos"`
      echo "[$fprofile][$fi] $ftime"
    fi
  done
done


#
# OUTPUT OK/NO OK
#

echo "
----RESULTS--------
"
for profile in "$@";
do
  profileiter=${iters["$profile"]}
  fprofile=`printf "%-"$profilenamemaxlen"s" "$profile"`
  if [ $profileiter == $iter ];
  then
    echo "[$fprofile] ALL OK";
  else
    echo "[$fprofile] WITH ERRORS";
  fi
done



#
# SUM UP TIMES FOR EACH PROFILE
#

declare -A sumtimes

for profile in "$@";
do
  profilesum=0
  profiletimes=${times["$profile"]}
  timeslen=${iters["$profile"]}
  for i in `seq $timeslen`;
  do
    profileitertime=${times["$profile,$i"]}
    ((profilesum+=profileitertime))
  done
  sumtimes["$profile"]=$profilesum
done



#
# COMPUTE AVERAGE TIMES
#

declare -A avgtimes
for profile in "$@";
do
  profiletime=${sumtimes["$profile"]}
  profileiters=${iters["$profile"]}
  profileavg=`awk 'BEGIN{printf("%0.2f", '$profiletime' / '$profileiters')}'`
  avgtimes["$profile"]=$profileavg
done



#
# COMPUTE SMALLEST (REFERENCE) VALUE
#

smallestvalue=0.0
smallestprofile=
for profile in "$@";
do
  profileavg=${avgtimes["$profile"]}
  iszero=$(awk 'BEGIN{ print "'$smallestvalue'" == "0.0" }')
  cond=$(awk 'BEGIN{ print "'$profileavg'" < "'$smallestvalue'" }')
  if [ "$iszero" == 1 ] || [ "$cond" == 1 ];
  then
    smallestvalue=$profileavg
	smallestprofile=$profile
  fi
done


#
# OUTPUT RESULTS
#

echo "
----TOTAL TIMES----
"
for profile in "$@";
do
  profileavg=${avgtimes["$profile"]}
  profileiters=${iters["$profile"]}
  fprofile=`printf "%-"$profilenamemaxlen"s" "$profile"`
  ftime=`printf "%"$timemaxlen"sns" "$profileavg"`
  if [ "$profile" == "$smallestprofile" ];
  then
    echo "[$fprofile][$profileiters] $ftime *"
  else
	difference=`awk 'BEGIN{printf("%0.2f", '$profileavg' / '$smallestvalue' * 100)}'`
    echo "[$fprofile][$profileiters] $ftime ($difference%)"
  fi
done

