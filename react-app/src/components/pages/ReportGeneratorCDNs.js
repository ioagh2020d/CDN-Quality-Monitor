import {ReportSettings, generateCSV, downloadStrFile} from '../reports/ReportSettings'
import React, {useEffect, useState} from "react";
import generatePDFComponent from "../reports/PDFReport";
import {pdf} from '@react-pdf/renderer';
import {getRTT, getThroughput, getDataPrepared} from "../../DataGetter";
import {Card, Grid} from "@material-ui/core";
import FormControl from "@material-ui/core/FormControl";
import InputLabel from "@material-ui/core/InputLabel";
import Select from "@material-ui/core/Select";
import CircularProgress from "@material-ui/core/CircularProgress";
import {makeStyles} from "@material-ui/core/styles";
import MenuItem from "@material-ui/core/MenuItem";
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

const generatePDF = async (monitor, data) => {
  let commonToQueries = [
    data.startDate, data.endDate, data.granularity, monitor
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
  if (data.reportType === "PDF") {
    const reportComponent = generatePDFComponent(exportData, data);
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
      downloadStrFile(generateCSV(exportData.rtt, data, "CDN", "RTT [ms]"), "ReportRTT.csv");
    }
    if (exportData.throughput) {
      downloadStrFile(generateCSV(exportData.throughput, data, "CDN", "Throughput [kbps]"), "ReportThroughput.csv");
    }
    if (exportData.packetLoss) {
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

const ReportGeneratorCDNs = () => {
  const classes = useStyles();

  const [monitor, setMonitor] = useState("all")
  const [allMonitorsItems, setAllMonitorsItems] = useState([])
  const [monitorsLoaded, setMonitorsLoaded] = useState(false)

  const [generatePDFFunc, setGeneratePDFFunc] = useState(() => (data) => generatePDF("all", data));

  useEffect(() => {
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
    setGeneratePDFFunc(() => (data) => generatePDF(monitor, data));

  }, [monitor]);

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
        {!monitorsLoaded && <CircularProgress size={44}/>}
      </Card></Grid>
    <Grid item xs={12}>
      <ReportSettings generatePDF={generatePDFFunc} getAllEntities={getAllCdns} label="CDNs"/>
    </Grid>
  </Grid>
}


export default ReportGeneratorCDNs;