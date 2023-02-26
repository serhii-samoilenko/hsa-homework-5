# Highload Software Architecture 8 Lesson 5 Homework

Stress Testing
---

## Test project setup

The web service is written in Kotlin/Quarkus and uses MongoDB as a database.

The database stores `Person` entities, each include a `firstName` and `lastName` field. Each time the web service is started, it defines a
pool of first and last names, and uses it to randomly create and store a number of `Person` entities.
Total size of a pool is determined by `app.entity-count` property in `application.yml` file or `APP_ENTITY_COUNT` environment variable.
The count of pre-generated entities is determined by `prepopulate-percentage` property in `application.yml` file
or `APP_PREPOPULATE_PERCENTAGE` environment variable.

In my local setup, I use 100_000 entities in the pool and 70% of them are pre-populated in the database.

The load is generated by `siege` which uses two endpoints:

- `GET /person/random` - gets a random first and last name from the pool and searches the database for a person with these names. If the
  person is found, it is returned with response code `200`, if not, `204` is returned.
- `POST /person/random` - creates a new person with a random first and last name from the pool and saves it to the database. The response
  code is always `200`.

Apart from 'random' endpoints used to simplify a load testing, there are regular endpoints, referenced in the [test.http](test.http) file.

During the load testing, the siege file [urls.txt](urls.txt) is used to generate a preferred load with 5:1 Read/Write ratio.

## How to run

```shell script
# Use image from Docker Hub
docker-compose up -d
# Run load test
siege -c 50 -t 5m -b -i -f urls.txt
```

## How to build

The project **Requires Java 17+** to build.

```shell script
./gradlew build && \
docker build -f src/main/docker/Dockerfile -t ssamoilenko/hsal5-stress-test
```

You can also run application in dev mode that enables live coding using:

```shell script
./gradlew quarkusDev
```

## Test results

The results of load test with different number of concurrent users are located in the [results](results) folder.

Summary of the results:

|          Concurrency:           | **10** | **25** | **50** | **100** | **200** | **400** |
|:-------------------------------:|:------:|:------:|:------:|:-------:|:-------:|:-------:|
|        **Transactions**         | 13416  | 11343  | 12153  |  14419  |  13872  |         |
|       **Availability**, %       | 100.00 | 100.00 | 100.00 | 100.00  | 100.00  |         |
|     **Elapsed time**, secs      | 300.69 | 300.47 | 300.65 | 300.23  | 300.19  |         |
|    **Data transferred**, MB     |  0.92  |  0.79  |  0.86  |  1.00   |  0.97   |         |
|     **Response time**, secs     |  0.22  |  0.66  |  1.23  |  2.07   |  4.30   |         |
| **Transaction rate**, trans/sec | 44.62  | 37.75  | 40.42  |  48.03  |  46.21  |         |
|         **Concurrency**         |  9.99  | 24.96  | 49.82  |  99.59  | 198.48  |         |
|   **Successful transactions**   | 13416  | 11343  | 12153  |  14419  |  13872  |         |
|     **Failed transactions**     |   0    |   0    |   0    |    0    |    0    |         |
|  **Longest transaction**, secs  |  1.23  |  3.62  |  4.27  |  4.93   |  10.55  |         |
| **Shortest transaction**, secs  |  0.00  |  0.00  |  0.00  |  0.00   |  0.86   |         |
