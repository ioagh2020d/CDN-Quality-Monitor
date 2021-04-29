const apiURL = process.env.REACT_APP_API_URL;
const rttEndpoint = "/api/samples/rtt"
const throughputEndpoint = "/api/samples/throughput"



async function getRTT(param, startDate, endDate, granularity){
    if(typeof param !== "string"){
        throw new Error("no param in getRTT");
    }
    return fetch(apiURL + rttEndpoint + `?startDate=${startDate.toISOString()}&endDate=${endDate.toISOString()}&granularity=${granularity*60*1000}`)
    .then(response => {
        if(response.status !== 200){
            throw new Error(response.status)
        }      
        return response.json()
    }).then(d => {
        const dArr = Object.entries(d.samples).map(r => {
            let id = r[0];
            let samples = r[1].map(s => {
                let x = new Date(s.timestamp)
                let y = s[param];
                return {x:x, y:y};
            });
            return {
                id: id,
                data: samples
            };
        });

        const markers = []; // TODO
        // const markers = d.parameterHistory.map(r => {
        //     const ts = new Date(r.timestamp);
        //     const legend = `activeSamplingRate ${r.activeSamplingRate}\r\nactiveTestIntensity ${r.nactiveTestIntensity}`;
        //     return {
        //         axis: 'x',
        //         value: ts,
        //         lineStyle: { stroke: '#b0413e', strokeWidth: 2 },
        //         legend: legend
        //     };
        // })
        return {data: dArr, markers: markers};
    });    
}

async function getRTTAverage(...args){
    return getRTT("average", ...args);
}
async function getRTTMin(...args){
    return getRTT("min", ...args);
}
async function getRTTMax(...args){
    return getRTT("max", ...args);
}
async function getRTTStdDev(...args){
    return getRTT("standardDeviation", ...args);
}
async function getRTTPacketLoss(...args){
    return getRTT("packetLoss", ...args);
}
async function getThroughput(startDate, endDate, granularity){

    return fetch(apiURL + throughputEndpoint + `?startDate=${startDate.toISOString()}&endDate=${endDate.toISOString()}&granularity=${granularity*60*1000}`)
    .then(response => {
        if(response.status !== 200){
            throw new Error(response.status)
        }      
        return response.json()
    })
    .then(d => {
        const dArr = Object.entries(d.samples).map(r => {
            let id = r[0];
            let samples = r[1].map(s => {
                let x = new Date(s.timestamp)
                let y = s.throughput;
                return {x:x, y:y/1000};
            });
            return {
                id: id,
                data: samples
            };
        });
        const markers = [{
            axis: 'x',
            value: new Date(Date.now() - (1000*60*10)),
            lineStyle: { stroke: '#b0413e', strokeWidth: 2 },
            legend: 'x marker',
        }]; // TODO
        // const markers = d.parameterHistory.map(r => {
        //     const ts = new Date(r.timestamp);
        //     const legend = `passiveSamplingRate ${r.passiveSamplingRate}`;
        //     return {
        //         axis: 'x',
        //         value: ts,
        //         lineStyle: { stroke: '#b0413e', strokeWidth: 2 },
        //         legend: legend
        //     };
        // })
        return {data: dArr, markers: markers};
    });    
}

export {getRTTAverage, getThroughput, getRTTPacketLoss};