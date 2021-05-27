import {getThroughput, getDataPrepared} from "../DataGetter";
import React from 'react';
import SingleChartGeneral from "./SingleChartGeneral";



async function getDataCb(...args){

    const data = await getDataPrepared(getThroughput, 'throughput', 'throughput', ...args);
    const markers = data.response.parameterHistory.map(r => {
        const ts = new Date(r.timestamp);
        const legend = `psr ${r.passiveSamplingRate}`;
        return {
            axis: 'x',
            value: ts,
            lineStyle: { stroke: '#b0413e', strokeWidth: 2 },
            textStyle: {fill: 'grey' },
            legend: legend
        };
    })
    
    data.markers = data.markers.concat(markers);
    return data;
} 


const SingleChartTput = ({ data_init }) => {

    return (
        <SingleChartGeneral dataInit={[]} getDataCb={getDataCb} chartDesc={{leftAxisDesc: "kb/s"}}/>
    );

}

export default SingleChartTput;