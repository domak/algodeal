#!/bin/sh

if test -f ../../eclipse/MyStrategies/libs/marketrunner-gui-1.7.0.jar; then MARKET_RUNNER_FILENAME=../../eclipse/MyStrategies/libs/marketrunner-gui-1.7.0.jar ; fi
if test -f ../libs/marketrunner-gui-1.7.0.jar; then MARKET_RUNNER_FILENAME=../libs/marketrunner-gui-1.7.0.jar ; fi
if test -f libs/marketrunner-gui-1.7.0.jar; then MARKET_RUNNER_FILENAME=libs/marketrunner-gui-1.7.0.jar ; fi
java -Xmx1024m -DmarketRunner.marketData.path=../../eclipse/MyStrategies/marketData -jar $MARKET_RUNNER_FILENAME $1 $2 $3 $4 $5 $6 $7 $8 $9