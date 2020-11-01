jacocoReportPath := "./target/site/jacoco/index.html"
mvn := "./mvnw"

.PHONY: build
build: clean
	@$(mvn) package

.PHONY: test
test: clean
	@$(mvn) test

.PHONY: coverage-check
coverage-check:
	@if [[ ! -f "$(jacocoReportPath)" ]]; then $(mvn) package; fi
	@open $(jacocoReportPath)

.PHONY: run
run:
	@java -Xmx512m -Xms256m -jar target/http-log-monitor-*.jar $(args)

.PHONY: clean
clean:
	@./mvnw -q clean

