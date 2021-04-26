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
            List<RTTSampleDTO> samples = entry.getValue();

            Map<String, List<Pair<Instant, Number>>> values = getValuesFromRTTSamples(samples);
            Map<String, List<DeviationDTO>> cdnDeviations = Map.of(
                    "rtt", detectDeviations(values.get("rtt")),
                    "packetLoss", detectDeviations(values.get("packetLoss"))
            );
            deviations.put(cdn, cdnDeviations);
        }
        return deviations;
    }

    @Override
    public Map<String, Map<String, List<DeviationDTO>>> getThroughputDeviations(
            Map<String, List<ThroughputSampleDTO>> throughputSamplesMap) {
        Map<String, Map<String, List<DeviationDTO>>> deviations = new HashMap<>();

        for (Map.Entry<String, List<ThroughputSampleDTO>> entry : throughputSamplesMap.entrySet()) {
            String cdn = entry.getKey();
            List<ThroughputSampleDTO> samples = entry.getValue();

            Map<String, List<Pair<Instant, Number>>> values = getValuesFromThroughputSamples(samples);
            Map<String, List<DeviationDTO>> cdnDeviations = Map.of(
                    "throughput", detectDeviations(values.get("throughput"))
            );
            deviations.put(cdn, cdnDeviations);
        }
        return deviations;
    }

    @Override
    public Map<String, Map<String, List<DeviationDTO>>> getAllDeviations(
            Map<String, List<RTTSampleDTO>> rttSamplesMap,
            Map<String, List<ThroughputSampleDTO>> throughputSamplesMap) {
        Map<String, Map<String, List<DeviationDTO>>> rttDeviations = getRTTDeviations(rttSamplesMap);
        Map<String, Map<String, List<DeviationDTO>>> throughputDeviations = getThroughputDeviations(throughputSamplesMap);

        Map<String, Map<String, List<DeviationDTO>>> allDeviations = new HashMap<>();
        for (String cdn : rttDeviations.keySet()) {
            allDeviations.put(cdn, Map.of(
                    "rtt", rttDeviations.get(cdn).get("rtt"),
                    "packetLoss", rttDeviations.get(cdn).get("packetLoss"),
                    "throughput", throughputDeviations.get(cdn).get("throughput")
            ));
        }
        return allDeviations;
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

    private Map<String, List<Pair<Instant, Number>>> getValuesFromThroughputSamples(List<ThroughputSampleDTO> samples) {
        List<Pair<Instant, Number>> throughputValues = new ArrayList<>(samples.size());

        for (ThroughputSampleDTO sample : samples) {
            throughputValues.add(Pair.of(sample.getTimestamp(), sample.getThroughput()));
        }

        return Map.of("throughput", throughputValues);
    }

    // TODO - CQM-51
    private List<DeviationDTO> detectDeviations(List<Pair<Instant, Number>> values) {
        if (values.isEmpty()) {
            return List.of();
        } else {
            return List.of(new DeviationDTO(values.get(0).getFirst(), values.get(0).getFirst().plusSeconds(1), "test"));
        }
    }
}
