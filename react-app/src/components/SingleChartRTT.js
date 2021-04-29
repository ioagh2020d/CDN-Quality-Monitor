import {getRTT, getDataPrepared} from "../DataGetter";
import React from 'react';
import SingleChartGeneral from "./SingleChartGeneral";


async function getDataCb(...args){

    return getDataPrepared(getRTT, 'average', 'rtt', ...args);

} 


const SingleChartRTT = ({ data_init }) => {

    return (
        <SingleChartGeneral dataInit={[]} getDataCb={getDataCb} chartDesc={{leftAxisDesc: "ms"}}/>
    );
}

export default SingleChartRTT;