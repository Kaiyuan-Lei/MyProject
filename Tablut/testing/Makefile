# This makefile is defined to give you the following targets:
#
#    default: Same as check
#    check: For each file, F-1.in, use F-1.in and if it exists, 
#           F-2.in as arguments to test-amazons.  This will produce files
#           F-1.out (and F-2.out, if F-2.in is present); compare them to
#           to F-1.std (and F-2.std) and report discrepencies.
#    clean: Remove all the .class files produced by java compilation, 
#          all Emacs backup files, and testing output files.
#    outputs: Create .std files from current application and .in files.
#
# You can use this file without understanding most of it, of course, but
# I strongly recommend that you try to figure it out, and where you cannot,
# that you ask questions.  The Lab Reader contains documentation.

SHELL = /bin/bash

PYTHON = python3

# Flags to Java interpreter: check assertions
JFLAGS = -ea

# Name of class containing main procedure 
MAIN = tablut.Main

TESTER_FLAGS = --show=-1

TESTS := $(wildcard *-1.in)

.PHONY: default check clean outputs

# First, and therefore default, target.
default: compile
	"$(MAKE)" check

compile:
	"$(MAKE)" -C ..

# A non-standard classpath that works Linux, Mac, and Windows.
# We include everything in CLASSPATH, plus the proj2 directory itself
# (from which one should be able to find the amazons classes).
CPATH = "..:$(CLASSPATH):;..;$(CLASSPATH)"

# 'make check' will run the tests in the testing directory.
# For each .in file, it runs test-amazons, which is described in the
# project specification. The "CLASSPATH=..." clause in front of 
# the command below temporarily changes the location in which Java searches
# for classes to be the directory that contains your project.
check: 
	@echo "Testing application $(MAIN)..."
	@CLASSPATH=$(CPATH) "$(PYTHON)" tester.py $(TESTER_FLAGS) $(TESTS)

# 'make tidy' will clean up stuff you don't need.
clean:
	$(RM) -r *~ *.out *.err __pycache__
