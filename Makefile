APP_NAME=file-search-app
IMAGE_NAME=file-search-image
APP_PORT_1=8081
APP_PORT_2=8082

POSTGRES_CONTAINER=pg-db
POSTGRES_USER=appuser
POSTGRES_PASSWORD=apppass
POSTGRES_DB=appdb
POSTGRES_PORT=5432

CONTAINER_ENGINE := $(shell command -v podman 2> /dev/null)

.PHONY: run build start

build:
	$(CONTAINER_ENGINE) build \
	  --network host \
	  --security-opt label=disable \
	  -v /run/podman/podman.sock:/var/run/podman.sock:Z \
	  -t $(IMAGE_NAME):latest .

start:
	# The "ss" is "Socket Statictics" command, which is used to check for open ports.
	# -l: listen; -n: show numerical addresses; -t: TCP sockets
	@if ss -lnt | grep -q -E ':($(APP_PORT_1)|$(APP_PORT_2))\b'; then \
		echo "Error: One of the ports $(APP_PORT_1) or $(APP_PORT_2) is already in use."; \
		exit 1; \
	fi

	@if ! podman ps --format '{{.Names}}' | grep -q '^$(POSTGRES_CONTAINER)$$'; then \
		echo "Starting postgres container..."; \
		podman run -d --rm --name $(POSTGRES_CONTAINER) \
			-e POSTGRES_USER=$(POSTGRES_USER) \
			-e POSTGRES_PASSWORD=$(POSTGRES_PASSWORD) \
			-e POSTGRES_DB=$(POSTGRES_DB) \
			-p $(POSTGRES_PORT):5432 \
			postgres:17; \
	else \
		echo "Postgres container already running."; \
	fi
	podman run -d --rm --name $(APP_NAME)-1 \
		-e SPRING_DATASOURCE_URL=jdbc:postgresql://host.containers.internal:$(POSTGRES_PORT)/$(POSTGRES_DB) \
		-e SPRING_DATASOURCE_USERNAME=$(POSTGRES_USER) \
		-e SPRING_DATASOURCE_PASSWORD=$(POSTGRES_PASSWORD) \
		-p $(APP_PORT_1):8080 \
		--user user1 \
		$(IMAGE_NAME)

	podman run -d --rm --name $(APP_NAME)-2 \
		-e SPRING_DATASOURCE_URL=jdbc:postgresql://host.containers.internal:$(POSTGRES_PORT)/$(POSTGRES_DB) \
		-e SPRING_DATASOURCE_USERNAME=$(POSTGRES_USER) \
		-e SPRING_DATASOURCE_PASSWORD=$(POSTGRES_PASSWORD) \
		-p $(APP_PORT_2):8080 \
		--user user2 \
		$(IMAGE_NAME)

run: build start