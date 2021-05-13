import {Card} from "@material-ui/core";
import SingleChartRTTInd from "./SingleChartRTTInd";
import SingleChartTputInd from "./SingleChartTputInd";
import SingleChartPacketLossInd from "./SingleChartPacketLossInd";
import { Typography } from '@material-ui/core';
import Select from '@material-ui/core/Select';
import FormControl from '@material-ui/core/FormControl';
import { useState, useEffect } from "react";
import { makeStyles } from '@material-ui/core/styles';
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


async function getAllCdns(){
  return fetch(process.env.REACT_APP_API_URL + "/api/parameters")
    .then(response => response.json())
    .then(data => data['cdns'].map(cdn => cdn.name))
    .then(d =>{
      console.log(d);
      return d;
    })
}


const ChartsIndividual = (cdns) => {
  const classes = useStyles();
  const [cdn, setCDN] = useState("");
  const [allCdnsItems, setAllCdnsItems] = useState([]);




  useEffect(() => {
    getAllCdns().then( cdns => {
      let items = cdns.map(c => {
        return <MenuItem value={c}>{c}</MenuItem>
      });
      setAllCdnsItems(items);
      setCDN(cdns[0]);// TODO handle no cdns

    }).catch(error => console.log(error))
  }, []
  )
  const handleChange = (event) => {
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
          onChange={handleChange}

        >
          {allCdnsItems}
        </Select>
        </FormControl>
    </Card></Grid>
    <Grid item xs={12}> <Card className={classes.cardsG}>

      <Typography variant="h6">RTT</Typography>
      <SingleChartRTTInd cdnName={cdn}/>
      </Card></Grid>

      <Grid item xs={12}><Card className={classes.cardsG}>

      <Typography variant="h6">Throughput</Typography>
      <SingleChartTputInd cdnName={cdn}/>
      </Card></Grid>

      <Grid item xs={12}><Card className={classes.cardsG}>

      <Typography variant="h6">PacketLoss</Typography>
      <SingleChartPacketLossInd cdnName={cdn}/>      
      </Card></Grid>
      </Grid>
  )
}

export default ChartsIndividual