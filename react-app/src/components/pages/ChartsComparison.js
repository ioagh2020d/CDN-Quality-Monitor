import {Card} from "@material-ui/core";
import SingleChartRTTComp from "../charts/SingleChartRTTComp";
import SingleChartTputComp from "../charts/SingleChartTputComp";
import SingleChartPacketLossComp from "../charts/SingleChartPacketLossComp";
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

const ChartsComparison = () => {
  const classes = useStyles();
  const [cdn, setCDN] = useState("");
  const [allCdnsItems, setAllCdnsItems] = useState([]);
  const [cdnsLoaded, setCdnsLoaded] = useState(false);

  useEffect(() =>{
    if(cdn !== "") setCdnsLoaded(true);
  }, [cdn]);

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
  const handleChangeCDN = (event) => {
    setCDN(event.target.value);
  };
  return (
    <Grid container spacing={2}>
      <Grid item xs={12}>
        <Card className={classes.cardsG} style={{textAlign: 'left', padding: '1em'}}>
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
          <SingleChartRTTComp cdnName={cdn}/>
        </Card></Grid>}


      {cdnsLoaded && <Grid item xs={12}>
        <Card className={classes.cardsG}>

          <Typography variant="h6">Throughput</Typography>
          <SingleChartTputComp cdnName={cdn}/>
        </Card></Grid>}

      {cdnsLoaded && <Grid item xs={12}>
        <Card className={classes.cardsG}>

          <Typography variant="h6">PacketLoss</Typography>
          <SingleChartPacketLossComp cdnName={cdn}/>
        </Card></Grid>}
      </Grid>
        )
}

export default ChartsComparison