import {getRTTComp, getDataPrepared} from "../../DataGetter";
import {parametersHistoryFilter, legendOffsetCalculator} from "../../DataGetter";
import React, {useEffect, useState} from 'react';
import SingleChartGeneral from "./SingleChartGeneral";


const SingleChartRTTComp = ({cdnName}) => {
  console.log(cdnName);
  const [reloadToggler, setReloadToggler] = useState(false);

  let getDataCb = async (...args) => {

    const data = await getDataPrepared(getRTTComp, 'average', 'rtt', ...args, null, cdnName);
    const markers = parametersHistoryFilter(data.response.parameterHistory, ['activeSamplingRate', 'activeTestsIntensity']).map((r, i) => {
      const ts = new Date(r.timestamp);
      const legend = `asr ${r.activeSamplingRate} ati ${r.activeTestsIntensity}`;
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
  // let getDataCb = null;
  useEffect(() => {
    setReloadToggler(!reloadToggler);
  }, [cdnName]);


  return (
    <SingleChartGeneral dataInit={[]} getDataCb={getDataCb} reloadToggler={reloadToggler}
                        chartDesc={{leftAxisDesc: "ms"}}/>
  );
}

export default SingleChartRTTComp;