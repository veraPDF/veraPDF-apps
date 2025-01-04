#!/usr/bin/env bash
#
# This file is part of veraPDF PDF/A Validation Applications, a module of the veraPDF project.
# Copyright (c) 2015-2025, veraPDF Consortium <info@verapdf.org>
# All rights reserved.
#
# veraPDF PDF/A Validation Applications is free software: you can redistribute it and/or modify
# it under the terms of either:
#
# The GNU General public license GPLv3+.
# You should have received a copy of the GNU General Public License
# along with veraPDF PDF/A Validation Applications as the LICENSE.GPL file in the root of the source
# tree.  If not, see http://www.gnu.org/licenses/ or
# https://www.gnu.org/licenses/gpl-3.0.en.html.
#
# The Mozilla Public License MPLv2+.
# You should have received a copy of the Mozilla Public License along with
# veraPDF PDF/A Validation Applications as the LICENSE.MPL file in the root of the source tree.
# If a copy of the MPL was not distributed with this file, you can obtain one at
# http://mozilla.org/MPL/2.0/.
#


function echo() {
  command echo -e "$@"
}

if (( NO_CD != 1 )); then
  cd /tmp || exit
fi

if [[ ! -e "./veraPDF-corpus-master" ]]; then
  curl -LO https://github.com/veraPDF/veraPDF-corpus/archive/master.zip
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
"$VERAPDF" "./veraPDF-corpus-master/PDF_A-1b/6.1 File structure/6.1.5 Document information dictionary/veraPDF test suite 6-1-5-t02-pass-a.pdf" "./veraPDF-corpus-master/PDF_A-1b/6.1 File structure/6.1.5 Document information dictionary/veraPDF test suite 6-1-5-t02-pass-b.pdf" "./veraPDF-corpus-master/PDF_A-1b/6.1 File structure/6.1.5 Document information dictionary/veraPDF test suite 6-1-5-t02-pass-c.pdf"
resBatchPass=$?
"$VERAPDF" "./veraPDF-corpus-master/PDF_A-1b/6.1 File structure/6.1.5 Document information dictionary/veraPDF test suite 6-1-5-t01-fail-a.pdf" "./veraPDF-corpus-master/PDF_A-1b/6.1 File structure/6.1.5 Document information dictionary/veraPDF test suite 6-1-5-t01-fail-b.pdf" "./veraPDF-corpus-master/PDF_A-1b/6.1 File structure/6.1.5 Document information dictionary/veraPDF test suite 6-1-5-t01-fail-c.pdf"
resBatchFail=$?
"$VERAPDF" "./veraPDF-corpus-master/PDF_A-1b/6.1 File structure/6.1.5 Document information dictionary/"
resBatchPassFail=$?
"$VERAPDF" -f jbnd -m --params --help
resBadParams=$?
export JAVA_OPTS="-Xmx2200k"
"$VERAPDF" "./veraPDF-corpus-master/PDF_A-1b/6.1 File structure/6.1.4 Cross reference table/"
resOutOfMem=$?
unset JAVA_OPTS
touch test.pdf
"$VERAPDF" test.pdf
resParseError=$?

expSinglePass=0
expSingleFail=1
expBatchPass=0
expBatchFail=1
expBatchPassFail=1
expBadParams=2
expOutOfMem=3
expOutOfMemAlt=1 # HACK: accept alternative exit code 1 for out-of-memmory errors, for the time being.
expParseError=7

echo
echo "RESULTS"
echo "======="
echo " - single pass exit code:   \t$resSinglePass\t(expected $expSinglePass)"
echo " - single fail exit code:   \t$resSingleFail\t(expected $expSingleFail)"
echo " - batch pass exit code:    \t$resBatchPass\t(expected $expBatchPass)"
echo " - batch fail exit code:    \t$resBatchFail\t(expected $expBatchFail)"
echo " - batch mixed exit code:   \t$resBatchPassFail\t(expected $expBatchPassFail)"
echo " - bad params exit code:    \t$resBadParams\t(expected $expBadParams)"
echo " - out of memory exit code: \t$resOutOfMem\t(expected $expOutOfMem or $expOutOfMemAlt)"
echo " - parse error exit code:   \t$resParseError\t(expected $expParseError)"

[[ $resSinglePass == $expSinglePass && $resSingleFail == $expSingleFail && $resBatchPass == $expBatchPass && $resBatchFail == $expBatchFail && $resBatchPassFail == $expBatchPassFail && $resBadParams == $expBadParams && ($resOutOfMem == $expOutOfMem || $resOutOfMem = $expOutOfMemAlt) && $resParseError == $expParseError ]]
passed=$?

echo
if [[ $passed == 0 ]]; then
	echo "PASSED"
else
	echo "FAILED"
fi

exit $passed
