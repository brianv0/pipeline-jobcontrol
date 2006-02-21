#!/bin/csh
source /afs/slac/g/glast/ground/scripts/group.cshrc
setenv OUTDIR `pwd`
cd /afs/slac.stanford.edu/g/glast/ground/DataServer/users/dragon/pipeline
./runPrune.pl "$task" "merit" "$tcut" "$runStart" "$runEnd" "$OUTDIR" ""

echo "Your data server job has finished. Data is in ftp://ftp-glast.slac.stanford.edu/glast.u13/DataServer/$dir" | mail -s "Data Server" $email