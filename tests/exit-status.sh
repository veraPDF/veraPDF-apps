#!/usr/bin/env bash
# Grab the execution directory
SCRIPT_DIR="$( dirname "$( readlink -f "${BASH_SOURCE[0]}" )")"
export SCRIPT_DIR

cd /tmp || exit
if  [[ ! -e "./veraPDF-corpus-master" ]]
then
  wget https://github.com/veraPDF/veraPDF-corpus/archive/master.zip
  unzip master.zip
  rm master.zip
fi
./verapdf/verapdf "./veraPDF-corpus-master/PDF_A-1b/6.1 File structure/6.1.4 Cross reference table/veraPDF test suite 6-1-4-t03-pass-b.pdf"
resSinglePass=$?
./verapdf/verapdf "./veraPDF-corpus-master/PDF_A-1b/6.1 File structure/6.1.4 Cross reference table/veraPDF test suite 6-1-4-t02-fail-a.pdf"
resSingleFail=$?
./verapdf/verapdf ./veraPDF-corpus-master/PDF_A-1b/6.1\ File\ structure/6.1.5\ Document\ information\ dictionary/veraPDF\ test\ suite\ 6-1-5-t02-pass-a.pdf ./veraPDF-corpus-master/PDF_A-1b/6.1\ File\ structure/6.1.5\ Document\ information\ dictionary/veraPDF\ test\ suite\ 6-1-5-t02-pass-b.pdf ./veraPDF-corpus-master/PDF_A-1b/6.1\ File\ structure/6.1.5\ Document\ information\ dictionary/veraPDF\ test\ suite\ 6-1-5-t02-pass-c.pdf
resBatchPass=$?
./verapdf/verapdf ./veraPDF-corpus-master/PDF_A-1b/6.1 File\ structure/6.1.5\ Document\ information\ dictionary/veraPDF\ test\ suite\ 6-1-5-t01-fail-a.pdf ./veraPDF-corpus-master/PDF_A-1b/6.1\ File\ structure/6.1.5\ Document\ information\ dictionary/veraPDF\ test\ suite\ 6-1-5-t01-fail-b.pdf ./veraPDF-corpus-master/PDF_A-1b/6.1\ File\ structure/6.1.5\ Document\ information\ dictionary/veraPDF\ test\ suite\ 6-1-5-t01-fail-c.pdf
resBatchFail=$?
./verapdf/verapdf "./veraPDF-corpus-master/PDF_A-1b/6.1 File structure/6.1.5 Document information dictionary/"
resBatchPassFail=$?
./verapdf/verapdf -f jbnd -m --params --help
resBadParams=$?
export JAVA_OPTS="-Xmx2200k"
./verapdf/verapdf "./veraPDF-corpus-master/PDF_A-1b/6.1 File structure/6.1.4 Cross reference table/"
outOfMem=$?
unset JAVA_OPTS
touch test.pdf
./verapdf/verapdf test.pdf
parseError=$?
echo ""
echo "RESULTS"
echo "======="
echo " - single pass exit code:   ${resSinglePass}"
echo " - single fail exit code:   ${resSingleFail}"
echo " - batch pass exit code:    ${resBatchPass}"
echo " - batch fail exit code:    ${resBatchFail}"
echo " - batch mixed exit code:   ${resBatchPassFail}"
echo " - bad params exit code:    ${resBadParams}"
echo " - Out of memory exit code: ${outOfMem}"
echo " - parse error exit code:   ${parseError}"
exit
