import {getThroughput, getDataPrepared} from "../DataGetter";
import React from 'react';
import SingleChartGeneral from "./SingleChartGeneral";



async function getDataCb(...args){

    return getDataPrepared(getThroughput, 'throughput', 'throughput', ...args);
    
} 


const SingleChartTput = ({ data_init }) => {

    return (
        <SingleChartGeneral dataInit={[]} getDataCb={getDataCb} chartDesc={{leftAxisDesc: "kb/s"}}/>
    );

}

export default SingleChartTput;