package pl.edu.agh.cqm.service;

import pl.edu.agh.cqm.data.model.ConfigCdn;

import java.util.List;

public interface ParameterService {

    void updateCdns(List<String> cdns);
    void updateSampleParameters(int activeSamplingRate, int activeTestIntensity, int passiveSamplingRate);

    List<ConfigCdn> getCdns();
    int getActiveSamplingRate();
    int getActiveTestIntensity();
    int getPassiveSamplingRate();

}
