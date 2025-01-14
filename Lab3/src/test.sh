#!/usr/bin/env bash
# This stops the script when a command's exit code is non-zero (i.e., error).
set -e

T=2
S=GlobalLog
D=Normal
V=100000
A=1
R=1
C=8
O=100000
W=5
M=10


javac Main.java

for T in 1 2 4 8 16 32 48; do
        echo "$T ----------------------------------------------" 
        java Main $T $S $D $V $A:$R:$C $O $W $M
done
