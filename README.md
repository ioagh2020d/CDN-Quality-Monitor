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

`granularity` : Granularity of the samples in minutes

`monitor` : Optional, name of the monitor

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
  },
  "parameterHistory": [
    {
      "timestamp": "2021-04-02T23:04:11Z",
      "activeSamplingRate": 2,
      "activeTestsIntensity": 10,
      "passiveSamplingRate": 5
    }
  ]
}
```

### GET `/api/samples/throughput`

#### Query parameters:

`startDate` : ISO8601 string - Start of the period for which samples will be returned

`endDate` : ISO8601 string - End of the period for which samples will be returned

`granularity` : Granularity of the samples in minutes

`monitor` : Optional, name of the monitor

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
  },
  "parameterHistory": [
    {
      "timestamp": "2021-04-02T23:04:11Z",
      "activeSamplingRate": 2,
      "activeTestsIntensity": 10,
      "passiveSamplingRate": 5
    }
  ]
}
```

### GET `/api/samples/all`

#### Query parameters:

`startDate` : ISO8601 string - Start of the period for which samples will be returned

`endDate` : ISO8601 string - End of the period for which samples will be returned

`granularity` : Granularity of the samples in minutes

`monitor` : Optional, name of the monitor

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
  },
  "parameterHistory": [
    {
      "timestamp": "2021-04-02T23:04:11Z",
      "activeSamplingRate": 2,
      "activeTestsIntensity": 10,
      "passiveSamplingRate": 5
    }
  ]
}
```

### GET `/api/samples/singleCdn/rtt`

#### Query parameters:

`cdn` : string - CDN name

`startDate` : ISO8601 string - Start of the period for which samples will be returned

`endDate` : ISO8601 string - End of the period for which samples will be returned

#### Example response:

```json
{
  "cdn": "Youtube",
  "startDate": "2021-05-13T14:04:06Z",
  "endDate": "2021-05-25T11:35:50Z",
  "samples": {
    "www.youtube.com": [
      {
        "timestamp": "2021-05-13T14:12:21.669Z",
        "average": 7.262,
        "min": 6.682,
        "max": 7.853,
        "standardDeviation": 0.0021593187,
        "packetLoss": 0.0
      }
    ]
  },
  "deviations": {
    "www.youtube.com": {
      "packetLoss": [],
      "rtt": []
    }
  },
  "parameterHistory": [
    {
      "timestamp": "2021-05-13T14:11:19.474281Z",
      "activeSamplingRate": 1,
      "activeTestsIntensity": 5,
      "passiveSamplingRate": 2
    },
    {
      "timestamp": "2021-05-13T14:11:22.951142Z",
      "activeSamplingRate": 2,
      "activeTestsIntensity": 10,
      "passiveSamplingRate": 1
    }
  ]
}
```

### GET `/api/samples/singleCdn/throughput`

#### Query parameters:

`cdn` : string - CDN name

`startDate` : ISO8601 string - Start of the period for which samples will be returned

`endDate` : ISO8601 string - End of the period for which samples will be returned

#### Example response:

```json
{
  "cdn": "Youtube",
  "startDate": "2021-05-13T14:04:06Z",
  "endDate": "2021-05-25T11:35:50Z",
  "samples": {
    "www.youtube.com": [
      {
        "timestamp": "2021-05-13T14:14:23.449Z",
        "throughput": 2656
      }
    ]
  },
  "deviations": {
    "www.youtube.com": {
      "throughput": []
    }
  },
  "parameterHistory": [
    {
      "timestamp": "2021-05-13T14:11:19.474281Z",
      "activeSamplingRate": 1,
      "activeTestsIntensity": 5,
      "passiveSamplingRate": 2
    },
    {
      "timestamp": "2021-05-13T14:11:22.951142Z",
      "activeSamplingRate": 2,
      "activeTestsIntensity": 10,
      "passiveSamplingRate": 1
    }
  ]
}
```

### GET `/api/samples/singleCdn/all`

#### Query parameters:

`cdn` : string - CDN name

`startDate` : ISO8601 string - Start of the period for which samples will be returned

`endDate` : ISO8601 string - End of the period for which samples will be returned

#### Example response:

```json
{
  "cdn": "Youtube",
  "startDate": "2021-05-13T14:04:06Z",
  "endDate": "2021-05-25T11:35:50Z",
  "rttSamples": {
    "www.youtube.com": [
      {
        "timestamp": "2021-05-13T14:12:21.669Z",
        "average": 7.262,
        "min": 6.682,
        "max": 7.853,
        "standardDeviation": 0.0021593187,
        "packetLoss": 0.0
      }
    ]
  },
  "throughputSamples": {
    "www.youtube.com": [
      {
        "timestamp": "2021-05-13T14:14:23.449Z",
        "throughput": 2656
      }
    ]
  },
  "deviations": {
    "www.youtube.com": {
      "rtt": [],
      "packetLoss": [],
      "throughput": []
    }
  },
  "parameterHistory": [
    {
      "timestamp": "2021-05-13T14:11:19.474281Z",
      "activeSamplingRate": 1,
      "activeTestsIntensity": 5,
      "passiveSamplingRate": 2
    },
    {
      "timestamp": "2021-05-13T14:11:22.951142Z",
      "activeSamplingRate": 2,
      "activeTestsIntensity": 10,
      "passiveSamplingRate": 1
    }
  ]
}
```

### PUT `/api/parameters`

#### Body (JSON keys):

`cdns` : List of objects with fields: "name" - cdn name, "urls" - cdn urls

`activeSamplingRate` : integer - active sampling rate

`activeTestIntensity` : integer - active test intensity

`passiveSamplingRate` : integer - passive sampling rate

### GET `/api/parameters`

#### Example response:

```json
{
  "cdns": [
    {
      "name": "Youtube",
      "urls": [
        "www.youtube.com",
        "www.yt.com"
      ]
    },
    {
      "name": "Facebook",
      "urls": [
        "www.fb.com"
      ]
    }
  ],
  "activeSamplingRate": 2,
  "activeTestIntensity": 10,
  "passiveSamplingRate": 5
}
```

### GET `/api/monitors`

#### Example response:

```json
{
  "monitors": [
    {
      "id": 1,
      "name": "1.2.3.4"
    },
    {
      "id": 2,
      "name": "5.6.7.8"
    }
  ]
}
```

### POST `/api/remotes/rtt`

#### Example request:

```json
{
  "samples":[
    {
      "cdnName":"www.facebook.com",
      "url":"testUrl",
      "sample": {
        "timestamp":"2021-04-22T12:45:04.927+00:00",
        "average":10,
        "min":10,
        "max":10,
        "packetLoss":1,
        "standardDeviation":0.1,
        "type": "TCP"
      }
    }
  ]
}
```

### POST `/api/remotes/throughput`

#### Example request:

```json
{
  "samples":[
    {
      "cdnName": "facebook",
      "url": "www.facebook.com",
      "sample": {
        "timestamp": "2021-04-22T12:45:04.927+00:00",
        "throughput": 1000
      }
    }
  ]
}
```