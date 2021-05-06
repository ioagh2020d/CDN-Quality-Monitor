import {
  Box,
  Button,
  Card, Divider,
  InputAdornment,
  makeStyles,
  TextField,
  Typography
} from "@material-ui/core";
import React, {useEffect} from "react";
import {useForm} from "react-hook-form";
import {useHistory} from "react-router-dom"


const useStyles = makeStyles(theme => ({
  flexGap: {
    gap: theme.spacing(3)
  }
}))

const Settings = () => {
  const classes = useStyles()
  const history = useHistory()

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
        .catch(error => console.log(error))
    }, []
  )

  const onSubmit = (data) => {
    data = {
      ...data,
      cdns: data.cdns.split(',')
    }
    fetch(process.env.REACT_APP_API_URL + "/api/parameters", {
      "method": "PUT",
      headers: {"Content-Type": "application/json"},
      body: JSON.stringify(data)
    }).then(() => history.push(""))
      .catch(error => console.log(error))
  };

  const {ref: activeSamplingRateRef, ...activeSamplingRateParams} = register("activeSamplingRate")
  const {ref: activeTestIntensityRef, ...activeTestIntensityParams} = register("activeTestIntensity")
  const {ref: passiveSamplingRateRef, ...passiveSamplingRateParams} = register("passiveSamplingRate")
  const {ref: cdnsRef, ...cdnsParams} = register("cdns")

  return (
    <Card>
      <Box p={4}>
        <form onSubmit={handleSubmit(onSubmit)} noValidate>
          <Typography variant={"h4"}>Settings</Typography>
          <Box mt={1} mb={3}>
            <Divider/>
          </Box>
          <Box display={"flex"} flexDirection={"column"}>
            <TextField
              label="CDN domains (coma separated)"
              inputRef={cdnsRef} {...cdnsParams}
              InputLabelProps={{shrink: true}}/>
            <Box mt={3}>
              <Typography variant={"h6"}>Active Tests</Typography>
              <Box display={"flex"} className={classes.flexGap} mt={1}>
                <TextField
                  label={"Test intensity"}
                  InputProps={{
                    endAdornment: <InputAdornment position={"end"}>packets</InputAdornment>
                  }}
                  InputLabelProps={{shrink: true}}
                  inputRef={activeTestIntensityRef} {...activeTestIntensityParams}/>
                <TextField
                  label={"Test rate"}
                  InputProps={{
                    endAdornment: <InputAdornment position={"end"}>min</InputAdornment>
                  }}
                  InputLabelProps={{shrink: true}}
                  inputRef={activeSamplingRateRef} {...activeSamplingRateParams}/>
              </Box>
            </Box>

            <Box mt={3}>
              <Typography variant={"h6"}>Passive tests</Typography>
              <Box mt={1}>
                <TextField
                  label={"Test rate"}
                  InputProps={{
                    endAdornment: <InputAdornment position={"end"}>min</InputAdornment>
                  }}
                  InputLabelProps={{shrink: true}}
                  inputRef={passiveSamplingRateRef} {...passiveSamplingRateParams}/>
              </Box>
            </Box>
            <Box display={"flex"} justifyContent={"end"} mt={3}>
              <Button
                variant="contained"
                color="primary"
                type="submit"
                disabled={isSubmitting}
              >
                Submit
              </Button>
            </Box>
          </Box>
        </form>
      </Box>
    </Card>
  )
}

export default Settings