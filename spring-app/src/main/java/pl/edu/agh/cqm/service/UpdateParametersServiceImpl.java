package pl.edu.agh.cqm.service;

import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import pl.edu.agh.cqm.configuration.CqmConfiguration;

import java.util.List;

@Service
@AllArgsConstructor
public class UpdateParametersServiceImpl implements UpdateParametersService {

    private final CqmConfiguration cqmConfiguration;
    private final Logger logger = LogManager.getLogger(UpdateParametersService.class);

    @Override
    public void updateParameters(List<String> cdns,
                                 int activeSamplingRate,
                                 int activeTestIntensity,
                                 int passiveSamplingRate) {
        boolean updated = false;
        if (cdns != null && !cdns.equals(cqmConfiguration.getCdns())) {
            updated = true;
            cqmConfiguration.setCdns(cdns);
        }

        if (activeSamplingRate > 0 && activeSamplingRate != cqmConfiguration.getActiveSamplingRate()) {
            updated = true;
            cqmConfiguration.setActiveSamplingRate(activeSamplingRate);
        }
        if (activeTestIntensity > 0 && activeTestIntensity != cqmConfiguration.getActiveTestsIntensity()) {
            updated = true;
            cqmConfiguration.setActiveTestsIntensity(activeTestIntensity);
        }

        if (passiveSamplingRate > 0 && passiveSamplingRate != cqmConfiguration.getPassiveSamplingRate()) {
            updated = true;
            cqmConfiguration.setPassiveSamplingRate(passiveSamplingRate);
        }

        if (updated) {
            logger.info("Updated the parameters: " + cqmConfiguration);
        } else {
            logger.info("None of the parameters was updated");
        }
    }

}