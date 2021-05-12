import {getRTT, getDataPrepared} from "../DataGetter";
import React from 'react';
import SingleChartGeneral from "./SingleChartGeneral";


async function getDataCb(...args){

    const data = await getDataPrepared(getRTT, 'average', 'rtt', ...args);
    const markers = data.response.parameterHistory.map(r => {
        const ts = new Date(r.timestamp);
        const legend = `asr ${r.activeSamplingRate} ati ${r.activeTestsIntensity}`;
        return {
            axis: 'x',
            value: ts,
            lineStyle: { stroke: '#b0413e', strokeWidth: 2 },
            textStyle: {fill: 'grey' },
            legend: legend
        };
    })
    data.markers.concat(markers);
    return data;
} 


const SingleChartRTT = ({ data_init }) => {

    return (
        <SingleChartGeneral dataInit={[]} getDataCb={getDataCb} chartDesc={{leftAxisDesc: "ms"}}/>
    );
}

export default SingleChartRTT;