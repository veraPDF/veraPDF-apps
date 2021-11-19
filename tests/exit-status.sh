#!/usr/bin/env bash

if (( NO_CD != 1 )); then
  cd /tmp || exit
fi

if [[ ! -e "./veraPDF-corpus-master" ]]; then
  wget https://github.com/veraPDF/veraPDF-corpus/archive/master.zip
  unzip master.zip
  rm master.zip
fi

if [[ -z "$VERAPDF" ]]; then
  VERAPDF=./verapdf/verapdf
fi

"$VERAPDF" "./veraPDF-corpus-master/PDF_A-1b/6.1 File structure/6.1.4 Cross reference table/veraPDF test suite 6-1-4-t03-pass-b.pdf"
resSinglePass=$?
"$VERAPDF" "./veraPDF-corpus-master/PDF_A-1b/6.1 File structure/6.1.4 Cross reference table/veraPDF test suite 6-1-4-t02-fail-a.pdf"
resSingleFail=$?
"$VERAPDF" ./veraPDF-corpus-master/PDF_A-1b/6.1\ File\ structure/6.1.5\ Document\ information\ dictionary/veraPDF\ test\ suite\ 6-1-5-t02-pass-a.pdf ./veraPDF-corpus-master/PDF_A-1b/6.1\ File\ structure/6.1.5\ Document\ information\ dictionary/veraPDF\ test\ suite\ 6-1-5-t02-pass-b.pdf ./veraPDF-corpus-master/PDF_A-1b/6.1\ File\ structure/6.1.5\ Document\ information\ dictionary/veraPDF\ test\ suite\ 6-1-5-t02-pass-c.pdf
resBatchPass=$?
"$VERAPDF" ./veraPDF-corpus-master/PDF_A-1b/6.1 File\ structure/6.1.5\ Document\ information\ dictionary/veraPDF\ test\ suite\ 6-1-5-t01-fail-a.pdf ./veraPDF-corpus-master/PDF_A-1b/6.1\ File\ structure/6.1.5\ Document\ information\ dictionary/veraPDF\ test\ suite\ 6-1-5-t01-fail-b.pdf ./veraPDF-corpus-master/PDF_A-1b/6.1\ File\ structure/6.1.5\ Document\ information\ dictionary/veraPDF\ test\ suite\ 6-1-5-t01-fail-c.pdf
resBatchFail=$?
"$VERAPDF" "./veraPDF-corpus-master/PDF_A-1b/6.1 File structure/6.1.5 Document information dictionary/"
resBatchPassFail=$?
"$VERAPDF" -f jbnd -m --params --help
resBadParams=$?
export JAVA_OPTS="-Xmx2200k"
"$VERAPDF" "./veraPDF-corpus-master/PDF_A-1b/6.1 File structure/6.1.4 Cross reference table/"
outOfMem=$?
unset JAVA_OPTS
touch test.pdf
"$VERAPDF" test.pdf
parseError=$?

echo
echo "RESULTS"
echo "======="
echo " - single pass exit code:   $resSinglePass"
echo " - single fail exit code:   $resSingleFail"
echo " - batch pass exit code:    $resBatchPass"
echo " - batch fail exit code:    $resBatchFail"
echo " - batch mixed exit code:   $resBatchPassFail"
echo " - bad params exit code:    $resBadParams"
echo " - out of memory exit code: $outOfMem"
echo " - parse error exit code:   $parseError"

! [[ $resSinglePass || $resSingleFail || $resBatchPass || $resBatchFail || $resBatchPassFail || $resBadParams || $outOfMem || $parseError ]]
exit
