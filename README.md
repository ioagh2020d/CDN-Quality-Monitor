# CQM


# Installation

### In memory H2 database

Using docker-compose stack:

```yml
version: "3"
services:
  cqm:
    image: hubertus248/cqm
    network_mode: host
    environment:
      - spring.profiles.active=h2
      - CQM_INTERFACE=ens33
      - CQM_PORT=8080
```

### Postgres

Using docker-compose stack:

```yml
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
  
  postgres:
    image: postgres
    network_mode: host
    volumes:
      - postgres-data:/var/lib/postgresql/data
    environment:
      - POSTGRES_PASSWORD=secret_password #replace with a new random password
      
volumes:
  postgres-data:
```

# API


### GET `/api/samples/rtt`

#### Query parameters:

`startDate` : ISO8601 string - Start of the period for which samples will be returned

`endDate` : ISO8601 string - End of the period for which samples will be returned


#### Example response:

```json
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
  },
  "deviations": {
    "www.facebook.com": {
      "packetLoss": [],
      "rtt": []
    },
    "www.youtube.com": {
      "packetLoss": [],
      "rtt": []
    }
  }
}
```


### GET `/api/samples/throughput`

#### Query parameters:

`startDate` : ISO8601 string - Start of the period for which samples will be returned

`endDate` : ISO8601 string - End of the period for which samples will be returned


#### Example response:

```json
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
  },
  "deviations": {
    "www.facebook.com": {
      "throughput": []
    },
    "www.youtube.com": {
      "throughput": []
    }
  }
}
```

### GET `/api/samples/all`

#### Query parameters:

`startDate` : ISO8601 string - Start of the period for which samples will be returned

`endDate` : ISO8601 string - End of the period for which samples will be returned


#### Example response:

```json
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
  },
  "deviations": {
    "www.facebook.com": {
      "packetLoss": [],
      "rtt": [],
      "throughput": []
    },
    "www.youtube.com": {
      "packetLoss": [],
      "rtt": [],
      "throughput": []
    }
  }
}
```

### PUT `/api/parameters`

#### Query parameters:

`cdns` : List - CDNs

`activeSamplingRate` : integer - active sampling rate

`activeTestIntensity` : integer - active test intensity

`passiveSamplingRate` : integer - passive sampling rate

### GET `/api/parameters`

#### Example response:

```json
{
  "cdns": [
    "www.youtube.com",
    "www.facebook.com"
  ],
  "activeSamplingRate": 2,
  "activeTestIntensity": 10,
  "passiveSamplingRate": 5
}
```