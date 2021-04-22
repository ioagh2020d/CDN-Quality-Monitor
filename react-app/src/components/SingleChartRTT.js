import {getRTT} from "../DataGetter";
import React from 'react';
import SingleChartGeneral from "./SingleChartGeneral";


const SingleChartRTT = ({ data_init }) => {

    return (
        <SingleChartGeneral dataInit={[]} getDataCb={getRTT} chartDesc={{leftAxisDesc: "ms"}}/>
    );
}

export default SingleChartRTT;