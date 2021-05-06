const apiURL = process.env.REACT_APP_API_URL;
const rttEndpoint = "/api/samples/rtt"
const throughputEndpoint = "/api/samples/throughput"



async function getRTT(startDate, endDate, granularity){
    return fetch(apiURL + rttEndpoint + `?startDate=${startDate.toISOString()}&endDate=${endDate.toISOString()}&granularity=${granularity*60*1000}`)
    .then(response => {
        if(response.status !== 200){
            throw new Error(response.status)
        }      
        return response.json();
    });   
}


async function getThroughput(startDate, endDate, granularity){

    return fetch(apiURL + throughputEndpoint + `?startDate=${startDate.toISOString()}&endDate=${endDate.toISOString()}&granularity=${granularity*60*1000}`)
    .then(response => {
        if(response.status !== 200){
            throw new Error(response.status)
        }      
        return response.json()
    });
  
}

async function getDataPrepared(getDataJson, samplesParam, deviationsParam, sd, ed, granularity){
    const response = await getDataJson(sd, ed, granularity);
    const datasets = [];
    const markers = [];


    // TODO filter out markers by date 
    response.parameterHistory = response.parameterHistory.filter( o => {
        const ts = new Date(o.timestamp);
        return (sd <= ts && ts <= ed);
    });

    function matchAnyDeviation(sample, deviations){
        for(const deviation of deviations){
            if(deviation.startDate <= sample.x && sample.x <= deviation.endDate){
                return true;
            } 
        }
        return false;

    }

    for(const cdn in response.samples){
        let samples = response.samples[cdn].map(s => {
            const ts = new Date(s.timestamp);
            const value = s[samplesParam];
            return { x: ts, y: value};
        });
        let deviations = response.deviations[cdn][deviationsParam].map((r) => {
            return {
                // startDate: new Date(new Date(r.startDate)).getTime() + (1000*60*2), // TODO
                // endDate: new Date(new Date(r.endDate)).getTime() + (1000*60*8), // TODO
                startDate: new Date(r.startDate), // TODO
                endDate: new Date(r.endDate), // TODO
                description: r.description
            }
        });


        // for(const deviation of deviations){
        //     markers.push({
        //         axis: 'x',
        //         // legend: `start of deviation`,
        //         value: deviation.startDate,
        //         lineStyle: { stroke: '#00ff00', strokeWidth: 2 }
        //     });
        //     markers.push({
        //         axis: 'x',
        //         // legend: `end of deviation`,
        //         value: deviation.endDate,
        //         lineStyle: { stroke: '#ff0000', strokeWidth: 2 }
        //     });            
        // }

        let dataFinal = []
        let deviationsFinal = []
        let last_dev = false;
        for(const sample of samples){
            if(matchAnyDeviation(sample, deviations)){
                deviationsFinal.push(sample);
                if(!last_dev){
                    dataFinal.push(sample);
                }else{
                    dataFinal.push({x: sample.x, y: null});
                }
                last_dev = true;
            }else{
                
                dataFinal.push(sample);
                if(last_dev){
                    deviationsFinal.push(sample);
                }else{
                    deviationsFinal.push({x: sample.x, y: null});
                }
                last_dev = false;
            }
        }
        datasets.push({id: cdn, data: dataFinal});
        // datasets.push({id: "deviation "+cdn, data: deviationsFinal});

    }
    console.log(datasets);
    return {data: datasets, markers: markers, response: response};
}



export {getRTT, getThroughput, getDataPrepared};