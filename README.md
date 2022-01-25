veraPDF-apps
===============
*Command line and GUI industry supported PDF/A Validation*

[![Build Status](https://travis-ci.org/veraPDF/veraPDF-apps.svg?branch=integration)](https://travis-ci.org/veraPDF/apps/ "Travis-CI")
[![Build Status](http://jenkins.openpreservation.org/buildStatus/icon?job=veraPDF-apps)](http://jenkins.openpreservation.org/job/veraPDF-apps/ "OPF Jenkins Release")
[![Build Status](http://jenkins.openpreservation.org/buildStatus/icon?job=veraPDF-apps-dev)](http://jenkins.openpreservation.org/job/veraPDF-apps-dev/ "OPF Jenkins Development")
[![Maven Central](https://img.shields.io/maven-central/v/org.verapdf/verapdf-apps.svg)](http://repo1.maven.org/maven2/org/verapdf/verapdf-apps/ "Maven central")
[![CodeCov Coverage](https://img.shields.io/codecov/c/github/veraPDF/veraPDF-apps.svg)](https://codecov.io/gh/veraPDF/veraPDF-apps/ "CodeCov coverage")
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/881d40fed7b54552839a347575e3ad80)](https://www.codacy.com/app/carlwilson/veraPDF-apps?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=veraPDF/veraPDF-apps&amp;utm_campaign=Badge_Grade)

[![GitHub issues](https://img.shields.io/github/issues/veraPDF/veraPDF-apps.svg)](https://github.com/veraPDF/veraPDF-apps/issues "Open issues on GitHub")
[![GitHub issues](https://img.shields.io/github/issues-closed/veraPDF/veraPDF-apps.svg)](https://github.com/veraPDF/veraPDF-apps/issues-closed "Open issues on GitHub")
[![GitHub issues](https://img.shields.io/github/issues-pr/veraPDF/veraPDF-apps.svg)](https://github.com/veraPDF/veraPDF-apps/issues-pr "Open issues on GitHub")
[![GitHub issues](https://img.shields.io/github/issues-pr-closed/veraPDF/veraPDF-apps.svg)](https://github.com/veraPDF/veraPDF-apps/issues-pr-closed "Open issues on GitHub")

Licensing
---------
The veraPDF PDF/A Validation Library is dual-licensed, see:

 - [GPLv3+](LICENSE.GPL "GNU General Public License, version 3")
 - [MPLv2+](LICENSE.MPL "Mozilla Public License, version 2.0")

Documentation
-------------
See the [veraPDF documentation site](http://docs.verapdf.org/).

Quick Start
-----------

### veraPDF GUI
#### Download release version
You can download a Java-based installer for the latest veraPDF GUI release [from our download site](https://software.verapdf.org/rel/verapdf-installer.zip). The current installation process requires Java 1.7 to be pre-installed.

#### Download latest development version
If you want to try the latest development version you can obtain it from our [development download site](https://software.verapdf.org/dev/verapdf-installer.zip). Be aware that we release development snapshots regularly, often more than once a day. While we try to ensure that development builds are well tested there are no guarantees.

#### Install from zip package
Once downloaded unzip the archive which contains the installer jar with batch and shell scripts to launch, the zip contents are as follows:

    verapdf-${project.version}/verapdf-install.bat
    verapdf-${project.version}/verapdf-install.sh
    verapdf-${project.version}/verapdf-izpack-installer-${project.version}.jar

Where `${project.version}` is the last development or release version.

Windows users should run the 'verapdf-install.bat' dos batch file, while Linux and OSX users should run the shell script, `verapdf-install.sh`. It's possible to run the installer directly on any platform:

    java - jar <path-to-installer-jar>/verapdf-izpack-installer-${project.version}.jar

#### Linux full command line download and install
Linux users can download and execute the veraPDF installer using the following commands:

    wget http://downloads.verapdf.org/rel/verapdf-installer.zip
    unzip verapdf-installer.zip
    cd verapdf-<version>
    ./verapdf-install.sh

#### veraPDF GUI manual
We've prepared a manual for the GUI which is included in the library project and can be [downloaded from GitHub](https://github.com/veraPDF/veraPDF-apps/raw/release-1.4/veraPDFPDFAConformanceCheckerGUI.pdf).

#### JVM configuration options
The startup script found in the install dir, e.g. `.../verapdf/verapdf-gui` for Linux, or `.../verapdf/verapdf-gui.bat` for Windows can be used to pass
configuration options to the JVM. This is done by setting `$JAVA_OPTS` for Linux, or `%JAVA_OPTS%` in the Window batch file. Alternatively these can be
passed directly as parameters when calling the shell or batch script.

Building the veraPDF-apps from Source
----------------------------------------
### Pre-requisites

In order to build this project you'll need:

 * Java 7, which can be downloaded [from Oracle](http://www.oracle.com/technetwork/java/javase/downloads/index.html), or for Linux users [OpenJDK](http://openjdk.java.net/install/index.html).
 * [Maven v3+](https://maven.apache.org/)

Life will be easier if you also use [Git](https://git-scm.com/) to obtain and manage the source.

### Building veraPDF
First you'll need to obtain a version of the veraPDF-apps source code. You can compile either the latest relase version or the latest development source.
#### Downloading the latest release source
Use Git to clone the repository and ensure that the `master` branch is checked out:
```
git clone https://github.com/veraPDF/veraPDF-apps
git checkout master
```
or download the latest [tar archive](https://github.com/veraPDF/veraPDF-apps/archive/master.tar.gz "veraPDF-apps latest GitHub tar archive") or [zip archive](https://github.com/veraPDF/veraPDF-apps/archive/master.zip "veraPDF-apps latest GitHub zip archive") from GitHub.

#### Downloading the latest development source
Use Git to clone the repository and ensure that the `integration` branch is checked out:

    git clone https://github.com/veraPDF/veraPDF-apps
    git checkout integration

or download the latest [tar archive](https://github.com/veraPDF/veraPDF-apps/archive/integration.tar.gz "veraPDF-apps latest GitHub tar archive") or [zip archive](https://github.com/veraPDF/veraPDF-apps/archive/integration.zip "veraPDF-apps latest GitHub zip archive") from GitHub.

#### Use Maven to compile the source
Move to the downloaded project directory and call Maven install:

    cd veraPDF-apps
    mvn clean install

#### Testing the build
You can test your build by running the greenfield GUI application from the `greenfield-apps` sub-module.

    java -jar greenfield-apps/target/greenfield-apps-${project.version}.jar

Where `${project.version}` is the current Maven project version. This should bring up the veraPDF GUI main window if the build was successful.
