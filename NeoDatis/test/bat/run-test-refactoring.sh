echo starting
echo @pwd
export CLASSPATH=../../bin:../../lib/ext/javassist.jar:../../lib/junit.jar
echo Classpath = $CLASSPATH
java -classpath $CLASSPATH org.neodatis.odb.test.refactoring.Result start
echo java -classpath $CLASSPATH org.neodatis.odb.test.refactoring.TestRefactoring1 step1
