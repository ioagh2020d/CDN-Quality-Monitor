import {Card} from "@material-ui/core";
import SingleChartRTT from "./SingleChartRTT";
import SingleChartTput from "./SingleChartTput";
import SingleChartPacketLoss from "./SingleChartPacketLoss";
import {Typography} from '@material-ui/core';
import Select from '@material-ui/core/Select';
import FormControl from '@material-ui/core/FormControl';
import {useState, useEffect} from "react";
import {makeStyles} from '@material-ui/core/styles';
import InputLabel from '@material-ui/core/InputLabel';
import MenuItem from '@material-ui/core/MenuItem';
import {Grid} from '@material-ui/core';

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

async function getAllAvailableMonitors() {
  return fetch(process.env.REACT_APP_API_URL + "/api/monitors")
    .then(response => response.json())
    .then(data => data['monitors'].map(monitor => monitor.address))
    .then(a => {
      const choices = ["LOCAL", "ALL", "COMPARE"];
      a.map(m => choices.push(m));
      console.log(choices);
      return choices;
    })
}

const Charts = (monitors) => {
  const classes = useStyles();
  const [monitor, setMonitor] = useState("");
  const [allMonitorsItems, setAllMonitorsItems] = useState([]);

  useEffect(() => {
      getAllAvailableMonitors().then(monitors => {
        let items = monitors.map(m => {
          return <MenuItem value={m}>{m}</MenuItem>
        });
        setAllMonitorsItems(items);
        setMonitor(monitors[0]);// TODO handle no monitors
      }).catch(error => console.log(error))
    }, []
  )
  const handleChange = (event) => {
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
              onChange={handleChange}
            >
              {allMonitorsItems}
            </Select>
          </FormControl>
        </Card></Grid>
      <Grid item xs={12}> <Card className={classes.cardsG}>

        <Typography variant="h6">RTT</Typography>
        <SingleChartRTT monitorIP={monitor}/>
      </Card></Grid>

      <Grid item xs={12}><Card className={classes.cardsG}>

        <Typography variant="h6">Throughput</Typography>
        <SingleChartTput monitorIP={monitor}/>
      </Card></Grid>

      <Grid item xs={12}><Card className={classes.cardsG}>

        <Typography variant="h6">PacketLoss</Typography>
        <SingleChartPacketLoss monitorIP={monitor}/>
      </Card></Grid>
    </Grid>
  )
}

export default Charts