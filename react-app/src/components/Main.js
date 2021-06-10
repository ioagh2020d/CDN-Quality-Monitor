import MenuBar from "./MenuBar";
import {Box, Card, Container, makeStyles} from "@material-ui/core";
import Settings from "./Settings";
import {Route, Switch} from "react-router-dom"
import Charts from "./Charts";
import ChartsIndividual from "./ChartsIndividual";
import ReportSettingsAll from "./ReportSettingsAll";
import ReportSettingsURLs from "./ReportSettingsURLs";
import ChartsComparison from "./ChartsComparison";

const Main = () => {
  return (
    <>
      <MenuBar/>
      <Container>
        <Box mt={3}>
          <Switch>
            <Route path={"/settings"}>
              <Settings/>
            </Route>
            <Route path={"/reportSettingsCDNs"}>
              <ReportSettingsAll/>
            </Route>
            <Route path={"/reportSettingsURLs"}>
              <ReportSettingsURLs/>
            </Route>
            <Route path={"/individualCDN"}>
              <ChartsIndividual/>
            </Route>
            <Route path={"/comparisonMonitors"}>
              <ChartsComparison/>
            </Route>
            <Route path={""}>
              <Charts/>
            </Route>
          </Switch>
        </Box>
      </Container>
    </>
  )
}

export default Main