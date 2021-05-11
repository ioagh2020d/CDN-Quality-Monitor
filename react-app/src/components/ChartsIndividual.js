import {Card} from "@material-ui/core";
import SingleChartRTT from "./SingleChartRTT";
import SingleChartTput from "./SingleChartTput";
import SingleChartPacketLoss from "./SingleChartPacketLoss";
import { Typography } from '@material-ui/core';
const Charts = () => {

  return (
    <Card style={{ textAlign: "center"}}>
      <Typography variant="h6">RTT</Typography>
      <SingleChartRTT />
      <Typography variant="h6">Throughput</Typography>
      <SingleChartTput />
      <Typography variant="h6">PacketLoss</Typography>
      <SingleChartPacketLoss />      
    </Card>
  )
}

export default Charts