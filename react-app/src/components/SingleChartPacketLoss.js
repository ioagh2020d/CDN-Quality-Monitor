import {getDataPrepared, getRTT} from "../DataGetter";
import React from 'react';
import SingleChartGeneral from "./SingleChartGeneral";


async function getDataCb(sd, ed){

    return getDataPrepared(getRTT, 'packetLoss', 'packetLoss', sd, ed);

} 



const SingleChartPacketLoss = ({ data_init }) => {

    return (
        <SingleChartGeneral dataInit={[]} getDataCb={getDataCb}  chartDesc={{leftAxisDesc: "%"}}/>
    );
}

export default SingleChartPacketLoss;