import {
  AppBar,
  Box, Button,
  ButtonBase,
  CssBaseline,
  Icon,
  IconButton,
  makeStyles,
  Tab,
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