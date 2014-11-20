#!/bin/bash

# Configuration stuff

num_files="$2"
echo $num_files
# Work out lines per file.

total_lines=$(wc -l <"$1")
((lines_per_file = 1 + (total_lines + num_files - 1) / num_files))
echo $lines_per_file

split -dl ${lines_per_file} "$1" "$1".