#!/bin/csh
source /afs/slac/g/glast/ground/scripts/group.cshrc
/afs/slac.stanford.edu/g/glast/ground/DataServer/v1r0p2/DataServer/src/runPrune.pl 
sleep 400
echo "Your data server job has finished." | mail -s "Data Server" $email