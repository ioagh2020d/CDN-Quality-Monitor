import {ReportSettings, generateCSV, downloadStrFile} from '../reports/ReportSettings'
import React, {useEffect, useState} from "react";
import generatePDFComponent from "../reports/PDFReport";
import {pdf} from '@react-pdf/renderer';
import {getRTTInd, getThroughputInd, getDataPrepared} from "../../DataGetter";
import {Card} from "@material-ui/core";
import Select from '@material-ui/core/Select';
import FormControl from '@material-ui/core/FormControl';
import {makeStyles} from '@material-ui/core/styles';
import InputLabel from '@material-ui/core/InputLabel';
import MenuItem from '@material-ui/core/MenuItem';
import {Grid} from '@material-ui/core';

import CircularProgress from '@material-ui/core/CircularProgress';
import {getAllAvailableMonitors} from "../../Monitors";


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

const generatePDF = async (cdn, monitor, data) => {
  let commonToQueries = [
    data.startDate, data.endDate, data.granularity, monitor, cdn
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
  if (data.reportType === "PDF") {
    pdf(reportComponent).toBlob().then(b => {
      const fileDownloadUrl = URL.createObjectURL(b);
      let a = document.createElement('a');
      a.href = fileDownloadUrl;
      a.download = "Report.pdf"
      a.click();
      setTimeout(() => {
        window.URL.revokeObjectURL(fileDownloadUrl);
      }, 0)
    });
  } else {
    if (exportData.rtt) {
      downloadStrFile(generateCSV(exportData.rtt, data, "URL", "RTT [ms]"), "ReportRTT.csv");
    }
    if (exportData.throughput) {
      downloadStrFile(generateCSV(exportData.throughput, data, "URL", "Throughput [kbps]"), "ReportThroughput.csv");
    }
    if (exportData.packetLoss) {
      downloadStrFile(generateCSV(exportData.packetLoss, data, "URL", "packetloss [%]"), "ReportPacketLoss.csv");
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

const getAllUrls = async (cdn) => {
  return fetch(process.env.REACT_APP_API_URL + "/api/parameters")
    .then(response => response.json())
    .then(data => data['cdns'].filter(c => c.name === cdn).map(c => c.urls))
    .then(d => {
      if (d.length === 1) return d[0].map(u => {
        return {'name': u}
      });
      else return [];
    })
}


const ReportGeneratorURLs = () => {
  const classes = useStyles();
  const [cdn, setCDN] = useState("");
  const [monitor, setMonitor] = useState("all");
  const [allCdnsItems, setAllCdnsItems] = useState([]);
  const [allMonitorsItems, setAllMonitorsItems] = useState([]);
  const [cdnsLoaded, setCdnsLoaded] = useState(false);
  const [monitorsLoaded, setMonitorsLoaded] = useState(false);


  const [getAllFunc, setGetAllFunc] = useState(() => () => getAllUrls(""));
  const [generatePDFFunc, setGeneratePDFFunc] = useState(() => (data) => generatePDF("", "all", data));

  useEffect(() => {
      getAllCdns().then(cdns => {
        let items = cdns.map(c => {
          return <MenuItem key={c} value={c}>{c}</MenuItem>
        });
        setAllCdnsItems(items);
        setCdnsLoaded(true)
        setCDN(cdns[0]);// TODO handle no cdns
      }).catch(error => console.log(error));
      getAllAvailableMonitors().then(monitors => {
        let items = monitors.map(m => {
          return <MenuItem key={m} value={m}>{m}</MenuItem>
        });
        setAllMonitorsItems(items);
        setMonitor(monitors[0]);
        setMonitorsLoaded(true);
      }).catch(error => console.log(error))

    }, []
  )

  useEffect(() => {
    setGetAllFunc(() => () => getAllUrls(cdn));
    setGeneratePDFFunc(() => (data) => generatePDF(cdn, monitor, data));

  }, [cdn, monitor]);
  const handleChangeCDN = (event) => {
    setCDN(event.target.value);
  };

  const handleChangeMonitor = (event) => {
    setMonitor(event.target.value)
  }

  return <Grid container spacing={2}>
    <Grid item xs={12}>
      <Card className={classes.cardsG} style={{textAlign: 'left', padding: '1em'}}>
        <FormControl className={classes.formControl}>
          <InputLabel id="demo-simple-select-label">Monitor</InputLabel>
          <Select
            labelId="demo-simple-select-label"
            id="demo-simple-select"
            value={monitor}
            onChange={handleChangeMonitor}
          >
            {allMonitorsItems}
          </Select>
        </FormControl>
        <FormControl className={classes.formControl}>
          <InputLabel id="demo-simple-select-label">CDN</InputLabel>
          <Select
            labelId="demo-simple-select-label"
            id="demo-simple-select"
            value={cdn}
            onChange={handleChangeCDN}
          >
            {allCdnsItems}

          </Select>
        </FormControl>
        {(!cdnsLoaded || !monitorsLoaded) && <CircularProgress size={44}/>}
      </Card></Grid>
    <Grid item xs={12}>
      <ReportSettings generatePDF={generatePDFFunc} getAllEntities={getAllFunc} label="URLs"/>
    </Grid>
  </Grid>
}


export default ReportGeneratorURLs;