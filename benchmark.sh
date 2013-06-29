#/bin/sh
mvn -P $1 clean compile test -Dtest=org.thymeleaf.benchmark.BenchmarkTest

