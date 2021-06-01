In order to setup properly, the following needs to be done:
The javafx SDK needs to be downloaded: gluonhq.com/products/javafx/ - you want
the SDK, not jmods
You need to provide the JVM with the appropriate arguments as follows:

1) javac -cp "pathToJavaFiles;pathToSDKLib" --module-path "path to SDKLib" --add-modules javafx.controls,javafx.fxml *.java
2) java -cp "pathToJavaFiles;pathToSDKLib" --module-path "path to SDKLib"
--add-modules javafx.controls,javafx.fxml Gui

Note that the quotes are required for windows, and the semicolon is a colon on
linux systems. I would keep the quotes for both systems.
