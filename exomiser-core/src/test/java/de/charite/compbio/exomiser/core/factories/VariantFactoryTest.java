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

import de.charite.compbio.exomiser.core.model.Variant;
import de.charite.compbio.exomiser.core.model.VariantEvaluation;
import de.charite.compbio.jannovar.data.JannovarData;
import de.charite.compbio.jannovar.htsjdk.VariantContextAnnotator;
import htsjdk.tribble.TribbleException;
import htsjdk.variant.variantcontext.Genotype;
import htsjdk.variant.variantcontext.GenotypeType;
import htsjdk.variant.variantcontext.GenotypesContext;
import htsjdk.variant.variantcontext.VariantContext;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Jules Jacobsen <jules.jacobsen@sanger.ac.uk>
 */
public class VariantFactoryTest {

    private VariantFactory instance;

    private final JannovarData jannovarData;
    private final VariantContextAnnotator variantContextAnnotator;
    private final VariantAnnotator variantAnnotator;

    public VariantFactoryTest() {
        jannovarData = new TestJannovarDataFactory().getJannovarData();
        variantContextAnnotator = new VariantContextAnnotator(jannovarData.getRefDict(), jannovarData.getChromosomes());
        variantAnnotator = new VariantAnnotator(variantContextAnnotator);
    }

    @Before
    public void setUp() {
        instance = new VariantFactory(variantAnnotator);
    }

    private void printVariant(VariantEvaluation variant) {
        GenotypesContext genotypes = variant.getVariantContext().getGenotypes();
        List<GenotypeType> genotypeTypes = genotypes.stream().map(Genotype::getType).collect(toList());
        System.out.printf("%s %s %s %s %s %s %s offExome=%s gene=%s %s%n", variant.getChromosome(), variant.getPosition(), variant.getRef(), variant.getAlt(), variant.getGenotypeAsString(), genotypes, genotypeTypes, variant.isOffExome(), variant.getGeneSymbol(), variant.getVariantContext());
    }


    @Test(expected = TribbleException.class)
    public void testCreateVariantContexts_NonExistentFile() {
        Path vcfPath = Paths.get("src/test/resources/wibble.vcf");
        instance.createVariantContexts(vcfPath);
    }

    @Test
    public void testCreateVariantContexts_SingleAlleles() {
        Path vcfPath = Paths.get("src/test/resources/smallTest.vcf");
        List<VariantContext> variants = instance.createVariantContexts(vcfPath);
        assertThat(variants.size(), equalTo(3));
    }

    @Test
    public void testStreamVariantContexts_SingleAlleles() {
        Path vcfPath = Paths.get("src/test/resources/smallTest.vcf");
        List<VariantContext> variants = instance.streamVariantContexts(vcfPath)
                .filter(variantContext -> (variantContext.getChr().equals("1")))
                .collect(toList());

        assertThat(variants.size(), equalTo(3));
    }

    @Test
    public void testStreamCreateVariants_SingleAlleles() {
        Path vcfPath = Paths.get("src/test/resources/smallTest.vcf");
        List<VariantEvaluation> variants = instance.streamVariantEvaluations(vcfPath).collect(toList());
        variants.forEach(this::printVariant);
        assertThat(variants.size(), equalTo(3));

    }

    @Test
    public void testCreateVariantContexts_MultipleAlleles() {
        Path vcfPath = Paths.get("src/test/resources/altAllele.vcf");
        List<VariantContext> variants = instance.createVariantContexts(vcfPath);
        assertThat(variants.size(), equalTo(1));
    }

    @Test
    public void testCreateVariantContexts_MultipleAlleles_DiferentSingleSampleGenotypes() {
        Path vcfPath = Paths.get("src/test/resources/multiAlleleGenotypes.vcf");
        List<VariantContext> variants = instance.createVariantContexts(vcfPath);
        assertThat(variants.size(), equalTo(11));
    }

    @Test
    public void testCreateVariants_SingleAlleles() {
        Path vcfPath = Paths.get("src/test/resources/smallTest.vcf");
        List<VariantEvaluation> variants = instance.createVariantEvaluations(vcfPath);
        variants.forEach(this::printVariant);
        assertThat(variants.size(), equalTo(3));

    }

    @Test
    public void testCreateVariants_MultipleAllelesProduceOneVariantPerAllele() {
        Path vcfPath = Paths.get("src/test/resources/altAllele.vcf");
        List<VariantEvaluation> variants = instance.createVariantEvaluations(vcfPath);
        variants.forEach(this::printVariant);
        assertThat(variants.size(), equalTo(2));
    }

    @Test
    public void testCreateVariants_MultipleAlleles_SingleSampleGenotypesShouldOnlyReturnRepresentedVariationFromGenotype() {
        Path vcfPath = Paths.get("src/test/resources/multiAlleleGenotypes.vcf");
        List<VariantEvaluation> variants = instance.createVariantEvaluations(vcfPath);
        variants.forEach(this::printVariant);
        assertThat(variants.size(), equalTo(11));
    }

    @Test
    public void testCreateVariants_NoVariantAnnotationsProduceVariantEvaluationsWithNoAnnotations() {
        Path vcfPath = Paths.get("src/test/resources/noAnnotations.vcf");
        List<VariantEvaluation> variants = instance.createVariantEvaluations(vcfPath);
        assertThat(variants.size(), equalTo(2));

        for (VariantEvaluation variant : variants) {
            System.out.println(variant.getChromosomeName() + " " + variant);
            assertThat(variant.hasAnnotations(), is(false));
        }
    }

}
