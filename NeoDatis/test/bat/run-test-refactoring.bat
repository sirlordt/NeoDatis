set classpath=../../bin;../../lib/ext/javassist.jar;../../lib/junit.jar
@echo off

java org.neodatis.odb.test.refactoring.Result start

java org.neodatis.odb.test.refactoring.TestRefactoring1 step1
java org.neodatis.odb.test.refactoring.TestRefactoring1 step2
java org.neodatis.odb.test.refactoring.TestRefactoring1 step3
java org.neodatis.odb.test.refactoring.TestRefactoring1 step4

java org.neodatis.odb.test.refactoring.TestRefactoring2 step1
java org.neodatis.odb.test.refactoring.TestRefactoring2 step2
java org.neodatis.odb.test.refactoring.TestRefactoring2 step3
java org.neodatis.odb.test.refactoring.TestRefactoring2 step4
java org.neodatis.odb.test.refactoring.TestRefactoring2 step5
java org.neodatis.odb.test.refactoring.TestRefactoring2 step6

java org.neodatis.odb.test.refactoring.TestRefactoring3 step1
java org.neodatis.odb.test.refactoring.TestRefactoring3 step2

java org.neodatis.odb.test.refactoring.Result end
