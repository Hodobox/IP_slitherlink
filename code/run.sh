#!/usr/bin/bash
javac Go.java -classpath ../choco-solver-4.10.2.jar Slitherlink.java
java -classpath .:../choco-solver-4.10.2.jar Go $1 -solve -brief