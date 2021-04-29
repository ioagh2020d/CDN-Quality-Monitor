import {getRTTPacketLoss} from "../DataGetter";
import React from 'react';
import SingleChartGeneral from "./SingleChartGeneral";


const SingleChartPacketLoss = ({ data_init }) => {

    return (
        <SingleChartGeneral dataInit={[]} getDataCb={getRTTPacketLoss}  chartDesc={{leftAxisDesc: "%"}}/>
    );
}

export default SingleChartPacketLoss;