package pl.edu.agh.cqm.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.edu.agh.cqm.configuration.CqmConfiguration;
import pl.edu.agh.cqm.data.dto.MonitorDTO;
import pl.edu.agh.cqm.data.model.Monitor;
import pl.edu.agh.cqm.data.dto.RTTSampleDTO;
import pl.edu.agh.cqm.data.dto.SubmitSamplesDTO;
import pl.edu.agh.cqm.data.dto.ThroughputSampleDTO;
import pl.edu.agh.cqm.data.model.*;
import pl.edu.agh.cqm.data.repository.MonitorRepository;
import pl.edu.agh.cqm.data.repository.RTTSampleRepository;
import pl.edu.agh.cqm.data.repository.ThroughputSampleRepository;
import pl.edu.agh.cqm.service.MonitorService;
import pl.edu.agh.cqm.service.ParameterService;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;
import javax.transaction.Transactional;

@Service
@AllArgsConstructor
public class MonitorServiceImpl implements MonitorService {

    private final MonitorRepository monitorRepository;
    private final ParameterService parameterService;
    private final RTTSampleRepository rttSampleRepository;
    private final ThroughputSampleRepository throughputSampleRepository;
    private final CqmConfiguration cqmConfiguration;

    @Override
    public boolean isLocal() {
        return cqmConfiguration.isLocal();
    }

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

    @Transactional
    @Override
    public void submitRTTSamples(SubmitSamplesDTO<RTTSampleDTO> samplesDTO, String address) {
        var monitor = getOrCreateMonitor(address);

        samplesDTO.getSamples().stream()
            .map(sampleDTO -> RTTSample.builder()
                .monitor(monitor)
                .url(parameterService.getOrCreateUrl(sampleDTO.getCdnName(), sampleDTO.getUrl()))
                .average(sampleDTO.getSample().getAverage())
                .max(sampleDTO.getSample().getMax())
                .min(sampleDTO.getSample().getMin())
                .packetLoss(sampleDTO.getSample().getPacketLoss())
                .standardDeviation(sampleDTO.getSample().getStandardDeviation())
                .timestamp(sampleDTO.getSample().getTimestamp())
                .type(sampleDTO.getSample().getType())
                .build())
            .forEach(rttSampleRepository::save);

    }

    @Transactional
    @Override
    public void submitThroughputSamples(SubmitSamplesDTO<ThroughputSampleDTO> samplesDTO, String address) {
        var monitor = getOrCreateMonitor(address);

        samplesDTO.getSamples().stream()
            .map(sampleDTO -> ThroughputSample.builder()
                .monitor(monitor)
                .url(parameterService.getOrCreateUrl(sampleDTO.getCdnName(), sampleDTO.getUrl()))
                .throughput(sampleDTO.getSample().getThroughput())
                .timestamp(sampleDTO.getSample().getTimestamp())
                .build())
            .forEach(throughputSampleRepository::save);
    }

    private Monitor getOrCreateMonitor(String address) {
        return monitorRepository.getMonitorByName(address)
            .orElseGet(() -> {
                var newMonitor = new Monitor(null, address);
                monitorRepository.save(newMonitor);
                return newMonitor;
            });
    }

    @PostConstruct
    private void init() {
        if (cqmConfiguration.isLocal()) {
            if (monitorRepository.getMonitorByName(MonitorRepository.LOCAL_MONITOR_NAME).isEmpty()) {
                monitorRepository.save(Monitor.builder()
                    .name(MonitorRepository.LOCAL_MONITOR_NAME)
                    .build());
            }
        }
    }
}
