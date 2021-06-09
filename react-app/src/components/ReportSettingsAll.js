import {ReportSettings, generateCSV, downloadStrFile} from './ReportSettings'
import React from "react";
import generatePDFComponent from "./PDFReport";
import { pdf } from '@react-pdf/renderer';
import CircularProgress from '@material-ui/core/CircularProgress';
import { getRTT, getThroughput, getDataPrepared } from "../DataGetter";



const generatePDF =  async  (data) =>{
  let commonToQueries = [
    data.startDate, data.endDate, data.granularity
  ]
  const exportData = {}

  if (data.rtt) {
    exportData.rtt = await getDataPrepared(getRTT, 'average', 'rtt', ...commonToQueries).catch(error => console.warn(error));
  }
  if (data.throughput) {
    exportData.throughput = await getDataPrepared(getThroughput, 'throughput', 'throughput', ...commonToQueries).catch(error => console.warn(error));
  }
  if (data.packetLoss) {
    exportData.packetLoss = await getDataPrepared(getRTT, 'packetLoss', 'packetLoss', ...commonToQueries).catch(error => console.warn(error));
  }
  if(data.reportType === "PDF"){
    const reportComponent = generatePDFComponent(exportData, data);
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
      downloadStrFile(generateCSV(exportData.rtt, data, "CDN", "RTT [ms]"), "ReportRTT.csv");
    }
    if(exportData.throughput){
      downloadStrFile(generateCSV(exportData.throughput, data, "CDN", "Throughput [kbps]"), "ReportThroughput.csv");
    }
    if(exportData.packetLoss){
      downloadStrFile(generateCSV(exportData.packetLoss, data, "CDN", "packetloss [%]"), "ReportPacketLoss.csv");
    }
  }

};

async function getAllCdns() {
  return fetch(process.env.REACT_APP_API_URL + "/api/parameters")
    .then(response => response.json())
    .then(data => data['cdns'])
    .then(d => {
      return d;
    })
}

const ReportSettingsAll = () => {

      
  return <ReportSettings generatePDF={generatePDF} getAllEntities={getAllCdns} label="CDNs"/>
}


export default ReportSettingsAll;