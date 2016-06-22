IF NOT EXIST build mkdir build
cd build
IF NOT EXIST master mkdir master
cd ../src/shared
javac *.java -d ../../build/master
cd ../master
javac *.java -d ../../build/master -classpath ../../build/master
cd ../../build/master
jar cfm ../../bin/IssueSystemMaster.jar ../MasterManifest.txt *.class
pause