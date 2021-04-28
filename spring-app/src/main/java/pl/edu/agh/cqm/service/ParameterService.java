package pl.edu.agh.cqm.service;

import pl.edu.agh.cqm.data.dto.ConfigSampleDTO;

import java.time.Instant;
import java.util.List;

public interface ParameterService {

    void updateCdns(List<String> cdns);
    void updateSampleParameters(int activeSamplingRate, int activeTestIntensity, int passiveSamplingRate);

    List<String> getCdns();
    int getActiveSamplingRate();
    int getActiveTestIntensity();
    int getPassiveSamplingRate();

    List<ConfigSampleDTO> getParameterHistory(Instant startDate, Instant endDate);
}
