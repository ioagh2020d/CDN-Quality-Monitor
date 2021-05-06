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
import java.util.Collection;
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
    public void updateCdnNames(Collection<String> cdnNames) {
        for (Cdn cdn : cdnRepository.findAll()) {
            cdn.setInUse(false);
            cdnRepository.save(cdn);
        }

        for (String cdnName : cdnNames) {
            Cdn cdn = cdnRepository.findByAddressEquals(cdnName);
            if (cdn != null) {
                cdn.setInUse(true);
                cdnRepository.save(cdn);
            } else {
                cdnRepository.save(new Cdn(cdnName));
            }
        }
    }

    @Override
    @Transactional
    public void updateUrls(String cdnName, Collection<String> urls) {
        long cdnId = getCdnId(cdnName);

        for (Url url : urlRepository.findByCdnIdEquals(cdnId)) {
            url.setInUse(false);
            urlRepository.save(url);
        }

        for (String address : urls) {
            Url url = urlRepository.findByCdnIdEqualsAndAddressEquals(cdnId, address);
            if (url != null) {
                url.setInUse(true);
                urlRepository.save(url);
            } else {
                urlRepository.save(new Url(cdnId, address));
            }
        }
    }

    @Override
    @Transactional
    public void updateCdns(List<CdnWithUrlsDTO> cdns) {
        updateCdnNames(cdns.stream().map(CdnWithUrlsDTO::getName).collect(Collectors.toList()));
        cdns.stream().forEach(x -> updateUrls(x.getName(), x.getUrls()));
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
    public List<String> getCdnNames() {
        return cdnRepository.findByInUseTrue().stream()
                .map(Cdn::getAddress)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getUrls(String cdn) {
        long cdnId = getCdnId(cdn);
        return urlRepository.findByCdnIdEqualsAndInUseTrue(cdnId).stream()
                .map(Url::getAddress)
                .collect(Collectors.toList());
    }

    @Override
    public List<CdnWithUrlsDTO> getCdns() {
        return getCdnNames().stream().map(cdn -> new CdnWithUrlsDTO(cdn, getUrls(cdn))).collect(Collectors.toList());
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

    private long getCdnId(String cdnName) {
        return cdnRepository.findByAddressEquals(cdnName).getId();
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
