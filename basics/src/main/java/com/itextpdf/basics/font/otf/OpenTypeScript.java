/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.itextpdf.basics.font.otf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author admin
 */
public class OpenTypeScript {

    public final String DEFAULT_SCRIPT = "DFLT";
    
    private OpenTypeFontTableReader openTypeReader;
    private List<ScriptRecord> records;
    
    public OpenTypeScript(OpenTypeFontTableReader openTypeReader, int locationScriptTable) throws IOException {
        this.openTypeReader = openTypeReader;
        records = new ArrayList<ScriptRecord>();
        openTypeReader.rf.seek(locationScriptTable);
        TagAndLocation[] tagsLocs = openTypeReader.readTagAndLocations(locationScriptTable);
        for (TagAndLocation tagLoc : tagsLocs) {
            readScriptRecord(tagLoc);
        }        
    }
    
    public List<ScriptRecord> getScriptRecords() {
        return records;
    }
    
    public LanguageRecord getLanguageRecord(String[] scripts, String language) {
        ScriptRecord scriptFound = null;
        ScriptRecord scriptDefault = null;
        for (ScriptRecord sr : records) {
            if (DEFAULT_SCRIPT.equals(sr.tag)) {
                scriptDefault = sr;
                break;
            }
        }
        for (String script : scripts) {
            for (ScriptRecord sr : records) {
                if (sr.tag.equals(script)) {
                    scriptFound = sr;
                    break;
                }
                if (DEFAULT_SCRIPT.equals(script)) {
                    scriptDefault = sr;
                }
            }
            if (scriptFound != null) {
                break;
            }
        }
        if (scriptFound == null) {
            scriptFound = scriptDefault;
        }
        if (scriptFound == null) {
            return null;
        }
        LanguageRecord lang = null;
        for (LanguageRecord lr : scriptFound.languages) {
            if (lr.tag.equals(language)) {
                lang = lr;
                break;
            }
        }
        if (lang == null) {
            lang = scriptFound.defaultLanguage;
        }
        return lang;
    }
    
    private void readScriptRecord(TagAndLocation tagLoc) throws IOException {
        openTypeReader.rf.seek(tagLoc.location);
        int locationDefaultLanguage = openTypeReader.rf.readUnsignedShort();
        if (locationDefaultLanguage > 0) {
            locationDefaultLanguage += tagLoc.location;
        }
        TagAndLocation[] tagsLocs = openTypeReader.readTagAndLocations(tagLoc.location);
        ScriptRecord srec = new ScriptRecord();
        srec.tag = tagLoc.tag;
        srec.languages = new LanguageRecord[tagsLocs.length];
        for (int k = 0; k < tagsLocs.length; ++k) {
            srec.languages[k] = readLanguageRecord(tagsLocs[k]);
        }
        if (locationDefaultLanguage > 0) {
            TagAndLocation t = new TagAndLocation();
            t.tag = "";
            t.location = locationDefaultLanguage;
            srec.defaultLanguage = readLanguageRecord(t);
        }
        records.add(srec);
    }
    
    private LanguageRecord readLanguageRecord(TagAndLocation tagLoc) throws IOException {
        LanguageRecord rec = new LanguageRecord();
        //skip lookup order
        openTypeReader.rf.seek(tagLoc.location + 2);
        rec.featureRequired = openTypeReader.rf.readUnsignedShort();
        int count = openTypeReader.rf.readUnsignedShort();
        rec.features = openTypeReader.readUShortArray(count);
        rec.tag = tagLoc.tag;
        return rec;
    }
}