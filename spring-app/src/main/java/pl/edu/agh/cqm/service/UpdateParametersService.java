package pl.edu.agh.cqm.service;

import java.util.List;

public interface UpdateParametersService {

    void updateParameters(List<String> cdns,
                          int activeSamplingRate,
                          int activeTestIntensity,
                          int passiveSamplingRate);

}
