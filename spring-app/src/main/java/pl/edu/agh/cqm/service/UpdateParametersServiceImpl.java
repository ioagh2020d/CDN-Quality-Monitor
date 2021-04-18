package pl.edu.agh.cqm.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.edu.agh.cqm.configuration.CqmConfiguration;

@Service
@AllArgsConstructor
public class UpdateParametersServiceImpl implements UpdateParametersService {

    private final CqmConfiguration cqmConfiguration;

    @Override
    public void updateParameters(int activeSamplingRate, int activeTestIntensity, int passiveSamplingRate) {
        cqmConfiguration.setActiveSamplingRate(activeSamplingRate);
        cqmConfiguration.setActiveTestsIntensity(activeTestIntensity);
        cqmConfiguration.setPassiveSamplingRate(passiveSamplingRate);
    }

}
