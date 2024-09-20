# veraPDF-apps

*Command line and GUI industry supported PDF/A and PDF/UA Validation*

[![Build Status](https://jenkins.openpreservation.org/job/veraPDF/job/1.27/job/apps/badge/icon)](https://jenkins.openpreservation.org/job/veraPDF/job/1.27/job/apps/ "OPF Jenkins")
[![Maven Central](https://img.shields.io/maven-central/v/org.verapdf/verapdf-apps.svg)](https://repo1.maven.org/maven2/org/verapdf/verapdf-apps/ "Maven central")
[![CodeCov Coverage](https://img.shields.io/codecov/c/github/veraPDF/veraPDF-apps.svg)](https://codecov.io/gh/veraPDF/veraPDF-apps/ "CodeCov coverage")
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/ac55527c6ac04c3ab57c932c85c9be4d)](https://app.codacy.com/gh/veraPDF/veraPDF-apps/dashboard?utm_source=gh&utm_medium=referral&utm_content=&utm_campaign=Badge_grade "Codacy grade")

[![GitHub issues](https://img.shields.io/github/issues/veraPDF/veraPDF-library.svg)](https://github.com/veraPDF/veraPDF-library/issues "Open issues on GitHub")
[![GitHub issues](https://img.shields.io/github/issues-closed/veraPDF/veraPDF-library.svg)](https://github.com/veraPDF/veraPDF-library/issues?q=is%3Aissue+is%3Aclosed "Closed issues on GitHub")
[![GitHub issues](https://img.shields.io/github/issues-pr/veraPDF/veraPDF-apps.svg)](https://github.com/veraPDF/veraPDF-apps/pulls "Open pull requests on GitHub")
[![GitHub issues](https://img.shields.io/github/issues-pr-closed/veraPDF/veraPDF-apps.svg)](https://github.com/veraPDF/veraPDF-apps/pulls?q=is%3Apr+is%3Aclosed "Closed pull requests on GitHub")

Licensing
---------
The veraPDF PDF/A Validation Library is dual-licensed, see:

- [GPLv3+](LICENSE.GPL "GNU General Public License, version 3")
- [MPLv2+](LICENSE.MPL "Mozilla Public License, version 2.0")

## Documentation

See the [veraPDF documentation site](https://docs.verapdf.org/).

## Quick Start

### veraPDF GUI

#### Download release version

You can download a Java-based installer for the latest veraPDF GUI release [from our download site](https://software.verapdf.org/rel/verapdf-installer.zip). The current installation process requires Java 8, 11, 17 or 21 to be pre-installed.

#### Download latest development version

If you want to try the latest development version you can obtain it from our [development download site](https://software.verapdf.org/dev/verapdf-installer.zip). Be aware that we release development snapshots regularly, often more than once a day. While we try to ensure that development builds are well tested there are no guarantees.

#### Install from zip package

Once downloaded unzip the archive which contains the installer jar with batch and shell scripts to launch, the zip contents are as follows:

    verapdf-${project.version}/verapdf-install.bat
    verapdf-${project.version}/verapdf-install
    verapdf-${project.version}/verapdf-izpack-installer-${project.version}.jar

Where `${project.version}` is the last development or release version.

Windows users should run the 'verapdf-install.bat' batch file, while Linux and OSX users should run the shell script, `verapdf-install`. It's possible to run the installer directly on any platform:

    java -jar <path-to-installer-jar>/verapdf-izpack-installer-${project.version}.jar

#### Linux full command line download and install

Linux users can download and execute the veraPDF installer using the following commands:

    wget http://downloads.verapdf.org/rel/verapdf-installer.zip
    unzip verapdf-installer.zip
    cd verapdf-<version>
    ./verapdf-install

#### veraPDF GUI manual

We've prepared a manual for the GUI which is included in the library project and can be [downloaded from GitHub](https://github.com/veraPDF/veraPDF-apps/raw/integration/veraPDFPDFAConformanceCheckerGUI.pdf).

#### JVM configuration options

The startup script found in the install dir, e.g. `.../verapdf/verapdf-gui` for Linux, or `.../verapdf/verapdf-gui.bat` for Windows can be used to pass
configuration options to the JVM. This is done by setting `$JAVA_OPTS` for Linux, or `%JAVA_OPTS%` in the Window batch file. Alternatively these can be
passed directly as parameters when calling the shell or batch script.

## Building the veraPDF-apps from Source

### Pre-requisites

In order to build this project you'll need:

- Java 8, 11, 17 or 21, which can be downloaded [from Oracle](https://www.oracle.com/technetwork/java/javase/downloads/index.html), or for Linux users [OpenJDK](https://openjdk.java.net/install/index.html).
- [Maven v3+](https://maven.apache.org/)

Life will be easier if you also use [Git](https://git-scm.com/) to obtain and manage the source.

### Building veraPDF

First you'll need to obtain a version of the veraPDF-apps source code. You can compile either the latest release version or the latest development source.

#### Downloading the latest release source

Use Git to clone the repository and ensure that the `master` branch is checked out:

    git clone https://github.com/veraPDF/veraPDF-apps
    cd veraPDF-apps
    git checkout master

or download the latest [tar archive](https://github.com/veraPDF/veraPDF-apps/archive/master.tar.gz "veraPDF-apps latest GitHub tar archive") or [zip archive](https://github.com/veraPDF/veraPDF-apps/archive/master.zip "veraPDF-apps latest GitHub zip archive") from GitHub.

#### Downloading the latest development source

Use Git to clone the repository and ensure that the `integration` branch is checked out:

    git clone https://github.com/veraPDF/veraPDF-apps
    cd veraPDF-apps
    git checkout integration

or download the latest [tar archive](https://github.com/veraPDF/veraPDF-apps/archive/integration.tar.gz "veraPDF-apps latest GitHub tar archive") or [zip archive](https://github.com/veraPDF/veraPDF-apps/archive/integration.zip "veraPDF-apps latest GitHub zip archive") from GitHub.

#### Use Maven to compile the source

Call Maven install:

    mvn clean install

#### Testing the build

You can test your build by running the greenfield GUI application from the `greenfield-apps` sub-module.

    java -jar greenfield-apps/target/greenfield-apps-${project.version}.jar

Where `${project.version}` is the current Maven project version. This should bring up the veraPDF GUI main window if the build was successful.

## Building the Docker image

The accompanying [`Dockerfile`](Dockerfile) can be used to build a Docker image containing the veraPDF CLI and GUI applications. The image is based on the official Alpine image. It doesn't build the project, instead it downloads a version of the installer. It also builds a slimline JRE which is used to trim the final image size.

The version built is controlled by three arguments in the Dockerfile, `VERAPDF_VERSION`, `VERAPDF_MINOR_VERSION` and `VERAPDF_INSTALLER_FOLDER`. These can be used to select a specific installer at invocation time. The default values are `VERAPDF_VERSION=1.26`, `VERAPDF_MINOR_VERSION=2` and `VERAPDF_INSTALLER_FOLDER=releases`, which builds the latest production version, e.g. `1.26.2`.

To build and run the very latest version:

    docker build -t verapdf .
    docker run -it -v "$(pwd)":/data --name verapdf verapdf a.pdf 

To build a specific version, e.g. `1.22.3`:

    docker build --build-arg VERAPDF_VERSION=1.22 --build-arg VERAPDF_MINOR_VERSION=3 -t verapdf .

To build a specific development version argument `VERAPDF_INSTALLER_FOLDER` should be set to `develop`.
