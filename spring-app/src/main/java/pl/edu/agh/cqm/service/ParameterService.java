package pl.edu.agh.cqm.service;

import pl.edu.agh.cqm.data.dto.ConfigSampleDTO;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface ParameterService {

    void updateCdns(Collection<String> cdns);
    void updateUrls(String cdn, Collection<String> urls);
    void updateCdnsWithUrls(Map<String, List<String>> cdns);

    void updateSampleParameters(int activeSamplingRate, int activeTestIntensity, int passiveSamplingRate);

    List<String> getCdns();
    List<String> getUrls(String cdnAddress);
    Map<String, List<String>> getCdnsWithUrls();

    int getActiveSamplingRate();
    int getActiveTestIntensity();
    int getPassiveSamplingRate();

    List<ConfigSampleDTO> getParameterHistory(Instant startDate, Instant endDate);
}
