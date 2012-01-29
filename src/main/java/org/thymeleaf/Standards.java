/*
 * =============================================================================
 * 
 *   Copyright (c) 2011, The THYMELEAF team (http://www.thymeleaf.org)
 * 
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 * 
 * =============================================================================
 */
package org.thymeleaf;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.thymeleaf.doctype.DocTypeIdentifier;
import org.thymeleaf.doctype.resolution.ClassLoaderDocTypeResolutionEntry;
import org.thymeleaf.doctype.resolution.IDocTypeResolutionEntry;



/**
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.0
 *
 */
public final class Standards {
    
    private static final String DTD_STANDARD_PATH = "org/thymeleaf/dtd/standard/";

    
    public static final String XML_DECLARATION = "<?xml version=\"1.0\">";
    
    
    public static final DocTypeIdentifier XHTML_1_STRICT_SYSTEMID = 
        DocTypeIdentifier.forValue("http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd");
    public static final DocTypeIdentifier XHTML_1_TRANSITIONAL_SYSTEMID = 
        DocTypeIdentifier.forValue("http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd");
    public static final DocTypeIdentifier XHTML_1_FRAMESET_SYSTEMID = 
        DocTypeIdentifier.forValue("http://www.w3.org/TR/xhtml1/DTD/xhtml1-frameset.dtd");
    public static final DocTypeIdentifier XHTML_11_SYSTEMID = 
        DocTypeIdentifier.forValue("http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd");
    public static final DocTypeIdentifier HTML_5_LEGACY_WILDCARD_SYSTEMID = 
        DocTypeIdentifier.forValue("about:legacy-compat");
    
    
    public static final DocTypeIdentifier XHTML_1_STRICT_PUBLICID = 
        DocTypeIdentifier.forValue("-//W3C//DTD XHTML 1.0 Strict//EN");
    public static final DocTypeIdentifier XHTML_1_TRANSITIONAL_PUBLICID = 
        DocTypeIdentifier.forValue("-//W3C//DTD XHTML 1.0 Transitional//EN");
    public static final DocTypeIdentifier XHTML_1_FRAMESET_PUBLICID = 
        DocTypeIdentifier.forValue("-//W3C//DTD XHTML 1.0 Frameset//EN");
    public static final DocTypeIdentifier XHTML_11_PUBLICID = 
        DocTypeIdentifier.forValue("-//W3C//DTD XHTML 1.1//EN");
    public static final DocTypeIdentifier ELEMENTS_XHTML_INLINE_STYLE_1_PUBLICID = 
        DocTypeIdentifier.forValue("-//W3C//ELEMENTS XHTML Inline Style 1.0//EN");
    public static final DocTypeIdentifier ENTITIES_XHTML_MODULAR_FRAMEWORK_1_PUBLICID = 
        DocTypeIdentifier.forValue("-//W3C//ENTITIES XHTML Modular Framework 1.0//EN");
    public static final DocTypeIdentifier ENTITIES_XHTML_DATATYPES_1_PUBLICID = 
        DocTypeIdentifier.forValue("-//W3C//ENTITIES XHTML Datatypes 1.0//EN");
    public static final DocTypeIdentifier ENTITIES_XHTML_QUALIFIED_NAMES_1_PUBLICID = 
        DocTypeIdentifier.forValue("-//W3C//ENTITIES XHTML Qualified Names 1.0//EN");
    public static final DocTypeIdentifier ENTITIES_XHTML_INTRINSIC_EVENTS_1_PUBLICID = 
        DocTypeIdentifier.forValue("-//W3C//ENTITIES XHTML Intrinsic Events 1.0//EN");
    public static final DocTypeIdentifier ELEMENTS_XHTML_FORMS_1_PUBLICID = 
        DocTypeIdentifier.forValue("-//W3C//ELEMENTS XHTML Forms 1.0//EN");
    public static final DocTypeIdentifier ELEMENTS_XHTML_BASE_ELEMENT_1_PUBLICID = 
        DocTypeIdentifier.forValue("-//W3C//ELEMENTS XHTML Base Element 1.0//EN");
    public static final DocTypeIdentifier ELEMENTS_XHTML_BLOCK_STRUCTURAL_1_PUBLICID = 
        DocTypeIdentifier.forValue("-//W3C//ELEMENTS XHTML Block Structural 1.0//EN");
    public static final DocTypeIdentifier ELEMENTS_XHTML_TABLES_1_PUBLICID = 
        DocTypeIdentifier.forValue("-//W3C//ELEMENTS XHTML Tables 1.0//EN");
    public static final DocTypeIdentifier ELEMENTS_XHTML_STYLE_SHEETS_1_PUBLICID = 
        DocTypeIdentifier.forValue("-//W3C//ELEMENTS XHTML Style Sheets 1.0//EN");
    public static final DocTypeIdentifier ELEMENTS_XHTML_INLINE_STRUCTURAL_1_PUBLICID = 
        DocTypeIdentifier.forValue("-//W3C//ELEMENTS XHTML Inline Structural 1.0//EN");
    public static final DocTypeIdentifier ENTITIES_XHTML_CHARACTER_ENTITIES_1_PUBLICID = 
        DocTypeIdentifier.forValue("-//W3C//ENTITIES XHTML Character Entities 1.0//EN");
    public static final DocTypeIdentifier ENTITIES_XHTML_11_DOCUMENT_MODEL_1_PUBLICID = 
        DocTypeIdentifier.forValue("-//W3C//ENTITIES XHTML 1.1 Document Model 1.0//EN");
    public static final DocTypeIdentifier ELEMENTS_XHTML_HYPERTEXT_1_PUBLICID = 
        DocTypeIdentifier.forValue("-//W3C//ELEMENTS XHTML Hypertext 1.0//EN");
    public static final DocTypeIdentifier ELEMENTS_XHTML_BLOCK_PRESENTATION_1_PUBLICID = 
        DocTypeIdentifier.forValue("-//W3C//ELEMENTS XHTML Block Presentation 1.0//EN");
    public static final DocTypeIdentifier ELEMENTS_XHTML_BIDI_OVERRIDE_ELEMENT_1_PUBLICID = 
        DocTypeIdentifier.forValue("-//W3C//ELEMENTS XHTML BIDI Override Element 1.0//EN");
    public static final DocTypeIdentifier ENTITIES_SPECIAL_FOR_XHTML_PUBLICID = 
        DocTypeIdentifier.forValue("-//W3C//ENTITIES Special for XHTML//EN");
    public static final DocTypeIdentifier ELEMENTS_XHTML_BLOCK_PHRASAL_1_PUBLICID = 
        DocTypeIdentifier.forValue("-//W3C//ELEMENTS XHTML Block Phrasal 1.0//EN");
    public static final DocTypeIdentifier ELEMENTS_XHTML_LINK_ELEMENT_1_PUBLICID = 
        DocTypeIdentifier.forValue("-//W3C//ELEMENTS XHTML Link Element 1.0//EN");
    public static final DocTypeIdentifier ELEMENTS_XHTML_INLINE_PRESENTATION_1_PUBLICID = 
        DocTypeIdentifier.forValue("-//W3C//ELEMENTS XHTML Inline Presentation 1.0//EN");
    public static final DocTypeIdentifier ELEMENTS_XHTML_LISTS_1_PUBLICID = 
        DocTypeIdentifier.forValue("-//W3C//ELEMENTS XHTML Lists 1.0//EN");
    public static final DocTypeIdentifier ENTITIES_SYMBOLS_FOR_XHTML_PUBLICID = 
        DocTypeIdentifier.forValue("-//W3C//ENTITIES Symbols for XHTML//EN");
    public static final DocTypeIdentifier ELEMENTS_XHTML_EMBEDDED_OBJECT_1_PUBLICID = 
        DocTypeIdentifier.forValue("-//W3C//ELEMENTS XHTML Embedded Object 1.0//EN");
    public static final DocTypeIdentifier ELEMENTS_XHTML_SERVER_SIDE_IMAGE_MAPS_1_PUBLICID = 
        DocTypeIdentifier.forValue("-//W3C//ELEMENTS XHTML Server-side Image Maps 1.0//EN");
    public static final DocTypeIdentifier ENTITIES_LATIN_1_FOR_XHTML_PUBLICID = 
        DocTypeIdentifier.forValue("-//W3C//ENTITIES Latin 1 for XHTML//EN");
    public static final DocTypeIdentifier ELEMENTS_XHTML_PARAM_ELEMENT_1_PUBLICID = 
        DocTypeIdentifier.forValue("-//W3C//ELEMENTS XHTML Param Element 1.0//EN");
    public static final DocTypeIdentifier ENTITIES_XHTML_COMMON_ATTRIBUTES_1_PUBLICID = 
        DocTypeIdentifier.forValue("-//W3C//ENTITIES XHTML Common Attributes 1.0//EN");
    public static final DocTypeIdentifier ELEMENTS_XHTML_INLINE_PHRASAL_1_PUBLICID = 
        DocTypeIdentifier.forValue("-//W3C//ELEMENTS XHTML Inline Phrasal 1.0//EN");
    public static final DocTypeIdentifier ELEMENTS_XHTML_METAINFORMATION_1_PUBLICID = 
        DocTypeIdentifier.forValue("-//W3C//ELEMENTS XHTML Metainformation 1.0//EN");
    public static final DocTypeIdentifier ELEMENTS_XHTML_EDITING_ELEMENTS_1_PUBLICID = 
        DocTypeIdentifier.forValue("-//W3C//ELEMENTS XHTML Editing Elements 1.0//EN");
    public static final DocTypeIdentifier ELEMENTS_XHTML_CLIENT_SIDE_IMAGE_MAPS_1_PUBLICID = 
        DocTypeIdentifier.forValue("-//W3C//ELEMENTS XHTML Client-side Image Maps 1.0//EN");
    public static final DocTypeIdentifier ELEMENTS_XHTML_SCRIPTING_1_PUBLICID = 
        DocTypeIdentifier.forValue("-//W3C//ELEMENTS XHTML Scripting 1.0//EN");
    public static final DocTypeIdentifier ELEMENTS_XHTML_TEXT_1_PUBLICID = 
        DocTypeIdentifier.forValue("-//W3C//ELEMENTS XHTML Text 1.0//EN");
    public static final DocTypeIdentifier ELEMENTS_XHTML_RUBY_1_PUBLICID = 
        DocTypeIdentifier.forValue("-//W3C//ELEMENTS XHTML Ruby 1.0//EN");
    public static final DocTypeIdentifier ELEMENTS_XHTML_DOCUMENT_STRUCTURE_1_PUBLICID = 
        DocTypeIdentifier.forValue("-//W3C//ELEMENTS XHTML Document Structure 1.0//EN");
    public static final DocTypeIdentifier ELEMENTS_XHTML_PRESENTATION_1_PUBLICID = 
        DocTypeIdentifier.forValue("-//W3C//ELEMENTS XHTML Presentation 1.0//EN");
    public static final DocTypeIdentifier ELEMENTS_XHTML_IMAGES_1_PUBLICID = 
        DocTypeIdentifier.forValue("-//W3C//ELEMENTS XHTML Images 1.0//EN");
    

    
    

    
    
    
    public static final IDocTypeResolutionEntry XHTML_1_STRICT_DOC_TYPE_RESOLUTION_ENTRY =
        new ClassLoaderDocTypeResolutionEntry(
            XHTML_1_STRICT_PUBLICID, 
            XHTML_1_STRICT_SYSTEMID, 
            DTD_STANDARD_PATH + "xhtml1-strict.dtd");
    
    public static final IDocTypeResolutionEntry XHTML_1_TRANSITIONAL_DOC_TYPE_RESOLUTION_ENTRY =
        new ClassLoaderDocTypeResolutionEntry(
            XHTML_1_TRANSITIONAL_PUBLICID, 
            XHTML_1_TRANSITIONAL_SYSTEMID, 
            DTD_STANDARD_PATH + "xhtml1-transitional.dtd");
    
    public static final IDocTypeResolutionEntry XHTML_1_FRAMESET_DOC_TYPE_RESOLUTION_ENTRY =
        new ClassLoaderDocTypeResolutionEntry(
            XHTML_1_FRAMESET_PUBLICID, 
            XHTML_1_FRAMESET_SYSTEMID, 
            DTD_STANDARD_PATH + "xhtml1-frameset.dtd");
    
    public static final IDocTypeResolutionEntry XHTML_11_DOC_TYPE_RESOLUTION_ENTRY =
        new ClassLoaderDocTypeResolutionEntry(
            XHTML_11_PUBLICID, 
            XHTML_11_SYSTEMID, 
            DTD_STANDARD_PATH + "xhtml11.dtd");
    
    public static final IDocTypeResolutionEntry ELEMENTS_XHTML_INLINE_STYLE_1_DOC_TYPE_RESOLUTION_ENTRY =
        new ClassLoaderDocTypeResolutionEntry(
            ELEMENTS_XHTML_INLINE_STYLE_1_PUBLICID, 
            DocTypeIdentifier.ANY, 
            DTD_STANDARD_PATH + "xhtml-inlstyle-1.mod");
    
    public static final IDocTypeResolutionEntry ENTITIES_XHTML_MODULAR_FRAMEWORK_1_DOC_TYPE_RESOLUTION_ENTRY =
        new ClassLoaderDocTypeResolutionEntry(
            ENTITIES_XHTML_MODULAR_FRAMEWORK_1_PUBLICID, 
            DocTypeIdentifier.ANY, 
            DTD_STANDARD_PATH + "xhtml-framework-1.mod");
    
    public static final IDocTypeResolutionEntry ENTITIES_XHTML_DATATYPES_1_DOC_TYPE_RESOLUTION_ENTRY =
        new ClassLoaderDocTypeResolutionEntry(
            ENTITIES_XHTML_DATATYPES_1_PUBLICID, 
            DocTypeIdentifier.ANY, 
            DTD_STANDARD_PATH + "xhtml-datatypes-1.mod");
    
    public static final IDocTypeResolutionEntry ENTITIES_XHTML_QUALIFIED_NAMES_1_DOC_TYPE_RESOLUTION_ENTRY =
        new ClassLoaderDocTypeResolutionEntry(
            ENTITIES_XHTML_QUALIFIED_NAMES_1_PUBLICID, 
            DocTypeIdentifier.ANY, 
            DTD_STANDARD_PATH + "xhtml-qname-1.mod");
    
    public static final IDocTypeResolutionEntry ENTITIES_XHTML_INTRINSIC_EVENTS_1_DOC_TYPE_RESOLUTION_ENTRY =
        new ClassLoaderDocTypeResolutionEntry(
            ENTITIES_XHTML_INTRINSIC_EVENTS_1_PUBLICID, 
            DocTypeIdentifier.ANY, 
            DTD_STANDARD_PATH + "xhtml-events-1.mod");
    
    public static final IDocTypeResolutionEntry ELEMENTS_XHTML_FORMS_1_DOC_TYPE_RESOLUTION_ENTRY = 
        new ClassLoaderDocTypeResolutionEntry(
            ELEMENTS_XHTML_FORMS_1_PUBLICID,
            DocTypeIdentifier.ANY, 
            DTD_STANDARD_PATH + "xhtml-form-1.mod");

    public static final IDocTypeResolutionEntry ELEMENTS_XHTML_BASE_ELEMENT_1_DOC_TYPE_RESOLUTION_ENTRY = 
        new ClassLoaderDocTypeResolutionEntry(
            ELEMENTS_XHTML_BASE_ELEMENT_1_PUBLICID,
            DocTypeIdentifier.ANY, 
            DTD_STANDARD_PATH + "xhtml-base-1.mod");

    public static final IDocTypeResolutionEntry ELEMENTS_XHTML_BLOCK_STRUCTURAL_1_DOC_TYPE_RESOLUTION_ENTRY = 
        new ClassLoaderDocTypeResolutionEntry(
            ELEMENTS_XHTML_BLOCK_STRUCTURAL_1_PUBLICID,
            DocTypeIdentifier.ANY, 
            DTD_STANDARD_PATH + "xhtml-blkstruct-1.mod");

    public static final IDocTypeResolutionEntry ELEMENTS_XHTML_TABLES_1_DOC_TYPE_RESOLUTION_ENTRY = 
        new ClassLoaderDocTypeResolutionEntry(
            ELEMENTS_XHTML_TABLES_1_PUBLICID,
            DocTypeIdentifier.ANY, 
            DTD_STANDARD_PATH + "xhtml-table-1.mod");

    public static final IDocTypeResolutionEntry ELEMENTS_XHTML_STYLE_SHEETS_1_DOC_TYPE_RESOLUTION_ENTRY = 
        new ClassLoaderDocTypeResolutionEntry(
            ELEMENTS_XHTML_STYLE_SHEETS_1_PUBLICID,
            DocTypeIdentifier.ANY, 
            DTD_STANDARD_PATH + "xhtml-style-1.mod");

    public static final IDocTypeResolutionEntry ELEMENTS_XHTML_INLINE_STRUCTURAL_1_DOC_TYPE_RESOLUTION_ENTRY = 
        new ClassLoaderDocTypeResolutionEntry(
            ELEMENTS_XHTML_INLINE_STRUCTURAL_1_PUBLICID,
            DocTypeIdentifier.ANY, 
            DTD_STANDARD_PATH + "xhtml-inlstruct-1.mod");

    public static final IDocTypeResolutionEntry ENTITIES_XHTML_CHARACTER_ENTITIES_1_DOC_TYPE_RESOLUTION_ENTRY = 
        new ClassLoaderDocTypeResolutionEntry(
            ENTITIES_XHTML_CHARACTER_ENTITIES_1_PUBLICID,
            DocTypeIdentifier.ANY, 
            DTD_STANDARD_PATH + "xhtml-charent-1.mod");

    public static final IDocTypeResolutionEntry ENTITIES_XHTML_11_DOCUMENT_MODEL_1_DOC_TYPE_RESOLUTION_ENTRY = 
        new ClassLoaderDocTypeResolutionEntry(
            ENTITIES_XHTML_11_DOCUMENT_MODEL_1_PUBLICID,
            DocTypeIdentifier.ANY, 
            DTD_STANDARD_PATH + "xhtml11-model-1.mod");

    public static final IDocTypeResolutionEntry ELEMENTS_XHTML_HYPERTEXT_1_DOC_TYPE_RESOLUTION_ENTRY = 
        new ClassLoaderDocTypeResolutionEntry(
            ELEMENTS_XHTML_HYPERTEXT_1_PUBLICID,
            DocTypeIdentifier.ANY, 
            DTD_STANDARD_PATH + "xhtml-hypertext-1.mod");

    public static final IDocTypeResolutionEntry ELEMENTS_XHTML_BLOCK_PRESENTATION_1_DOC_TYPE_RESOLUTION_ENTRY = 
        new ClassLoaderDocTypeResolutionEntry(
            ELEMENTS_XHTML_BLOCK_PRESENTATION_1_PUBLICID,
            DocTypeIdentifier.ANY, 
            DTD_STANDARD_PATH + "xhtml-blkpres-1.mod");

    public static final IDocTypeResolutionEntry ELEMENTS_XHTML_BIDI_OVERRIDE_ELEMENT_1_DOC_TYPE_RESOLUTION_ENTRY = 
        new ClassLoaderDocTypeResolutionEntry(
            ELEMENTS_XHTML_BIDI_OVERRIDE_ELEMENT_1_PUBLICID,
            DocTypeIdentifier.ANY, 
            DTD_STANDARD_PATH + "xhtml-bdo-1.mod");

    public static final IDocTypeResolutionEntry ENTITIES_SPECIAL_FOR_XHTML_DOC_TYPE_RESOLUTION_ENTRY = 
        new ClassLoaderDocTypeResolutionEntry(
            ENTITIES_SPECIAL_FOR_XHTML_PUBLICID,
            DocTypeIdentifier.ANY, 
            DTD_STANDARD_PATH + "xhtml-special.ent");

    public static final IDocTypeResolutionEntry ELEMENTS_XHTML_BLOCK_PHRASAL_1_DOC_TYPE_RESOLUTION_ENTRY = 
        new ClassLoaderDocTypeResolutionEntry(
            ELEMENTS_XHTML_BLOCK_PHRASAL_1_PUBLICID,
            DocTypeIdentifier.ANY, 
            DTD_STANDARD_PATH + "xhtml-blkphras-1.mod");

    public static final IDocTypeResolutionEntry ELEMENTS_XHTML_LINK_ELEMENT_1_DOC_TYPE_RESOLUTION_ENTRY = 
        new ClassLoaderDocTypeResolutionEntry(
            ELEMENTS_XHTML_LINK_ELEMENT_1_PUBLICID,
            DocTypeIdentifier.ANY, 
            DTD_STANDARD_PATH + "xhtml-link-1.mod");

    public static final IDocTypeResolutionEntry ELEMENTS_XHTML_INLINE_PRESENTATION_1_DOC_TYPE_RESOLUTION_ENTRY = 
        new ClassLoaderDocTypeResolutionEntry(
            ELEMENTS_XHTML_INLINE_PRESENTATION_1_PUBLICID,
            DocTypeIdentifier.ANY, 
            DTD_STANDARD_PATH + "xhtml-inlpres-1.mod");

    public static final IDocTypeResolutionEntry ELEMENTS_XHTML_LISTS_1_DOC_TYPE_RESOLUTION_ENTRY = 
        new ClassLoaderDocTypeResolutionEntry(
            ELEMENTS_XHTML_LISTS_1_PUBLICID,
            DocTypeIdentifier.ANY, 
            DTD_STANDARD_PATH + "xhtml-list-1.mod");

    public static final IDocTypeResolutionEntry ENTITIES_SYMBOLS_FOR_XHTML_DOC_TYPE_RESOLUTION_ENTRY = 
        new ClassLoaderDocTypeResolutionEntry(
            ENTITIES_SYMBOLS_FOR_XHTML_PUBLICID,
            DocTypeIdentifier.ANY, 
            DTD_STANDARD_PATH + "xhtml-symbol.ent");

    public static final IDocTypeResolutionEntry ELEMENTS_XHTML_EMBEDDED_OBJECT_1_DOC_TYPE_RESOLUTION_ENTRY = 
        new ClassLoaderDocTypeResolutionEntry(
            ELEMENTS_XHTML_EMBEDDED_OBJECT_1_PUBLICID,
            DocTypeIdentifier.ANY, 
            DTD_STANDARD_PATH + "xhtml-object-1.mod");

    public static final IDocTypeResolutionEntry ELEMENTS_XHTML_SERVER_SIDE_IMAGE_MAPS_1_DOC_TYPE_RESOLUTION_ENTRY = 
        new ClassLoaderDocTypeResolutionEntry(
            ELEMENTS_XHTML_SERVER_SIDE_IMAGE_MAPS_1_PUBLICID,
            DocTypeIdentifier.ANY, 
            DTD_STANDARD_PATH + "xhtml-ssismap-1.mod");

    public static final IDocTypeResolutionEntry ENTITIES_LATIN_1_FOR_XHTML_DOC_TYPE_RESOLUTION_ENTRY = 
        new ClassLoaderDocTypeResolutionEntry(
            ENTITIES_LATIN_1_FOR_XHTML_PUBLICID,
            DocTypeIdentifier.ANY, 
            DTD_STANDARD_PATH + "xhtml-lat1.ent");

    public static final IDocTypeResolutionEntry ELEMENTS_XHTML_PARAM_ELEMENT_1_DOC_TYPE_RESOLUTION_ENTRY = 
        new ClassLoaderDocTypeResolutionEntry(
            ELEMENTS_XHTML_PARAM_ELEMENT_1_PUBLICID,
            DocTypeIdentifier.ANY, 
            DTD_STANDARD_PATH + "xhtml-param-1.mod");

    public static final IDocTypeResolutionEntry ENTITIES_XHTML_COMMON_ATTRIBUTES_1_DOC_TYPE_RESOLUTION_ENTRY = 
        new ClassLoaderDocTypeResolutionEntry(
            ENTITIES_XHTML_COMMON_ATTRIBUTES_1_PUBLICID,
            DocTypeIdentifier.ANY, 
            DTD_STANDARD_PATH + "xhtml-attribs-1.mod");

    public static final IDocTypeResolutionEntry ELEMENTS_XHTML_INLINE_PHRASAL_1_DOC_TYPE_RESOLUTION_ENTRY = 
        new ClassLoaderDocTypeResolutionEntry(
            ELEMENTS_XHTML_INLINE_PHRASAL_1_PUBLICID,
            DocTypeIdentifier.ANY, 
            DTD_STANDARD_PATH + "xhtml-inlphras-1.mod");

    public static final IDocTypeResolutionEntry ELEMENTS_XHTML_METAINFORMATION_1_DOC_TYPE_RESOLUTION_ENTRY = 
        new ClassLoaderDocTypeResolutionEntry(
            ELEMENTS_XHTML_METAINFORMATION_1_PUBLICID,
            DocTypeIdentifier.ANY, 
            DTD_STANDARD_PATH + "xhtml-meta-1.mod");

    public static final IDocTypeResolutionEntry ELEMENTS_XHTML_EDITING_ELEMENTS_1_DOC_TYPE_RESOLUTION_ENTRY = 
        new ClassLoaderDocTypeResolutionEntry(
            ELEMENTS_XHTML_EDITING_ELEMENTS_1_PUBLICID,
            DocTypeIdentifier.ANY, 
            DTD_STANDARD_PATH + "xhtml-edit-1.mod");

    public static final IDocTypeResolutionEntry ELEMENTS_XHTML_CLIENT_SIDE_IMAGE_MAPS_1_DOC_TYPE_RESOLUTION_ENTRY = 
        new ClassLoaderDocTypeResolutionEntry(
            ELEMENTS_XHTML_CLIENT_SIDE_IMAGE_MAPS_1_PUBLICID,
            DocTypeIdentifier.ANY, 
            DTD_STANDARD_PATH + "xhtml-csismap-1.mod");

    public static final IDocTypeResolutionEntry ELEMENTS_XHTML_SCRIPTING_1_DOC_TYPE_RESOLUTION_ENTRY = 
        new ClassLoaderDocTypeResolutionEntry(
            ELEMENTS_XHTML_SCRIPTING_1_PUBLICID,
            DocTypeIdentifier.ANY, 
            DTD_STANDARD_PATH + "xhtml-script-1.mod");

    public static final IDocTypeResolutionEntry ELEMENTS_XHTML_TEXT_1_DOC_TYPE_RESOLUTION_ENTRY = 
        new ClassLoaderDocTypeResolutionEntry(
            ELEMENTS_XHTML_TEXT_1_PUBLICID,
            DocTypeIdentifier.ANY, 
            DTD_STANDARD_PATH + "xhtml-text-1.mod");

    public static final IDocTypeResolutionEntry ELEMENTS_XHTML_RUBY_1_DOC_TYPE_RESOLUTION_ENTRY = 
        new ClassLoaderDocTypeResolutionEntry(
            ELEMENTS_XHTML_RUBY_1_PUBLICID,
            DocTypeIdentifier.ANY, 
            DTD_STANDARD_PATH + "xhtml-ruby-1.mod");

    public static final IDocTypeResolutionEntry ELEMENTS_XHTML_DOCUMENT_STRUCTURE_1_DOC_TYPE_RESOLUTION_ENTRY = 
        new ClassLoaderDocTypeResolutionEntry(
            ELEMENTS_XHTML_DOCUMENT_STRUCTURE_1_PUBLICID,
            DocTypeIdentifier.ANY, 
            DTD_STANDARD_PATH + "xhtml-struct-1.mod");

    public static final IDocTypeResolutionEntry ELEMENTS_XHTML_PRESENTATION_1_DOC_TYPE_RESOLUTION_ENTRY = 
        new ClassLoaderDocTypeResolutionEntry(
            ELEMENTS_XHTML_PRESENTATION_1_PUBLICID,
            DocTypeIdentifier.ANY, 
            DTD_STANDARD_PATH + "xhtml-pres-1.mod");

    public static final IDocTypeResolutionEntry ELEMENTS_XHTML_IMAGES_1_DOC_TYPE_RESOLUTION_ENTRY = 
        new ClassLoaderDocTypeResolutionEntry(
            ELEMENTS_XHTML_IMAGES_1_PUBLICID,
            DocTypeIdentifier.ANY, 
            DTD_STANDARD_PATH + "xhtml-image-1.mod");
    


    public static final IDocTypeResolutionEntry HTML_5_LEGACY_WILDCARD_DOC_TYPE_RESOLUTION_ENTRY =
        new ClassLoaderDocTypeResolutionEntry(
            DocTypeIdentifier.NONE,
            HTML_5_LEGACY_WILDCARD_SYSTEMID,
            DTD_STANDARD_PATH + "xhtml5-legacy-wildcard.dtd"); 
    

    
    
    
    
    public static final Set<IDocTypeResolutionEntry> ALL_XHTML_1_STRICT_RESOLUTION_ENTRIES =
        new HashSet<IDocTypeResolutionEntry>(Arrays.asList(
                new IDocTypeResolutionEntry[] {
                    XHTML_1_STRICT_DOC_TYPE_RESOLUTION_ENTRY,
                    ENTITIES_LATIN_1_FOR_XHTML_DOC_TYPE_RESOLUTION_ENTRY, 
                    ENTITIES_SYMBOLS_FOR_XHTML_DOC_TYPE_RESOLUTION_ENTRY, 
                    ENTITIES_SPECIAL_FOR_XHTML_DOC_TYPE_RESOLUTION_ENTRY 
                }));
    
    
    public static final Set<IDocTypeResolutionEntry> ALL_XHTML_1_TRANSITIONAL_RESOLUTION_ENTRIES =
        new HashSet<IDocTypeResolutionEntry>(Arrays.asList(
                new IDocTypeResolutionEntry[] {
                    XHTML_1_TRANSITIONAL_DOC_TYPE_RESOLUTION_ENTRY,
                    ENTITIES_LATIN_1_FOR_XHTML_DOC_TYPE_RESOLUTION_ENTRY, 
                    ENTITIES_SYMBOLS_FOR_XHTML_DOC_TYPE_RESOLUTION_ENTRY, 
                    ENTITIES_SPECIAL_FOR_XHTML_DOC_TYPE_RESOLUTION_ENTRY 
                }));
    
    
    public static final Set<IDocTypeResolutionEntry> ALL_XHTML_1_FRAMESET_RESOLUTION_ENTRIES =
        new HashSet<IDocTypeResolutionEntry>(Arrays.asList(
                new IDocTypeResolutionEntry[] {
                    XHTML_1_FRAMESET_DOC_TYPE_RESOLUTION_ENTRY,
                    ENTITIES_LATIN_1_FOR_XHTML_DOC_TYPE_RESOLUTION_ENTRY, 
                    ENTITIES_SYMBOLS_FOR_XHTML_DOC_TYPE_RESOLUTION_ENTRY, 
                    ENTITIES_SPECIAL_FOR_XHTML_DOC_TYPE_RESOLUTION_ENTRY 
                }));
    
    
    public static final Set<IDocTypeResolutionEntry> ALL_XHTML_11_RESOLUTION_ENTRIES =
        new HashSet<IDocTypeResolutionEntry>(Arrays.asList(
                new IDocTypeResolutionEntry[] {
                    XHTML_11_DOC_TYPE_RESOLUTION_ENTRY,
                    ELEMENTS_XHTML_INLINE_STYLE_1_DOC_TYPE_RESOLUTION_ENTRY,
                    ENTITIES_XHTML_MODULAR_FRAMEWORK_1_DOC_TYPE_RESOLUTION_ENTRY,
                    ENTITIES_XHTML_DATATYPES_1_DOC_TYPE_RESOLUTION_ENTRY,
                    ENTITIES_XHTML_QUALIFIED_NAMES_1_DOC_TYPE_RESOLUTION_ENTRY,
                    ENTITIES_XHTML_INTRINSIC_EVENTS_1_DOC_TYPE_RESOLUTION_ENTRY,
                    ELEMENTS_XHTML_FORMS_1_DOC_TYPE_RESOLUTION_ENTRY, 
                    ELEMENTS_XHTML_BASE_ELEMENT_1_DOC_TYPE_RESOLUTION_ENTRY, 
                    ELEMENTS_XHTML_BLOCK_STRUCTURAL_1_DOC_TYPE_RESOLUTION_ENTRY, 
                    ELEMENTS_XHTML_TABLES_1_DOC_TYPE_RESOLUTION_ENTRY, 
                    ELEMENTS_XHTML_STYLE_SHEETS_1_DOC_TYPE_RESOLUTION_ENTRY, 
                    ELEMENTS_XHTML_INLINE_STRUCTURAL_1_DOC_TYPE_RESOLUTION_ENTRY, 
                    ENTITIES_XHTML_CHARACTER_ENTITIES_1_DOC_TYPE_RESOLUTION_ENTRY, 
                    ENTITIES_XHTML_11_DOCUMENT_MODEL_1_DOC_TYPE_RESOLUTION_ENTRY, 
                    ELEMENTS_XHTML_HYPERTEXT_1_DOC_TYPE_RESOLUTION_ENTRY, 
                    ELEMENTS_XHTML_BLOCK_PRESENTATION_1_DOC_TYPE_RESOLUTION_ENTRY, 
                    ELEMENTS_XHTML_BIDI_OVERRIDE_ELEMENT_1_DOC_TYPE_RESOLUTION_ENTRY, 
                    ENTITIES_SPECIAL_FOR_XHTML_DOC_TYPE_RESOLUTION_ENTRY, 
                    ELEMENTS_XHTML_BLOCK_PHRASAL_1_DOC_TYPE_RESOLUTION_ENTRY, 
                    ELEMENTS_XHTML_LINK_ELEMENT_1_DOC_TYPE_RESOLUTION_ENTRY, 
                    ELEMENTS_XHTML_INLINE_PRESENTATION_1_DOC_TYPE_RESOLUTION_ENTRY, 
                    ELEMENTS_XHTML_LISTS_1_DOC_TYPE_RESOLUTION_ENTRY, 
                    ENTITIES_SYMBOLS_FOR_XHTML_DOC_TYPE_RESOLUTION_ENTRY, 
                    ELEMENTS_XHTML_EMBEDDED_OBJECT_1_DOC_TYPE_RESOLUTION_ENTRY, 
                    ELEMENTS_XHTML_SERVER_SIDE_IMAGE_MAPS_1_DOC_TYPE_RESOLUTION_ENTRY, 
                    ENTITIES_LATIN_1_FOR_XHTML_DOC_TYPE_RESOLUTION_ENTRY, 
                    ELEMENTS_XHTML_PARAM_ELEMENT_1_DOC_TYPE_RESOLUTION_ENTRY, 
                    ENTITIES_XHTML_COMMON_ATTRIBUTES_1_DOC_TYPE_RESOLUTION_ENTRY, 
                    ELEMENTS_XHTML_INLINE_PHRASAL_1_DOC_TYPE_RESOLUTION_ENTRY, 
                    ELEMENTS_XHTML_METAINFORMATION_1_DOC_TYPE_RESOLUTION_ENTRY, 
                    ELEMENTS_XHTML_EDITING_ELEMENTS_1_DOC_TYPE_RESOLUTION_ENTRY, 
                    ELEMENTS_XHTML_CLIENT_SIDE_IMAGE_MAPS_1_DOC_TYPE_RESOLUTION_ENTRY, 
                    ELEMENTS_XHTML_SCRIPTING_1_DOC_TYPE_RESOLUTION_ENTRY, 
                    ELEMENTS_XHTML_TEXT_1_DOC_TYPE_RESOLUTION_ENTRY, 
                    ELEMENTS_XHTML_RUBY_1_DOC_TYPE_RESOLUTION_ENTRY, 
                    ELEMENTS_XHTML_DOCUMENT_STRUCTURE_1_DOC_TYPE_RESOLUTION_ENTRY, 
                    ELEMENTS_XHTML_PRESENTATION_1_DOC_TYPE_RESOLUTION_ENTRY, 
                    ELEMENTS_XHTML_IMAGES_1_DOC_TYPE_RESOLUTION_ENTRY 
                }));
    
    
    public static final Set<IDocTypeResolutionEntry> ALL_HTML_5_RESOLUTION_ENTRIES =
        new HashSet<IDocTypeResolutionEntry>(Arrays.asList(
                new IDocTypeResolutionEntry[] {
                    HTML_5_LEGACY_WILDCARD_DOC_TYPE_RESOLUTION_ENTRY
                }));
    

    
    /*
     * These are the only tags in the XHTML spec that are allowed to have an
     * empty body
     */
    public static final String[] MINIMIZABLE_XHTML_TAGS =
        new String[] {
                "area", "base", "basefont", "br", "col", "command", "embed", 
                "frame", "hr", "img", "input", "isindex", "keygen", "link", 
                "meta", "param", "source", "wbr"
            };


    
    
    
    
    private Standards() {
        super();
    }
    
    
    
}
