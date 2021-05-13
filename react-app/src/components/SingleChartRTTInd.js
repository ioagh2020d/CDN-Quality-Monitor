import {getRTT, getDataPrepared} from "../DataGetter";
import React from 'react';
import SingleChartGeneral from "./SingleChartGeneral";




const SingleChartRTTInd = ({ cdn_name }) => {

    async function getDataCb(...args){

        const data = await getDataPrepared(getRTT, 'average', 'rtt', ...args, cdn_name);
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
        data.markers = data.markers.concat(markers);
        return data;
    } 

    return (
        <SingleChartGeneral dataInit={[]} getDataCb={getDataCb} chartDesc={{leftAxisDesc: "ms"}}/>
    );
}

export default SingleChartRTTInd;