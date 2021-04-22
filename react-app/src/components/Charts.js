import {Card} from "@material-ui/core";
import SingleChartRTT from "./SingleChartRTT";
import SingleChartTput from "./SingleChartTput";
import { Typography } from '@material-ui/core';
const Charts = () => {

  return (
    <Card style={{ textAlign: "center"}}>
      <Typography variant="h6">RTT</Typography>
      <SingleChartRTT />
      <Typography variant="h6">Throughput</Typography>
      <SingleChartTput />
    </Card>
  )
}

export default Charts