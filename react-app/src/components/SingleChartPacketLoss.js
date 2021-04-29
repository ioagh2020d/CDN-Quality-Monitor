import {getDataPrepared, getRTT} from "../DataGetter";
import React from 'react';
import SingleChartGeneral from "./SingleChartGeneral";


async function getDataCb(...args){

    return getDataPrepared(getRTT, 'packetLoss', 'packetLoss', ...args);

} 



const SingleChartPacketLoss = ({ data_init }) => {

    return (
        <SingleChartGeneral dataInit={[]} getDataCb={getDataCb}  chartDesc={{leftAxisDesc: "%"}}/>
    );
}

export default SingleChartPacketLoss;