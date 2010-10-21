// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package org.datacite.mds.web;

import java.lang.Long;
import java.lang.String;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.validation.Valid;
import org.datacite.mds.domain.Dataset;
import org.datacite.mds.domain.Metadata;
import org.joda.time.format.DateTimeFormat;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

privileged aspect MetadataController_Roo_Controller {
    
    @RequestMapping(method = RequestMethod.POST)
    public String MetadataController.create(@Valid Metadata metadata, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("metadata", metadata);
            addDateTimeFormatPatterns(model);
            return "metadatas/create";
        }
        metadata.persist();
        return "redirect:/metadatas/" + metadata.getId();
    }
    
    @RequestMapping(params = "form", method = RequestMethod.GET)
    public String MetadataController.createForm(Model model) {
        model.addAttribute("metadata", new Metadata());
        addDateTimeFormatPatterns(model);
        List dependencies = new ArrayList();
        if (Dataset.countDatasets() == 0) {
            dependencies.add(new String[]{"dataset", "datasets"});
        }
        model.addAttribute("dependencies", dependencies);
        return "metadatas/create";
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public String MetadataController.show(@PathVariable("id") Long id, Model model) {
        addDateTimeFormatPatterns(model);
        model.addAttribute("metadata", Metadata.findMetadata(id));
        model.addAttribute("itemId", id);
        return "metadatas/show";
    }
    
    @RequestMapping(method = RequestMethod.GET)
    public String MetadataController.list(@RequestParam(value = "page", required = false) Integer page, @RequestParam(value = "size", required = false) Integer size, Model model) {
        if (page != null || size != null) {
            int sizeNo = size == null ? 10 : size.intValue();
            model.addAttribute("metadatas", Metadata.findMetadataEntries(page == null ? 0 : (page.intValue() - 1) * sizeNo, sizeNo));
            float nrOfPages = (float) Metadata.countMetadatas() / sizeNo;
            model.addAttribute("maxPages", (int) ((nrOfPages > (int) nrOfPages || nrOfPages == 0.0) ? nrOfPages + 1 : nrOfPages));
        } else {
            model.addAttribute("metadatas", Metadata.findAllMetadatas());
        }
        addDateTimeFormatPatterns(model);
        return "metadatas/list";
    }
    
    @ModelAttribute("datasets")
    public Collection<Dataset> MetadataController.populateDatasets() {
        return Dataset.findAllDatasets();
    }
    
    void MetadataController.addDateTimeFormatPatterns(Model model) {
        model.addAttribute("metadata_lastupdated_date_format", DateTimeFormat.patternForStyle("S-", LocaleContextHolder.getLocale()));
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.GET, headers = "Accept=application/json")
    @ResponseBody
    public String MetadataController.showJson(@PathVariable("id") Long id) {
        return Metadata.findMetadata(id).toJson();
    }
    
    @RequestMapping(method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<String> MetadataController.createFromJson(@RequestBody String json) {
        Metadata.fromJsonToMetadata(json).persist();
        return new ResponseEntity<String>("Metadata created", HttpStatus.CREATED);
    }
    
    @RequestMapping(headers = "Accept=application/json")
    @ResponseBody
    public String MetadataController.listJson() {
        return Metadata.toJsonArray(Metadata.findAllMetadatas());
    }
    
    @RequestMapping(value = "/jsonArray", method = RequestMethod.POST, headers = "Accept=application/json")
    public ResponseEntity<String> MetadataController.createFromJsonArray(@RequestBody String json) {
        for (Metadata metadata: Metadata.fromJsonArrayToMetadatas(json)) {
            metadata.persist();
        }
        return new ResponseEntity<String>("Metadata created", HttpStatus.CREATED);
    }
    
}
