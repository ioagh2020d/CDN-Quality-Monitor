import {ReportSettings, generateCSV, downloadStrFile} from './ReportSettings'
import React, { useEffect, useState } from "react";
import generatePDFComponent from "./PDFReport";
import { pdf } from '@react-pdf/renderer';
import { getRTTInd, getThroughputInd, getDataPrepared } from "../DataGetter";
import { Card } from "@material-ui/core";
import Select from '@material-ui/core/Select';
import FormControl from '@material-ui/core/FormControl';
import {makeStyles} from '@material-ui/core/styles';
import InputLabel from '@material-ui/core/InputLabel';
import MenuItem from '@material-ui/core/MenuItem';
import { Grid } from '@material-ui/core';

import CircularProgress from '@material-ui/core/CircularProgress';


const useStyles = makeStyles((theme) => ({
  formControl: {
    margin: theme.spacing(1),
    minWidth: 200,
  },
  selectEmpty: {
    marginTop: theme.spacing(2),
  },
  cardsG: {
    textAlign: 'center',
    paddingTop: '1em'
  }
}));

const generatePDF =  async  (cdn, data) =>{
  let commonToQueries = [
    data.startDate, data.endDate, data.granularity, cdn
  ]
  const exportData = {}

  if (data.rtt) {
    exportData.rtt = await getDataPrepared(getRTTInd, 'average', 'rtt', ...commonToQueries).catch(error => console.warn(error));
  }
  if (data.throughput) {
    exportData.throughput = await getDataPrepared(getThroughputInd, 'throughput', 'throughput', ...commonToQueries).catch(error => console.warn(error));
  }
  if (data.packetLoss) {
    exportData.packetLoss = await getDataPrepared(getRTTInd, 'packetLoss', 'packetLoss', ...commonToQueries).catch(error => console.warn(error));
  }
  const reportComponent = generatePDFComponent(exportData, data);
    if(data.reportType === "PDF"){
    const blob = pdf(reportComponent).toBlob().then(b => {
      const fileDownloadUrl = URL.createObjectURL(b);
      let a = document.createElement('a');
      a.href = fileDownloadUrl;
      a.download = "Report.pdf"
      a.click();
      setTimeout(() => {
        window.URL.revokeObjectURL(fileDownloadUrl);
      }, 0)

    });
  }else{
    if(exportData.rtt){
      downloadStrFile(generateCSV(exportData.rtt, data, "RTT [ms]"), "ReportRTT.csv");
    }
    if(exportData.throughput){
      downloadStrFile(generateCSV(exportData.throughput, data, "Throughput [kbps]"), "ReportThroughput.csv");
    }
    if(exportData.packetLoss){
      downloadStrFile(generateCSV(exportData.packetLoss, data, "packetloss [%]"), "ReportPacketLoss.csv");
    }
  }
};


async function getAllCdns() {
  return fetch(process.env.REACT_APP_API_URL + "/api/parameters")
    .then(response => response.json())
    .then(data => data['cdns'].map(cdn => cdn.name))
    .then(d => {
      return d;
    })
}
const getAllUrls = async (cdn) =>{
  return fetch(process.env.REACT_APP_API_URL + "/api/parameters")
    .then(response => response.json())
    .then(data => data['cdns'].filter(c => c.name === cdn).map(c => c.urls))
    .then(d => {
      if(d.length == 1) return d[0].map(u => {return {'name': u}});
      else return [];
    })
}



const ReportSettingsURLs = () => {
  const classes = useStyles();
  const [cdn, setCDN] = useState("");
  const [allCdnsItems, setAllCdnsItems] = useState([]);


  const [getAllFunc, setGetAllFunc] = useState(() => () => getAllUrls(""));
  const [generatePDFFunc, setGeneratePDFFunc] = useState(() => (data) => generatePDF("", data));

  useEffect(() => {
    getAllCdns().then(cdns => {
      let items = cdns.map(c => {
        return <MenuItem key={c} value={c}>{c}</MenuItem>
      });
      setAllCdnsItems(items);
      setCDN(cdns[0]);// TODO handle no cdns
    }).catch(error => console.log(error));

  }, []
)

useEffect(() => {
  setGetAllFunc(() => () => getAllUrls(cdn));
  setGeneratePDFFunc(() => (data) => generatePDF(cdn, data));

}, [cdn]);
const handleChange = (event) => {
  setCDN(event.target.value);
};

  return <Grid container spacing={2}>
  <Grid item xs={12}>
    <Card className={classes.cardsG} style={{textAlign: 'left', padding: '1em'}}>
      <FormControl className={classes.formControl}>
        <InputLabel id="demo-simple-select-label">CDN</InputLabel>
        <Select
          labelId="demo-simple-select-label"
          id="demo-simple-select"
          value={cdn}
          onChange={handleChange}
        >
          {allCdnsItems}
        </Select>
      </FormControl>
    </Card></Grid>
  <Grid item xs={12}>
    <ReportSettings generatePDF={generatePDFFunc} getAllEntities={getAllFunc} label="URLs"/>
    </Grid>
    </Grid>
  
  
}


export default ReportSettingsURLs;