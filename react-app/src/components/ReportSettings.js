import {
  Box,
  Button,
  Card, Divider,
  InputAdornment,
  makeStyles,
  TextField,
  Typography
} from "@material-ui/core";
import React, { useEffect, useState } from "react";
import { useForm, Controller } from "react-hook-form";
import generatePDFComponent from "./PDFReport";
import { pdf } from '@react-pdf/renderer';
import Checkbox from '@material-ui/core/Checkbox';
import FormControlLabel from '@material-ui/core/FormControlLabel';
import CircularProgress from '@material-ui/core/CircularProgress';
import FormLabel from '@material-ui/core/FormLabel';
import DateFnsUtils from '@date-io/date-fns';
import { getRTT, getThroughput, getDataPrepared } from "../DataGetter";
import { MuiPickersUtilsProvider, DateTimePicker } from '@material-ui/pickers';
import Slider from '@material-ui/core/Slider';

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

async function getAllCdns() {
  return fetch(process.env.REACT_APP_API_URL + "/api/parameters")
    .then(response => response.json())
    .then(data => data['cdns'])
    .then(d => {
      return d;
    })
}

const ReportSettings = () => {
  const classes = useStyles()
  const [allCdnsItems, setAllCdnsItems] = useState([]);
  const [cdnLoading, setCdnLoading] = useState(true);
  const { handleSubmit, control, reset } = useForm({
    defaultValues: {
      'startDate': new Date(Date.now() - (1000 * 3600 * 5)),
      'endDate': new Date(Date.now()),
      'rtt': false,
      'granularity': 10
    }
  });



  useEffect(() => {
    getAllCdns().then(cdns => {
      let items = cdns.map(cdn => {
        return <Controller
          name={cdn.name.replace('.', '_')}
          key={cdn.name}
          control={control}
          defaultValue={false}
          render={({ field }) => <FormControlLabel control={<Checkbox {...field} />} label={cdn.name} />}
        />
      });
      setAllCdnsItems(items);
      setCdnLoading(false);

    }).catch(error => console.warn(error))
  }, [])



  const onSubmit = async (data) => {
    let commonToQueries = [
      data.startDate, data.endDate, data.granularity
    ]
    const exportData = {}

    if (data.rtt) {
      exportData.rtt = await getDataPrepared(getRTT, 'average', 'rtt', ...commonToQueries).catch(error => console.warn(error));
    }
    if (data.throughput) {
      exportData.throughput = await getDataPrepared(getThroughput, 'throughput', 'throughput', ...commonToQueries).catch(error => console.warn(error));
    }
    if (data.packetLoss) {
      exportData.packetLoss = await getDataPrepared(getRTT, 'packetLoss', 'packetLoss', ...commonToQueries).catch(error => console.warn(error));
    }
    const reportComponenet = generatePDFComponent(exportData, data);

    const blob = pdf(reportComponenet).toBlob().then(b => {
      const fileDownloadUrl = URL.createObjectURL(b);
      let ref;
      let a = document.createElement('a');
      a.href = fileDownloadUrl;
      a.download = "Report.pdf"
      a.click();
      setTimeout(() => {
        window.URL.revokeObjectURL(fileDownloadUrl);
      }, 0)

    });

  };


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
            <FormLabel component="legend" style={{ marginBottom: '1rem' }}>CDNs:</FormLabel>
            {!cdnLoading &&
              allCdnsItems}
            {cdnLoading && <Box p={2}><CircularProgress size={64} /></Box>}
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
                  onChangeCommitted={(event, gr) => field.onChange(granularityValues[gr - 1])}/>}
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

export default ReportSettings
