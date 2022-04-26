# book P246
readonly SECS=3600
readonly UNIT_TIME=60

readonly STEPS=$(($SEC/$UNIT_TIME))

echo Watching CPU usage...

trap 'echo Remove files; rm -rf /tmp/cpu_usage.$$' EXIT
for ((i=0; i<STEPS; i++)) do
    ps -eccomm,pcpu | tail -n +2 >> /tmp/cpu_usage.$$
    sleep $UNIT_TIME
done

echo
echo CPU eaters:

cat /tmp/cpu_usage.$$ | \
awk '
{ process[$1]+=$2; }
END{
    for (i in process) {
        printf("%-20s %s\n", i, process[i])
    }
}' | sort -nrk 2 | head
