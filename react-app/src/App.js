import SingleChartRTT from './SingleChartRTT';
import SingleChartTput from './SingleChartTput';

const data = [
  {
    "id": "japan",
    "color": "hsl(317, 70%, 50%)",
    "data": [
      {
        "x": "plane",
        "y": 102
      },
      {
        "x": "helicopter",
        "y": 127
      },
      {
        "x": "boat",
        "y": 163
      },
      {
        "x": "train",
        "y": 225
      },
      {
        "x": "subway",
        "y": 65
      },
      {
        "x": "bus",
        "y": 183
      },
      {
        "x": "car",
        "y": 47
      },
      {
        "x": "moto",
        "y": 96
      },
      {
        "x": "bicycle",
        "y": 25
      },
      {
        "x": "horse",
        "y": 298
      },
      {
        "x": "skateboard",
        "y": 113
      },
      {
        "x": "others",
        "y": 127
      }
    ]
  },
  {
    "id": "france",
    "color": "hsl(170, 70%, 50%)",
    "data": [
      {
        "x": "plane",
        "y": 267
      },
      {
        "x": "helicopter",
        "y": 50
      },
      {
        "x": "boat",
        "y": 98
      },
      {
        "x": "train",
        "y": 151
      },
      {
        "x": "subway",
        "y": 6
      },
      {
        "x": "bus",
        "y": 120
      },
      {
        "x": "car",
        "y": 20
      },
      {
        "x": "moto",
        "y": 65
      },
      {
        "x": "bicycle",
        "y": 67
      },
      {
        "x": "horse",
        "y": 205
      },
      {
        "x": "skateboard",
        "y": 214
      },
      {
        "x": "others",
        "y": 288
      }
    ]
  },
  {
    "id": "us",
    "color": "hsl(354, 70%, 50%)",
    "data": [
      {
        "x": "plane",
        "y": 273
      },
      {
        "x": "helicopter",
        "y": 276
      },
      {
        "x": "boat",
        "y": 139
      },
      {
        "x": "train",
        "y": 158
      },
      {
        "x": "subway",
        "y": 93
      },
      {
        "x": "bus",
        "y": 218
      },
      {
        "x": "car",
        "y": 63
      },
      {
        "x": "moto",
        "y": 257
      },
      {
        "x": "bicycle",
        "y": 235
      },
      {
        "x": "horse",
        "y": 227
      },
      {
        "x": "skateboard",
        "y": 172
      },
      {
        "x": "others",
        "y": 259
      }
    ]
  },
  {
    "id": "germany",
    "color": "hsl(216, 70%, 50%)",
    "data": [
      {
        "x": "plane",
        "y": 207
      },
      {
        "x": "helicopter",
        "y": 44
      },
      {
        "x": "boat",
        "y": 232
      },
      {
        "x": "train",
        "y": 259
      },
      {
        "x": "subway",
        "y": 152
      },
      {
        "x": "bus",
        "y": 213
      },
      {
        "x": "car",
        "y": 61
      },
      {
        "x": "moto",
        "y": 122
      },
      {
        "x": "bicycle",
        "y": 155
      },
      {
        "x": "horse",
        "y": 141
      },
      {
        "x": "skateboard",
        "y": 210
      },
      {
        "x": "others",
        "y": 159
      }
    ]
  },
  {
    "id": "norway",
    "color": "hsl(226, 70%, 50%)",
    "data": [
      {
        "x": "plane",
        "y": 185
      },
      {
        "x": "helicopter",
        "y": 98
      },
      {
        "x": "boat",
        "y": 103
      },
      {
        "x": "train",
        "y": 183
      },
      {
        "x": "subway",
        "y": 37
      },
      {
        "x": "bus",
        "y": 245
      },
      {
        "x": "car",
        "y": 217
      },
      {
        "x": "moto",
        "y": 137
      },
      {
        "x": "bicycle",
        "y": 209
      },
      {
        "x": "horse",
        "y": 36
      },
      {
        "x": "skateboard",
        "y": 23
      },
      {
        "x": "others",
        "y": 144
      }
    ]
  }
]

function App() {
  return (
    <div className="App">
      <h3>Rtt</h3>
      <SingleChartRTT data={data} className="Chart"></SingleChartRTT>
      <h3>Throughput</h3>
      <SingleChartTput data={data} className="Chart"></SingleChartTput>
    </div>
  );
}

export default App;
