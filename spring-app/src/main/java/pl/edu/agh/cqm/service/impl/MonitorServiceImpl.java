package pl.edu.agh.cqm.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.edu.agh.cqm.data.dto.MonitorDTO;
import pl.edu.agh.cqm.data.model.Monitor;
import pl.edu.agh.cqm.data.repository.MonitorRepository;
import pl.edu.agh.cqm.service.MonitorService;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class MonitorServiceImpl implements MonitorService {

    private final MonitorRepository monitorRepository;

    @Override
    public Monitor getLocalMonitor() {
        return monitorRepository.getMonitorByName(MonitorRepository.LOCAL_MONITOR_NAME)
            .orElseThrow(() -> new IllegalStateException("Local monitor not present"));
    }

    @Override
    public List<MonitorDTO> getActiveMonitors() {
        return monitorRepository.findAll()
                .stream()
                .map(Monitor::toDTO)
                .collect(Collectors.toList());
    }

    @PostConstruct
    private void init() {
        if (monitorRepository.getMonitorByName(MonitorRepository.LOCAL_MONITOR_NAME).isEmpty()) {
            monitorRepository.save(Monitor.builder()
                .name(MonitorRepository.LOCAL_MONITOR_NAME)
                .build());
        }
    }
}
