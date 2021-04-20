package pl.edu.agh.cqm.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.edu.agh.cqm.data.model.ConfigCdn;
import pl.edu.agh.cqm.data.model.ConfigSample;
import pl.edu.agh.cqm.data.repository.ConfigCdnRepository;
import pl.edu.agh.cqm.data.repository.ConfigSampleRepository;

import javax.annotation.PostConstruct;
import java.time.Instant;
import java.util.List;

@Service
public class UpdateParametersServiceImpl implements UpdateParametersService {

    private final ConfigCdnRepository configCdnRepository;
    private final ConfigSampleRepository configSampleRepository;
    private final Logger logger = LogManager.getLogger(UpdateParametersService.class);

    @Value("${cqm.cdns}")
    private List<String> cdns;

    @Value("${cqm.active.sampling_rate}")
    private int activeSamplingRate;

    @Value("${cqm.active.tests_intensity}")
    private int activeTestsIntensity;

    @Value("${cqm.passive.sampling_rate}")
    private int passiveSamplingRate;

    public UpdateParametersServiceImpl(ConfigCdnRepository configCdnRepository,
                                       ConfigSampleRepository configSampleRepository) {
        this.configCdnRepository = configCdnRepository;
        this.configSampleRepository = configSampleRepository;
    }

    @Override
    public void updateCdns(List<String> cdns) {
        if (cdns != null) {
            configCdnRepository.deleteAll();
            for (String cdn : cdns) {
                configCdnRepository.save(ConfigCdn.builder().cdn(cdn).build());
            }
            logger.info("Updated the cdns: " + cdns);
        }
        else {
            logger.info("Cdns were not updated");
        }
    }

    @Override
    public void updateSampleParameters(int activeSamplingRate, int activeTestIntensity, int passiveSamplingRate) {

        ConfigSample prevParameters = configSampleRepository.findFirstByOrderByIdDesc();
        ConfigSample configSample = new ConfigSample();

        if (activeSamplingRate > 0) {
            configSample.setActiveSamplingRate(activeSamplingRate);
        } else {
            configSample.setActiveSamplingRate(prevParameters.getActiveSamplingRate());
        }

        if (activeTestIntensity > 0) {
            configSample.setActiveTestIntensity(activeTestIntensity);
        } else {
            configSample.setActiveTestIntensity(prevParameters.getActiveTestIntensity());
        }

        if (passiveSamplingRate > 0) {
            configSample.setPassiveSamplingRate(passiveSamplingRate);
        } else {
            configSample.setPassiveSamplingRate(prevParameters.getPassiveSamplingRate());
        }

        if (!prevParameters.equals(configSample)) {
            configSample.setTimestamp(Instant.now());
            configSampleRepository.save(configSample);

            logger.info("Updated the sample parameters: " + configSample);
        } else {
            logger.info("None of the sample parameters were updated.");
        }
    }

    @PostConstruct
    public void initConfigCdnsRepository() {
        if (configCdnRepository.count() == 0) {
            for (String cdn : cdns) {
                configCdnRepository.save(ConfigCdn.builder().cdn(cdn).build());
            }
        }
    }

    @PostConstruct
    public void initConfigSampleDirectory() {
        if (configSampleRepository.count() == 0) {
            configSampleRepository.save(ConfigSample.builder()
                    .timestamp(Instant.now())
                    .activeSamplingRate(activeSamplingRate)
                    .activeTestIntensity(activeTestsIntensity)
                    .passiveSamplingRate(passiveSamplingRate)
                    .build());
        }
    }

}