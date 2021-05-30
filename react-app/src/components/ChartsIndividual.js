import {Card} from "@material-ui/core";
import SingleChartRTTInd from "./SingleChartRTTInd";
import SingleChartTputInd from "./SingleChartTputInd";
import SingleChartPacketLossInd from "./SingleChartPacketLossInd";
import {Typography} from '@material-ui/core';
import Select from '@material-ui/core/Select';
import FormControl from '@material-ui/core/FormControl';
import React, {useState, useEffect} from "react";
import {makeStyles} from '@material-ui/core/styles';
import InputLabel from '@material-ui/core/InputLabel';
import MenuItem from '@material-ui/core/MenuItem';
import {Grid} from '@material-ui/core';
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
  },
}));

async function getAllCdns() {
  return fetch(process.env.REACT_APP_API_URL + "/api/parameters")
    .then(response => response.json())
    .then(data => data['cdns'].map(cdn => cdn.name))
    .then(d => {
      return d;
    })
}

async function getAllAvailableMonitors() {
  return fetch(process.env.REACT_APP_API_URL + "/api/monitors")
    .then(response => response.json())
    .then(data => data['monitors'].map(monitor => monitor.name))
    .then(a => {
      a.push("all")
      console.log(a);
      return a;
    })
}

const ChartsIndividual = (monitors, cdns) => {
  const classes = useStyles();
  const [cdn, setCDN] = useState("");
  const [allCdnsItems, setAllCdnsItems] = useState([]);
  const [cdnsLoaded, setCdnsLoaded] = useState(false);
  const [monitor, setMonitor] = useState("");
  const [allMonitorsItems, setAllMonitorsItems] = useState([]);

  useEffect(() =>{
    if(cdn != "") setCdnsLoaded(true);
  }, [cdn]);

  useEffect(() => {
      getAllCdns().then(cdns => {
        let items = cdns.map(c => {
          return <MenuItem key={c} value={c}>{c}</MenuItem>
        });
        setAllCdnsItems(items);
        setCDN(cdns[0]);// TODO handle no cdns
      }).catch(error => console.log(error));
      getAllAvailableMonitors().then(monitors => {
        let items = monitors.map(m => {
          return <MenuItem key={m} value={m}>{m}</MenuItem>
        });
        setAllMonitorsItems(items);
        setMonitor(monitors[0]);
      }).catch(error => console.log(error))
    }, []
  )
  const handleChangeCDN = (event) => {
    setCDN(event.target.value);
  };
  const handleChangeMonitor = (event) => {
    setMonitor(event.target.value);
  };
  return (
    <Grid container spacing={2}>
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
          {!cdnsLoaded && <CircularProgress size={44}/>}
        </Card></Grid>
      {cdnsLoaded && <Grid item xs={12}>
        <Card className={classes.cardsG}>

          <Typography variant="h6">RTT</Typography>
          <SingleChartRTTInd cdnName={cdn} monitorIP={monitor}/>
        </Card></Grid>}


      {cdnsLoaded && <Grid item xs={12}>
        <Card className={classes.cardsG}>

          <Typography variant="h6">Throughput</Typography>
          <SingleChartTputInd cdnName={cdn} monitorIP={monitor}/>
        </Card></Grid>}

      {cdnsLoaded && <Grid item xs={12}>
        <Card className={classes.cardsG}>

          <Typography variant="h6">PacketLoss</Typography>
          <SingleChartPacketLossInd cdnName={cdn} monitorIP={monitor}/>
        </Card></Grid>}
      </Grid>
        )
}

export default ChartsIndividual