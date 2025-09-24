target = target/marketing-data-import-new-etl-0.1.jar

.PHONY: build

test:
	mvn clean package
	zip -d $(target) 'META-INF/*.SF' 'META-INF/*.RSA' 'META-INF/*SF'

build:
	mvn clean package -Dmaven.test.skip=true

# build container for TC
docker_build_container:
	cd docker && docker build -t analytics:java-tc-build-1.0 ./
	aws ecr get-login-password --region us-west-2  | docker login --username AWS --password-stdin 388090105529.dkr.ecr.us-west-2.amazonaws.com
	docker tag analytics:java-tc-build-1.0 388090105529.dkr.ecr.us-west-2.amazonaws.com/analytics:java-tc-build-1.0
	docker push 388090105529.dkr.ecr.us-west-2.amazonaws.com/analytics:java-tc-build-1.0

