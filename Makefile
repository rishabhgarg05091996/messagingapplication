default:
	mvn package
	docker-compose down
	docker-compose up -d --force-recreate --build
	docker logs -f codingassignment