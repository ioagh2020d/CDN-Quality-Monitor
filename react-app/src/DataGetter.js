const apiURL = process.env.REACT_APP_API_URL;
const rttEndpoint = "/api/samples/rtt"
const throughputEndpoint = "/api/samples/throughput"
async function getRTT(startDate, endDate){

    return fetch(apiURL + rttEndpoint + `?startDate=${startDate.toISOString()}&endDate=${endDate.toISOString()}`)
    .then(response => response.json())
    .then(d => {
        const dArr = Object.entries(d.samples).map(r => {
            let id = r[0];
            let samples = r[1].map(s => {
                let x = new Date(s.timestamp)
                let y = s.average;
                return {x:x, y:y};
            });
            return {
                id: id,
                data: samples
            };
        });
        return dArr;
    });    
}
async function getThroughput(startDate, endDate){

    return fetch(apiURL + throughputEndpoint + `?startDate=${startDate.toISOString()}&endDate=${endDate.toISOString()}`)
    .then(response => response.json())
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
        return dArr;
    });    
}

export {getRTT, getThroughput};