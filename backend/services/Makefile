OS := $(shell uname 2>/dev/null || echo Windows)

cc:
	@if [ -z "$(s)" ]; then echo "Service is required"; exit 1; fi
	@if [ "$(OS)" = "Windows" ]; then \
		cd $(s) && cmd /c mvnw.cmd clean compile; \
	else \
		cd $(s) && ./mvnw clean compile; \
	fi
