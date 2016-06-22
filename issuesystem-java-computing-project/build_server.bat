IF NOT EXIST build mkdir build
cd build
IF NOT EXIST server mkdir server
cd ../src/shared
javac *.java -d ../../build/server
cd ../server
javac *.java -d ../../build/server -classpath ../../build/server
cd ../../build/server
jar cfm ../../bin/IssueSystemManager.jar ../ServerManifest.txt *.class
pause