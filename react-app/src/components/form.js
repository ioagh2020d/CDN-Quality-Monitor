import React, {useEffect} from "react";
import {useForm} from "react-hook-form";
import {
  TextField,
  Grid,
  Button,
  FormLabel,
} from '@material-ui/core';

function Form() {
  const {register, handleSubmit, reset, formState: {isSubmitting}} = useForm({});

  useEffect(() => {
    fetch(process.env.REACT_APP_API_URL + "/api/parameters")
      .then(response => response.json())
      .then(data => {
        reset({
          ...data,
          cdns: data["cdns"].join(','),
        })
      })
  }, [])

  const onSubmit = (data) => {
    data = {
      ...data,
      cdns: data.cdns.split(',')
    }
    fetch(process.env.REACT_APP_API_URL + "/api/parameters", {
      "method": "PUT",
      headers: {"Content-Type": "application/json"},
      body: JSON.stringify(data)
    }).then(() => console.log("sent"))
      .catch(error => console.log(error))
    alert(JSON.stringify(data));
  };

  const {ref: activeSamplingRateRef, ...activeSamplingRateParams} = register("activeSamplingRate")
  const {ref: activeTestIntensityRef, ...activeTestIntensityParams} = register("activeTestIntensity")
  const {ref: passiveSamplingRateRef, ...passiveSamplingRateParams} = register("passiveSamplingRate")
  const {ref: cdnsRef, ...cdnsParams} = register("cdns")
  return (
    <>
      <div style={{padding: 16, margin: 'auto', maxWidth: 600}}>
        <form onSubmit={handleSubmit(onSubmit)} noValidate>
          <Grid container alignItems="flex-start" spacing={2}>
            <Grid item xs={12}>
              <FormLabel component="legend">Domains of CDNs (comma separated):</FormLabel>
              <TextField inputRef={cdnsRef} {...cdnsParams}/>
            </Grid>
            <Grid item xs={12}>
              <FormLabel component="legend">Active tests sampling rate [minutes]</FormLabel>
              <TextField inputRef={activeSamplingRateRef} {...activeSamplingRateParams}/>
            </Grid>
            <Grid item xs={12}>
              <FormLabel component="legend">Active tests intensity [packets]</FormLabel>
              <TextField inputRef={activeTestIntensityRef} {...activeTestIntensityParams}/>
            </Grid>
            <Grid item xs={12}>
              <FormLabel component="legend">Passive tests rate [minutes]</FormLabel>
              <TextField inputRef={passiveSamplingRateRef} {...passiveSamplingRateParams}/>
            </Grid>
            <Button
              variant="contained"
              color="primary"
              type="submit"
              disabled={isSubmitting}
            >
              Submit
            </Button>
          </Grid>
        </form>
      </div>
    </>
  );
}

export default Form