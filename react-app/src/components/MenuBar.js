import {
  AppBar,
  Box, CssBaseline,
  IconButton,
  makeStyles,
  Toolbar,
  Typography
} from "@material-ui/core";
import {Settings} from "@material-ui/icons";
import {Link} from "react-router-dom"

const useStyles = makeStyles(theme => ({
  offset: theme.mixins.toolbar,
  title: {
    flexGrow: 1
  },
  titleText: {
    textDecoration: "none"
  }
}))

const MenuBar = () => {
  const classes = useStyles()

  return (
    <div>
      <CssBaseline/>
      <AppBar>
        <Toolbar>
          <Box className={classes.title}>
            <IconButton color={"inherit"}>
              <Typography variant={"h6"} component={Link} color={"inherit"} to={""} className={classes.titleText}>
                CQM
              </Typography>
            </IconButton>
          </Box>
          <Box className={classes.title}>
            <IconButton color={"inherit"}>
              <Typography variant={"h6"} component={Link} color={"inherit"} to={"/individualCDN"}
                          className={classes.titleText}>
                Individual
              </Typography>
            </IconButton>
          </Box>
          <Box className={classes.title}>
            <IconButton color={"inherit"}>
              <Typography variant={"h6"} component={Link} color={"inherit"} to={"/comparisonMonitors"}
                          className={classes.titleText}>
                Comparison
              </Typography>
            </IconButton>
          </Box>
          <Box className={classes.title}>
            <IconButton color={"inherit"}>
              <Typography variant={"h6"} component={Link} color={"inherit"} to={"/reportSettingsCDNs"}
                          className={classes.titleText}>
                Report CDNs
              </Typography>
            </IconButton>
          </Box>
          <Box className={classes.title}>
            <IconButton color={"inherit"}>
              <Typography variant={"h6"} component={Link} color={"inherit"} to={"/reportSettingsURLs"}
                          className={classes.titleText}>
                Report URLs
              </Typography>
            </IconButton>
          </Box>
          <IconButton component={Link} to={"/settings"} color={"inherit"}>
            <Settings/>
          </IconButton>
        </Toolbar>
      </AppBar>
      <div className={classes.offset}/>
    </div>
  )
}

export default MenuBar