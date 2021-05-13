import {Card} from "@material-ui/core";
import SingleChartRTT from "./SingleChartRTT";
import SingleChartTput from "./SingleChartTput";
import SingleChartPacketLoss from "./SingleChartPacketLoss";
import { Typography } from '@material-ui/core';
import { makeStyles } from '@material-ui/core/styles';
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

const Charts = () => {
  const classes = useStyles();

  return (
    <Grid container spacing={2}>

    <Grid item xs={12}> <Card className={classes.cardsG}>

      <Typography variant="h6">RTT</Typography>
      <SingleChartRTT />
      </Card></Grid>

      <Grid item xs={12}><Card className={classes.cardsG}>

      <Typography variant="h6">Throughput</Typography>
      <SingleChartTput />
      </Card></Grid>

      <Grid item xs={12}><Card className={classes.cardsG}>

      <Typography variant="h6">PacketLoss</Typography>
      <SingleChartPacketLoss />      
      </Card></Grid>
      </Grid>
  )
}

export default Charts