package pl.edu.agh.cqm.service;

import pl.edu.agh.cqm.data.dto.MonitorDTO;
import pl.edu.agh.cqm.data.model.Monitor;

import java.util.List;

public interface MonitorService {

    Monitor getLocalMonitor();

    List<MonitorDTO> getActiveMonitors();
}
