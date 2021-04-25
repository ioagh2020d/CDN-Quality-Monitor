package pl.edu.agh.cqm.service;

import pl.edu.agh.cqm.data.dto.DeviationDTO;
import pl.edu.agh.cqm.data.dto.RTTSampleDTO;
import pl.edu.agh.cqm.data.dto.ThroughputSampleDTO;

import java.util.List;
import java.util.Map;

public interface DeviationsService {

    Map<String, Map<String, List<DeviationDTO>>> getRTTDeviations(
            Map<String, List<RTTSampleDTO>> rttSamplesMap);

    Map<String, Map<String, List<DeviationDTO>>> getThroughputDeviations(
            Map<String, List<ThroughputSampleDTO>> throughputSamplesMap);

    Map<String, Map<String, List<DeviationDTO>>> getAllDeviations(
            Map<String, List<RTTSampleDTO>> rttSamplesMap,
            Map<String, List<ThroughputSampleDTO>> throughputSamplesMap);
}
