@echo off
set DIR=%~dp0
set JAVA_EXE=java
if defined JAVA_HOME set JAVA_EXE=%JAVA_HOME%\bin\java.exe
"%JAVA_EXE%" -classpath "%DIR%gradle\wrapper\*" org.gradle.wrapper.GradleWrapperMain %*
