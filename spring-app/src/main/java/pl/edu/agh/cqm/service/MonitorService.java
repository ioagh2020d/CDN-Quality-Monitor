package pl.edu.agh.cqm.service;

import pl.edu.agh.cqm.data.dto.RTTSampleDTO;
import pl.edu.agh.cqm.data.dto.SubmitSamplesDTO;
import pl.edu.agh.cqm.data.dto.ThroughputSampleDTO;
import pl.edu.agh.cqm.data.model.Monitor;

public interface MonitorService {

    Monitor getLocalMonitor();

    void submitRTTSamples(SubmitSamplesDTO<RTTSampleDTO> samplesDTO, String address);

    void submitThroughputSamples(SubmitSamplesDTO<ThroughputSampleDTO> samplesDTO, String address);

}
