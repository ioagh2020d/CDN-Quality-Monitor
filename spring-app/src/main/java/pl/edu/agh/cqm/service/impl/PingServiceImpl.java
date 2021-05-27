package pl.edu.agh.cqm.service.impl;

import lombok.AllArgsConstructor;
import lombok.Builder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import pl.edu.agh.cqm.configuration.CqmConfiguration;
import pl.edu.agh.cqm.data.model.RTTSample;
import pl.edu.agh.cqm.data.model.Url;
import pl.edu.agh.cqm.data.repository.RTTSampleRepository;
import pl.edu.agh.cqm.service.CentralApiService;
import pl.edu.agh.cqm.service.MonitorService;
import pl.edu.agh.cqm.service.ParameterService;
import pl.edu.agh.cqm.service.PingService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.Instant;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static pl.edu.agh.cqm.configuration.CqmConfiguration.ActiveTestType.ICMP;
import static pl.edu.agh.cqm.configuration.CqmConfiguration.ActiveTestType.TCP;

@Service
@AllArgsConstructor
@Builder
public class PingServiceImpl implements PingService {

    private final RTTSampleRepository rttSampleRepository;
    private final CqmConfiguration cqmConfiguration;
    private final ParameterService parameterService;
    private final MonitorService monitorService;
    private final CentralApiService centralApiService;
    private final Logger logger = LogManager.getLogger(PingServiceImpl.class);

    @Override
    public void doMeasurement() {
        for (Url url : parameterService.getActiveUrls()) {
            try {
                CqmConfiguration.ActiveTestType type = cqmConfiguration.getActiveTestsType();
                RTTSample sample;
                switch (type) {
                    case ICMP -> sample = pingICMP(url);
                    case TCP -> sample = pingTCP(url);
                    default -> throw new IllegalStateException("Unexpected value: " + type);
                }
                rttSampleRepository.save(sample);
                centralApiService.sendSample(sample);
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }
    }

    private RTTSample pingTCP(Url url) throws IOException {
        DecimalFormat format = (DecimalFormat) DecimalFormat.getInstance();
        DecimalFormatSymbols symbols = format.getDecimalFormatSymbols();
        char sep = symbols.getDecimalSeparator();

        String command = String.join(" ",
                "nping --tcp --delay ", "0" + sep + "2", "-c", parameterService.getActiveTestIntensity() + "", url.getAddress());
        logger.info("Starting active sampling with command \"" + command + "\"");
        BufferedReader inputStream = runSystemCommand(command);
        List<String> lines = inputStream.lines().collect(Collectors.toList());

        double[] vals = new double[lines.size() - 3];
        double[] stds = new double[(lines.size() - 3) / 2];
        for (int i = 2; i < lines.size() - 3; i++) {
            String line = lines.get(i);
            double val = getValFromString(line.replaceAll("s", "qazwsx"), "((\\d+)(\\.)(\\d+))qazwsx");
            if (val != -1) {
                vals[i - 2] = val;
            }
        }
        for (int i = 0; i < (vals.length / 2); i++) {
            stds[i] = vals[2 * i + 1] - vals[2 * i];
        }
        return RTTSample.builder()
                .id(0)
                .packetLoss(getValFromString(lines.get(lines.size() - 2), "(\\d+)(%)"))
                .min(getValFromString(lines.get(lines.size() - 3).replaceAll("ms", " "), "Min rtt: ((\\d+)(\\.)(\\d+)) "))
                .average(getValFromString(lines.get(lines.size() - 3).replaceAll("ms", " "), "Avg rtt: ((\\d+)(\\.)(\\d+)) "))
                .max(getValFromString(lines.get(lines.size() - 3).replaceAll("ms", " "), "Max rtt: ((\\d+)(\\.)(\\d+)) "))
                .standardDeviation((float) getStandardDeviation(stds))
                .timestamp(Instant.now())
                .type(TCP)
                .url(url)
                .monitor(monitorService.getLocalMonitor())
                .build();
    }

    private double getStandardDeviation(double[] stds) {
        int n = stds.length;
        double sum = 0;
        for (double v : stds) {
            sum = sum + v;
        }
        double mean = sum / (n);
        double standardDeviation = 0;
        for (double std : stds) {
            standardDeviation = standardDeviation + Math.pow((std - mean), 2);
        }
        return Math.sqrt(standardDeviation / n);
    }

    private RTTSample pingICMP(Url url) throws IOException {
        DecimalFormat format = (DecimalFormat) DecimalFormat.getInstance();
        DecimalFormatSymbols symbols = format.getDecimalFormatSymbols();
        char sep = symbols.getDecimalSeparator();

        String command = String.join(" ",
                "ping", "-c", parameterService.getActiveTestIntensity() + "", "-i 0" + sep + "2", url.getAddress());
        logger.info("Starting active sampling with command \"" + command + "\"");
        BufferedReader inputStream = runSystemCommand(command);

        // reading output stream of the command
        List<String> lines = inputStream.lines().collect(Collectors.toList());
        return RTTSample.builder()
                .id(0)
                .packetLoss(getValFromString(lines.get(lines.size() - 2), "(\\d+)% packet loss"))
                .min(getValFromString(lines.get(lines.size() - 1), " ((\\d+)(\\.)(\\d+))/"))
                .average(getValFromString(lines.get(lines.size() - 1), "/((\\d+)(\\.)(\\d+))/"))
                .max(getValFromString(lines.get(lines.size() - 1), "/((\\d+)(\\.)(\\d+))/((\\d+)(\\.)(\\d+)) ms"))
                .standardDeviation(getValFromString(lines.get(lines.size() - 1), "/((\\d+)(\\.)(\\d+)) ms"))
                .timestamp(Instant.now())
                .type(ICMP)
                .url(url)
                .monitor(monitorService.getLocalMonitor())
                .build();
    }

    private static BufferedReader runSystemCommand(String command) throws IOException {
        Process p = Runtime.getRuntime().exec(command);
        return new BufferedReader(
                new InputStreamReader(p.getInputStream()));
    }

    private static float getValFromString(String inputLine, String patternS) {
        Pattern pattern = Pattern.compile(patternS);
        Matcher m = pattern.matcher(inputLine);
        if (m.find()) {
            return Float.parseFloat((m.group(1)));
        }
        return -1;
    }
}
