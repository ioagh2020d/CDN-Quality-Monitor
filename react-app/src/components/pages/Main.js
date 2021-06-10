import MenuBar from "../MenuBar";
import {Box, Container} from "@material-ui/core";
import Settings from "../Settings";
import {Route, Switch} from "react-router-dom"
import Charts from "./Charts";
import ChartsIndividual from "./ChartsIndividual";
import ReportGeneratorCDNs from "./ReportGeneratorCDNs";
import ReportGeneratorURLs from "./ReportGeneratorURLs";
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
              <ReportGeneratorCDNs/>
            </Route>
            <Route path={"/reportSettingsURLs"}>
              <ReportGeneratorURLs/>
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