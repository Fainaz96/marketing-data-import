FROM adoptopenjdk/openjdk11:jre

COPY target/marketing-data-import-new-etl-0.1.jar marketing-data-import-new-etl-0.1.jar

ENTRYPOINT ["java","-classpath","marketing-data-import-new-etl-0.1.jar","-Duser.timezone=UTC"]
