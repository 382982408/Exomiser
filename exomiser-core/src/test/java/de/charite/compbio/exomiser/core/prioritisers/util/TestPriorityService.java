/*
 * The Exomiser - A tool to annotate and prioritize variants
 *
 * Copyright (C) 2012 - 2016  Charite Universitätsmedizin Berlin and Genome Research Ltd.
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.charite.compbio.exomiser.core.prioritisers.util;

import de.charite.compbio.exomiser.core.model.Model;
import de.charite.compbio.exomiser.core.model.PhenotypeMatch;
import de.charite.compbio.exomiser.core.model.PhenotypeTerm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Jules Jacobsen <jules.jacobsen@sanger.ac.uk>
 */
public class TestPriorityService {

    private static final Logger logger = LoggerFactory.getLogger(TestPriorityService.class);

    public static final PriorityService TEST_SERVICE = setUpPriorityService();

    private static PriorityService setUpPriorityService() {
        logger.info("Creating test priority service - this is static test data");

        Map<String, PhenotypeTerm> hpPhenotypesTerms = new HashMap<>();
        hpPhenotypesTerms.put("HP:0010055", new PhenotypeTerm("HP:0010055", "Broad hallux", 0));
        hpPhenotypesTerms.put("HP:0001363", new PhenotypeTerm("HP:0001363", "Craniosynostosis", 0));
        hpPhenotypesTerms.put("HP:0001156", new PhenotypeTerm("HP:0001156", "Brachydactyly syndrome", 0));
        hpPhenotypesTerms.put("HP:0011304", new PhenotypeTerm("HP:0011304", "Broad thumb", 0));

        logger.info("This data links {} phenotypes:", hpPhenotypesTerms.size());
        hpPhenotypesTerms.values().forEach(term ->logger.info("{} - {}", term.getId(), term.getTerm()));

        logger.info("With cross-species phenotype mappings:");
        List<PhenotypeMatch> hpHpMappings = TestPrioritiserDataFileReader.readOntologyMatchData("src/test/resources/prioritisers/hp-hp-mappings");
        logger.info("hp-hp: " + hpHpMappings.size());

        List<PhenotypeMatch> hpMpMappings = TestPrioritiserDataFileReader.readOntologyMatchData("src/test/resources/prioritisers/hp-mp-mappings");
        logger.info("hp-mp: " + hpMpMappings.size());

        List<PhenotypeMatch> hpZpMappings = TestPrioritiserDataFileReader.readOntologyMatchData("src/test/resources/prioritisers/hp-zp-mappings");
        logger.info("hp-zp: " + hpZpMappings.size());

        logger.info("To the following models associated with genes FGFR2, ROR2, FREM2:");
        List<Model> diseaseModels = TestPrioritiserDataFileReader.readDiseaseData("src/test/resources/prioritisers/disease-models");
        logger.info("Disease Models: " + diseaseModels.size());

        List<Model> mouseModels = TestPrioritiserDataFileReader.readOrganismData("src/test/resources/prioritisers/mouse-models");
        logger.info("Mouse Models: " + mouseModels.size());

        List<Model> fishModels = TestPrioritiserDataFileReader.readOrganismData("src/test/resources/prioritisers/fish-models");
        logger.info("Fish Models: {}", fishModels.size());

        ModelService testModelService = new TestModelService(diseaseModels, mouseModels, fishModels);


        OntologyService testOntologyService = TestOntologyService.newBuilder()
                .setHpIdPhenotypeTerms(hpPhenotypesTerms)
                .setHumanHumanMappings(createPhenotypeMap(hpHpMappings))
                .setHumanMouseMappings(createPhenotypeMap(hpMpMappings))
                .setHumanFishMappings(createPhenotypeMap(hpZpMappings))
                .build();


        return new PriorityService(testOntologyService, testModelService, null);
    }

    private static Map<PhenotypeTerm, List<PhenotypeMatch>> createPhenotypeMap(List<PhenotypeMatch> crossOntologyMappings) {
        return crossOntologyMappings.parallelStream().collect(Collectors.groupingBy(PhenotypeMatch::getQueryPhenotype));
    }
}
