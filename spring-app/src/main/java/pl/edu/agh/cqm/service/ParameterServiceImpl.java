package pl.edu.agh.cqm.service;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.edu.agh.cqm.data.dto.ConfigSampleDTO;
import pl.edu.agh.cqm.data.model.ConfigCdn;
import pl.edu.agh.cqm.data.model.ConfigSample;
import pl.edu.agh.cqm.data.repository.ConfigCdnRepository;
import pl.edu.agh.cqm.data.repository.ConfigSampleRepository;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ParameterServiceImpl implements ParameterService {

    private final ConfigCdnRepository configCdnRepository;
    private final ConfigSampleRepository configSampleRepository;
    private final Logger logger = LogManager.getLogger(ParameterService.class);

    @Value("${cqm.cdns}")
    private List<String> cdns;

    @Value("${cqm.active.sampling_rate}")
    private int activeSamplingRate;

    @Value("${cqm.active.tests_intensity}")
    private int activeTestsIntensity;

    @Value("${cqm.passive.sampling_rate}")
    private int passiveSamplingRate;

    @Override
    @Transactional
    public void updateCdns(List<String> cdns) {
        configCdnRepository.deleteAll();
        for (String cdn : cdns) {
            configCdnRepository.save(new ConfigCdn(0, cdn));
        }
        logger.info("Updated the cdns: " + cdns);
    }

    @Override
    public void updateSampleParameters(int activeSamplingRate, int activeTestIntensity, int passiveSamplingRate) {

        if (activeSamplingRate == getActiveSamplingRate()
                && activeTestIntensity == getActiveTestIntensity()
                && passiveSamplingRate == getPassiveSamplingRate()) {
            logger.info("None of the sample parameters were updated");
        } else {
            ConfigSample configSample = ConfigSample.builder()
                    .timestamp(Instant.now())
                    .activeSamplingRate(activeSamplingRate)
                    .activeTestIntensity(activeTestIntensity)
                    .passiveSamplingRate(passiveSamplingRate)
                    .build();

            configSampleRepository.save(configSample);
            logger.info("Updated the sample parameters: " + configSample);
        }
    }

    @Override
    public List<String> getCdns() {
        return configCdnRepository.findAll().stream().map(x -> x.getCdn()).collect(Collectors.toList());
    }

    @Override
    public int getActiveSamplingRate() {
        return configSampleRepository.findFirstByOrderByTimestampDesc().getActiveSamplingRate();
    }

    @Override
    public int getActiveTestIntensity() {
        return configSampleRepository.findFirstByOrderByTimestampDesc().getActiveTestIntensity();
    }

    @Override
    public int getPassiveSamplingRate() {
        return configSampleRepository.findFirstByOrderByTimestampDesc().getPassiveSamplingRate();
    }

    @Override
    public List<ConfigSampleDTO> getParameterHistory(Instant startDate, Instant endDate) {
        ConfigSample firstConfig = configSampleRepository.findFirstByTimestampLessThanEqualOrderByTimestampDesc(startDate);
        List<ConfigSample> configs = configSampleRepository.findAllByTimestampBetweenOrderByTimestamp(startDate, endDate);
        if (firstConfig != null) {
            configs.add(0, firstConfig);
        }

        return configs.stream().map(ConfigSample::toDTO).collect(Collectors.toList());
    }

    @PostConstruct
    private void initConfigCdnsRepository() {
        if (configCdnRepository.count() == 0) {
            for (String cdn : cdns) {
                configCdnRepository.save(new ConfigCdn(0, cdn));
            }
        }
    }

    @PostConstruct
    private void initConfigSampleDirectory() {
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