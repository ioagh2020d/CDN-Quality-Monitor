import {getThroughput, getDataPrepared} from "../DataGetter";
import React from 'react';
import SingleChartGeneral from "./SingleChartGeneral";



async function getDataCb(sd, ed){

    return getDataPrepared(getThroughput, 'throughput', 'throughput', sd, ed);
    
} 


const SingleChartTput = ({ data_init }) => {

    return (
        <SingleChartGeneral dataInit={[]} getDataCb={getDataCb} chartDesc={{leftAxisDesc: "kb/s"}}/>
    );

}

export default SingleChartTput;