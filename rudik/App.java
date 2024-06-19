package asu.edu.rule_miner.rudik;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;

import asu.edu.rule_miner.rudik.configuration.ConfigurationFacility;
import asu.edu.rule_miner.rudik.model.horn_rule.HornRule;
import asu.edu.rule_miner.rudik.model.statistic.StatisticsContainer;
import asu.edu.rule_miner.rudik.rule_generator.DynamicPruningRuleDiscovery;

public class App {
    private final static Logger LOGGER = LoggerFactory.getLogger(App.class.getName());

    public static void main(String[] args) {
        try {
            // Load configuration using ConfigurationFacility
            ConfigurationFacility.setConfigurationFile("src/main/config/Configuration.xml");
            org.apache.commons.configuration.Configuration config = ConfigurationFacility.getConfiguration();

            // Log configuration details for troubleshooting
            LOGGER.info("Loaded configuration: {}", config);

            // Extract configurations
            String typeSubject = "http://www.wikidata.org/entity/Q5"; // Fixed typeSubject
            String typeObject = "http://www.wikidata.org/entity/Q5"; // Fixed typeObject
            Set<String> relations = Sets.newHashSet("http://www.wikidata.org/prop/direct/P3373");

            LOGGER.info("Using SPARQL endpoint: {}", config.getString("naive.sparql.parameters.sparql_endpoint"));
            LOGGER.info("Using graph IRI: {}", config.getString("naive.sparql.graph_iri"));

            int limitSubject = config.getInt("naive.sparql.limits.edges.subject", -1);
            int limitObject = config.getInt("naive.sparql.limits.edges.object", -1);
            int positiveLimit = config.getInt("naive.sparql.limits.examples.positive", 1000);
            int negativeLimit = config.getInt("naive.sparql.limits.examples.negative", 1000);
            double alpha = config.getDouble("naive.runtime.score.alpha", 0.3);
            double beta = config.getDouble("naive.runtime.score.beta", 0.7);
            double gamma = config.getDouble("naive.runtime.score.gamma", 0.1);
            double subWeight = 0.5;
            double objWeight = 0.5;
            boolean isTopK = false;

            DynamicPruningRuleDiscovery naive = new DynamicPruningRuleDiscovery();
            StatisticsContainer.setFileName(new File("statisticsFile"));

            naive.setSubjectLimit(limitSubject);
            naive.setObjectLimit(limitObject);
            naive.setGenerationSmartLimit(-1); // assuming genLimit is not explicitly mentioned in the provided config
            naive.setSmartWeights(alpha, beta, gamma, subWeight, objWeight);
            naive.setIsTopK(isTopK);

            String id = relations + "_" + limitSubject + "_" + limitObject + "_" + positiveLimit + "_" + negativeLimit + "_" + -1 + "_" + alpha
                    + "_" + beta + "_" + gamma + "_" + isTopK;
            StatisticsContainer.initialiseContainer(id);

            Set<Pair<String, String>> negativeExamples;
            if (negativeLimit == -1)
                negativeExamples = naive.generateNegativeExamples(relations, typeSubject, typeObject);
            else
                negativeExamples = naive.generateNegativeExamples(relations, typeSubject, typeObject, negativeLimit);

            Set<Pair<String, String>> positiveExamples;
            if (positiveLimit == -1)
                positiveExamples = naive.generatePositiveExamples(relations, typeSubject, typeObject);
            else
                positiveExamples = naive.generatePositiveExamples(relations, typeSubject, typeObject, positiveLimit);

            Map<HornRule, Double> discoveredNegativeRulesMap = naive.discoverAllNegativeHornRules(negativeExamples, positiveExamples, relations, typeSubject, typeObject, -1);
            List<HornRule> discoveredNegativeRules = discoveredNegativeRulesMap.keySet().stream().collect(Collectors.toList());

            Map<HornRule, Double> discoveredPositiveRulesMap = naive.discoverAllPositiveHornRules(negativeExamples, positiveExamples, relations, typeSubject, typeObject, -1);
            List<HornRule> discoveredPositiveRules = discoveredPositiveRulesMap.keySet().stream().collect(Collectors.toList());

            // Write discovered rules to files
            writeRulesToFile(discoveredNegativeRules, "negative_horn_rules.txt");
            writeRulesToFile(discoveredPositiveRules, "positive_horn_rules.txt");

            StatisticsContainer.printStatistics();

        } catch (Exception e) {
            LOGGER.error("An error occurred while executing the rule discovery process", e);
            e.printStackTrace();
        }
    }

    private static void writeRulesToFile(List<HornRule> rules, String fileName) {
        if (rules == null || rules.isEmpty()) {
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (HornRule rule : rules) {
                writer.write(rule.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            LOGGER.error("Failed to write rules to file: {}", fileName, e);
        }
    }
}
