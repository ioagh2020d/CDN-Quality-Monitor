package pl.edu.agh.cqm.service;

import pl.edu.agh.cqm.data.dto.CdnDeviations;
import pl.edu.agh.cqm.data.dto.DeviationDTO;
import pl.edu.agh.cqm.data.dto.RTTSampleDTO;
import pl.edu.agh.cqm.data.dto.ThroughputSampleDTO;

import java.util.List;
import java.util.Map;

public interface DeviationsService {

    Map<String, CdnDeviations> getRTTDeviations(
            Map<String, List<RTTSampleDTO>> rttSamplesMap);

    Map<String, CdnDeviations> getThroughputDeviations(
            Map<String, List<ThroughputSampleDTO>> throughputSamplesMap);

    Map<String, CdnDeviations> getAllDeviations(
            Map<String, List<RTTSampleDTO>> rttSamplesMap,
            Map<String, List<ThroughputSampleDTO>> throughputSamplesMap);
}
