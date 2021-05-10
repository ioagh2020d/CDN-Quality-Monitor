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
    gap: theme.spacing(3),
  },
  urlField: {
    display: "flex",
    flexGrow: 1
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
          console.log(data)
          reset({
            ...data,
            cdns: data["cdns"],
            cdns1: data["cdns"][0].name,
            cdns2: data["cdns"][1].name,
            cdns3: data["cdns"][2].name,
            cdns4: data["cdns"][3].name,
            urls1: data["cdns"][0].urls.join(','),
            urls2: data["cdns"][1].urls.join(','),
            urls3: data["cdns"][2].urls.join(','),
            urls4: data["cdns"][3].urls.join(','),
          })
        })
        .catch(error => console.log(error))
    }, []
  )

  const onSubmit = (data) => {
    var cdns = [
      {name: data.cdns1.toString(), urls: data.urls1.split(',')},
      {name: data.cdns2.toString(), urls: data.urls2.split(',')},
      {name: data.cdns3.toString(), urls: data.urls3.split(',')},
      {name: data.cdns4.toString(), urls: data.urls4.split(',')}
    ]
    data = {
      activeSamplingRate: data.activeSamplingRate,
      activeTestIntensity: data.activeTestIntensity,
      passiveSamplingRate: data.passiveSamplingRate,
      cdns: cdns,
    }
    console.log(data)
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
  const {ref: cdnsRef1, ...cdnsParams1} = register("cdns1")
  const {ref: cdnsRef2, ...cdnsParams2} = register("cdns2")
  const {ref: cdnsRef3, ...cdnsParams3} = register("cdns3")
  const {ref: cdnsRef4, ...cdnsParams4} = register("cdns4")
  const {ref: urlsRef1, ...urlsParams1} = register("urls1")
  const {ref: urlsRef2, ...urlsParams2} = register("urls2")
  const {ref: urlsRef3, ...urlsParams3} = register("urls3")
  const {ref: urlsRef4, ...urlsParams4} = register("urls4")

  return (
    <Card>
      <Box p={4}>
        <form onSubmit={handleSubmit(onSubmit)} noValidate>
          <Typography variant={"h4"}>Settings</Typography>
          <Box mt={1} mb={3}>
            <Divider/>
          </Box>
          <Box display={"flex"} flexDirection={"column"}>
            <Box>
              <Typography variant={"h6"}>CDN domains</Typography>
              <Box display={"flex"} className={classes.flexGap} mt={1}>
                <TextField
                  label="First CDN name"
                  inputRef={cdnsRef1} {...cdnsParams1}
                  InputLabelProps={{shrink: true}}/>
                <TextField
                  className={classes.urlField}
                  label="First CDN urls (coma separated)"
                  inputRef={urlsRef1} {...urlsParams1}
                  InputLabelProps={{shrink: true}}/>
              </Box>
              <Box display={"flex"} className={classes.flexGap} mt={1}>
                <TextField
                  label="Second CDN name"
                  inputRef={cdnsRef2} {...cdnsParams2}
                  InputLabelProps={{shrink: true}}/>
                <TextField
                  className={classes.urlField}
                  label="Second CDN urls (coma separated)"
                  inputRef={urlsRef2} {...urlsParams2}
                  InputLabelProps={{shrink: true}}/>
              </Box>
              <Box display={"flex"} className={classes.flexGap} mt={1}>
                <TextField
                  label="Third CDN name"
                  inputRef={cdnsRef3} {...cdnsParams3}
                  InputLabelProps={{shrink: true}}/>
                <TextField
                  className={classes.urlField}
                  label="Third CDN urls (coma separated)"
                  inputRef={urlsRef3} {...urlsParams3}
                  InputLabelProps={{shrink: true}}/>
              </Box>
              <Box display={"flex"} className={classes.flexGap} mt={1}>
                <TextField
                  label="Fourth CDN name"
                  inputRef={cdnsRef4} {...cdnsParams4}
                  InputLabelProps={{shrink: true}}/>
                <TextField
                  className={classes.urlField}
                  label="Fourth CDN urls (coma separated)"
                  inputRef={urlsRef4} {...urlsParams4}
                  InputLabelProps={{shrink: true}}/>
              </Box>
            </Box>
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