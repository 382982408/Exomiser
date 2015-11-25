/*
 * The Exomiser - A tool to annotate and prioritize variants
 *
 * Copyright (C) 2012 - 2015  Charite Universitätsmedizin Berlin and Genome Research Ltd.
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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.charite.compbio.exomiser.core.factories;

import de.charite.compbio.jannovar.data.JannovarData;
import de.charite.compbio.jannovar.htsjdk.VariantContextAnnotator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author Jules Jacobsen <jules.jacobsen@sanger.ac.uk>
 */
@Configuration
public class SampleDataFactoryTestConfig {

    @Bean
    public SampleDataFactory SampleDataFactory() {
        return new SampleDataFactory();
    }

    @Bean
    public JannovarData jannovarData() {
        return TestFactory.buildDefaultJannovarData();
    }

    @Bean
    public VariantAnnotator variantAnnotator() {
        JannovarData jannovarData = jannovarData();
        VariantContextAnnotator variantContextAnnotator = new VariantContextAnnotator(jannovarData.getRefDict(), jannovarData.getChromosomes());
        return new VariantAnnotator(variantContextAnnotator);
    }

    @Bean
    public VariantFactory variantFactory() {
        return new VariantFactory(variantAnnotator());
    }

}
