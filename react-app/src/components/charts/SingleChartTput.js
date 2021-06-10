import {getThroughput, getDataPrepared, getThroughputInd} from "../../DataGetter";
import {parametersHistoryFilter, legendOffsetCalculator} from "../DataGetter";
import React, {useEffect, useState} from 'react';
import SingleChartGeneral from "./SingleChartGeneral";


const SingleChartTput = ({monitorIP}) => {
  const [reloadToggler, setReloadToggler] = useState(false);

  async function getDataCb(...args) {

    const data = await getDataPrepared(getThroughput, 'throughput', 'throughput', ...args, monitorIP);
    const markers = parametersHistoryFilter(data.response.parameterHistory, ['passiveSamplingRate']).map((r, i) => {
      const ts = new Date(r.timestamp);
      const legend = `psr ${r.passiveSamplingRate}`;
      return {
        axis: 'x',
        value: ts,
        lineStyle: {stroke: '#b0413e', strokeWidth: 2},
        textStyle: {fill: 'grey'},
        legend: legend,
        legendOffsetY: legendOffsetCalculator(i),
        legendOffsetX: 1
      };
    })
    data.markers = data.markers.concat(markers);
    return data;
  }

  useEffect(() => {
    setReloadToggler(!reloadToggler);
  }, [monitorIP]);
  return (
    <SingleChartGeneral dataInit={[]} getDataCb={getDataCb} reloadToggler={reloadToggler}
                        chartDesc={{leftAxisDesc: "kb/s"}}/>
  );

}

export default SingleChartTput;