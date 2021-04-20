// install (please make sure versions match peerDependencies)
// yarn add @nivo/core @nivo/line
import { ResponsiveLine } from '@nivo/line'
import React from 'react';
// make sure parent container have a defined height when using
// responsive component, otherwise height will be 0 and
// no chart will be rendered.
// website examples showcase many properties,
// you'll often use just a few of them.




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
const SingleChartGeneral = ({ dataInit, chartDesc /* see data tab */ }) => {
    let data = dataInit;
    let minMax = {min: 'auto', max: 'auto'};
    chartDesc = chartDesc | {};
    const chartDescFin = {
        bottomAxisDesc: chartDesc.bottomAxisDesc || "time scale",
        leftAxisDesc: chartDesc.leftAxisDesc || "count"
    };
    try{
        minMax = findMinMaxDate(data);

    }catch(e){
        if(e instanceof TypeError){
            
        }else{
            throw e;
        }
    }
    console.log(data);
    return (<ResponsiveLine
        data={data}
        margin={{ top: 50, right: 200, bottom: 50, left: 100 }}
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
            tickValues: 'every 15 minutes',
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
    />);
}

export default SingleChartGeneral;