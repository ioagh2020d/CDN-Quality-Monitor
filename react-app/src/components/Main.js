import MenuBar from "./MenuBar";
import {Box, Card, Container, makeStyles} from "@material-ui/core";
import Settings from "./Settings";
import {Route, Switch} from "react-router-dom"
import Charts from "./Charts";

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