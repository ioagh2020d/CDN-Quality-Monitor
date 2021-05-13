package pl.edu.agh.cqm.service.impl;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.edu.agh.cqm.data.dto.CdnWithUrlsDTO;
import pl.edu.agh.cqm.data.dto.ConfigSampleDTO;
import pl.edu.agh.cqm.data.model.Cdn;
import pl.edu.agh.cqm.data.model.ConfigSample;
import pl.edu.agh.cqm.data.model.Url;
import pl.edu.agh.cqm.data.repository.CdnRepository;
import pl.edu.agh.cqm.data.repository.ConfigSampleRepository;
import pl.edu.agh.cqm.data.repository.UrlRepository;
import pl.edu.agh.cqm.service.ParameterService;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ParameterServiceImpl implements ParameterService {

    private final CdnRepository cdnRepository;
    private final UrlRepository urlRepository;
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
    public void updateCdnsWithUrls(List<CdnWithUrlsDTO> cdnsWithUrls) {
        // set all cdns to unactive
        for (Cdn cdn : cdnRepository.findAll()) {
            cdn.setActive(false);
            cdnRepository.save(cdn);
        }

        // set all urls to unactive
        for (Url url : urlRepository.findAll()) {
            url.setActive(false);
            urlRepository.save(url);
        }

        // set to active or add new cdns and urls
        for (CdnWithUrlsDTO cdnWithUrls : cdnsWithUrls) {
            if (cdnWithUrls.getName().isBlank()) continue;
            // cdns
            Cdn cdn = cdnRepository.findByNameEquals(cdnWithUrls.getName())
                    .orElse(new Cdn(cdnWithUrls.getName()));
            cdn.setActive(true);
            cdnRepository.save(cdn);

            // urls
            for (String address : cdnWithUrls.getUrls()) {
                if (address.isBlank()) continue;
                Url url = urlRepository.findByCdnAndAddressEquals(cdn, address)
                        .orElse(new Url(cdn, address));
                url.setActive(true);
                urlRepository.save(url);
            }
        }
        logger.info("Updated the cdns and urls: " + cdnsWithUrls);
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
    public List<Url> getActiveUrls() {
        return urlRepository.findByActiveTrue();
    }

    @Override
    public List<Url> getActiveUrls(String cndName) {
        return urlRepository.findByCdnAndActiveTrue(getCdn(cndName));
    }

    @Override
    public List<CdnWithUrlsDTO> getActiveCdnsWithUrls() {
        return cdnRepository.findByActiveTrue().stream()
                .map(cdn -> new CdnWithUrlsDTO(cdn.getName(), getActiveUrlAddresses(cdn)))
                .collect(Collectors.toList());
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

    private Cdn getCdn(String cdnName) {
        return cdnRepository.findByNameEquals(cdnName)
                .orElseThrow(IllegalArgumentException::new);
    }

    private List<String> getActiveUrlAddresses(Cdn cdn) {
        return urlRepository.findByCdnAndActiveTrue(cdn).stream()
                .map(Url::getAddress)
                .collect(Collectors.toList());
    }

    @PostConstruct
    private void initConfigCdnsRepository() {
        if (cdnRepository.count() == 0) {
            for (String cdn : cdns) {
                cdnRepository.save(new Cdn(cdn));
            }
        }
    }

    @PostConstruct
    private void initConfigSampleRepository() {
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
