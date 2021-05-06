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
    private static int slowChangeMinLength = 5;
    private static double rapidChangePercentage = 0.2;

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
            List<DeviationDTO> deviations = new ArrayList<>(rapidChanges(values));
            deviations.addAll(slowChanges(values));
            return deviations;
        }
    }

    private double getSampleValue(List<Pair<Instant, Number>> values, int index){
        return values.get(index).getSecond().doubleValue();
    }

    private Instant getSampleTime(List<Pair<Instant, Number>> values, int index){
        return values.get(index).getFirst();
    }

    private boolean smallerThanPrev(List<Pair<Instant, Number>> values, int index){
        return getSampleValue(values, index) < getSampleValue(values, index-1);
    }

    private boolean biggerThanPrev(List<Pair<Instant, Number>> values, int index){
        return getSampleValue(values, index) > getSampleValue(values, index-1);
    }

    private boolean smallerThanPrevScaled(List<Pair<Instant, Number>> values, int index, double scale){
        return getSampleValue(values, index) < scale*getSampleValue(values, index-1);
    }

    private boolean biggerThanPrevScaled(List<Pair<Instant, Number>> values, int index, double scale){
        return getSampleValue(values, index) > scale*getSampleValue(values, index-1);
    }

    private boolean rapidChangeDetected(List<Pair<Instant, Number>> values, int index){
        return smallerThanPrevScaled(values, index, (1 - rapidChangePercentage)) || biggerThanPrevScaled(values, index, (1 + rapidChangePercentage));
    }

    private DeviationDTO getDeviationDTO(List<Pair<Instant, Number>> values, int startIndex, int endIndex, String description){
        return new DeviationDTO(getSampleTime(values, startIndex), getSampleTime(values,endIndex), description);
    }

    private List<DeviationDTO> slowChanges(List<Pair<Instant, Number>> values) {
        if (values.size() > slowChangeMinLength) {
            List<DeviationDTO> slowChanges = new ArrayList<>();
            for (int i = 1; i < values.size(); i++) {
                if (smallerThanPrev(values, i)) {
                    int startIndex = i - 1;
                    do {
                        i++;
                    } while (i < values.size() && smallerThanPrev(values, i));
                    i--;
                    if (i - startIndex >= slowChangeMinLength) {
                        slowChanges.add(getDeviationDTO(values, startIndex, i, "Decrease"));
                    }
                } else if (biggerThanPrev(values, i)) {
                    int startIndex = i - 1;
                    do {
                        i++;
                    } while (i < values.size() && biggerThanPrev(values, i));
                    i--;
                    if (i - startIndex >= slowChangeMinLength) {
                        slowChanges.add(getDeviationDTO(values, startIndex, i, "Increase"));
                    }
                }
            }
            return slowChanges;
        }
        return List.of();
    }

    private List<DeviationDTO> rapidChanges(List<Pair<Instant, Number>> values) {
        if (values.size() > 1) {
            List<DeviationDTO> rapidChanges = new ArrayList<>();
            for (int i = 1; i < values.size(); i++) {
                if (rapidChangeDetected(values, i)) {
                    int startIndex = i - 1;
                    do {
                        i++;
                    } while (i < values.size() && rapidChangeDetected(values, i));
                    i--;
                    rapidChanges.add(getDeviationDTO(values, startIndex, i, "Rappid Changes"));
                }
            }
            return rapidChanges;
        }
        return List.of();
    }
}
