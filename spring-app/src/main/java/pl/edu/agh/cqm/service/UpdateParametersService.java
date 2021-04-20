package pl.edu.agh.cqm.service;

import java.util.List;

public interface UpdateParametersService {

    void updateCdns(List<String> cdns);

    void updateSampleParameters(int activeSamplingRate, int activeTestIntensity, int passiveSamplingRate);

}
