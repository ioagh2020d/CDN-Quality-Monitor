package pl.edu.agh.cqm.service;

import pl.edu.agh.cqm.data.dto.CdnWithUrlsDTO;
import pl.edu.agh.cqm.data.dto.ConfigSampleDTO;
import pl.edu.agh.cqm.data.model.Cdn;
import pl.edu.agh.cqm.data.model.Url;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface ParameterService {

    void updateCdnsWithUrls(List<CdnWithUrlsDTO> cdns);

    void updateSampleParameters(int activeSamplingRate, int activeTestIntensity, int passiveSamplingRate);

    List<Url> getActiveUrls();
    List<Url> getActiveUrls(String cdnName);

    List<Cdn> getActiveCdns();
    List<CdnWithUrlsDTO> getActiveCdnsWithUrls();

    int getActiveSamplingRate();
    int getActiveTestIntensity();
    int getPassiveSamplingRate();

    List<ConfigSampleDTO> getParameterHistory(Instant startDate, Instant endDate);

    void addNewUrl(String cdn, String url);

    Optional<Url> getURL(String cdnName, String urlName);

    Cdn getOrCreateCdn(String name);

    Url getOrCreateUrl(Cdn cdn, String url);

    Url getOrCreateUrl(String cdnName, String url);
}
