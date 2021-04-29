import {getRTT, getDataPrepared} from "../DataGetter";
import React from 'react';
import SingleChartGeneral from "./SingleChartGeneral";


async function getDataCb(sd, ed){

    return getDataPrepared(getRTT, 'average', 'rtt', sd, ed);

} 


const SingleChartRTT = ({ data_init }) => {

    return (
        <SingleChartGeneral dataInit={[]} getDataCb={getDataCb} chartDesc={{leftAxisDesc: "ms"}}/>
    );
}

export default SingleChartRTT;