import {Card} from "@material-ui/core";
import SingleChartRTT from "../charts/SingleChartRTT";
import SingleChartTput from "../charts/SingleChartTput";
import SingleChartPacketLoss from "../charts/SingleChartPacketLoss";
import {Typography} from '@material-ui/core';
import Select from '@material-ui/core/Select';
import FormControl from '@material-ui/core/FormControl';
import {useState, useEffect} from "react";
import {makeStyles} from '@material-ui/core/styles';
import InputLabel from '@material-ui/core/InputLabel';
import MenuItem from '@material-ui/core/MenuItem';
import {Grid} from '@material-ui/core';
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

const Charts = () => {
  const classes = useStyles();
  const [monitor, setMonitor] = useState("all");
  const [allMonitorsItems, setAllMonitorsItems] = useState([]);

  useEffect(() => {
      getAllAvailableMonitors().then(monitors => {
        let items = monitors.map(m => {
          return <MenuItem key={m} value={m}>{m}</MenuItem>
        });
        setAllMonitorsItems(items);
        setMonitor(monitors[0]);
      }).catch(error => console.log(error))
    }, []
  )
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