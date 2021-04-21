import {getThroughput} from "./DataGetter";
import React from 'react';
import SingleChartGeneral from "./SingleChartGeneral";


const SingleChartTput = ({ data_init }) => {

    // data = [
    //     {
    //         id: "throughput",
    //         color: "rgb(200,100,50)",
    //         data: [
    //             {x: new Date("2021-04-02T10:04:11Z"), y: 30},
    //             {x: new Date("2021-04-02T11:05:11Z"), y: 35},
    //             {x: new Date("2021-04-02T12:04:11Z"), y: 37},
    //             {x: new Date("2021-04-02T13:04:11Z"), y: 39},
    //             {x: new Date("2021-04-02T14:04:11Z"), y: 59},
    //             {x: new Date("2021-04-02T15:04:11Z"), y: 139},
    //             {x: new Date("2021-04-02T16:04:11Z"), y: 39}
    //         ] 
    //     },
    //     {
    //         id: "throughput222",
    //         color: "rgb(200,100,50)",
    //         data: [
    //             {x: new Date("2021-04-02T08:04:11Z"), y: 10},
    //             {x: new Date("2021-04-02T11:05:11Z"), y: 15},
    //             {x: new Date("2021-04-02T12:04:11Z"), y: 17},
    //             {x: new Date("2021-04-02T13:04:11Z"), y: 69},
    //             {x: new Date("2021-04-02T14:04:11Z"), y: 79},
    //             {x: new Date("2021-04-02T15:04:11Z"), y: 79},
    //             {x: new Date("2021-04-02T16:04:11Z"), y: 9}
    //         ] 
    //     }
    // ]


    return (
        <SingleChartGeneral dataInit={[]} getDataCb={getThroughput} chartDesc={{leftAxisDesc: "kb/s"}}/>
    );

}

export default SingleChartTput;