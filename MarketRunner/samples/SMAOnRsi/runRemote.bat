IF EXIST ../../eclipse/MyStrategies/libs/marketrunner-gui-1.7.0.jar set MARKET_RUNNER_FILENAME=../../eclipse/MyStrategies/libs/marketrunner-gui-1.7.0.jar
IF EXIST ../libs/marketrunner-gui-1.7.0.jar set MARKET_RUNNER_FILENAME=../libs/marketrunner-gui-1.7.0.jar
IF EXIST libs/marketrunner-gui-1.7.0.jar set MARKET_RUNNER_FILENAME=libs/marketrunner-gui-1.7.0.jar
java -jar %MARKET_RUNNER_FILENAME% -url https://beta.algodeal.com %1 %2 %3 %4 %5 %6 %7 %8 %9
