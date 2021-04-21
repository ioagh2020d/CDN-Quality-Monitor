import {getThroughput} from "../DataGetter";
import React from 'react';
import SingleChartGeneral from "./SingleChartGeneral";


const SingleChartTput = ({ data_init }) => {

    return (
        <SingleChartGeneral dataInit={[]} getDataCb={getThroughput} chartDesc={{leftAxisDesc: "kb/s"}}/>
    );

}

export default SingleChartTput;