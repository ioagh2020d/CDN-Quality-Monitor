import {getDataPrepared, getRTT} from "../DataGetter";
import React from 'react';
import SingleChartGeneral from "./SingleChartGeneral";





const SingleChartPacketLossInd = ({ cdn_name }) => {
    async function getDataCb(...args){

        let data = await getDataPrepared(getRTT, 'packetLoss', 'packetLoss', ...args, cdn_name);
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
        <SingleChartGeneral dataInit={[]} getDataCb={getDataCb}  chartDesc={{leftAxisDesc: "%"}}/>
    );
}

export default SingleChartPacketLossInd;