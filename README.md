# CQM


# Installation

### In memory H2 database

Using docker-compose stack:

```yml=
version: "3"
services:
  cqm:
    image: hubertus248/cqm
    network_mode: host
    environment:
      - spring.profiles.active=h2
      - CQM_INTERFACE=ens33
      - CQM_PORT=8080
      - CQM_CDNS=www.facebook.com,www.youtube.com
      - CQM_ACTIVE_SAMPLING_RATE=10 #default 10 minutes
      - CQM_ACTIVE_TEST_INTENSITY=100 #default 100 samples
      - CQM_PASSIVE_SAMPLING_RATE=5 #default 5 minutes
```

### Postgres

Using docker-compose stack:

```yml=
version: "3"
services:
  cqm:
    image: hubertus248/cqm
    network_mode: host
    environment:
#      - CQM_DDL_AUTO=create #uncomment for first run and database initialization
      - CQM_JDBC_STRING=jdbc:postgresql://localhost/postgres
      - CQM_DB_USER=postgres
      - CQM_DB_PASS=secret_password #replace with a new random password
      - CQM_INTERFACE=ens33
      - CQM_PORT=8080
      - CQM_CDNS=www.facebook.com,www.youtube.com
      - CQM_ACTIVE_SAMPLING_RATE=10 #default 10 minutes
      - CQM_ACTIVE_TEST_INTENSITY=100 #default 100 samples
      - CQM_PASSIVE_SAMPLING_RATE=5 #default 5 minutes
  
  postgres:
    image: postgres
    volumes:
      - postgres-data:/var/lib/postgresql/data
    environment:
      - POSTGRES_PASSWORD=secret_password #replace with a new random password
    ports:
      - 5432:5432
      
volumes:
  postgres-data:
```

# API


### GET `/api/samples/rtt`

#### Query parameters:

`startDate` : ISO8601 string - Start of the period for which samples will be returned

`endDate` : ISO8601 string - End of the period for which samples will be returned


#### Example response:

```json=
{
  "startDate": "2021-04-02T23:04:11Z",
  "endDate": "2022-04-02T23:04:11Z",
  "samples": {
    "www.facebook.com": [
      {
        "timestamp": "2021-04-08T09:29:21.296973Z",
        "average": 6.631,
        "min": 5.929,
        "max": 12.213,
        "standardDeviation": 0.817,
        "packetLoss": 0
      }
    ],
    "www.youtube.com": [
      {
        "timestamp": "2021-04-08T09:29:41.369124Z",
        "average": 8.242,
        "min": 7.29,
        "max": 13.998,
        "standardDeviation": 1.213,
        "packetLoss": 0
      }
    ]
  }
}
```


### GET `/api/samples/throughput`

#### Query parameters:

`startDate` : ISO8601 string - Start of the period for which samples will be returned

`endDate` : ISO8601 string - End of the period for which samples will be returned


#### Example response:

```json=
{
  "startDate": "2021-04-02T23:04:11Z",
  "endDate": "2022-04-02T23:04:11Z",
  "samples": {
    "www.facebook.com": [
      {
        "timestamp": "2021-04-08T09:29:12.563542Z",
        "throughput": 3376
      }
    ],
    "www.youtube.com": [
      {
        "timestamp": "2021-04-08T09:29:12.563542Z",
        "throughput": 3376
      }
    ]
  }
}
```

### GET `/api/samples/all`

#### Query parameters:

`startDate` : ISO8601 string - Start of the period for which samples will be returned

`endDate` : ISO8601 string - End of the period for which samples will be returned


#### Example response:

```json=
{
  "startDate": "2021-04-02T23:04:11Z",
  "endDate": "2022-04-02T23:04:11Z",
  "rttSamples": {
    "www.facebook.com": [
      {
        "timestamp": "2021-04-08T09:29:21.296973Z",
        "average": 6.631,
        "min": 5.929,
        "max": 12.213,
        "standardDeviation": 0.817,
        "packetLoss": 0
      }
    ],
    "www.youtube.com": [
      {
        "timestamp": "2021-04-08T09:29:41.369124Z",
        "average": 8.242,
        "min": 7.29,
        "max": 13.998,
        "standardDeviation": 1.213,
        "packetLoss": 0
      }
    ]
  },
  "throughputSamples": {
    "www.facebook.com": [
      {
        "timestamp": "2021-04-08T09:29:12.563542Z",
        "throughput": 3376
      }
    ],
    "www.youtube.com": [
      {
        "timestamp": "2021-04-08T09:29:12.563542Z",
        "throughput": 3376
      }
    ]
  }
}
```

### PUT `/api/samples/update-parameters`

#### Query parameters:

`cdns` : List - CDNs

`activeSamplingRate` : integer - active sampling rate

`activeTestIntensity` : integer - active test intensity

`passiveSamplingRate` : integer - passive sampling rate

If some parameters are not in a query, they will not be updated.