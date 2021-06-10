import {
  Box,
  Button,
  Card,
  makeStyles,
} from "@material-ui/core";
import React, { useEffect, useState } from "react";
import { useForm, Controller } from "react-hook-form";
import Select from '@material-ui/core/Select';
import MenuItem from '@material-ui/core/MenuItem';
import Checkbox from '@material-ui/core/Checkbox';
import FormControlLabel from '@material-ui/core/FormControlLabel';
import CircularProgress from '@material-ui/core/CircularProgress';
import FormLabel from '@material-ui/core/FormLabel';
import DateFnsUtils from '@date-io/date-fns';
import {parametersHistoryFilter, legendOffsetCalculator} from "../DataGetter";
import { MuiPickersUtilsProvider, DateTimePicker } from '@material-ui/pickers';
import Slider from '@material-ui/core/Slider';
import {parse} from "json2csv";


function mergeDeviations(data){
  const resultArr = []

  for(let i = 0; i < data.length; i+=2){
    const data1 = data[i].data.filter(d => d.y != null);
    const data2 = data[i+1].data.filter(d => d.y != null);
    resultArr.push({id: data[i+1].id, data: data1.concat(data2)})

  }
  return resultArr;
}


const generateCSV = (measuredData, formData, entityTypeLabel, valueLabel) => {
  const columns = [entityTypeLabel, "TIMESTAMP", valueLabel];
  let mergedData = mergeDeviations(measuredData.data)
  mergedData = mergedData.filter(d => {
    return formData[d.id.replaceAll('.','^')] === true
  })
  const records = [];
  records.push(columns);

  for(const cdn of mergedData){
    for(const sample of cdn.data){
      records.push([cdn.id, sample.x.toISOString(), sample.y.toString()]);
    }
  }
  const csv = parse(records, {header: false});
  return csv;
};
const downloadStrFile = (str, filename) => {
  const csv =  new Blob([str], {
    type: 'text/plain'
  });
  const fileDownloadUrl = URL.createObjectURL(csv);
  let a = document.createElement('a');
  a.href = fileDownloadUrl;
  a.download = filename
  a.click();
  setTimeout(() => {
    window.URL.revokeObjectURL(fileDownloadUrl);
  }, 0)
}

const granularityValues = [5, 10, 20, 30, 60, 120, 240, 720, 1440]
const granularityMarks = [
  {
    value: 1,
    label: '5 min',
  },
  {
    value: 2,
    label: '10 min',
  },
  {
    value: 3,
    label: '20 min',
  },
  {
    value: 4,
    label: '30 min',
  },
  {
    value: 5,
    label: '1 h',
  },
  {
    value: 6,
    label: '2 h',
  },
  {
    value: 7,
    label: '4 h',
  },
  {
    value: 8,
    label: '12 h',
  },
  {
    value: 9,
    label: '1 day',
  },
];


const useStyles = makeStyles(theme => ({
  flexGap: {
    gap: theme.spacing(3),
  },

}))


const ReportSettings = ({ getAllEntities, generatePDF, label }) => {
  const classes = useStyles()
  const [allEntities, setAllEntities] = useState([]);
  const [entitiesLoading, setEntitesLoading] = useState(true);
  const { handleSubmit, control } = useForm({
    defaultValues: {
      'startDate': new Date(Date.now() - (1000 * 3600 * 5)),
      'endDate': new Date(Date.now()),
      'rtt': false,
      'granularity': 10
    }
  });



  useEffect(() => {
    getAllEntities().then(entities => {
      let items = entities.map(entity => {
        return <Controller
          name={entity.name.replaceAll('.', '^')}
          key={entity.name}
          control={control}
          defaultValue={false}
          render={({ field }) => <FormControlLabel control={<Checkbox {...field} />} label={entity.name} />}
        />
      });
      setAllEntities(items);
      setEntitesLoading(false);

    }).catch(error => console.warn(error))
  }, [getAllEntities, generatePDF])



  const onSubmit = async (data) => {
    generatePDF(data);
  }


  return (
    <Card>

      <form onSubmit={handleSubmit(onSubmit)} noValidate>
        <Box m={4} display="flex" flexDirection='column' >
          <Box m={2}>
            <MuiPickersUtilsProvider utils={DateFnsUtils}>
              <Controller
                passRef={true}
                name="startDate"
                control={control}
                rules={{ required: true }}
                render={({ field }) => <DateTimePicker value={field.value} onChange={field.onChange} style={{ margin: 2 }} ampm={false} />}
              />
              <Controller
                name="endDate"
                control={control}
                rules={{ required: true }}
                render={({ field }) => <DateTimePicker value={field.value} onChange={field.onChange} style={{ margin: 2 }} ampm={false} />}
              />
            </MuiPickersUtilsProvider>
          </Box>
          <Box m={2} display='flex' flexDirection='column'>
            <FormLabel component="legend" style={{ marginBottom: '1rem' }}>{label}:</FormLabel>
            {!entitiesLoading &&
              allEntities}
            {entitiesLoading && <Box p={2}><CircularProgress size={64} /></Box>}
          </Box>
          <Box m={2}>
            <FormLabel component="legend">Parameters</FormLabel>
            <Controller
              name="rtt"
              control={control}
              defaultValue={false}
              render={({ field }) => <FormControlLabel control={<Checkbox {...field} />} label="rtt" />}
            />
            <Controller
              name="throughput"
              control={control}
              defaultValue={false}
              render={({ field }) => <FormControlLabel control={<Checkbox {...field} />} label="throughput" />}
            />
            <Controller
              name="packetLoss"
              control={control}
              defaultValue={false}
              render={({ field }) => <FormControlLabel control={<Checkbox {...field} />} label="packetLoss" />}
            />
            <Box mt={4} mb={4} width="20%" display='flex' flexDirection='column'>
            <FormLabel component="legend" style={{ marginBottom: '1rem' }}>Report type:</FormLabel>
            <Controller
              name="reportType"
              control={control}
              defaultValue={"PDF"}
              render={({ field }) => <Select
                labelId="demo-simple-select-label"
                id="demo-simple-select"
                value={field.value}
                onChange={field.onChange}>
                <MenuItem value={"PDF"}>PDF</MenuItem>
                <MenuItem value={"CSV"}>CSV</MenuItem>
              </Select>}
            />
            </Box>
          </Box>
          <Box m={2} width='50%'>
            <Controller
              passRef={true}
              name="granularity"
              control={control}
              rules={{ required: true }}
              render={({ field }) => <Slider
                min={1}
                max={9}
                defaultValue={2}
                step={null}
                scale={(x) => granularityValues[x]}
                valueLabelFormat={(x) => ""}
                aria-labelledby="granularity"
                valueLabelDisplay="auto"
                marks={granularityMarks}
                onChangeCommitted={(event, gr) => field.onChange(granularityValues[gr - 1])} />}
            />

          </Box>
          <Box display={"flex"} alignSelf='flex-end'>
            <Button
              variant="contained"
              color="primary"
              type="submit"
            >
              download
                </Button>
          </Box>

        </Box>

      </form>
    </Card>
  )
}

export {ReportSettings, mergeDeviations, generateCSV, downloadStrFile}
