const getAllAvailableMonitors = async () => {
  return fetch(process.env.REACT_APP_API_URL + "/api/monitors")
    .then(response => response.json())
    .then(data => data['monitors'].map(monitor => monitor.name))
    .then(a => {
      if (a.length > 1) {
        a.unshift("all")
      }
      return a;
    })
}

export {getAllAvailableMonitors}