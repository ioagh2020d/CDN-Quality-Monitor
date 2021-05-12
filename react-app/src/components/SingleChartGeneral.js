import {ResponsiveLine} from '@nivo/line';
import React, {useState, useEffect} from 'react';
// yarn add @nivo/core @nivo/line
// import DateFnsUtils from '@date-io/date-fns';
// import 'date-fns';
import {
  MuiPickersUtilsProvider,
  DateTimePicker,
} from '@material-ui/pickers';
import DateFnsUtils from '@date-io/date-fns';
import Slider from '@material-ui/core/Slider';

const granularityValues = [5, 10, 20, 30, 60, 120, 240, 720, 1440]
const granularityMarks = [
  {
    value: 1,
    label: '5 min',
  },
  {
    value: 2,
    label: '10 min',
  },
  {
    value: 3,
    label: '20 min',
  },
  {
    value: 4,
    label: '30 min',
  },
  {
    value: 5,
    label: '1 h',
  },
  {
    value: 6,
    label: '2 h',
  },
  {
    value: 7,
    label: '4 h',
  },
  {
    value: 8,
    label: '12 h',
  },
  {
    value: 9,
    label: '1 day',
  },
];

const availableColors = ['red', 'green', 'blue', 'orange', 'purple'];


const SingleChartGeneral = ({dataInit, chartDesc, getDataCb /* see data tab */}) => {

  const [data, setData] = useState(dataInit);
  const [markers, setMarkers] = useState([]);
  const [minMax, setMinMax] = useState({min: 'auto', max: 'auto'})
  const [startDateTime, setStartDateTime] = useState(new Date(Date.now() - (1000 * 3600 * 5)));
  const [endDateTime, setEndDateTime] = useState(new Date(Date.now()));
  const [granularityValue, setGranularityValue] = useState(10);

  chartDesc = chartDesc || {};


  const chartDescFin = {
    bottomAxisDesc: chartDesc.bottomAxisDesc || "time scale",
    leftAxisDesc: chartDesc.leftAxisDesc || "count"
  };

  function updateData(sd, ed, gr) {

    getDataCb(sd, ed, gr).then((d) => {

      d.data = d.data.map(cdn => {
        cdn.id = cdn.id+" ".repeat(granularityValues.findIndex((v) => v.valueOf() === granularityValue));
        return cdn;
      }).map((d, id) => {
        if(d.id.endsWith(" deviation ")) d.color = availableColors[id+1];
        else d.color = availableColors[id];
        return d;
      });
      setData(d.data);
      if (d.markers) {
        setMarkers(d.markers);
      }
      try {
        setMinMax({
          min: startDateTime,
          max: endDateTime
          // max: minmax.max
        });
      } catch (e) {

        throw e;
      }
    }).catch(e => {
      setData([]);
      setMarkers([]);
      if (e instanceof SyntaxError) {
        console.warn(e);
      } else if (e instanceof TypeError) {
        console.warn(e);
      } else if (e.message == 500) {
        console.warn("internal server error");
      } else if (e.message == 400) {
        console.log("no data")
      } else {
        throw e;
      }
    });

  }


  const styleById = (key) =>{

    if(key.endsWith(" deviation ")){
      return{
        strokeDasharray: '1, 8',
        strokeWidth: 4,
        strokeLinejoin: 'round',
        strokeLinecap: 'round',
      };
    }else{
      return {
        strokeWidth: 2
      };
    }
  }
  const CustomLine = ({ series, lineGenerator, xScale, yScale }) => {
    return series.map(({ id, data, color }) =>{ 

      return (

        <path
            key={id}
            d={lineGenerator(
                data.map(d => ({
                    x: xScale(d.data.x),
                    y: yScale(d.data.y),
                })).map(d => {
                  if(d.y == undefined) d.y = null;
                  return d;
                })
            )}
            fill="none"
            stroke={color}
            style={styleById(id)}
        />
    )})
}

  useEffect(() => {
    updateData(startDateTime, endDateTime, granularityValue);
  }, [startDateTime, endDateTime, granularityValue]);

  return (<div className="Chart">
    <div className="ChartDatePickers">
      <MuiPickersUtilsProvider utils={DateFnsUtils}>
        <DateTimePicker ampm={false} value={startDateTime} onChange={sd => setStartDateTime(sd)}/>
        <DateTimePicker ampm={false} value={endDateTime} onChange={ed => setEndDateTime(ed)}/>
      </MuiPickersUtilsProvider>

      <div className="Granularity">
        <Slider
          min={1}
          max={9}
          defaultValue={2}
          step={null}
          scale={(x) => granularityValues[x]}
          valueLabelFormat={(x) => ""}
          aria-labelledby="granularity"
          valueLabelDisplay="auto"
          marks={granularityMarks}
          onChangeCommitted={(event, gr) => setGranularityValue(granularityValues[gr - 1])}
        />
      </div>
    </div>
    <ResponsiveLine
      data={data}
      margin={{top: 50, right: 200, bottom: 50, left: 50}}
      xScale={{
        type: 'time',
        format: "native",
        useUTC: true,
        precision: 'minute',
        min: minMax.min,
        max: minMax.max
      }}
      xFormat="time: %H-%M"
      yScale={{
        type: 'linear',
        stacked: false,
      }}
      axisLeft={{
        legend: chartDescFin.leftAxisDesc,
        legendOffset: 12,

      }}
      axisBottom={{
        format: '%H:%M',
        tickValues: 10,
        legendOffset: 33,
        legendPosition: "middle",
        legend: chartDescFin.bottomAxisDesc
      }}
      enablePointLabel={false}
      pointSize={5}
      pointBorderWidth={1}
      pointBorderColor={{
        from: 'color',
        modifiers: [['darker', 0.3]],
      }}
      useMesh={true}
      enableSlices={false}
      colors={{datum: 'color'}}
      markers={markers}
      legends={[
        {
          anchor: 'bottom-right',
          direction: 'column',
          justify: false,
          translateX: 100,
          translateY: 0,
          itemsSpacing: 0,
          itemDirection: 'left-to-right',
          itemWidth: 80,
          itemHeight: 20,
          itemOpacity: 0.75,
          symbolSize: 12,
          symbolShape: 'circle',
          symbolBorderColor: 'rgba(0, 0, 0, .5)',
          effects: [{
              on: 'hover',
              style: {
                itemBackground: 'rgba(0, 0, 0, .03)',
                itemOpacity: 1
              }
            }
          ]
        }
      ]}
      enableSlices="x"

      sliceTooltip={({ slice }) => {
        return (
            <div
                style={{
                    background: 'white',
                    padding: '9px 12px',
                    border: '1px solid #ccc',
                }}
            >
                <div>x: {slice.id}</div>
                {slice.points.map(point => (
                    <div
                        key={point.id}
                        style={{
                            color: point.serieColor,
                            padding: '3px 0',
                        }}
                    >
                        <strong>{point.serieId}</strong> [{point.data.yFormatted}]
                    </div>
                ))}
            </div>
        )
    }}
    layers={['grid', 'markers', 'areas', CustomLine, 'slices', 'points', 'axes', 'legends']}
    /></div>);
}

export default SingleChartGeneral;