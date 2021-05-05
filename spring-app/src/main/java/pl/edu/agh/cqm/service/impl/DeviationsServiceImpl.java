package pl.edu.agh.cqm.service.impl;

import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import pl.edu.agh.cqm.data.dto.CdnDeviations;
import pl.edu.agh.cqm.data.dto.DeviationDTO;
import pl.edu.agh.cqm.data.dto.RTTSampleDTO;
import pl.edu.agh.cqm.data.dto.ThroughputSampleDTO;
import pl.edu.agh.cqm.service.DeviationsService;

import java.time.Instant;
import java.util.*;

@Service
public class DeviationsServiceImpl implements DeviationsService {

    @Override
    public Map<String, CdnDeviations> getRTTDeviations(
            Map<String, List<RTTSampleDTO>> rttSamplesMap) {
        Map<String, CdnDeviations> deviations = new HashMap<>();

        for (Map.Entry<String, List<RTTSampleDTO>> entry : rttSamplesMap.entrySet()) {
            String cdn = entry.getKey();
            List<RTTSampleDTO> samples = entry.getValue();

            Map<String, List<Pair<Instant, Number>>> values = getValuesFromRTTSamples(samples);
            CdnDeviations cdnDeviations = new CdnDeviations(Map.of(
                    "rtt", detectDeviations(values.get("rtt")),
                    "packetLoss", detectDeviations(values.get("packetLoss"))
            ));
            deviations.put(cdn, cdnDeviations);
        }
        return deviations;
    }

    @Override
    public Map<String, CdnDeviations> getThroughputDeviations(
            Map<String, List<ThroughputSampleDTO>> throughputSamplesMap) {
        Map<String, CdnDeviations> deviations = new HashMap<>();

        for (Map.Entry<String, List<ThroughputSampleDTO>> entry : throughputSamplesMap.entrySet()) {
            String cdn = entry.getKey();
            List<ThroughputSampleDTO> samples = entry.getValue();

            Map<String, List<Pair<Instant, Number>>> values = getValuesFromThroughputSamples(samples);
            CdnDeviations cdnDeviations = new CdnDeviations(Map.of(
                    "throughput", detectDeviations(values.get("throughput"))
            ));
            deviations.put(cdn, cdnDeviations);
        }
        return deviations;
    }

    @Override
    public Map<String, CdnDeviations> getAllDeviations(
            Map<String, List<RTTSampleDTO>> rttSamplesMap,
            Map<String, List<ThroughputSampleDTO>> throughputSamplesMap) {
        Map<String, CdnDeviations> rttDeviations = getRTTDeviations(rttSamplesMap);
        Map<String, CdnDeviations> throughputDeviations = getThroughputDeviations(throughputSamplesMap);

        Map<String, CdnDeviations> allDeviations = new HashMap<>();
        for (String cdn : rttDeviations.keySet()) {
            allDeviations.put(cdn, new CdnDeviations(Map.of(
                    "rtt", rttDeviations.get(cdn).get("rtt"),
                    "packetLoss", rttDeviations.get(cdn).get("packetLoss"),
                    "throughput", throughputDeviations.get(cdn).get("throughput")
            )));
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

    private List<DeviationDTO> detectDeviations(List<Pair<Instant, Number>> values) {
        if (values.isEmpty()) {
            return List.of();
        } else {
            List<DeviationDTO> deviations = new ArrayList<>(rappidChanges(values, 0.2));
            deviations.addAll(slowChanges(values, 5));
            return deviations;
        }
    }

    private List<DeviationDTO> slowChanges(List<Pair<Instant, Number>> values, double slowChangeMinLength) {
        if (values.size() > slowChangeMinLength) {
            List<DeviationDTO> slowChanges = new ArrayList<>();
            for (int i = 1; i < values.size(); i++) {
                if (values.get(i - 1).getSecond().doubleValue() > values.get(i).getSecond().doubleValue()) {
                    int is = i - 1;
                    while (i < values.size() && values.get(i - 1).getSecond().doubleValue() > values.get(i).getSecond().doubleValue()) {
                        i++;
                    }
                    i--;
                    int ie = i;
                    if (~ i < values.size()){
                        ie--;
                    }
                    if (ie - is >= slowChangeMinLength) {
                        slowChanges.add(new DeviationDTO(values.get(is).getFirst(), values.get(ie).getFirst(), "Decrease"));
                    }
                } else if (values.get(i - 1).getSecond().doubleValue() < values.get(i).getSecond().doubleValue()) {
                    int is = i - 1;
                    while (i < values.size() && values.get(i - 1).getSecond().doubleValue() < values.get(i).getSecond().doubleValue()) {
                        i++;
                    }
                    i--;
                    int ie = i;
                    if (~ i < values.size()){
                        ie--;
                    }
                    if (ie - is >= slowChangeMinLength) {
                        slowChanges.add(new DeviationDTO(values.get(is).getFirst(), values.get(ie).getFirst(), "Increase"));
                    }
                }
            }
            return slowChanges;
        }
        return List.of();
    }

    private List<DeviationDTO> rappidChanges(List<Pair<Instant, Number>> values, double rappidChangePercentage) {
        if (values.size() > 1) {
            List<DeviationDTO> rappidChanges = new ArrayList<>();
            for (int i = 1; i < values.size(); i++) {
                if (values.get(i - 1).getSecond().doubleValue() >
                        (1 + rappidChangePercentage) * values.get(i).getSecond().doubleValue() ||
                        values.get(i - 1).getSecond().doubleValue() <
                                (1 - rappidChangePercentage) * values.get(i).getSecond().doubleValue()) {
                    int is = i - 1;
                    while (i < values.size() && (values.get(i - 1).getSecond().doubleValue() >
                            (1 + rappidChangePercentage) * values.get(i).getSecond().doubleValue() ||
                            values.get(i - 1).getSecond().doubleValue() <
                                    (1 - rappidChangePercentage) * values.get(i).getSecond().doubleValue())) {
                        i++;
                    }
                    i--;
                    int ie = i;
                    if (~ i < values.size()){
                        ie--;
                    }
                    rappidChanges.add(new DeviationDTO(values.get(is).getFirst(), values.get(ie).getFirst(), "Rappid Changes"));
                }
            }
            return rappidChanges;
        }
        return List.of();
    }
}
