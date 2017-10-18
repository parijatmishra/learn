#!/bin/bash
FILE=$1
PNG=${FILE%.txt}.png
java -jar ~/Software/plantuml.jar $FILE
open $PNG
