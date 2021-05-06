package pl.edu.agh.cqm.service;

import pl.edu.agh.cqm.data.dto.CdnWithUrlsDTO;
import pl.edu.agh.cqm.data.dto.ConfigSampleDTO;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

public interface ParameterService {

    void updateCdnNames(Collection<String> cdns);
    void updateUrls(String cdn, Collection<String> urls);
    void updateCdns(List<CdnWithUrlsDTO> cdns);

    void updateSampleParameters(int activeSamplingRate, int activeTestIntensity, int passiveSamplingRate);

    List<String> getCdnNames();
    List<String> getUrls(String cdn);
    List<CdnWithUrlsDTO> getCdns();

    int getActiveSamplingRate();
    int getActiveTestIntensity();
    int getPassiveSamplingRate();

    List<ConfigSampleDTO> getParameterHistory(Instant startDate, Instant endDate);
}
