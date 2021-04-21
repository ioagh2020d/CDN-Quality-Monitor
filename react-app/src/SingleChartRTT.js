import {getRTT} from "./DataGetter";
import React from 'react';
import SingleChartGeneral from "./SingleChartGeneral";


const SingleChartRTT = ({ data_init }) => {

    // data = [
    //     {
    //         id: "throughput",
    //         color: "rgb(200,100,50)",
    //         data: [
    //             {x: 20, y: 30},
    //             {x: 30, y: 35},
    //             {x: 40, y: 37},
    //             {x: 50, y: 39}
    //         ] 
    //     }
    // ]

    return (
        <SingleChartGeneral dataInit={[]} getDataCb={getRTT}/>
    );
}

export default SingleChartRTT;