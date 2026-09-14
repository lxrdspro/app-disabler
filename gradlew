#!/usr/bin/env sh

#
# Copyright 2015 the original author or authors.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      https://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

##############################################################################
##
##  Gradle start up script for UN*X
##
##############################################################################

# Attempt to set APP_HOME
# Resolve links: $0 may be a link
PRG="$0"
# Need this for relative symlinks.
while [ -h "$PRG" ] ; do
    ls -ld "$PRG"
    LINK=`expr "$PRG" : '.*-> \(.*\)$'`
    if expr "$LINK" : '/.*' > /dev/null; then
        PRG="$LINK"
    else
        PRG=`dirname "$PRG"`"/$LINK"
    fi
done
SAVED="`pwd`"
cd "`dirname \"$PRG\"`/" >/dev/null
APP_HOME="`pwd -P`"
cd "$SAVED" >/dev/null

APP_NAME="Gradle"
APP_BASE_NAME=`basename "$0"`

# Add default JVM options here. You can also use JAVA_OPTS and GRADLE_OPTS to pass JVM options to this script.
DEFAULT_JVM_OPTS='"-Xmx64m" "-Xms64m"'

# Use the maximum available, or set MAX_FD != null to use that value.
MAX_FD="maximum"

warn () {
    echo "$*"
}

die () {
    echo
    echo "$*"
    echo
    exit 1
}

# OS specific support (must be 'true' or 'false').
cygwin=false
msys=false
darwin=false
nonstop=false
case "`uname`" in
  CYGWIN* )
    cygwin=true
    ;;
  Darwin* )
    darwin=true
    ;;
  MINGW* )
    msys=true
    ;;
  NONSTOP* )
    nonstop=true
    ;;
esac

# Determine the Java command to use to start the JVM.
if [ -n "$JAVA_HOME" ] ; then
    if [ -x "$JAVA_HOME/jre/sh/java" ] ; then
        # IBM's JDK on AIX uses strange locations for the executables
        JAVACMD="$JAVA_HOME/jre/sh/java"
    else
        JAVACMD="$JAVA_HOME/bin/java"
    fi
    if [ ! -x "$JAVACMD" ] ; then
        die "ERROR: JAVA_HOME is set to an invalid directory: $JAVA_HOME

Please set the JAVA_HOME variable in your environment to match the
location of your Java installation."
    fi
else
    JAVACMD="java"
    which java >/dev/null 2>&1 || die "ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.

Please set the JAVA_HOME variable in your environment to match the
location of your Java installation."
fi

# Increase the maximum file descriptors if we can.
if [ "$cygwin" = "false" -a "$darwin" = "false" -a "$nonstop" = "false" ] ; then
    MAX_FD_LIMIT=`ulimit -H -n`
    if [ $? -eq 0 ] ; then
        if [ "$MAX_FD" = "maximum" -o "$MAX_FD" = "max" ] ; then
            MAX_FD="$MAX_FD_LIMIT"
        fi
        ulimit -n $MAX_FD
        if [ $? -ne 0 ] ; then
            warn "Could not set maximum file descriptor limit: $MAX_FD"
        fi
    else
        warn "Could not query maximum file descriptor limit: $MAX_FD_LIMIT"
    fi
fi

# For Darwin, add options to specify how the application appears in the dock
if $darwin; then
    GRADLE_OPTS="$GRADLE_OPTS \"-Xdock:name=$APP_NAME\" \"-Xdock:icon=$APP_HOME/media/gradle.icns\""
fi

# For Cygwin or MSYS, switch paths to Windows format before running java
if [ "$cygwin" = "true" -o "$msys" = "true" ] ; then
    APP_HOME=`cygpath --path --mixed "$APP_HOME"`
    CLASSPATH=`cygpath --path --mixed "$CLASSPATH"`

    JAVACMD=`cygpath --unix "$JAVACMD"`

    # We build the pattern for arguments to be converted via cygpath
    ROOTDIRSRAW=`find -L / -maxdepth 3 -type d -name gradle 2>/dev/null | head -n 1`
    SEP=""
    for dir in $ROOTDIRSRAW ; do
        ROOTDIR="$dir"
        SEP=":"
        break
    done
    if [ -n "$ROOTDIR" ] ; then
        ROOTDIR=`cygpath --path --ignore --mixed "$ROOTDIR"`
        CLASSPATH="$ROOTDIR/lib/gradle-bootstrap.jar:$CLASSPATH"

        # Determine the Java command to use to start the JVM.
        if [ -n "$JAVA_HOME" ] ; then
            JAVACMD="$JAVA_HOME/bin/java"
        else
            JAVACMD=`cygpath --unix "$JAVACMD"`
        fi
    fi
    if [ -n "$CLASSPATH" ] ; then
        CLASSPATH=`cygpath --path --mixed "$CLASSPATH"`
    fi

    if [ "$1" = "-?" -o "$1" = "-h" -o "$1" = "--help" ] ; then
        echo "Usage: $0 [<option>...] [<task>...]"
        echo ""
        echo "Where <option> includes:"
        echo "    -?: Show this help message."
        echo "    -a, --project-cache-dir: Specify the project-specific cache directory. By default, this is the .gradle directory in the root of the source tree."
        echo "    -b, --build-file: Specify the build file."
        echo "    -c, --settings-file: Specify the settings file."
        echo "    --configure-on-demand: Configure necessary projects in order to execute the requested tasks. This means that only the projects that are needed are configured, which can lead to faster builds for large multi-project builds."
        echo "    --console: Specifies which type of console output to generate. Values are 'plain', 'auto' (default), 'rich' or 'verbose'. You can also use system property org.gradle.console with the same values."
        echo "    --continue: Continues task execution after a task failure."
        echo "    -D, --system-prop: Set a system property of the JVM, for example -Dmyprop=myvalue. You can also use it for gradle properties, for example -Dorg.gradle.jvmargs=-Xmx1024m."
        echo "    -d, --debug: Log in debug mode (includes normal stacktrace)."
        echo "    --daemon: Use the Gradle daemon to run the build. Starts the daemon if not running."
        echo "    --foreground: Starts the Gradle daemon in the foreground."
        echo "    -g, --gradle-user-home: Specifies the gradle user home directory. The default is a directory named .gradle in your user home directory."
        echo "    -I, --init-script: Specify an initialization script."
        echo "    -i, --info: Set log level to info."
        echo "    --include-build: Include the specified build in the composite."
        echo "    -m, --dry-run: Run the build with all task actions disabled."
        echo "    --max-workers: Configure the number of workers Gradle is allowed to use."
        echo "    --no-build-cache: Disables the build cache."
        echo "    --no-configure-on-demand: Disables the use of configuration on demand."
        echo "    --no-daemon: Do not use the Gradle daemon to run the build. Useful when using the CI server where it is preferable to execute builds in the processs."
        echo "    --no-parallel: Disables parallel execution to build the project."
        echo "    --no-scan: Disables the creation of build scans. For more information about build scans, please visit https://gradle.com/build-scans."
        echo "    -o, --offline: Execute the build without accessing network resources."
        echo "    -P, --project-prop: Set a project property of the root project, for example -Pmyprop=myvalue."
        echo "    -p, --project-dir: Specifies the start directory for the gradle build. By default, this is the directory where the build is executed."
        echo "    --parallel: Build projects in parallel. Gradle will attempt to determine the optimal number of executor threads to use."
        echo "    --profile: Profile the build and write a HTML profile report to the build/reports/profile directory."
        echo "    --progress: Set progress output type to determinate or indeterminate (default: determinate). Use this when the build output is redirected to a file and it's not possible to detect whether or not the build is running in an interactive environment."
        echo "    -q, --quiet: Log errors only."
        echo "    --scan: Creates a build scan."
        echo "    -s, --stacktrace: Print out the stacktrace for all exceptions."
        echo "    -S, --full-stacktrace: Print out the full (very verbose) stacktrace for all exceptions."
        echo "    -t, --continuous: Enables continuous build. Gradle does not exit and will re-execute tasks when file inputs change."
        echo "    -u, --no-search-upward: Do not search in parent folders for a settings.gradle file."
        echo "    -v, --version: Print the version of Gradle you are using."
        echo "    -w, --project-cache-dir: Specify the project-specific cache directory. By default, this is the .gradle directory in the root of the source tree."
        echo "    -x, --exclude-task: Specify a task to be excluded from execution."
        echo ""
        exit 0
    fi
    if [ "$1" != "" ] ; then
        shift
    fi
    set -- "$@" "-Dorg.gradle.appname=$APP_BASE_NAME"
    # Escape application args
    save () {
        for i do printf %s\\n "$i" | sed "s/'/'\\\\''/g;1s/^/'/;\$s/\$/' \\\\/" ; done
        echo " "
    }
    APP_ARGS=$(save "$@")
    eval set -- $DEFAULT_JVM_OPTS $JAVA_OPTS $GRADLE_OPTS "\"-Dorg.gradle.appname=$APP_BASE_NAME\"" -classpath "\"$CLASSPATH\"" org.gradle.wrapper.GradleWrapperMain "$APP_ARGS"
else
    # For Mingw / MSYS, use the bash builtin test instead of [ ... ]
    if ! test -x "$JAVACMD" ; then
        die "ERROR: JAVA_HOME is not defined correctly in your environment. Please make sure that JAVA_HOME contains a valid installation of a Java Development Kit."
    fi

    APP_ARGS=()
    for i do
        j=$((i+1))
        eval "arg=\${$j}"
        case $arg in
            -*)  false;;
            *)   i=$(expr $i + 1)
                 APP_ARGS[${#APP_ARGS[@]}]="$arg"
                 ;;
        esac
    done

    exec "$JAVACMD" $DEFAULT_JVM_OPTS $JAVA_OPTS $GRADLE_OPTS -classpath "$CLASSPATH" org.gradle.wrapper.GradleWrapperMain "${APP_ARGS[@]}"
fi

exec "$JAVACMD" "${JVM_OPTS[@]}" -classpath "$CLASSPATH" org.gradle.wrapper.GradleWrapperMain "$@"
