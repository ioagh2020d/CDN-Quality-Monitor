import {Card} from "@material-ui/core";
import SingleChartRTT from "./SingleChartRTT";
import SingleChartTput from "./SingleChartTput";
const Charts = () => {

  return (
    <Card>
      <SingleChartRTT />
      <SingleChartTput />
    </Card>
  )
}

export default Charts