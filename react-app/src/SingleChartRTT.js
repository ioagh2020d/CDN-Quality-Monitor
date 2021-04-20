import {getRTT} from "./DataGetter";
import React, { useState, useEffect } from 'react';
import SingleChartGeneral from "./SingleChartGeneral";


const SingleChartRTT = ({ data_init /* see data tab */ }) => {
    const [data, setData] = useState([]);

    // console.log(endDate.toISOString())
    useEffect( () => {

        let startDate = new Date(Date.now() - (1000*3600*5));
        let endDate = new Date(Date.now());
        getRTT(startDate, endDate).then(d => {
            setData(d);
            console.log(d);
        });
        
    }, [])

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
        <SingleChartGeneral dataInit={data}/>
    );
}

export default SingleChartRTT;