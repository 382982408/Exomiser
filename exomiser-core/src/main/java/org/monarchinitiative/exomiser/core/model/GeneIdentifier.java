/*
 * The Exomiser - A tool to annotate and prioritize genomic variants
 *
 * Copyright (c) 2016-2017 Queen Mary University of London.
 * Copyright (c) 2012-2016 Charité Universitätsmedizin Berlin and Genome Research Ltd.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.monarchinitiative.exomiser.core.model;

import java.util.Objects;

/**
 * This is a bit of a hybrid concept - can represent any geneId and corresponding symbol, but also contains fields for linking explicitly to human
 * gene databases such as ENSEMBL, ENTREZ gene and UCSC and the HGNC identifiers. This could for instance represent a mouse gene - human ortholog pair
 * or a human gene using one of the mapped coordinate systems as an implicit default via the getGeneId and getGeneSymbol methods.
 *
 * @author Jules Jacobsen <j.jacobsen@qmul.ac.uk>
 */
public class GeneIdentifier {

    public static final String EMPTY_FIELD = "";

    private final String geneId;
    private final String geneSymbol;

    private final String hgncId;
    private final String hgncSymbol;

    private final String entrezId;
    private final int entrezIdIntValue;
    private final String ensemblId;
    private final String ucscId;

    private GeneIdentifier(Builder builder) {
        this.geneId = Objects.requireNonNull(builder.geneId, "GeneIdentifier geneId cannot be null");
        this.geneSymbol = Objects.requireNonNull(builder.geneSymbol, "GeneIdentifier geneSymbol cannot be null");

        this.hgncId = builder.hgncId;
        this.hgncSymbol = builder.hgncSymbol;

        //the entrezId is used by the Prioritisers as a primary key for the gene so this is important!
        this.entrezId = builder.entrezId;
        this.entrezIdIntValue = validateEntrezIdIntValue(builder.entrezId);
        this.ensemblId = builder.ensemblId;
        this.ucscId = builder.ucscId;
    }

    private int validateEntrezIdIntValue(String entrezId) {
        Objects.requireNonNull(entrezId, "GeneIdentifier entrezId cannot be null");
        if (entrezId.isEmpty()) {
            //This is permissible - will default to returning a NULL_ENTREZ_ID
            return -1;
        }
        try {
            return Integer.parseInt(entrezId);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("entrezId '" + entrezId + "' is invalid. GeneIdentifier entrezId must be a positive integer");
        }
    }

    public String getGeneId() {
        return geneId;
    }

    public String getGeneSymbol() {
        return geneSymbol;
    }

    public String getHgncId() {
        return hgncId;
    }

    public String getHgncSymbol() {
        return hgncSymbol;
    }

    public String getEntrezId() {
        return entrezId;
    }

    public Integer getEntrezIdAsInteger() {
        return entrezIdIntValue;
    }

    public boolean hasEntrezId() {
        return !entrezId.isEmpty();
    }

    public String getEnsemblId() {
        return ensemblId;
    }

    public String getUcscId() {
        return ucscId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GeneIdentifier that = (GeneIdentifier) o;
        return Objects.equals(geneId, that.geneId) &&
                Objects.equals(geneSymbol, that.geneSymbol) &&
                Objects.equals(hgncId, that.hgncId) &&
                Objects.equals(hgncSymbol, that.hgncSymbol) &&
                Objects.equals(entrezId, that.entrezId) &&
                Objects.equals(ensemblId, that.ensemblId) &&
                Objects.equals(ucscId, that.ucscId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(geneId, geneSymbol, hgncId, hgncSymbol, entrezId, ensemblId, ucscId);
    }

    @Override
    public String toString() {
        return "GeneIdentifier{" +
                "geneId='" + geneId + '\'' +
                ", geneSymbol='" + geneSymbol + '\'' +
                ", hgncId='" + hgncId + '\'' +
                ", hgncSymbol='" + hgncSymbol + '\'' +
                ", entrezId='" + entrezId + '\'' +
                ", ensemblId='" + ensemblId + '\'' +
                ", ucscId='" + ucscId + '\'' +
                '}';
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private String geneId = EMPTY_FIELD;
        private String geneSymbol = EMPTY_FIELD;

        private String hgncId = EMPTY_FIELD;
        private String hgncSymbol = EMPTY_FIELD;

        private String entrezId = EMPTY_FIELD;
        private String ensemblId = EMPTY_FIELD;
        private String ucscId = EMPTY_FIELD;

        private Builder() {}

        public Builder geneId(String geneId) {
            this.geneId = geneId;
            return this;
        }

        public Builder geneSymbol(String geneSymbol) {
            this.geneSymbol = geneSymbol;
            return this;
        }

        public Builder hgncId(String hgncId) {
            this.hgncId = hgncId;
            return this;
        }

        public Builder hgncSymbol(String hgncSymbol) {
            this.hgncSymbol = hgncSymbol;
            return this;
        }

        public Builder entrezId(String entrezId) {
            this.entrezId = entrezId;
            return this;
        }

        public Builder ensemblId(String ensemblId) {
            this.ensemblId = ensemblId;
            return this;
        }

        public Builder ucscId(String ucscId) {
            this.ucscId = ucscId;
            return this;
        }

        public GeneIdentifier build() {
            return new GeneIdentifier(this);
        }
    }
}
