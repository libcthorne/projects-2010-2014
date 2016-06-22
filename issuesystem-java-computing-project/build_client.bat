IF NOT EXIST build mkdir build
cd build
IF NOT EXIST client mkdir client
cd ../src/shared
javac *.java -d ../../build/client
cd ../client
javac *.java -d ../../build/client -classpath ../../build/client
cd ../../build/client
jar cfm ../../bin/IssueSystemReporter.jar ../ClientManifest.txt *.class
pause