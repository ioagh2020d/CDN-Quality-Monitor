package pl.edu.agh.cqm.service;

import java.util.List;

public interface ParameterService {

    void updateCdns(List<String> cdns);
    void updateSampleParameters(int activeSamplingRate, int activeTestIntensity, int passiveSamplingRate);

    List<String> getCdns();
    int getActiveSamplingRate();
    int getActiveTestIntensity();
    int getPassiveSamplingRate();
}
