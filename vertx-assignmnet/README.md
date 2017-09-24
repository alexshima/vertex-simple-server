#  Vert.x Server


To run:

```
mvn org.codehaus.mojo:exec-maven-plugin:exec -Dexec.executable=java \
	-Dexec.args="-cp %classpath io.vertx.core.Launcher run test.project1.Server"
```

To test:

```
curl -D- http://localhost:8000/analyze -d '{"text":"Abee"}'

```
You might need to compile:
```
mvn compile
```
