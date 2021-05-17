import {getDataPrepared, getRTTInd} from "../DataGetter";
import React, { useEffect, useState } from 'react';
import SingleChartGeneral from "./SingleChartGeneral";





const SingleChartPacketLossInd = ({ cdnName }) => {
    const [reloadToggler, setReloadToggler] = useState(false);
    async function getDataCb(...args){

        let data = await getDataPrepared(getRTTInd, 'packetLoss', 'packetLoss', ...args, cdnName);
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
    useEffect(() => {
        setReloadToggler(!reloadToggler);
    }, [cdnName]);
    return (
        <SingleChartGeneral dataInit={[]} getDataCb={getDataCb} reloadToggler={reloadToggler} chartDesc={{leftAxisDesc: "%"}}/>
    );
}

export default SingleChartPacketLossInd;