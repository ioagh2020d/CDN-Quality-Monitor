// yarn add @nivo/core @nivo/line
import { ResponsiveLine } from '@nivo/line';
import React, { useState, useEffect } from 'react';

// import DateFnsUtils from '@date-io/date-fns';
// import 'date-fns';
import {
  MuiPickersUtilsProvider,
  DateTimePicker,
} from '@material-ui/pickers';
import DateFnsUtils from '@date-io/date-fns';


function findMinMaxDate(data){
    let dates_arr = Object.entries(data)
        .map(r => r[1].data)
        .flat()
        .map(r => r.x);


    let min = dates_arr.reduce((a, b) => { return a < b ? a : b; }); 
    let max = dates_arr.reduce((a, b) => { return a > b ? a : b; });
    // max = new Date(max.getTime() + (1000*60*30));
    // let min = new Date(max.getTime() - (1000*3600*3));
    return {min, max};
}


const SingleChartGeneral = ({ dataInit, chartDesc, getDataCb /* see data tab */ }) => {
    
    const [data, setData] = useState(dataInit);
    const [minMax, setMinMax] = useState({min: 'auto', max: 'auto'})
    const [startDateTime, setStartDateTime] = useState(new Date(Date.now() - (1000*3600*5)));
    const [endDateTime, setEndDateTime] = useState(new Date(Date.now()));

    chartDesc = chartDesc || {};


    const chartDescFin = {
        bottomAxisDesc: chartDesc.bottomAxisDesc || "time scale",
        leftAxisDesc: chartDesc.leftAxisDesc || "count"
    };

    function updateData(sd, ed){

        getDataCb(sd, ed).then((d) =>{
            setData(d);
            try{
                let minmax = findMinMaxDate(d)
                setMinMax({
                    min: new Date(minmax.min.getTime() - (1000*60*1)),
                    max: new Date(minmax.max.getTime() + (1000*30*1))
                    // max: minmax.max
                });
            }catch(e){
                if(e instanceof TypeError){
                    console.log("no data")
                }else{
                    throw e;
                }
            }
        }).catch(e => console.log("no data"));

    }

    // useEffect(() => updateData(startDateTime,endDateTime), []);
    useEffect(() => {

        updateData(startDateTime, endDateTime);
    }, [startDateTime, endDateTime]);

    return (<div className="Chart">
        <div className="ChartDatePickers">
        <MuiPickersUtilsProvider utils={DateFnsUtils}>
        <DateTimePicker ampm={false} value={startDateTime} onChange={sd => setStartDateTime(sd)} />
        <DateTimePicker ampm={false} value={endDateTime} onChange={ed => setEndDateTime(ed)} />
        </MuiPickersUtilsProvider>
        </div>
        <ResponsiveLine
        data={data}
        margin={{ top: 50, right: 200, bottom: 50, left: 50 }}
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
            legend:chartDescFin.bottomAxisDesc
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
                effects: [
                    {
                        on: 'hover',
                        style: {
                            itemBackground: 'rgba(0, 0, 0, .03)',
                            itemOpacity: 1
                        }
                    }
                ]
            }
        ]}
    /></div>);
}

export default SingleChartGeneral;