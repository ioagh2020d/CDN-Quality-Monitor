package pl.edu.agh.cqm.service;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import pl.edu.agh.cqm.data.dto.DeviationDTO;
import pl.edu.agh.cqm.data.dto.RTTSampleDTO;
import pl.edu.agh.cqm.data.dto.ThroughputSampleDTO;

import java.time.Instant;
import java.util.*;

@Service
public class DeviationsServiceImpl implements DeviationsService {

    @Override
    public Map<String, Map<String, List<DeviationDTO>>> getRTTDeviations(
            Map<String, List<RTTSampleDTO>> rttSamplesMap) {

        Map<String, Map<String, List<DeviationDTO>>> deviations = new HashMap<>();
        for (Map.Entry<String, List<RTTSampleDTO>> entry : rttSamplesMap.entrySet()) {
            String cdn = entry.getKey();
            Map<String, List<Pair<Instant, Number>>> values = getValuesFromRTTSamples(entry.getValue());

            Map<String, List<DeviationDTO>> cdnDeviations = Map.of(
                    "rtt", detectDeviations(values.get("rtt")),
                    "packetLoss", detectDeviations(values.get("packetLoss"))
            );
            deviations.put(cdn, cdnDeviations);
        }
        return deviations;
    }

    // TODO
    @Override
    public Map<String, Map<String, List<DeviationDTO>>> getThroughputDeviations(
            Map<String, List<ThroughputSampleDTO>> throughputSamplesMap) {

        return null;
    }

    // TODO
    @Override
    public Map<String, Map<String, List<DeviationDTO>>> getAllDeviations(
            Map<String, List<RTTSampleDTO>> rttSamplesMap,
            Map<String, List<ThroughputSampleDTO>> throughputSamplesMap) {

        return null;
    }

    private Map<String, List<Pair<Instant, Number>>> getValuesFromRTTSamples(List<RTTSampleDTO> samples) {
        List<Pair<Instant, Number>> rttValues = new ArrayList<>(samples.size());
        List<Pair<Instant, Number>> packetLossValues = new ArrayList<>(samples.size());
        for (RTTSampleDTO sample : samples) {
            rttValues.add(Pair.of(sample.getTimestamp(), sample.getAverage()));
            packetLossValues.add(Pair.of(sample.getTimestamp(), sample.getPacketLoss()));
        }
        return Map.of(
                "rtt", rttValues,
                "packetLoss", packetLossValues
        );
    }

    // TODO
    private List<Pair<Instant, Number>> getValuesFromThroughputSamples(List<ThroughputSampleDTO> samples) {
        return null;
    }

    // TODO - CQM-51
    private List<DeviationDTO> detectDeviations(List<Pair<Instant, Number>> values) {
        return List.of(new DeviationDTO(values.get(0).getFirst(), values.get(0).getFirst().plusSeconds(1), "test"));
    }
}
