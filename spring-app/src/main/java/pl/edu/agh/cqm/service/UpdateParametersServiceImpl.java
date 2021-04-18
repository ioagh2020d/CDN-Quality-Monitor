package pl.edu.agh.cqm.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.edu.agh.cqm.configuration.CqmConfiguration;

import java.util.List;

@Service
@AllArgsConstructor
public class UpdateParametersServiceImpl implements UpdateParametersService {

    private final CqmConfiguration cqmConfiguration;

    @Override
    public void updateParameters(List<String> cdns,
                                 int activeSamplingRate,
                                 int activeTestIntensity,
                                 int passiveSamplingRate) {
        if (cdns != null) {
            cqmConfiguration.setCdns(cdns);
        }

        if (activeSamplingRate > 0) {
            cqmConfiguration.setActiveSamplingRate(activeSamplingRate);
        }
        if (activeTestIntensity > 0) {
            cqmConfiguration.setActiveTestsIntensity(activeTestIntensity);
        }

        if (passiveSamplingRate > 0) {
            cqmConfiguration.setPassiveSamplingRate(passiveSamplingRate);
        }
    }

}
